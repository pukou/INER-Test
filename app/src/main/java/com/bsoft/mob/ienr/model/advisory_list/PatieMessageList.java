package com.bsoft.mob.ienr.model.advisory_list;

import java.util.List;

public class PatieMessageList {
    private List<PatientMessageBean>data;
    private String message;
    private int code;

    public List<PatientMessageBean> getData() {
        return data;
    }

    public void setData(List<PatientMessageBean> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
