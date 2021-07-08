package com.prometteur.divaism.Utils;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UpdateProfileResponse {

    @SerializedName("status")
    private Integer status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private List<Boolean> result = null;
    private final static long serialVersionUID = 7474461130864118651L;

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
    public List<Boolean> getResult() {
        return result;
    }

    @SerializedName("result")
    public void setResult(List<Boolean> result) {
        this.result = result;
    }


}
