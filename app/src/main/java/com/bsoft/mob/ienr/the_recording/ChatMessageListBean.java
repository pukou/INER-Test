package com.bsoft.mob.ienr.the_recording;

import java.util.List;

public class ChatMessageListBean {
    private List<ChatMessageBean>data;
    private int code;
    private String message;

    public List<ChatMessageBean> getData() {
        return data;
    }

    public void setData(List<ChatMessageBean> data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
