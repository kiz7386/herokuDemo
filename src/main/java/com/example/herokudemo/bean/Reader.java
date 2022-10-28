package com.example.herokudemo.bean;

import okhttp3.*;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class Reader {
    @Autowired
    @Qualifier("getOkhttp")
    private OkHttpClient okHttpClient;
    private Map<String, List<Cookie>> cookieStore; // 保存 Cookie
    private CookieJar cookieJar;

    @PostConstruct
    public void init() throws Exception{
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
        okHttpClient.newCall(request).execute();
    }

    public List<Article> getList(String boardName) throws IOException, ParseException, ParseException {
        Response response = null;
        List<Article> result = new ArrayList<>();
        try {
            Board board = Config.BOARD_LIST.get(boardName);

            /* 如果找不到指定的看板 */
            if (board == null) {
                return null;
            }

            /* 如果看板需要成年檢查 */
            if (board.getAdultCheck() == true) {
                runAdultCheck(board.getUrl());
            }

            /* 抓取目標頁面 */
            Request request = new Request.Builder()
                    .url(Config.PTT_URL + board.getUrl())
                    .get()
                    .build();

            response = okHttpClient.newCall(request).execute();
            String body = response.body().string();

            /* 轉換 HTML 到 Article */
            List<Map<String, String>> articles = parseArticle(body);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
            Date date = Calendar.getInstance().getTime();

            for (Map<String, String> article : articles) {
                String url = article.get("url");
                String title = article.get("title");
                String author = article.get("author");
                Date authDate = simpleDateFormat.parse(article.get("date"));
                date.setDate(authDate.getDate());
                date.setMonth(authDate.getMonth());
                date.setHours(0);
                date.setMinutes(0);
                date.setSeconds(0);

                result.add(new Article(board, url, title, author, date));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            response.body().close();
        }
        return result;
    }

    /* 進行年齡確認 */
    private void runAdultCheck(String url) throws IOException {
        FormBody formBody = new FormBody.Builder()
                .add("from", url)
                .add("yes", "yes")
                .build();

        Request request = new Request.Builder()
                .url(Config.PTT_URL + "/ask/over18")
                .post(formBody)
                .build();

        okHttpClient.newCall(request).execute();
    }

    /* 解析看板文章列表 */
    private List<Map<String, String>> parseArticle(String body) {
        List<Map<String, String>> result = new ArrayList<>();
        Document doc = Jsoup.parse(body);
        Elements articleList = doc.select(".r-ent");

        for (Element element: articleList) {
            String url = element.select(".title a").attr("href");
            String title = element.select(".title a").text();
            String author = element.select(".meta .author").text();
            String date = element.select(".meta .date").text();

            result.add(new HashMap<String, String>(){{
                put("url", url);
                put("title", title);
                put("author", author);
                put("date", date);
            }});
        }

        return result;
    }
}