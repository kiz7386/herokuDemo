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

    @RequestMapping("/v1/telegram/changeTitle/{key}")
    public String changeTitle(@PathVariable String key){
        return telegramService.updateSearchTitleKey(key);
    }


    @RequestMapping("/v1/telegram/changeAuthor/{key}")
    public String changeAuthor(@PathVariable String key){
        return telegramService.updateSearchAuthorKey(key);
    }
}
