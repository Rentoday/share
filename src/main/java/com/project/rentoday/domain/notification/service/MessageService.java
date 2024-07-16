package com.project.rentoday.domain.notification.service;

public interface MessageService {

    public String commentMessage(String email);
    public String replyMessage(String email);
    public String salesMessage(String email);
    public String salesCancelMessage(String email);

}
