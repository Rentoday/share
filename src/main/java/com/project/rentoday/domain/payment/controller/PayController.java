package com.project.rentoday.domain.payment.controller;

import com.project.rentoday.domain.payment.dto.request.PayCallbackRequestDto;
import com.project.rentoday.domain.payment.dto.request.PayRequestDto;
import com.project.rentoday.domain.payment.dto.response.PayInfoResponse;
import com.project.rentoday.domain.payment.service.PayService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}

//    @GetMapping("/{id}")
//    public ResponseEntity<List<PayInfoResponse>> getPayInfoByMember(@PathVariable Long memberId) {
//        List<PayInfoResponse> pays = payService.getPayInfo(memberId);
//        return ResponseEntity.ok().body(pays);
//    }
//}
