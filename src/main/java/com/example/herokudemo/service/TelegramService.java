package com.example.herokudemo.service;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.bean.Config;
import com.example.herokudemo.robot.Bot;
import com.example.herokudemo.robot.HouseBot;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.example.herokudemo.constant.Constants.*;

@Service
public class TelegramService {

    @Value("${telegram.pttchatid}")
    private Long PTT_CHAT_ID;
    @Value("${telegram.groupchatid}")
    private Long GROUP_CHAT_ID;
    @Value("${telegram.updatepassword}")
    private String UPDATE_PASSWORD;
    @Value("${heroku.url}")
    private String HEROKU_URL;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    private MyBot pttBot;
    private HouseBot houseBot;

    @PostConstruct
    public void init(){
        Map<String, Bot> botMap = telegramRegister();
        pttBot = (MyBot) botMap.get("pttBot");
//        houseBot = (HouseBot) botMap.get("groupBot");
        setPttDefaultRedisKey();
//        deleteWebhookInfo(pttBot);
//        setWebhookInfo(pttBot);
    }

    public void setPttDefaultRedisKey(){
        for(Map.Entry entry :Config.BOARD_LIST.entrySet()){
            switch (entry.getKey().toString()){
                case "Gossiping":
                    stringRedisTemplate.opsForValue().set(entry.getKey()+UNDER_LINE+TITLE, pttBot.getPttGossipingSearchTitleKey());
                    stringRedisTemplate.opsForValue().set(entry.getKey()+UNDER_LINE+AUTHOR, pttBot.getPttGossipingSearchAuthorKey());
                    break;
                case "AllTogether":
                    stringRedisTemplate.opsForValue().set(entry.getKey()+UNDER_LINE+TITLE, pttBot.getPttGossipingSearchTitleKey());
                    stringRedisTemplate.opsForValue().set(entry.getKey()+UNDER_LINE+AUTHOR, pttBot.getPttGossipingSearchAuthorKey());
                    break;

            }
        }
    }

    public void sendMessage(List<Article> articleList) {
        // 如果有特定什麼文章 就發送telegram
        for (Article article : articleList) {
            switch (article.getParent().getUrl()) {
                case "/bbs/Gossiping":
                    if (isContains(pttBot.getPttGossipingSearchTitleKey(), article.getTitle()) || isContains(pttBot.getPttGossipingSearchAuthorKey(), article.getAuthor())) {
                        sendMsg(article);
                        saveSetKey(pttBot.getPttGossipingSearchTitleKey(), pttBot.getPttGossipingSearchAuthorKey(), "Gossiping");
                    }
                    break;
                case "/bbs/AllTogether":
                    if (isContains(pttBot.getPttAllTogetherSearchTitleKey(), article.getTitle()) || isContains(pttBot.getPttAllTogetherSearchAuthorKey(), article.getAuthor())) {
                        sendMsg(article);
                        saveSetKey(pttBot.getPttAllTogetherSearchTitleKey(), pttBot.getPttAllTogetherSearchAuthorKey(), "AllTogether");
                    }
                    break;
            }
        }
    }

    public boolean isContains(String botKey, String articleStr){
        List<String> botList = Arrays.stream(botKey.split(COMMA)).collect(Collectors.toList());
        return botList.stream().map(o ->articleStr.contains(o)).collect(Collectors.toList()).contains(Boolean.TRUE);
    }

    /**
     * 將文章資料寫入redis
     * 且發送到 對應的chat_id
     */
    public void sendMsg(Article article){
        String redisKey = article.getTitle()+UNDER_LINE+article.getParent().getNameCN()+UNDER_LINE+article.getAuthor()+UNDER_LINE+article.getDate();
        if(!stringRedisTemplate.opsForValue().getOperations().hasKey(redisKey)){
            stringRedisTemplate.opsForValue().set(redisKey, article.getAuthor()+UNDER_LINE+article.getBody(), 60, TimeUnit.DAYS);
            pttBot.sendMsg(redisKey+" "+PTT_URL+article.getUrl() , PTT_CHAT_ID);
            pttBot.sendMsg(redisKey+" "+PTT_URL+article.getUrl() , GROUP_CHAT_ID);
        }
    }
    public void saveSetKey(String botTitleKey, String botAuthorKey, String type){
        String titleRedisKey ="";
        String authorRedisKey ="";
        titleRedisKey = type+UNDER_LINE+TITLE;
        authorRedisKey = type+UNDER_LINE+AUTHOR;
        if(stringRedisTemplate.opsForValue().getOperations().hasKey(titleRedisKey)){
            if(!stringRedisTemplate.opsForValue().get(titleRedisKey).equalsIgnoreCase(botTitleKey)){
                stringRedisTemplate.opsForValue().set(titleRedisKey, botTitleKey);
            }
        }
        if(stringRedisTemplate.opsForValue().getOperations().hasKey(authorRedisKey)){
            if(!stringRedisTemplate.opsForValue().get(authorRedisKey).equalsIgnoreCase(botAuthorKey)){
                stringRedisTemplate.opsForValue().set(authorRedisKey, botAuthorKey);
            }
        }

    }
//    public void deleteWebhookInfo(pttBot pttBot){
//        Response response = null;
//        /* 抓取目標頁面 */
//        Request request = new Request.Builder()
//                .url(TELEGRAM_URL + "bot" + pttBot.getBotToken() +"/deleteWebhook?url=" + HEROKU_URL)
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
//    public void setWebhookInfo(pttBot pttBot){
//        Response response = null;
//        /* 抓取目標頁面 */
//        Request request = new Request.Builder()
//                .url(TELEGRAM_URL + "bot" + pttBot.getBotToken() +"/setWebhook?url=" + HEROKU_URL)
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

    public Map<String, Bot> telegramRegister(){
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
//            HouseBot bot3 = new HouseBot();
            telegramBotsApi.registerBot(bot2);
//            telegramBotsApi.registerBot(bot3);
            Map<String, Bot> map = new ConcurrentHashMap<>();
            map.put("pttBot", bot2);
//            map.put("group", bot3);
            return map;
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
////            pttBot bot = new pttBot(botOptions);
////            telegramBotsApi.registerBot(bot);
//            // 不需要代理
//            pttBot bot2 = new pttBot();
//            telegramBotsApi.registerBot(bot2);
//        } catch (TelegramApiException e){
//            e.printStackTrace();
//        }
//    }

}
