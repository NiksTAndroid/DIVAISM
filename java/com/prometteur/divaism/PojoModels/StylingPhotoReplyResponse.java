package com.prometteur.divaism.PojoModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StylingPhotoReplyResponse {

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

        @SerializedName("rep_id")
        private String repId;
        @SerializedName("rep_user_id")
        private String repUserId;
        @SerializedName("rep_image")
        private String repImage;
        @SerializedName("rep_text")
        private String repText;
        @SerializedName("rep_links")
        private String repLinks;
        @SerializedName("rep_request_id")
        private String repRequestId;
        @SerializedName("rep_create_date")
        private String repCreateDate;
        @SerializedName("rep_create_by")
        private String repCreateBy;

        @SerializedName("rep_id")
        public String getRepId() {
            return repId;
        }

        @SerializedName("rep_id")
        public void setRepId(String repId) {
            this.repId = repId;
        }

        @SerializedName("rep_user_id")
        public String getRepUserId() {
            return repUserId;
        }

        @SerializedName("rep_user_id")
        public void setRepUserId(String repUserId) {
            this.repUserId = repUserId;
        }

        @SerializedName("rep_image")
        public String getRepImage() {
            return repImage;
        }

        @SerializedName("rep_image")
        public void setRepImage(String repImage) {
            this.repImage = repImage;
        }

        @SerializedName("rep_text")
        public String getRepText() {
            return repText;
        }

        @SerializedName("rep_text")
        public void setRepText(String repText) {
            this.repText = repText;
        }

        @SerializedName("rep_links")
        public String getRepLinks() {
            return repLinks;
        }

        @SerializedName("rep_links")
        public void setRepLinks(String repLinks) {
            this.repLinks = repLinks;
        }

        @SerializedName("rep_request_id")
        public String getRepRequestId() {
            return repRequestId;
        }

        @SerializedName("rep_request_id")
        public void setRepRequestId(String repRequestId) {
            this.repRequestId = repRequestId;
        }

        @SerializedName("rep_create_date")
        public String getRepCreateDate() {
            return repCreateDate;
        }

        @SerializedName("rep_create_date")
        public void setRepCreateDate(String repCreateDate) {
            this.repCreateDate = repCreateDate;
        }

        @SerializedName("rep_create_by")
        public String getRepCreateBy() {
            return repCreateBy;
        }

        @SerializedName("rep_create_by")
        public void setRepCreateBy(String repCreateBy) {
            this.repCreateBy = repCreateBy;
        }

    }
}
