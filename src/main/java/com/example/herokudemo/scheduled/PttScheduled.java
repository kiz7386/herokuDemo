package com.example.herokudemo.scheduled;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.bean.Reader;
import com.example.herokudemo.service.TelegramService;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Component
@EnableScheduling
public class PttScheduled {

    @Autowired
    private Reader reader;
    @Autowired
    TelegramService telegramService;

    @Scheduled(cron = "*/10 * * * * ?")
    public void PttScan() throws IOException, ParseException {
        List<Article> result = reader.getList("Gossiping");
        telegramService.sendMessage(result);
        if(!ObjectUtils.isEmpty(result)){
            System.out.println(result.get(0).toString());
        }
    }
}
