package com.prometteur.divaism.PojoModels;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StylistProfileResponse {

    @SerializedName("status")
    private Integer status;
    @SerializedName("msg")
    private String msg;
    @SerializedName("result")
    private List<Result> result = null;
    @SerializedName("review")
    private List<Review> review = null;
    @SerializedName("give_rating")
    private Boolean giveRating;
    @SerializedName("average_rating")
    private String averageRating;

    public String getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(String averageRating) {
        this.averageRating = averageRating;
    }

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

    @SerializedName("give_rating")
    public Boolean getGiveRating() {
        return giveRating;
    }

    @SerializedName("give_rating")
    public void setGiveRating(Boolean giveRating) {
        this.giveRating = giveRating;
    }
    public class Result implements Serializable {

        @SerializedName("user_id")
        private String userId;
        @SerializedName("user_imgurl")
        private String userImgurl;
        @SerializedName("user_name")
        private String userName;
        @SerializedName("user_mb_no")
        private String userMbNo;
        @SerializedName("user_email")
        private String userEmail;
        @SerializedName("user_city")
        private String userCity;
        @SerializedName("user_country")
        private String userCountry;
        @SerializedName("user_fcm_key")
        private String userFcmKey;
        @SerializedName("user_type")
        private String userType;
        @SerializedName("user_facebook_id")
        private String userFacebookId;
        @SerializedName("user_instagram_id")
        private String userInstagramId;
        @SerializedName("user_instagram_url")
        private String userInstagramUrl;
        @SerializedName("user_google_id")
        private String userGoogleId;
        @SerializedName("user_background")
        private String userBackground;
        @SerializedName("user_skills")
        private String userSkills;
        @SerializedName("user_good_at")
        private String userGoodAt;
        @SerializedName("user_brands")
        private String userBrands;
        @SerializedName("user_other_links")
        private String userOtherLinks;
        @SerializedName("user_hashtag")
        private String userHashtag;
        @SerializedName("user_verified")
        private String userVerified;
        @SerializedName("user_session")
        private String userSession;
        @SerializedName("user_create_date")
        private String userCreateDate;
        @SerializedName("user_update_date")
        private String userUpdateDate;
        @SerializedName("user_status")
        private String userStatus;

        @SerializedName("user_id")
        public String getUserId() {
            return userId;
        }

        @SerializedName("user_id")
        public void setUserId(String userId) {
            this.userId = userId;
        }

        @SerializedName("user_imgurl")
        public String getUserImgurl() {
            return userImgurl;
        }

        @SerializedName("user_imgurl")
        public void setUserImgurl(String userImgurl) {
            this.userImgurl = userImgurl;
        }

        @SerializedName("user_name")
        public String getUserName() {
            return userName;
        }

        @SerializedName("user_name")
        public void setUserName(String userName) {
            this.userName = userName;
        }

        @SerializedName("user_mb_no")
        public String getUserMbNo() {
            return userMbNo;
        }

        @SerializedName("user_mb_no")
        public void setUserMbNo(String userMbNo) {
            this.userMbNo = userMbNo;
        }

        @SerializedName("user_email")
        public String getUserEmail() {
            return userEmail;
        }

        @SerializedName("user_email")
        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        @SerializedName("user_city")
        public String getUserCity() {
            return userCity;
        }

        @SerializedName("user_city")
        public void setUserCity(String userCity) {
            this.userCity = userCity;
        }

        @SerializedName("user_country")
        public String getUserCountry() {
            return userCountry;
        }

        @SerializedName("user_country")
        public void setUserCountry(String userCountry) {
            this.userCountry = userCountry;
        }

        @SerializedName("user_fcm_key")
        public String getUserFcmKey() {
            return userFcmKey;
        }

        @SerializedName("user_fcm_key")
        public void setUserFcmKey(String userFcmKey) {
            this.userFcmKey = userFcmKey;
        }

        @SerializedName("user_type")
        public String getUserType() {
            return userType;
        }

        @SerializedName("user_type")
        public void setUserType(String userType) {
            this.userType = userType;
        }

        @SerializedName("user_facebook_id")
        public String getUserFacebookId() {
            return userFacebookId;
        }

        @SerializedName("user_facebook_id")
        public void setUserFacebookId(String userFacebookId) {
            this.userFacebookId = userFacebookId;
        }

        @SerializedName("user_instagram_id")
        public String getUserInstagramId() {
            return userInstagramId;
        }

        @SerializedName("user_instagram_id")
        public void setUserInstagramId(String userInstagramId) {
            this.userInstagramId = userInstagramId;
        }

        @SerializedName("user_instagram_url")
        public String getUserInstagramUrl() {
            return userInstagramUrl;
        }

        @SerializedName("user_instagram_url")
        public void setUserInstagramUrl(String userInstagramUrl) {
            this.userInstagramUrl = userInstagramUrl;
        }

        @SerializedName("user_google_id")
        public String getUserGoogleId() {
            return userGoogleId;
        }

        @SerializedName("user_google_id")
        public void setUserGoogleId(String userGoogleId) {
            this.userGoogleId = userGoogleId;
        }

        @SerializedName("user_background")
        public String getUserBackground() {
            return userBackground;
        }

        @SerializedName("user_background")
        public void setUserBackground(String userBackground) {
            this.userBackground = userBackground;
        }

        @SerializedName("user_skills")
        public String getUserSkills() {
            return userSkills;
        }

        @SerializedName("user_skills")
        public void setUserSkills(String userSkills) {
            this.userSkills = userSkills;
        }

        @SerializedName("user_good_at")
        public String getUserGoodAt() {
            return userGoodAt;
        }

        @SerializedName("user_good_at")
        public void setUserGoodAt(String userGoodAt) {
            this.userGoodAt = userGoodAt;
        }

        @SerializedName("user_brands")
        public String getUserBrands() {
            return userBrands;
        }

        @SerializedName("user_brands")
        public void setUserBrands(String userBrands) {
            this.userBrands = userBrands;
        }

        @SerializedName("user_other_links")
        public String getUserOtherLinks() {
            return userOtherLinks;
        }

        @SerializedName("user_other_links")
        public void setUserOtherLinks(String userOtherLinks) {
            this.userOtherLinks = userOtherLinks;
        }

        @SerializedName("user_hashtag")
        public String getUserHashtag() {
            return userHashtag;
        }

        @SerializedName("user_hashtag")
        public void setUserHashtag(String userHashtag) {
            this.userHashtag = userHashtag;
        }

        @SerializedName("user_verified")
        public String getUserVerified() {
            return userVerified;
        }

        @SerializedName("user_verified")
        public void setUserVerified(String userVerified) {
            this.userVerified = userVerified;
        }

        @SerializedName("user_session")
        public String getUserSession() {
            return userSession;
        }

        @SerializedName("user_session")
        public void setUserSession(String userSession) {
            this.userSession = userSession;
        }

        @SerializedName("user_create_date")
        public String getUserCreateDate() {
            return userCreateDate;
        }

        @SerializedName("user_create_date")
        public void setUserCreateDate(String userCreateDate) {
            this.userCreateDate = userCreateDate;
        }

        @SerializedName("user_update_date")
        public String getUserUpdateDate() {
            return userUpdateDate;
        }

        @SerializedName("user_update_date")
        public void setUserUpdateDate(String userUpdateDate) {
            this.userUpdateDate = userUpdateDate;
        }

        @SerializedName("user_status")
        public String getUserStatus() {
            return userStatus;
        }

        @SerializedName("user_status")
        public void setUserStatus(String userStatus) {
            this.userStatus = userStatus;
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
