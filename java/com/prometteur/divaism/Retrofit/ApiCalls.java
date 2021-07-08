package com.prometteur.divaism.Retrofit;

import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.GetRequestFormResponse;
import com.prometteur.divaism.PojoModels.LoginResponse;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.PojoModels.ReviewRatingResponse;
import com.prometteur.divaism.PojoModels.ReviewStylingPhotoResponse;
import com.prometteur.divaism.PojoModels.StylingPhotoReplyResponse;
import com.prometteur.divaism.PojoModels.StylistListResponse;
import com.prometteur.divaism.PojoModels.StylistProfileResponse;
import com.prometteur.divaism.PojoModels.UserProfileResponse;
import com.prometteur.divaism.Utils.UpdateProfileResponse;
import com.squareup.okhttp.ResponseBody;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiCalls {


    @POST("mLogin")
    @FormUrlEncoded
    Call<LoginResponse> faceBookLogin(@Field("user_facebook_id") String userFB_ID,
                                      @Field("user_imgurl") String UserImgUrl,
                                      @Field("user_name") String user_name,
                                      @Field("user_mb_no") String user_mb_no,
                                      @Field("user_email") String user_email,
                                      @Field("user_type") String user_type,
                                      @Field("user_verified") String user_verified);


    @POST("mLogin")
    @FormUrlEncoded
    Call<LoginResponse> InstagramLogin(@Field("user_insta_id") String user_insta_id,
                                       @Field("user_imgurl") String UserImgUrl,
                                       @Field("user_name") String user_name,
                                       @Field("user_mb_no") String user_mb_no,
                                       @Field("user_email") String user_email,
                                       @Field("user_type") String user_type,
                                       @Field("user_instagram_url") String user_instagram_url,
                                       @Field("user_verified") String user_verified);

    @POST("mLogin")
    @FormUrlEncoded
    Call<LoginResponse> GoogleLogin(@Field("user_google_id") String user_google_id,
                                    @Field("user_imgurl") String UserImgUrl,
                                    @Field("user_name") String user_name,
                                    @Field("user_mb_no") String user_mb_no,
                                    @Field("user_email") String user_email,
                                    @Field("user_type") String user_type,
                                    @Field("user_verified") String user_verified);

    @POST("fields/profile")
    @FormUrlEncoded
    Call<UserProfileResponse> UserProfile(@Field("user_id") String user_id);

    @POST("fields/profile")
    @FormUrlEncoded
    Call<StylistProfileResponse> StylistProfile(@Field("user_id") String user_id, @Field("login_user_id") String loginUserId);

    @POST("mAdd/profile")
    @Multipart
    Call<UpdateProfileResponse> UpdateUserProfile(@Part("user_id") RequestBody user_id,
                                                  @Part MultipartBody.Part UserImgUrl,
                                                  @Part("user_name") RequestBody user_name,
                                                  @Part("user_city") RequestBody user_city,
                                                  @Part("user_country") RequestBody user_country,
                                                  @Part("user_background") RequestBody user_backgrounf,
                                                  @Part("user_skills") RequestBody user_skills,
                                                  @Part("user_good_at") RequestBody user_good_at,
                                                  @Part("user_brands") RequestBody user_brands,
                                                  @Part("user_instagram_id") RequestBody instagram_Id,
                                                  @Part("user_other_links") RequestBody user_other_link,
                                                  @Part("user_hashtag") RequestBody user_hashtag);


    @POST("fields/home")
    @FormUrlEncoded
    Call<StylistListResponse> getAllStylistList(@Field("user_type") String user_type);

    @POST("mAdd/request")
    @Multipart
    Call<FormAndReviewResponse> sendRequestForm(@Part("req_user_id") RequestBody req_user_id,
                                                @Part("req_body_type") RequestBody req_body_type,
                                                @Part("req_gender") RequestBody req_gender,
                                                @Part("req_height") RequestBody req_height,
                                                @Part("req_age") RequestBody req_age,
                                                @Part("req_brands") RequestBody req_brands,
                                                @Part("req_unfev_color") RequestBody req_unfev_color,
                                                @Part("req_price") RequestBody req_price,
                                                @Part("req_comment") RequestBody req_comment,
                                                @Part List<MultipartBody.Part> files,
                                                @Part("req_create_by") RequestBody req_create_by);

    @POST("mAdd/request")
    @FormUrlEncoded
    Call<FormAndReviewResponse> sendStringRequestForm(@Field("req_user_id") String req_user_id,
                                                @Field("req_body_type") String req_body_type,
                                                @Field("req_gender") String req_gender,
                                                @Field("req_height") String req_height,
                                                @Field("req_age") String req_age,
                                                @Field("req_brands") String req_brands,
                                                @Field("req_unfev_color") String req_unfev_color,
                                                @Field("req_price") String req_price,
                                                @Field("req_comment") String req_comment,
                                                @Field ("req_photo") String FilePath,
                                                @Field("req_create_by") String req_create_by);

    @POST("fields/get_messages")
    @FormUrlEncoded
    Call<MessageResponse> getMessageList(@Field("user_id") String user_id,
                                         @Field("user_type") String user_type);


    @POST("fields/request")
    @FormUrlEncoded
    Call<MessageResponse> getRequestForm(@Field("req_id") String req_id);

    @POST("fields/reply")
    @FormUrlEncoded
    Call<ReviewStylingPhotoResponse> getReplyFromStylist(@Field("rep_id") String rep_id);

    @POST("mAdd/reply")
    @Multipart
    Call<FormAndReviewResponse> sendStylingPhoto(@Part("rep_user_id") RequestBody rep_user_id,
                                                 @Part MultipartBody.Part files,
                                                 @Part("rep_text") RequestBody rep_text,
                                                 @Part("rep_links") RequestBody rep_links,
                                                 @Part("rep_request_id") RequestBody rep_request_id,
                                                 @Part("rep_create_by") RequestBody rep_create_by
    );

    @POST("mAdd/comment")
    @FormUrlEncoded
    Call<FormAndReviewResponse> reviewStylistDesign(@Field("rev_request_id") String rev_request_id,
                                                    @Field("rev_rating") float rev_rating,
                                                    @Field("rev_text") String rev_text,
                                                    @Field("rev_create_by") String rev_create_by
    );

    @POST("mAdd/comment")
    @FormUrlEncoded
    Call<FormAndReviewResponse> reviewStylistProfile(@Field("rev_stylist_id") String rev_stylist_id,
                                                     @Field("rev_rating") String rev_rating,
                                                     @Field("rev_text") String rev_text,
                                                     @Field("rev_create_by") String rev_create_by
    );

    //////this is a call to download the image ost stylist side in the request form
    @POST("http://divaism.prometteur.in/application/webroot/images/req_photo/{path}")
    @Streaming
    Call<ResponseBody> downloadImageCall(@Path("path") String fileUrl);

    @POST("mAdd/support_message")
    @FormUrlEncoded
    Call<FormAndReviewResponse> sendSupportMsg(@Field("sp_user_id") String userId,
                                               @Field("sp_message") String message
    );

    @POST("del/user")
    @FormUrlEncoded
    Call<CommonResponse> deleteUser(@Field("delete") String userId);

    @POST("mAdd/store_request_form")
    @Multipart
    Call<FormAndReviewResponse> storeRequestForm(@Part("uform_user_id") RequestBody userId,
                                                 @Part("uform_body_type") RequestBody bodyType,
                                                 @Part("uform_height") RequestBody userHeight,
                                                 @Part("uform_age") RequestBody age,
                                                 @Part("uform_brands") RequestBody brands,
                                                 @Part("uform_unfev_color") RequestBody unFavColor,
                                                 @Part("uform_price") RequestBody price,
                                                 @Part("uform_comment") RequestBody comment,
                                                 @Part List<MultipartBody.Part> files,
                                                 @Part("uform_gender") RequestBody gender);

    @POST("mAdd/store_request_form")
    @Multipart
    Call<CommonResponse> updateRequestForm(@Part("uform_id") RequestBody formId,
                                                  @Part("uform_user_id") RequestBody userId,
                                                  @Part("uform_body_type") RequestBody bodyType,
                                                  @Part("uform_height") RequestBody userHeight,
                                                  @Part("uform_age") RequestBody age,
                                                  @Part("uform_brands") RequestBody brands,
                                                  @Part("uform_unfev_color") RequestBody unFavColor,
                                                  @Part("uform_price") RequestBody price,
                                                  @Part("uform_comment") RequestBody comment,
                                                  @Part List<MultipartBody.Part> files,
                                                  @Part("uform_gender") RequestBody gender);

    @POST("mAdd/update_request_form")
    @Multipart
    Call<CommonResponse> updateNewRequestForm(@Part("uform_id") RequestBody formId,
                                                  @Part("uform_user_id") RequestBody userId,
                                                  @Part("uform_body_type") RequestBody bodyType,
                                                  @Part("uform_height") RequestBody userHeight,
                                                  @Part("uform_age") RequestBody age,
                                                  @Part("uform_brands") RequestBody brands,
                                                  @Part("uform_unfev_color") RequestBody unFavColor,
                                                  @Part("uform_price") RequestBody price,
                                                  @Part("uform_comment") RequestBody comment,
                                                  @Part MultipartBody.Part image1,
                                                  @Part MultipartBody.Part image2,
                                                  @Part MultipartBody.Part image3,
                                                  @Part("uform_gender") RequestBody gender);




    @POST("fields/user_form")
    @FormUrlEncoded
    Call<GetRequestFormResponse> getServerRequestForm(@Field("user_id") String userId);

    @POST("fields/review_details")
    @FormUrlEncoded
    Call<ReviewRatingResponse> getRatingsAndReviews(@Field("user_id") String stylistId);
}
