package com.szityu.stackoverflow.teamsandplayersdbexample.web;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class HttpExceptionHandler {

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler
    public String handleNoSuchElement(NoSuchElementException ex) {
        return ex.getMessage();
    }
}
