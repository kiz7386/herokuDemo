package com.example.herokudemo.controller;

import com.example.herokudemo.service.TelegramService;
import com.example.herokudemo.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TelegramController {
    @Autowired
    private TelegramService telegramService;

    @RequestMapping("/v1/telegram/{key}")
    public String test(@PathVariable String key){
        return telegramService.updateSearchKey(key);
    }
}
