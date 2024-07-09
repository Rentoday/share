package com.project.rentoday.domain.payment.controller;

import com.project.rentoday.domain.payment.dto.request.PayCallbackRequestDto;
import com.project.rentoday.domain.payment.dto.request.PayRequestDto;
import com.project.rentoday.domain.payment.service.PayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pays")
public class PayController {

    private final PayService payService;

    @PostMapping("/request")
    public ResponseEntity<PayRequestDto> requestPay(@RequestBody PayRequestDto requestDto) {

        PayRequestDto payRequestDto = payService.requestPay(requestDto.getReservationUid());
        return ResponseEntity.ok(payRequestDto);
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handlePayCallback(@RequestBody PayCallbackRequestDto callback) {

        payService.payByCallback(callback);
        return ResponseEntity.ok("결제가 성공적으로 진행되었습니다.");
    }
}
