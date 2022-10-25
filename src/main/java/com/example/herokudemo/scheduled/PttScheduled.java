package com.example.herokudemo.scheduled;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.bean.Reader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
@EnableScheduling
public class PttScheduled {
    @Autowired
    private Reader reader;

    @Scheduled(cron = "*/10 * * * * ?")
    public void PttScan() throws IOException, ParseException {
        List<Article> result = reader.getList("Gossiping");
//        Assertions.assertInstanceOf(List.class, result);
        System.out.println(result.toString());
    }
}
