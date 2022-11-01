package com.example.herokudemo.robot;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.example.herokudemo.constant.Constants.TELEGRAM_URL;

public class MyBot extends TelegramLongPollingBot {

    private String UPDATE_PASSWORD ="123$zxcV";
    private String token = "5448080630:AAGDUOWR_QCh2gTwgsENHBJ7hdpKHXcGl84";
    private String userName = "kiz7386_bot";
    private String pttSearchTitleKey ="退休";
    private String pttSearchAuthorKey ="kiz7386";

    public MyBot(){
        this(new DefaultBotOptions());
    }
    public MyBot(DefaultBotOptions options){
        super(options);
    }
    public void setPttSearchTitle(String key){
        this.pttSearchTitleKey = key;
    }
    public String getPttSearchTitle(){
        return this.pttSearchTitleKey;
    }
    public void setPttSearchAuthor(String key){
        this.pttSearchAuthorKey = key;
    }
    public String getPttSearchAuthor(){
        return this.pttSearchAuthorKey;
    }

    @Override
    public String getBotUsername() {
        // 填寫username
        return this.userName;
    }

    @Override
    public String getBotToken() {
        // 填寫token
        return this.token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            String[] split = text.split("=");
            if(split.length == 1){
                sendMsg("不處理該類型指令", chatId);
                return;
            }
            String s = executeLinuxCms(split[1]);

            switch (split[0]){
                case "changeTitle":
                    s = updateSearchTitleKey(split[1], false);
                    break;
                case "changeAuthor":
                    s = updateSearchAuthorKey(split[1], false);
                    break;
                default:
                    break;
            }
            sendMsg(s, chatId);
        }
    }

    public String updateSearchTitleKey(String key, boolean needCheck){
        if(needCheck){
            if(checkPassword(key)){
                key = key.split("_")[0];
                this.setPttSearchTitle(key);
                return key+"_success";
            } else {
                return key+"_fail";
            }
        } else {
            this.setPttSearchTitle(key);
            return "搜尋標題設定為 : " + key;
        }
    }

    public String updateSearchAuthorKey(String key, boolean needCheck){
        if(needCheck){
            if(checkPassword(key)){
                key = key.split("_")[0];
                this.setPttSearchTitle(key);
                return key+"_success";
            } else {
                return key+"_fail";
            }
        } else {
            this.setPttSearchAuthor(key);
            return "搜尋作者設定為 : " + key;
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

    public void sendMsg(String text, Long chatId){
        SendMessage response = new SendMessage();
        response.setChatId(String.valueOf(chatId));
        response.setText(text);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String executeLinuxCms(String cmd){
        System.out.println("執行指令[ " + cmd + " ]");
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(cmd);
            String line;
            BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuffer out = new StringBuffer();
            while ((line = stdoutReader.readLine()) != null){
                out.append(line+"\n");
            }
            try{
                process.waitFor();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            process.destroy();
            return out.toString();
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getWebhookInfo(MyBot myBot){
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        String result = "";
        /* 抓取目標頁面 */
        Request request = new Request.Builder()
                .url(TELEGRAM_URL + "bot" + myBot.getBotToken() +"/getWebhookInfo")
                .get()
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
            result = response.body().string();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        return result;
    }
    public String getUpdates(MyBot myBot){
        OkHttpClient okHttpClient = new OkHttpClient();
        Response response = null;
        String result = "";
        /* 抓取目標頁面 */
        Request request = new Request.Builder()
                .url(TELEGRAM_URL + "bot" + myBot.getBotToken() +"/getUpdates")
                .get()
                .build();
        try {
            response = okHttpClient.newCall(request).execute();
            result = response.body().string();
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        return result;
    }

}
