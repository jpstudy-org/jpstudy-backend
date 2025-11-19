package orinnetwork.jpstudy.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // [Auth 관련]
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "error.auth.login_failed"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH-002", "error.auth.locked"),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "AUTH-003", "error.auth.disabled"),
    BAD_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-001", "error.auth.bad_credentials"),

    // [Common - 공통]
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON-001", "error.common.invalid_input"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-002", "error.common.server_error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-003", "error.common.bad_request"),

    // Exam
    EXAM_NOT_FOUND(HttpStatus.NOT_FOUND, "EXAM-001", "error.exam.not_found"),
    EXAM_NOT_OWNER(HttpStatus.FORBIDDEN, "EXAM-002", "error.exam.not_owner"),

    // [Dictionary - Kanji]
    KANJI_NOT_FOUND(HttpStatus.NOT_FOUND, "DIC-001", "error.dictionary.kanji_not_found"),
    KANJI_ALREADY_EXISTS(HttpStatus.CONFLICT, "DIC-002", "error.dictionary.kanji_exists");



    private final HttpStatus status;
    private final String code;
    private final String messageKey;
}
