package orinnetwork.jpstudy.infrastructure.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.args = null;
    }

    public CustomException(ErrorCode errorCode, Object... args) {
        this.errorCode = errorCode;
        this.args = args;
    }
}