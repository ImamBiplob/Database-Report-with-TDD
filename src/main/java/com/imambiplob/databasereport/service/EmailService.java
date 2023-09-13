package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.EmailDetails;

public interface EmailService {

    String sendSimpleMail(EmailDetails details);

    String sendMailWithAttachment(EmailDetails details);

}
