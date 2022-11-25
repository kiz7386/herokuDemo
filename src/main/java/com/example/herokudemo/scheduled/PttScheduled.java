package com.example.herokudemo.scheduled;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.service.RestTempLateService;
import com.example.herokudemo.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

@Component
@EnableScheduling
public class PttScheduled {

    @Autowired
    private RestTempLateService restTempLateService;
    @Autowired
    TelegramService telegramService;


    @Scheduled(cron = "10/60 * * * * ?")
    public void PttGossipingScan() throws URISyntaxException, ParseException {
        List<Article> result = restTempLateService.getData("Gossiping");
        telegramService.sendMessage(result);
    }
    @Scheduled(cron = "40/60 * * * * ?")
    public void PttAllTogetherScan() throws URISyntaxException, ParseException {
        List<Article> result = restTempLateService.getData("AllTogether");
        telegramService.sendMessage(result);
    }


}
