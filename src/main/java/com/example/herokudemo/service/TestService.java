package com.example.herokudemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TestService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public String testService(String name){
        stringRedisTemplate.opsForValue().set(name+"_key", name+"_value");
        return name+"_success";
    }
}
