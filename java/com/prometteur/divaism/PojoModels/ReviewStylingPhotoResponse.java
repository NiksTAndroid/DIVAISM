package com.prometteur.divaism.PojoModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ReviewStylingPhotoResponse {

    @SerializedName("status")
    private Integer status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private List<Result> result = null;
    @SerializedName("review")
    private List<Review> review = null;
    private final static long serialVersionUID = -2531762516851417949L;

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

    @SerializedName("review")
    public List<Review> getReview() {
        return review;
    }

    @SerializedName("review")
    public void setReview(List<Review> review) {
        this.review = review;
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
        private final static long serialVersionUID = 7022902192493707702L;

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
    public class Review implements Serializable
    {

        @SerializedName("rev_id")
        private String revId;
        @SerializedName("rev_stylist_id")
        private String revStylistId;
        @SerializedName("rev_request_id")
        private String revRequestId;
        @SerializedName("rev_rating")
        private String revRating;
        @SerializedName("rev_text")
        private String revText;
        @SerializedName("rev_create_by")
        private String revCreateBy;
        @SerializedName("rev_create_date")
        private String revCreateDate;
        private final static long serialVersionUID = -7590414326698493338L;

        @SerializedName("rev_id")
        public String getRevId() {
            return revId;
        }

        @SerializedName("rev_id")
        public void setRevId(String revId) {
            this.revId = revId;
        }

        @SerializedName("rev_stylist_id")
        public String getRevStylistId() {
            return revStylistId;
        }

        @SerializedName("rev_stylist_id")
        public void setRevStylistId(String revStylistId) {
            this.revStylistId = revStylistId;
        }

        @SerializedName("rev_request_id")
        public String getRevRequestId() {
            return revRequestId;
        }

        @SerializedName("rev_request_id")
        public void setRevRequestId(String revRequestId) {
            this.revRequestId = revRequestId;
        }

        @SerializedName("rev_rating")
        public String getRevRating() {
            return revRating;
        }

        @SerializedName("rev_rating")
        public void setRevRating(String revRating) {
            this.revRating = revRating;
        }

        @SerializedName("rev_text")
        public String getRevText() {
            return revText;
        }

        @SerializedName("rev_text")
        public void setRevText(String revText) {
            this.revText = revText;
        }

        @SerializedName("rev_create_by")
        public String getRevCreateBy() {
            return revCreateBy;
        }

        @SerializedName("rev_create_by")
        public void setRevCreateBy(String revCreateBy) {
            this.revCreateBy = revCreateBy;
        }

        @SerializedName("rev_create_date")
        public String getRevCreateDate() {
            return revCreateDate;
        }

        @SerializedName("rev_create_date")
        public void setRevCreateDate(String revCreateDate) {
            this.revCreateDate = revCreateDate;
        }

    }
}
