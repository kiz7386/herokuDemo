package com.example.herokudemo.configuration;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OkhttpConfig {

    public OkHttpClient okHttpClient;

    @Bean("getOkhttp")
    public OkHttpClient getOkhttp() {
        return okHttpClient == null ? new OkHttpClient() : okHttpClient;
    }
//    @Bean("getRestTempLate")
//    public RestTemplate getRestTempLate() {
//        return new RestTemplate();
//    }
}
