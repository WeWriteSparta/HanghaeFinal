package com.example.hanghaefinal.exception;

import com.example.hanghaefinal.exception.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { IllegalArgumentException.class })
    public ResponseEntity<Object> handleApiRequestException(IllegalArgumentException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = { NullPointerException.class })
    public ResponseEntity<Object> handleApiRequestException(NullPointerException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(value = { IOException.class })
    public ResponseEntity<Object> handleApiRequestException(IOException ex) {
        RestApiException restApiException = new RestApiException();
        restApiException.setHttpStatus(HttpStatus.BAD_REQUEST);
        restApiException.setErrorMessage(ex.getMessage());

        return new ResponseEntity<>(
                restApiException,
                HttpStatus.BAD_REQUEST
        );
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("U001", e.getMessage()), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse("U001", e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AdminOnlyException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnlyException(AdminOnlyException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }

    @ExceptionHandler(CommentLimitException.class)
    public ResponseEntity<ErrorResponse> handleCommentLimitException(CommentLimitException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCommentNotFoundException(CommentNotFoundException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(ContentNullException.class)
    public ResponseEntity<ErrorResponse> handleContentNullException(ContentNullException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(EqualPasswordException.class)
    public ResponseEntity<ErrorResponse> handleEqualPasswordException(EqualPasswordException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(IdDuplicationException.class)
    public ResponseEntity<ErrorResponse> handleIdDuplicationException(IdDuplicationException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(IllegalUserException.class)
    public ResponseEntity<ErrorResponse> handleIllegalUserException(IllegalUserException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(IntroductionLimitException.class)
    public ResponseEntity<ErrorResponse> handleIntroductionLimitException(IntroductionLimitException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(NickDuplicationException.class)
    public ResponseEntity<ErrorResponse> handleNickDuplicationException(NickDuplicationException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(NoticeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoticeNotFoundException(NoticeNotFoundException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(ParagraphCountException.class)
    public ResponseEntity<ErrorResponse> handleParagraphCountException(ParagraphCountException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(ParagraphLimitException.class)
    public ResponseEntity<ErrorResponse> handleParagraphLimitException(ParagraphLimitException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(ParagraphNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleParagraphNotFoundException(ParagraphNotFoundException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(PasswordCheckException.class)
    public ResponseEntity<ErrorResponse> handlePasswordCheckException(PasswordCheckException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e){
        return new ResponseEntity<>(new ErrorResponse("", e.getMessage()), HttpStatus.UNAUTHORIZED);

    }
}

