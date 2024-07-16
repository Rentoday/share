package com.project.rentoday.domain.notification.service;

import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService{

    @Override
    public String commentMessage(String email) {
        return email + " 님께서 회원님의 상품에 댓글을 남기셨습니다.";
    }

    @Override
    public String replyMessage(String email) {
        return email + " 님께서 회원님의 댓글에 답글을 남기셨습니다.";
    }

    @Override
    public String salesMessage(String email) {
        return email + " 님께서 회원님의 상품을 구매하셨습니다.";
    }

    @Override
    public String salesCancelMessage(String email) {
        return email + " 님께서 회원님의 상품구매를 취소하셨습니다.";
    }
}
