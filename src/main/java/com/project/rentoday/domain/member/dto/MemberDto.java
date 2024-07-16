package com.project.rentoday.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

public class MemberDto {

    //회원 가입
    @Getter
    @Setter
    public static class CreateRequest {
        @NotNull(message = "email을 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{1,13}$", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자를 포함한 13자리 이하이어야 합니다.")
        private String password;

        @NotBlank(message = "이름을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름에는 특수문자를 포함할수 없습니다.")
        private String name;

        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^\\d{1,11}$", message = "11자리 이하의 숫자만을 입력해주세요.")
        private String phone;

        private MultipartFile profileImage;
    }

    //회원 조회
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonFormat(pattern = "yy.MM.dd")
    public static class ReadResponse {
        private String email;
        private String name;
        private String phone;
        private String profileImage;
        private LocalDateTime createdAt;
    }

    //회원 정보 수정
    @Getter
    @Setter
    public static class UpdateRequest {
        @NotBlank(message = "비밀번호를 입력해주세요.")
        private String password;
        private MultipartFile profileImage;
    }

    //회원 삭제
    @Getter
    @Setter
    public static class DeleteRequest {
        private String password;
    }

    @Getter
    @Setter
    public static class LoginRequest {

        @NotNull(message = "email을 입력해주세요.")
        @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$", message = "이메일 형식으로 입력해주세요.")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{1,13}$", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자를 포함한 13자리 이하이어야 합니다.")
        private String password;

    }

    //회원 삭제
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreateDetails {
        private String email;
        private String roleType;
        private String password;
    }

}