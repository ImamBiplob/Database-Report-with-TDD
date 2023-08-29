package com.imambiplob.databasereport.exception;

import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handling custom validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> customValidationErrorHandling(MethodArgumentNotValidException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Input Validation Failed",
                Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handling custom illegal query exception
    @ExceptionHandler(IllegalQueryException.class)
    public ResponseEntity<?> customIllegalQueryExceptionHandling(IllegalQueryException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "You Have Given Prohibited Query!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handling custom report not found exception
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<?> customReportNotFoundExceptionHandling(ReportNotFoundException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Report Not Found!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    // Handling custom Malformed SQL Exception
    @ExceptionHandler(GenericJDBCException.class)
    public ResponseEntity<?> customMalformedSQLExceptionHandling(GenericJDBCException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Malformed SQL!!! Edit Report with Correct SQL Syntax and Try Again", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handling custom Invalid SQL Exception
    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<?> customInvalidSQLExceptionHandling(SQLGrammarException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid SQL!!! Edit Report with Valid SQL and Try Again", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    // Handling global exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, WebRequest request){
        ErrorDetails errorDetails =
                new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}