package com.prometteur.divaism.PojoModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class MessageResponse {
    @SerializedName("status")
    private Integer status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private List<Result> result = null;

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
    public List<Result> getResult() {
        return result;
    }

    @SerializedName("result")
    public void setResult(List<Result> result) {
        this.result = result;
    }

    public class Result implements Serializable

    {

        @SerializedName("req_id")
        private String reqId;
        @SerializedName("req_user_id")
        private String reqUserId;
        @SerializedName("req_body_type")
        private String reqBodyType;
        @SerializedName("req_height")
        private String reqHeight;
        @SerializedName("req_age")
        private String reqAge;
        @SerializedName("req_brands")
        private String reqBrands;
        @SerializedName("req_unfev_color")
        private String reqUnfevColor;
        @SerializedName("req_price")
        private String reqPrice;
        @SerializedName("req_comment")
        private String reqComment;
        @SerializedName("req_photo")
        private String reqPhoto;
        @SerializedName("req_create_date")
        private String reqCreateDate;
        @SerializedName("req_update_date")
        private String reqUpdateDate;
        @SerializedName("req_create_by")
        private String reqCreateBy;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("reply_id")
        private String replyId;
        private final static long serialVersionUID = 3678015318093510059L;


        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }
        public String getReplyId() {
            return replyId;
        }

        public void setReplyId(String replyId) {
            this.replyId = replyId;
        }

        @SerializedName("req_id")
        public String getReqId() {
            return reqId;
        }

        @SerializedName("req_id")
        public void setReqId(String reqId) {
            this.reqId = reqId;
        }

        @SerializedName("req_user_id")
        public String getReqUserId() {
            return reqUserId;
        }

        @SerializedName("req_user_id")
        public void setReqUserId(String reqUserId) {
            this.reqUserId = reqUserId;
        }

        @SerializedName("req_body_type")
        public String getReqBodyType() {
            return reqBodyType;
        }

        @SerializedName("req_body_type")
        public void setReqBodyType(String reqBodyType) {
            this.reqBodyType = reqBodyType;
        }

        @SerializedName("req_height")
        public String getReqHeight() {
            return reqHeight;
        }

        @SerializedName("req_height")
        public void setReqHeight(String reqHeight) {
            this.reqHeight = reqHeight;
        }

        @SerializedName("req_age")
        public String getReqAge() {
            return reqAge;
        }

        @SerializedName("req_age")
        public void setReqAge(String reqAge) {
            this.reqAge = reqAge;
        }

        @SerializedName("req_brands")
        public String getReqBrands() {
            return reqBrands;
        }

        @SerializedName("req_brands")
        public void setReqBrands(String reqBrands) {
            this.reqBrands = reqBrands;
        }

        @SerializedName("req_unfev_color")
        public String getReqUnfevColor() {
            return reqUnfevColor;
        }

        @SerializedName("req_unfev_color")
        public void setReqUnfevColor(String reqUnfevColor) {
            this.reqUnfevColor = reqUnfevColor;
        }

        @SerializedName("req_price")
        public String getReqPrice() {
            return reqPrice;
        }

        @SerializedName("req_price")
        public void setReqPrice(String reqPrice) {
            this.reqPrice = reqPrice;
        }

        @SerializedName("req_comment")
        public String getReqComment() {
            return reqComment;
        }

        @SerializedName("req_comment")
        public void setReqComment(String reqComment) {
            this.reqComment = reqComment;
        }

        @SerializedName("req_photo")
        public String getReqPhoto() {
            return reqPhoto;
        }

        @SerializedName("req_photo")
        public void setReqPhoto(String reqPhoto) {
            this.reqPhoto = reqPhoto;
        }

        @SerializedName("req_create_date")
        public String getReqCreateDate() {
            return reqCreateDate;
        }

        @SerializedName("req_create_date")
        public void setReqCreateDate(String reqCreateDate) {
            this.reqCreateDate = reqCreateDate;
        }

        @SerializedName("req_update_date")
        public String getReqUpdateDate() {
            return reqUpdateDate;
        }

        @SerializedName("req_update_date")
        public void setReqUpdateDate(String reqUpdateDate) {
            this.reqUpdateDate = reqUpdateDate;
        }

        @SerializedName("req_create_by")
        public String getReqCreateBy() {
            return reqCreateBy;
        }

        @SerializedName("req_create_by")
        public void setReqCreateBy(String reqCreateBy) {
            this.reqCreateBy = reqCreateBy;
        }

    }
}
