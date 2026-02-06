package com.techbookstore.app.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service for retrieving internationalized messages.
 */
@Service
public class MessageService {
    
    private final MessageSource messageSource;
    
    public MessageService(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    
    public String getMessage(String code) {
        return getMessage(code, null);
    }
    
    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, locale);
    }
    
    public String getMessage(String code, Object[] args, String defaultMessage) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(code, args, defaultMessage, locale);
    }
}