package com.project.rentoday.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

public class EmailDto {

    @Getter
    @Setter
    @Schema(description = "사용자 이메일 요청 DTO")
    public static class emailRequest {
        @Schema(description = "사용자 이메일", required = true)
        @NotBlank(message = "email을 입력해주세요.")
        @Email
        private String email;
    }

    @Getter
    @Setter
    @Schema(description = "사용자 이메일 인증코드 요청 DTO")
    public static class codeRequest {
        @Schema(description = "사용자 이메일", required = true)
        @NotBlank(message = "email을 입력해주세요.")
        @Email
        private String email;
        @Schema(description = "이메일 인증코드", required = true)
        @NotBlank(message = "인증코드를 입력해주세요.")
        private String accessCode;
    }
}