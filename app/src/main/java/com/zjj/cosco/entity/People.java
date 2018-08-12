package com.zjj.cosco.entity;

import java.util.List;

/**
 * Created by administrator on 2018/8/10.
 */

public class People {
    private String name;
    private String content;
    private List<String> reply;
    private int zanNum;
    private String zan;

    public People(String name, String content, List<String> reply, int zanNum, String zan) {
        this.name = name;
        this.content = content;
        this.reply = reply;
        this.zanNum = zanNum;
        this.zan = zan;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getReply() {
        return reply;
    }

    public void setReply(List<String> reply) {
        this.reply = reply;
    }

    public int getZanNum() {
        return zanNum;
    }

    public void setZanNum(int zanNum) {
        this.zanNum = zanNum;
    }

    public String getZan() {
        return zan;
    }

    public void setZan(String zan) {
        this.zan = zan;
    }
}
