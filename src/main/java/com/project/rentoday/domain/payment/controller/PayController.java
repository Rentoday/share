package com.project.rentoday.domain.payment.controller;

import com.project.rentoday.domain.payment.dto.request.PayCallbackRequestDto;
import com.project.rentoday.domain.payment.dto.request.PayRequestDto;
import com.project.rentoday.domain.payment.dto.response.PayInfoResponse;
import com.project.rentoday.domain.payment.service.PayService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "Pay")
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

    //멤버별 결제 내역
    @GetMapping("/member")
    public Page<PayInfoResponse> getPaymentsByMember(
            @AuthenticationPrincipal UserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String email = principal.getUsername();
        return payService.getPaymentsByMember(email, page, size);
    }

    @PostMapping("/cancel/{paymentId}")
    public ResponseEntity<String> cancelPayment(@PathVariable Long paymentId,
                                                @AuthenticationPrincipal UserDetails principal) {
        String email = principal.getUsername();
        payService.cancelPayment(paymentId, email);
        return ResponseEntity.ok("결제가 성공적으로 취소되었습니다.");
    }
}


