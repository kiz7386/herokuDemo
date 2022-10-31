package com.example.herokudemo.scheduled;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.service.RestTempLateService;
import com.example.herokudemo.service.TelegramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.net.URISyntaxException;
import java.util.List;

@Component
@EnableScheduling
public class PttScheduled {

    @Autowired
    private RestTempLateService restTempLateService;
    @Autowired
    TelegramService telegramService;


    @Scheduled(fixedDelay = 1000)
    public void PttScan() throws URISyntaxException {
        List<Article> result = restTempLateService.getData("Gossiping");
        telegramService.sendMessage(result);
        if(!ObjectUtils.isEmpty(result)){
            System.out.println(result.get(0).toString());
        }
    }


}
