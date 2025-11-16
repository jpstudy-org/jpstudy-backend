package orinnetwork.jpstudy.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import orinnetwork.jpstudy.infrastructure.exception.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Auth 로그인 시 비밀번호 틀림
     */
    @ExceptionHandler(BadCredentialsException.class)
    protected ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException e) {
        ErrorResponse response = new ErrorResponse("LOGIN-001", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Auth: 계정 잠김 (LockedException)
     */
    @ExceptionHandler(LockedException.class)
    protected ResponseEntity<ErrorResponse> handleAccountLocked(LockedException e) {
        // e.getMessage()에는 "10분간 잠깁니다" 메시지가 담겨있습니다.
        ErrorResponse response = new ErrorResponse("AUTH-002", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * Auth: 탈퇴한 회원 등 (IllegalArgumentException)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorResponse response = new ErrorResponse("COMMON-001", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 그 외 모든 서버 내부 예외
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse response = new ErrorResponse("SERVER-ERR", "서버 내부 오류가 발생했습니다.");
        // (실제 서버에서는 e.printStackTrace() 등으로 로그를 남겨야 합니다)
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Auth: 비활성화된 계정 (탈퇴, 휴면 등)
     */
    @ExceptionHandler(DisabledException.class)
    protected ResponseEntity<ErrorResponse> handleAccountDisabled(DisabledException e) {
        // DisabledException은 기본 메시지가 "User is disabled" 뿐입니다.
        // 따라서 직접 메시지를 지정해주는 것이 좋습니다.
        ErrorResponse response = new ErrorResponse("AUTH-003", "탈퇴 처리되었거나 비활성화된 계정입니다.");
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
}
