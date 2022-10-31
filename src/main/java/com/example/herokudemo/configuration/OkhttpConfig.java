package com.example.herokudemo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
public class OkhttpConfig {

//    public OkHttpClient okHttpClient;

//    @Bean("getOkhttp")
//    public OkHttpClient getOkhttp() {
//        return okHttpClient == null ? new OkHttpClient() : okHttpClient;
//    }
//    @Bean("getRestTempLate")
//    public RestTemplate getRestTempLate() {
//        return new RestTemplate();
//    }
    @Bean("getRestTempLate")
    public RestTemplate restTemplate() {
        //切换RestTemplate底层调用为OkHttp,因为OkHttp的性能比较优越
        RestTemplate restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory());
        //配置消息转换器，处理所有响应乱码
        List<HttpMessageConverter<?>> httpMessageConverter = restTemplate.getMessageConverters();
        //设置编码
        httpMessageConverter.set(1,new StringHttpMessageConverter(StandardCharsets.UTF_8));
        restTemplate.setMessageConverters(httpMessageConverter);
        return restTemplate;
    }
}
