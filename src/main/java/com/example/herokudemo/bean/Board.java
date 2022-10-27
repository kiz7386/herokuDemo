package com.example.herokudemo.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Board {
    private String url; // 看板網址
    private String nameCN; // 中文名稱
    private String nameEN; // 英文名稱
    private Boolean adultCheck; // 成年檢查

    public Board(String url, String nameCN, String nameEN, Boolean adultCheck) {
        this.url = url;
        this.nameCN = nameCN;
        this.nameEN = nameEN;
        this.adultCheck = adultCheck;
    }

    @Override
    public String toString() {
        return "Board{" +
                "url='" + url + '\'' +
                ", nameCN='" + nameCN + '\'' +
                ", nameEN='" + nameEN + '\'' +
                ", adultCheck=" + adultCheck +
                '}';
    }
}
