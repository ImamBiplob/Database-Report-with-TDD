package com.imambiplob.databasereport.exception;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorDetails {

    private Date timeStamp;

    private String message;

    private String details;

    public ErrorDetails(Date timeStamp, String message, String details) {

        super();
        this.timeStamp = timeStamp;
        this.message = message;
        this.details = details;

    }

}