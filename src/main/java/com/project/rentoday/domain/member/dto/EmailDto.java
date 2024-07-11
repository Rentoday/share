package com.project.rentoday.domain.member.dto;

import lombok.Getter;
import lombok.Setter;


public class EmailDto {

    @Getter
    @Setter
    public static class Request {
        private String email;
        private String accessCode;
    }

    @Getter
    @Setter
    public static class codeRequest {
        private String email;
        private String accessCode;
    }
}