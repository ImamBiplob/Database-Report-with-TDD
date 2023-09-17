package com.imambiplob.databasereport.exception;

import org.hibernate.exception.GenericJDBCException;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Objects;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handling custom validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> customValidationErrorHandling(MethodArgumentNotValidException exception) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Input Validation Failed",
                Objects.requireNonNull(exception.getBindingResult().getFieldError()).getDefaultMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handling bad credentials exception
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> customBadCredentialsExceptionHandling(BadCredentialsException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Email or Password is INCORRECT!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handling access control exception
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> customAccessControlHandling(AccessDeniedException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Access to this Endpoint is SECURED!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
    }

    /**
     * Handling username not found exception
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> customUsernameNotFoundExceptionHandling(UsernameNotFoundException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "User Not Found!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling data integrity violation exception
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> customDataIntegrityViolationExceptionHandling(DataIntegrityViolationException exception) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid Input!!! Violating Column Specification!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handling custom illegal query exception
     */
    @ExceptionHandler(IllegalQueryException.class)
    public ResponseEntity<?> customIllegalQueryExceptionHandling(IllegalQueryException exception) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "You Have Given Prohibited Query!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handling custom report not found exception
     */
    @ExceptionHandler(ReportNotFoundException.class)
    public ResponseEntity<?> customReportNotFoundExceptionHandling(ReportNotFoundException exception) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Report Not Found!!!", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);

    }

    /**
     * Handling custom malformed SQL statement exception
     */
    @ExceptionHandler(GenericJDBCException.class)
    public ResponseEntity<?> customMalformedSQLStatementExceptionHandling(GenericJDBCException exception) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Malformed SQL Statement!!! Edit Report with Correct SQL Statement and Try Again", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handling custom invalid SQL exception
     */
    @ExceptionHandler(SQLGrammarException.class)
    public ResponseEntity<?> customInvalidSQLExceptionHandling(SQLGrammarException exception) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Invalid SQL!!! Edit Report with Valid SQL and Try Again", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handling custom illegal argument exception
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> customWrongQueryParameterExceptionHandling(IllegalArgumentException exception) {

        ErrorDetails errorDetails = new ErrorDetails(new Date(), "Parameter Error!!! Edit Report with Valid Parameter Name and Value Before Trying Again", exception.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);

    }

    /**
     * Handling global exception
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globalExceptionHandling(Exception exception, WebRequest request){

        ErrorDetails errorDetails =
                new ErrorDetails(new Date(), exception.getMessage(), request.getDescription(false));
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);

    }

}