package me.yjeong.springbootdeveloper.config.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 해당 패키지 내, 다른 패키지에서 상속받아 자손 클래스에서 접근 가능
public class ErrorResponse {
    private String message;
    private String code;

    private ErrorResponse(final ErrorCode code){
        this.message = code.getMessage();
        this.code = code.getCode();
    }

    public ErrorResponse(final ErrorCode code, final String message){
        this.message = message;
        this.code = code.getCode();
    }

    public static ErrorResponse of(final ErrorCode code){
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, final String message){
        return new ErrorResponse(code, message);
    }
}
