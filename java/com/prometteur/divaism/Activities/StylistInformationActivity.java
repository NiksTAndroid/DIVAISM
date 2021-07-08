package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.PojoModels.StylistProfileResponse;
import com.prometteur.divaism.PojoModels.UserProfileResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.CustomEditText;
import com.prometteur.divaism.Utils.CustomTextView;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityStylistInformationBinding;
import com.prometteur.divaism.databinding.NormalLinksViewBinding;
import com.prometteur.divaism.databinding.OtherLinksViewBinding;
import com.prometteur.divaism.databinding.StylistDetailsFormBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prometteur.divaism.Utils.CommonMethods.loadImage;
import static com.prometteur.divaism.Utils.ConstantStrings.client;
import static com.prometteur.divaism.Utils.ConstantStrings.stylist;
import static com.prometteur.divaism.Utils.ConstantStrings.stylistId;
import static com.prometteur.divaism.Utils.ConstantStrings.toBeSaved;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;

public class StylistInformationActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StylistInformationActiv";
    ActivityStylistInformationBinding binding;
    StylistDetailsFormBinding formBinding;
    Context nContext;
    String stylistId;
    String userType;
    Bundle nBundle;
    StylistProfileResponse.Result responseData;
    StylistProfileResponse.Review reviewData;
    String averageRating;
    Dialog loadingDialog;
    List<String> otherLinks = new ArrayList<>();
    private LinearLayout layoutsContainner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStylistInformationBinding.inflate(getLayoutInflater());
        formBinding = binding.detailForm;
        nContext = this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);
        setContentView(binding.getRoot());
        layoutsContainner = formBinding.llLinksContainer;
        nBundle = getIntent().getExtras();
        if (nBundle != null) {
            stylistId = nBundle.getString(ConstantStrings.stylistId);
            userType = nBundle.getString(ConstantStrings.usertype);
        }
        if (userType.equalsIgnoreCase(stylist)) {
            binding.ivMessage.setVisibility(View.GONE);
        }
        getstylistDetails();

        binding.ivMessage.setOnClickListener(this);
        formBinding.edtInstragramID.setOnClickListener(this);
        formBinding.tvSeeMore.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMessage:

                if (responseData != null) {
                    if (userType.equalsIgnoreCase(stylist) && !responseData.getUserId().equalsIgnoreCase(ConstantStrings.SavedUserID)) {
                        startActivity(new Intent(this, RequestFormActivity.class)
                                .putExtra(toBeSaved, false)
                                .putExtra(usertype, userType)
                                .putExtra(ConstantStrings.stylistId, stylistId));
                    }
                    if (userType.equalsIgnoreCase(client) && !responseData.getUserId().equalsIgnoreCase(ConstantStrings.SavedUserID)) {
                        startActivity(new Intent(this, RequestFormActivity.class)
                                .putExtra(toBeSaved, true)
                                .putExtra(usertype, userType)
                                .putExtra(ConstantStrings.toSend, true)
                                .putExtra(ConstantStrings.stylistId, stylistId));
                    }
                }
                break;

            case R.id.edtInstragramID:
                //Toast.makeText(nContext, "clicked", Toast.LENGTH_SHORT).show();
                Uri uri = Uri.parse(responseData.getUserInstagramId());
                Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

                likeIng.setPackage("com.instagram.android");

                try {
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/" + responseData.getUserInstagramId())));
                }
                break;

            case R.id.tvSeeMore:
                startActivity(new Intent(nContext,RatingAndReviews.class).putExtra(usertype, userType)
                        .putExtra(ConstantStrings.stylistId, stylistId));


        }
    }


    private void getstylistDetails() {
        loadingDialog.show();
        ApiConfing.getApiClient().StylistProfile(stylistId, ConstantStrings.SavedUserID).enqueue(new Callback<StylistProfileResponse>() {
            @Override
            public void onResponse(Call<StylistProfileResponse> call, Response<StylistProfileResponse> response) {
                StylistProfileResponse data = response.body();
                if (data != null) {
                    if (data.getStatus() == 1) {

                        responseData = data.getResult().get(0);
                        averageRating = data.getAverageRating();

                        setData();

                    }
                    loadingDialog.cancel();

                }
            }

            @Override
            public void onFailure(Call<StylistProfileResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                loadingDialog.cancel();
                ;
            }
        });


    }

    private void setData() {
        /*if (userType.equalsIgnoreCase(stylist) && responseData.getUserId().equalsIgnoreCase(ConstantStrings.SavedUserID)) {

            binding.ivMessage.setVisibility(View.GONE);

        }*/
        if (responseData.getUserImgurl() != null) {
            loadImage(binding.civStylistImage, responseData.getUserImgurl());
        } else {
            loadImage(binding.civStylistImage, "");
        }
        formBinding.edtCountry.setText(responseData.getUserCountry());
        formBinding.edtCountry.setEnabled(false);
        formBinding.edtExperience.setText("");
        formBinding.edtExperience.setEnabled(false);
        formBinding.edtFavBrands.setText(responseData.getUserBrands());
        formBinding.edtFavBrands.setEnabled(false);
        formBinding.edtGoodAt.setText(responseData.getUserGoodAt());
        formBinding.edtGoodAt.setEnabled(false);
        formBinding.edtHashTag.setText(responseData.getUserHashtag());
        formBinding.edtHashTag.setEnabled(false);
        formBinding.edtInstragramID.setText(responseData.getUserInstagramId());
        formBinding.edtName.setText(responseData.getUserName());
        formBinding.edtName.setEnabled(false);
        Log.e(TAG, "setData: " + responseData.getUserOtherLinks());
        if (responseData.getUserOtherLinks() != null && responseData.getUserOtherLinks().contains(",")) {
            for (int j = 0; j < responseData.getUserOtherLinks().length(); j++) {
                startindex = endindex;
                if (",".equalsIgnoreCase("" + responseData.getUserOtherLinks().charAt(j))) {
                    endindex = j;
                    if (endindex > 2) {
                        if (!responseData.getUserOtherLinks().substring(startindex).startsWith(",")) {
                            otherLinks.add(responseData.getUserOtherLinks().substring(startindex, endindex));
                        } else {
                            otherLinks.add(responseData.getUserOtherLinks().substring(startindex + 1, endindex));
                        }
                    }
                }
            }
        }
        if (otherLinks.size() >= 1) {
            for (int i = 0; i < otherLinks.size(); i++) {
                addOtherLinksViews(otherLinks.get(i), i);
            }
        } else {
            addOtherLinksViews(responseData.getUserOtherLinks(), 0);
        }

        if (averageRating != null && !averageRating.isEmpty()) {
            formBinding.rbRating.setRating(Float.parseFloat(averageRating));
        }
        formBinding.rbRating.setIsIndicator(true);
    }

    int endindex = 0;
    int startindex = 0;

    private void addOtherLinksViews(String text, int position) {

        final NormalLinksViewBinding productLayout = NormalLinksViewBinding.inflate(getLayoutInflater());
        View view = productLayout.getRoot();
        layoutsContainner.addView(view);
        CustomTextView edtotherLinks = layoutsContainner.getChildAt(position).findViewById(R.id.edtOtherLinks);
        edtotherLinks.setText(text);

    }




/*
    private void addReviewApiCall() {
        loadingDialog.show();
        if(formBinding.rbRating.getRating()<0){
            loadingDialog.cancel();
            Toast.makeText(nContext, "Please select star rate", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiConfing.getApiClient().reviewStylistProfile(stylistId,String.valueOf(formBinding.rbRating.getRating()),
                "",ConstantStrings.SavedUserID)
                .enqueue(new Callback<FormAndReviewResponse>() {
                    @Override
                    public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                        FormAndReviewResponse nResponse=response.body();
                        if (nResponse.getStatus()==1){
                            Toast.makeText(nContext,getResources().getString(R.string.reviewAddedSuccess),Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.cancel();

                    }

                    @Override
                    public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                        Toast.makeText(nContext,getResources().getString(R.string.reviewAddedFailure),Toast.LENGTH_SHORT).show();
                        loadingDialog.cancel();
                    }
                });
    }


    */

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}