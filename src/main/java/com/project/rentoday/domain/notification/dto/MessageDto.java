package com.project.rentoday.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
public class MessageDto implements Serializable {

    //역직렬화 InvalidClassException 방지
    private static final long serialVersionUID = 1L;

    //메시지
    private String message;
    //수신자
    private String receiver;


}