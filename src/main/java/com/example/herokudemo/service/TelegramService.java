package com.example.herokudemo.service;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.robot.MyBot;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.herokudemo.constant.Constants.*;

@Service
public class TelegramService {

    @Value("${telegram.chatid}")
    private Long CHAT_ID;
    @Value("${telegram.updatepassword}")
    private String UPDATE_PASSWORD;
    @Value("${heroku.url}")
    private String HEROKU_URL;
    @Autowired
    StringRedisTemplate stringRedisTemplate;
//    @Autowired
//    @Qualifier("getOkhttp")
//    private OkHttpClient okHttpClient;

    private MyBot myBot;

    @PostConstruct
    public void init(){
        myBot = telegramRegister();
//        deleteWebhookInfo(myBot);
//        setWebhookInfo(myBot);
    }


    public String updateSearchTitleKey(String key, boolean needCheck){
        if(needCheck){
            if(checkPassword(key)){
                key = key.split("_")[0];
                stringRedisTemplate.opsForValue().set("telegram_title_key", key);
                myBot.setPttSearchTitle(key);
                return key+"_success";
            } else {
                return key+"_fail";
            }
        } else {
            stringRedisTemplate.opsForValue().set("telegram_title_key", key);
            myBot.setPttSearchTitle(key);
            return key;
        }
    }

    public String updateSearchAuthorKey(String key, boolean needCheck){
        if(needCheck){
            if(checkPassword(key)){
                key = key.split("_")[0];
                stringRedisTemplate.opsForValue().set("telegram_title_key", key);
                myBot.setPttSearchTitle(key);
                return key+"_success";
            } else {
                return key+"_fail";
            }
        } else {
            stringRedisTemplate.opsForValue().set("telegram_title_key", key);
            myBot.setPttSearchAuthor(key);
            return key;
        }
    }

    public boolean checkPassword(String key){
        try{
            return UPDATE_PASSWORD.equalsIgnoreCase(key.split("_")[1]);
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void sendMessage(List<Article> articleList){
        // 如果有特定什麼文章 就發送telegram
        for(Article article : articleList){
           if(article.getTitle().contains(myBot.getPttSearchTitle()) || article.getAuthor().contains(myBot.getPttSearchAuthor())){
               String redisKey = article.getTitle()+"_"+article.getParent().getNameCN()+"_"+article.getAuthor()+"_"+article.getDate();
               if(!stringRedisTemplate.opsForValue().getOperations().hasKey(redisKey)){
                   stringRedisTemplate.opsForValue().set(redisKey, article.getAuthor()+"_"+article.getBody(), 60, TimeUnit.DAYS);
                   myBot.sendMsg(redisKey+" "+PTT_URL+article.getUrl() , CHAT_ID);
               }

           }
        }
    }
//    public void deleteWebhookInfo(MyBot myBot){
//        Response response = null;
//        /* 抓取目標頁面 */
//        Request request = new Request.Builder()
//                .url(TELEGRAM_URL + "bot" + myBot.getBotToken() +"/deleteWebhook?url=" + HEROKU_URL)
//                .get()
//                .build();
//        try {
//            response = okHttpClient.newCall(request).execute();
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void setWebhookInfo(MyBot myBot){
//        Response response = null;
//        /* 抓取目標頁面 */
//        Request request = new Request.Builder()
//                .url(TELEGRAM_URL + "bot" + myBot.getBotToken() +"/setWebhook?url=" + HEROKU_URL)
//                .get()
//                .build();
//        try {
//            response = okHttpClient.newCall(request).execute();
//            System.out.println(response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            response.body().close();
//        }
//
//    }

    public MyBot telegramRegister(){
        // 看localhost or 路由
        int proxyPort = 6006;

        DefaultBotOptions botOptions = new DefaultBotOptions();
        botOptions.setProxyHost(HEROKU_URL);
        botOptions.setProxyPort(proxyPort);

        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);

        DefaultBotSession defaultBotSession = new DefaultBotSession();
        defaultBotSession.setOptions(botOptions);
        try{
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(defaultBotSession.getClass());

            // 需要代理
//            MyBot bot = new MyBot(botOptions);
//            telegramBotsApi.registerBot(bot);
            // 不需要代理
            MyBot bot2 = new MyBot();
            telegramBotsApi.registerBot(bot2);
            return bot2;
        } catch (TelegramApiException e){
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        String proxyHost = "127.0.0.1";
//        int proxyPort = 6006;
//
//        DefaultBotOptions botOptions = new DefaultBotOptions();
//        botOptions.setProxyHost(proxyHost);
//        botOptions.setProxyPort(proxyPort);
//
//        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
//
//        DefaultBotSession defaultBotSession = new DefaultBotSession();
//        defaultBotSession.setOptions(botOptions);
//        try{
//            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(defaultBotSession.getClass());
//
//            // 需要代理
////            MyBot bot = new MyBot(botOptions);
////            telegramBotsApi.registerBot(bot);
//            // 不需要代理
//            MyBot bot2 = new MyBot();
//            telegramBotsApi.registerBot(bot2);
//        } catch (TelegramApiException e){
//            e.printStackTrace();
//        }
//    }

}
