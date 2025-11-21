package orinnetwork.jpstudy.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // [Auth]
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "AUTH-001", "error.auth.login_failed"),
    ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "AUTH-002", "error.auth.locked"),
    ACCOUNT_DISABLED(HttpStatus.UNAUTHORIZED, "AUTH-003", "error.auth.disabled"),
    NOT_ADMIN(HttpStatus.FORBIDDEN, "AUTH-004", "error.auth.not_admin"),
    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "AUTH-005", "error.auth.oauth_provider_not_supported"),
    OAUTH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-006", "error.auth.oauth_fail"),

    // [Member]
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEM-001", "error.member.not_found"),

    // [Post]
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-001", "error.post.not_found"),
    POST_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "POST-002", "error.post.not_active"),
    POST_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "POST-003", "error.post.category_not_found"),
    POST_NOT_OWNER(HttpStatus.FORBIDDEN, "POST-004", "error.post.not_owner"),

    // [Comment]
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CMT-001", "error.comment.not_found"),
    COMMENT_NOT_OWNER(HttpStatus.FORBIDDEN, "CMT-002", "error.comment.not_owner"),

    // [Common - 공통]
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON-001", "error.common.invalid_input"),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON-002", "error.common.server_error"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON-003", "error.common.bad_request"),

    // [Exam]
    EXAM_NOT_FOUND(HttpStatus.NOT_FOUND, "EXAM-001", "error.exam.not_found"),
    EXAM_NOT_OWNER(HttpStatus.FORBIDDEN, "EXAM-002", "error.exam.not_owner"),
    NOT_ENOUGH_QUESTIONS(HttpStatus.BAD_REQUEST, "EXAM-003", "error.exam.not_enough_questions"),
    TEST_ATTEMPT_NOT_FOUND(HttpStatus.NOT_FOUND, "EXAM-004", "error.exam.attempt_not_found"),
    TEST_ALREADY_SUBMITTED(HttpStatus.BAD_REQUEST, "EXAM-005", "error.exam.already_submitted"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "EXAM-006", "error.exam.resource.not_found"),

    // [Dictionary - Kanji]
    KANJI_NOT_FOUND(HttpStatus.NOT_FOUND, "DIC-001", "error.dictionary.kanji_not_found"),
    KANJI_ALREADY_EXISTS(HttpStatus.CONFLICT, "DIC-002", "error.dictionary.kanji_exists"),

    // [Dictionary - Word]
    WORD_NOT_FOUND(HttpStatus.NOT_FOUND, "DIC-003", "error.dictionary.word_not_found"),
    WORD_ALREADY_EXISTS(HttpStatus.CONFLICT, "DIC-004", "error.dictionary.word_exists"),

    // [Question]
    LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "QST-001", "error.question.level_not_found"),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "QST-002", "error.question.category_not_found"),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QST-003", "error.question.not_found"),
    SECTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QST-004", "error.question.section_not_found"),
    LEVEL_ALREADY_EXISTS(HttpStatus.CONFLICT, "QST-005", "error.question.level_exists"),
    TEST_NOT_OWNER(HttpStatus.FORBIDDEN, "EXM-005", "error.exam.not_owner"),

    // [Image/File]
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "IMG-001", "error.image.size_exceeded"),
    FILE_TYPE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "IMG-002", "error.image.type_not_supported"),

    // [Inquiry]
    INQUIRY_NOT_FOUND(HttpStatus.NOT_FOUND, "INQ-001", "error.inquiry.not_found"),
    INQUIRY_NOT_OWNER(HttpStatus.FORBIDDEN, "INQ-002", "error.inquiry.not_owner"),

    // [Notification]
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOT-001", "error.notification.not_found"),
    NOTIFICATION_NOT_OWNER(HttpStatus.FORBIDDEN, "NOT-002", "error.notification.not_owner");

    private final HttpStatus status;
    private final String code;
    private final String messageKey;
}
