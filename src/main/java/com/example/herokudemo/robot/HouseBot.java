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

public class HouseBot extends TelegramLongPollingBot implements Bot{

    private String UPDATE_PASSWORD ="123$zxcV";
    private String token = "5705257652:AAED1RneKVPoLQdZqa2y7RdjWEkE7z494aU";
    private String userName = "house591_bot";

    public HouseBot(){
        this(new DefaultBotOptions());
    }
    public HouseBot(DefaultBotOptions options){
        super(options);
    }

    @Override
    public String getBotUsername() {
        return this.userName;
    }

    @Override
    public String getBotToken() {
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
            String s = "";
//            String s = executeLinuxCms(split[1]);

            switch (split[0]){
                case "chatId":
                    s ="chatId : " + chatId;
                    break;
                default:
                    s = "無此關鍵字";
                    break;
            }
//            sendMsg(s, chatId);
        }
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

    public String getWebhookInfo(HouseBot myBot){
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
    public String getUpdates(HouseBot myBot){
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
