package com.techbookstore.app.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceTest {

    @Autowired
    private MessageService messageService;

    @Test
    void testJapaneseMessages() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        String welcome = messageService.getMessage("app.welcome");
        assertEquals("ようこそ", welcome);
    }

    @Test
    void testEnglishMessages() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String welcome = messageService.getMessage("app.welcome");
        assertEquals("Welcome", welcome);
    }

    @Test
    void testInventoryMessages() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        String inventoryTitle = messageService.getMessage("inventory.title");
        assertEquals("在庫管理", inventoryTitle);
        
        String outOfStock = messageService.getMessage("inventory.status.outofstock");
        assertEquals("在庫切れ", outOfStock);
    }

    @Test
    void testInventoryMessagesEnglish() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        String inventoryTitle = messageService.getMessage("inventory.title");
        assertEquals("Inventory Management", inventoryTitle);
        
        String outOfStock = messageService.getMessage("inventory.status.outofstock");
        assertEquals("Out of Stock", outOfStock);
    }

    @Test
    void testFallbackMessage() {
        LocaleContextHolder.setLocale(Locale.JAPANESE);
        String nonExistentKey = messageService.getMessage("non.existent.key", null, "Fallback");
        assertEquals("Fallback", nonExistentKey);
    }
}