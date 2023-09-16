package com.imambiplob.databasereport.service;

import com.imambiplob.databasereport.dto.EmailDetails;

public interface EmailService {

   void sendMailWithAttachment(EmailDetails details);

}
