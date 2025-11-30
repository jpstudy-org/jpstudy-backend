package orinnetwork.jpstudy.infrastructure.exception;

import java.util.Arrays;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessageKey());

        this.errorCode = errorCode;
        this.args = null;
    }

    public CustomException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessageKey() + " args: " + Arrays.toString(args));

        this.errorCode = errorCode;
        this.args = args;
    }
}