package com.project.rentoday.global.exception;

import com.project.rentoday.domain.comment.exception.CommentException;
import com.project.rentoday.domain.member.exception.EmailException;
import com.project.rentoday.domain.member.exception.MemberException;
import com.project.rentoday.domain.member.exception.VerificationException;
import com.project.rentoday.domain.notification.exception.NotificationException;
import com.project.rentoday.domain.payment.exception.ResourceNotFoundException;
import com.project.rentoday.global.jwt.exception.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class GlobalExceptionHandler {

    //회원예외 처리 핸들러
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
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<String> handleJwtException(JwtException exception) {
        return new ResponseEntity<>(exception.getMessage(), exception.getJwtErrorCode().getHttpStatus());
    }

    //이메일검증예외 처리 핸들러
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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(exception.getBindingResult().getAllErrors().get(0).getDefaultMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
