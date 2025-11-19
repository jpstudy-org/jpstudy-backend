package orinnetwork.jpstudy.infrastructure.exception;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import orinnetwork.jpstudy.infrastructure.exception.dto.ErrorResponse;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        return createErrorResponse(e.getErrorCode(), e.getArgs());
    }

    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        // ErrorCode.LOGIN_FAILED로 매핑
        return createErrorResponse(ErrorCode.LOGIN_FAILED);
    }

    @ExceptionHandler(LockedException.class)
    protected ResponseEntity<ErrorResponse> handleAccountLocked(LockedException e) {
        return createErrorResponse(ErrorCode.ACCOUNT_LOCKED);
    }

    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<ErrorResponse> handleAccountDisabled(DisabledException e) {
        return createErrorResponse(ErrorCode.ACCOUNT_DISABLED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        return createErrorResponse(ErrorCode.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(null);

        if (message == null) {
            return createErrorResponse(ErrorCode.INVALID_INPUT);
        }

        return new ResponseEntity<>(
                new ErrorResponse("INPUT-001", message),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unhandled Exception", e);
        return createErrorResponse(ErrorCode.SERVER_ERROR);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(ErrorCode errorCode, Object... args) {
        // 1. 다국어 메시지 가져오기
        String translatedMessage = messageSource.getMessage(
                errorCode.getMessageKey(),
                args,
                LocaleContextHolder.getLocale()
        );

        // 2. 응답 생성
        return new ResponseEntity<>(
                new ErrorResponse(errorCode.getCode(), translatedMessage),
                errorCode.getStatus()
        );
    }
}
