package com.example.herokudemo.scheduled;

import com.example.herokudemo.bean.Article;
import com.example.herokudemo.bean.Config;
import com.example.herokudemo.bean.Reader;
import com.example.herokudemo.service.TelegramService;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@EnableScheduling
public class PttScheduled {

    @Autowired
    private Reader reader;
    @Autowired
    TelegramService telegramService;
//    private OkHttpClient okHttpClient = new OkHttpClient();
    @Autowired
    @Qualifier("getOkhttp")
    private OkHttpClient okHttpClient;

    private Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private CookieJar cookieJar;

    @PostConstruct
    public void init() throws Exception{
        Response response =null;
        try{


            /* 初始化 */
            cookieStore = new HashMap<>();
            cookieJar = new CookieJar() {
                /* 保存每次伺服器端回傳的 Cookie */
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
                    List<Cookie> cookies = cookieStore.getOrDefault(
                            httpUrl.host(),
                            new ArrayList<>()
                    );
                    cookies.addAll(list);
                    cookieStore.put(httpUrl.host(), cookies);
                }

                /* 每次發送帶上儲存的 Cookie */
                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    return cookieStore.getOrDefault(
                            httpUrl.host(),
                            new ArrayList<>()
                    );
                }
            };
            okHttpClient = okHttpClient.newBuilder().cookieJar(cookieJar).build();

            /* 獲得網站的初始 Cookie */
            Request request = new Request.Builder().get().url(Config.PTT_URL).build();
            response = okHttpClient.newCall(request).execute();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            if(response != null){
                response.close();
            }
        }
    }

    @Scheduled(fixedDelay = 1000)
    public void PttScan() throws IOException, ParseException {
        List<Article> result = reader.getList(okHttpClient, "Gossiping");
        telegramService.sendMessage(result);
        if(!ObjectUtils.isEmpty(result)){
            System.out.println(result.get(0).toString());
        }
    }


}
