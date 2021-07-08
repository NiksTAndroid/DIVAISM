package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.prometteur.divaism.Adapters.ReviewRatingsAdapter;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.PojoModels.ReviewRatingResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityRatingAndReviewsBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prometteur.divaism.Utils.ConstantStrings.stylistId;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;

public class RatingAndReviews extends AppCompatActivity {
    private static final String TAG = "RatingAndReviews";
    ActivityRatingAndReviewsBinding binding;
    ReviewRatingsAdapter rateAdapter;
    Context nContext;
    Bundle nBundle;
    String USERTYPE;
    String STYLISTID;
    Dialog loadingDialog;
    List<ReviewRatingResponse.Result> responseData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityRatingAndReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext=this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);
        nBundle=getIntent().getExtras();
        if(nBundle!=null){

            STYLISTID=nBundle.getString(stylistId);
            USERTYPE=nBundle.getString(usertype);
            Log.e(TAG, "onCreate:  STYLISTID"+STYLISTID);
            Log.e(TAG, "onCreate: USERTYPE "+USERTYPE);

        }

        getRatingsApiCall();
    }

    private void getRatingsApiCall() {
        loadingDialog.show();
        ApiConfing.getApiClient().getRatingsAndReviews(STYLISTID)
                .enqueue(new Callback<ReviewRatingResponse>() {
                    @Override
                    public void onResponse(Call<ReviewRatingResponse> call, Response<ReviewRatingResponse> response) {
                        ReviewRatingResponse nResponse=response.body();
                        responseData=new ArrayList<>();
                        if(nResponse.getStatus()==1 && nResponse.getResult()!=null){
                            responseData.addAll(nResponse.getResult());
                            initAdapter();
                        }
                        else if(nResponse.getStatus()==0){
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            CommonMethods.logoutDueToSession(nContext);
                        }

                        loadingDialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<ReviewRatingResponse> call, Throwable t) {
                        loadingDialog.cancel();
                        Toast.makeText(nContext, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void initAdapter(){
        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Collections.sort(responseData, new Comparator<ReviewRatingResponse.Result>() {
            @Override
            public int compare(ReviewRatingResponse.Result result, ReviewRatingResponse.Result t1) {
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = dateFormat.parse(result.getRevCreateDate());
                    date2 = dateFormat.parse(t1.getRevCreateDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return (date1.getTime() > date2.getTime() ? -1 : 1);     //descending
                //return (date1.getTime() > date2.getTime() ? 1 : -1);
            }
        });
        rateAdapter=new ReviewRatingsAdapter(nContext,USERTYPE,STYLISTID,responseData);
        binding.recycleRatingReviews.setLayoutManager(new LinearLayoutManager(nContext));
        binding.recycleRatingReviews.setAdapter(rateAdapter);
    }
}