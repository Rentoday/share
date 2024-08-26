package com.project.rentoday.global.exception;

import com.project.rentoday.domain.comment.exception.CommentException;
import com.project.rentoday.domain.district.exception.DistrictNotFoundException;
import com.project.rentoday.domain.member.exception.EmailException;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.exception.VerificationException;
import com.project.rentoday.domain.notification.exception.NotificationException;
import com.project.rentoday.domain.park.exception.EndTimeBeforeStartTimeException;
import com.project.rentoday.domain.park.exception.ParkIdNotFoundException;
import com.project.rentoday.domain.park.exception.PriceUnderZeroException;
import com.project.rentoday.domain.payment.exception.PayNotFoundException;
import com.project.rentoday.domain.reservation.exception.ReservationAlreadyExistsException;
import com.project.rentoday.domain.reservation.exception.ReservationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class ExceptionController {

    //이메일예외 처리 핸들러
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<String> handleEmailException(EmailException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getEmailErrorCode().getHttpStatus());
    }

    //회원예외 처리 핸들러
    @ExceptionHandler(MemberException.class)
    public ResponseEntity<String> handleMemberException(MemberException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getMemberErrorCode().getHttpStatus());
    }

    //jwt예외 처리 핸들러
//    @ExceptionHandler(JwtException.class)
//    public ResponseEntity<String> handleJwtException(JwtException exception) {
//        return new ResponseEntity<>(exception.getMessage(), exception.getJwtErrorCode().getHttpStatus());
//    }

    //검증예외 처리 핸들러
    @ExceptionHandler(VerificationException.class)
    public ResponseEntity<String> handleVerificationException(VerificationException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getVerificationErrorCode().getHttpStatus());
    }

    //댓글예외 처리 핸들러
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<String> handleCommentException(CommentException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getCommentErrorCode().getHttpStatus());
    }

    //댓글예외 처리 핸들러
    @ExceptionHandler(NotificationException.class)
    public ResponseEntity<String> handleNotificationException(NotificationException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getNotificationErrorCode().getHttpStatus());
    }

    //dto 검증 예외처리 핸들러
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> handleValidationException(MethodArgumentNotValidException exception) {

        List<String> errors = new ArrayList<>();
        exception.getBindingResult().getFieldErrors().forEach(error -> {
            errors.add(error.getDefaultMessage());
        });

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    //주차 공간을 찾을 수 없음
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleParkIdNotFoundException(ParkIdNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //해당 구를 찾을 수 없음
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleDistrictNotFoundException(DistrictNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //해당 예약은 이미 존재함
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleReservationAlreadyExistsException(ReservationAlreadyExistsException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //존재하지 않는 예약
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleReservationNotFoundException(ReservationNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    //판매 끝 시간 > 판매 시작 시간
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleTimeException(EndTimeBeforeStartTimeException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    //판매 금액은 0원 이상이어야 함
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleParkPriceException(PriceUnderZeroException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_ACCEPTABLE.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
    }

    //해당 결제 건을 찾을 수 없음
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handlePayNotFoundException(PayNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
