package com.example.herokudemo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.herokudemo.bean.Article;
import com.example.herokudemo.bean.Board;
import com.example.herokudemo.bean.Config;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Service
public class RestTempLateService {

    Logger log = LoggerFactory.getLogger(RestTempLateService.class);
    @Autowired
    @Qualifier("getRestTempLate")
    private RestTemplate restTemplate;

    public List<Article> getData(String url) throws URISyntaxException, ParseException {
        Board board = Config.BOARD_LIST.get(url);
        URI uri2 = new URI(Config.PTT_URL+ board.getUrl());
        List<String> cookies = new ArrayList<>();
        cookies.add("over18=1"); // 驗證18歲cookie
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE,APPLICATION_JSON_VALUE);
        headers.add(HttpHeaders.USER_AGENT, "Application");
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.put(HttpHeaders.COOKIE, cookies);
        HttpEntity<JSONObject> entity = new HttpEntity<>( headers);
        String res = restTemplate.exchange(uri2, HttpMethod.GET, entity, String.class).getBody();
        List<Map<String, String>> list = parseArticle(res);
        List<Article> articleList = transferObj(list, board);
        log.info(list.get(0).toString());
        return articleList;
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
    public List<Article> transferObj(List<Map<String, String>> mapList, Board board) throws ParseException {
            List<Article> result = new ArrayList<>();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd");
            Date date = Calendar.getInstance().getTime();

            for (Map<String, String> article : mapList) {
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
            return result;
    }
}