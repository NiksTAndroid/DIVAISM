package com.prometteur.divaism.PojoModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class GetRequestFormResponse {




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

            @SerializedName("uform_id")
            private String uformId;
            @SerializedName("uform_user_id")
            private String uformUserId;
            @SerializedName("uform_req_gender")
            private String uformReqGender;
            @SerializedName("uform_body_type")
            private String uformBodyType;
            @SerializedName("uform_height")
            private String uformHeight;
            @SerializedName("uform_age")
            private String uformAge;
            @SerializedName("uform_brands")
            private String uformBrands;
            @SerializedName("uform_unfev_color")
            private String uformUnfevColor;
            @SerializedName("uform_price")
            private String uformPrice;
            @SerializedName("uform_comment")
            private String uformComment;
            @SerializedName("uform_photo")
            private String uformPhoto;
            @SerializedName("uform_create_date")
            private String uformCreateDate;
            @SerializedName("uform_update_date")
            private String uformUpdateDate;
            private final static long serialVersionUID = -1281455459900206481L;

            @SerializedName("uform_id")
            public String getUformId() {
                return uformId;
            }

            @SerializedName("uform_id")
            public void setUformId(String uformId) {
                this.uformId = uformId;
            }

            @SerializedName("uform_user_id")
            public String getUformUserId() {
                return uformUserId;
            }

            @SerializedName("uform_user_id")
            public void setUformUserId(String uformUserId) {
                this.uformUserId = uformUserId;
            }

            @SerializedName("uform_req_gender")
            public String getUformReqGender() {
                return uformReqGender;
            }

            @SerializedName("uform_req_gender")
            public void setUformReqGender(String uformReqGender) {
                this.uformReqGender = uformReqGender;
            }

            @SerializedName("uform_body_type")
            public String getUformBodyType() {
                return uformBodyType;
            }

            @SerializedName("uform_body_type")
            public void setUformBodyType(String uformBodyType) {
                this.uformBodyType = uformBodyType;
            }

            @SerializedName("uform_height")
            public String getUformHeight() {
                return uformHeight;
            }

            @SerializedName("uform_height")
            public void setUformHeight(String uformHeight) {
                this.uformHeight = uformHeight;
            }

            @SerializedName("uform_age")
            public String getUformAge() {
                return uformAge;
            }

            @SerializedName("uform_age")
            public void setUformAge(String uformAge) {
                this.uformAge = uformAge;
            }

            @SerializedName("uform_brands")
            public String getUformBrands() {
                return uformBrands;
            }

            @SerializedName("uform_brands")
            public void setUformBrands(String uformBrands) {
                this.uformBrands = uformBrands;
            }

            @SerializedName("uform_unfev_color")
            public String getUformUnfevColor() {
                return uformUnfevColor;
            }

            @SerializedName("uform_unfev_color")
            public void setUformUnfevColor(String uformUnfevColor) {
                this.uformUnfevColor = uformUnfevColor;
            }

            @SerializedName("uform_price")
            public String getUformPrice() {
                return uformPrice;
            }

            @SerializedName("uform_price")
            public void setUformPrice(String uformPrice) {
                this.uformPrice = uformPrice;
            }

            @SerializedName("uform_comment")
            public String getUformComment() {
                return uformComment;
            }

            @SerializedName("uform_comment")
            public void setUformComment(String uformComment) {
                this.uformComment = uformComment;
            }

            @SerializedName("uform_photo")
            public String getUformPhoto() {
                return uformPhoto;
            }

            @SerializedName("uform_photo")
            public void setUformPhoto(String uformPhoto) {
                this.uformPhoto = uformPhoto;
            }

            @SerializedName("uform_create_date")
            public String getUformCreateDate() {
                return uformCreateDate;
            }

            @SerializedName("uform_create_date")
            public void setUformCreateDate(String uformCreateDate) {
                this.uformCreateDate = uformCreateDate;
            }

            @SerializedName("uform_update_date")
            public String getUformUpdateDate() {
                return uformUpdateDate;
            }

            @SerializedName("uform_update_date")
            public void setUformUpdateDate(String uformUpdateDate) {
                this.uformUpdateDate = uformUpdateDate;
            }

        }
}
