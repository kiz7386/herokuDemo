package com.example.herokudemo.configuration;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OkhttpConfig {

    @Bean("getOkhttp")
    public OkHttpClient getOkhttp() {
        return new OkHttpClient();
    }
}
