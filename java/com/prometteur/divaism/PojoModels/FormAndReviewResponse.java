package com.prometteur.divaism.PojoModels;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FormAndReviewResponse {
    @SerializedName("status")
    private Integer status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private List<Integer> result = null;


    @SerializedName("status")
    public Integer getStatus() {
        return status;
    }

    @SerializedName("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @SerializedName("msg")
    public String getMsg() {
        return msg;
    }

    @SerializedName("msg")
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @SerializedName("result")
    public List<Integer> getResult() {
        return result;
    }

    @SerializedName("result")
    public void setResult(List<Integer> result) {
        this.result = result;
    }

}
