package com.project.rentoday.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.rentoday.domain.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
    @Schema(description = "사용자 회원가입 요청 DTO")
    public static class CreateRequest {
        
        @Schema(description = "사용자 이메일", required = true)
        @NotBlank(message = "email을 입력해주세요.")
        @Email
        private String email;

        @Schema(description = "사용자 패스워드", required = true)
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{1,13}$", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자를 포함한 13자리 이하이어야 합니다.")
        private String password;

        @Schema(description = "사용자 이름", required = true)
        @NotBlank(message = "이름을 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z가-힣]+$", message = "이름에는 특수문자를 포함할수 없습니다.")
        private String name;

        @Schema(description = "사용자 전화번호", required = true)
        @NotBlank(message = "전화번호를 입력해주세요.")
        @Pattern(regexp = "^\\d{1,11}$", message = "11자리 이하의 숫자만을 입력해주세요.")
        private String phone;

        @Schema(description = "사용자 프로필 이미지", required = true)
        private MultipartFile profileImage;
    }

    //회원 조회
    @Getter
    @Setter
    @NoArgsConstructor
    @JsonFormat(pattern = "yy.MM.dd")
    @Schema(description = "사용자 조회 응답 DTO")
    public static class ReadResponse {
        @Schema(description = "사용자 이메일")
        private String email;
        @Schema(description = "사용자 이름")
        private String name;
        @Schema(description = "사용자 전화번호")
        private String phone;
        @Schema(description = "사용자 프로필 이미지")
        private String profileImage;
        @Schema(description = "사용자 소셜 로그인 아이디")
        private String oauthId;
        @Schema(description = "사용자 가입일")
        private LocalDateTime createdDate;

        public ReadResponse(Member member) {
            this.email = member.getEmail();
            this.name = member.getName();
            this.phone = member.getPhone();
            this.profileImage = member.getProfileImage();
            this.oauthId = member.getOauthId();
            this.createdDate = member.getCreatedDate();
        }
    }

    //회원 정보 수정
    @Getter
    @Setter
    @Schema(description = "사용자 정보 수정 요청 DTO")
    public static class UpdateRequest {

        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{1,13}$", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자를 포함한 13자리 이하이어야 합니다.")
        @Schema(description = "사용자 패스워드")
        private String password;

        @Schema(description = "사용자 프로필 이미지")
        private MultipartFile profileImage;
    }

    //회원 삭제
    @Getter
    @Setter
    @Schema(description = "사용자 정보 삭제 요청 DTO")
    public static class DeleteRequest {
        @Schema(description = "사용자 패스워드")
        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{1,13}$", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자를 포함한 13자리 이하이어야 합니다.")
        private String password;
    }  
    
    //회원 로그인
    @Getter
    @Setter
    @Schema(description = "로그인 요청 DTO")
    public static class LoginRequest {

        @NotNull(message = "email을 입력해주세요.")
        @Pattern(regexp = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$", message = "이메일 형식으로 입력해주세요.")
        @Schema(description = "사용자 이메일")
        private String email;

        @NotBlank(message = "비밀번호를 입력해주세요.")
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{1,13}$", message = "비밀번호는 영어 대/소문자, 숫자, 특수문자를 포함한 13자리 이하이어야 합니다.")
        @Schema(description = "사용자 패스워드")
        private String password;

    }

    //회원 details 생성
    @Getter
    @Setter
    @AllArgsConstructor
    public static class CreateDetails {
        private String email;
        private String roleType;
        private String password;
    }

}