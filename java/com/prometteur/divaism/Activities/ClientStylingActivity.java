package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.PojoModels.ReviewStylingPhotoResponse;
import com.prometteur.divaism.PojoModels.StylingPhotoReplyResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.CustomTextView;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityClientStylingBinding;
import com.prometteur.divaism.databinding.DialogDownloadImageBinding;
import com.prometteur.divaism.databinding.NormalLinksViewBinding;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.prometteur.divaism.Utils.CommonMethods.loadImage;

public class ClientStylingActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClientStylingActivity";
    ActivityClientStylingBinding binding;
    Context nContext;
    Bundle nBundle;
    String stylistID;
    String replyId;
    ReviewStylingPhotoResponse.Result stylistReplyData;
    ReviewStylingPhotoResponse.Review reviewReplyData;
    Dialog loadingDialog;
    private LinearLayout layoutsContainner;
    /*List<String> addedLinks=new ArrayList<>();
    int startindex=0;
    int endindex=0;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityClientStylingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext=this;
        loadingDialog= LoadingDialog.getLoadingDialog(nContext);
        binding.btnSubmit.setOnClickListener(this);
        binding.civMainPic.setOnClickListener(this);
        ///binding.civPicToLeft.setOnClickListener(this);
        ///binding.civPicToRight.setOnClickListener(this);
        nBundle=getIntent().getExtras();
        layoutsContainner = binding.llLinksContainer;
        if(nBundle!=null){
            stylistID=nBundle.getString(ConstantStrings.stylistId);
            replyId=nBundle.getString(ConstantStrings.FormReplyID);
        }
        if(ConstantStrings.userDetails.getUserType().equalsIgnoreCase("2")) {
            binding.btnSubmit.setVisibility(View.GONE);
        }
        getReplyApiCall();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSubmit:
                if(ConstantStrings.userDetails.getUserType().equalsIgnoreCase("1")) {
                    rateStylistApiCall();
                }
                break;
            case R.id.civMainPic:
                    showDownlaodImageDialog(stylistReplyData.getRepImage());
                break;

        }
    }

    private void showDownlaodImageDialog(String url){

        Dialog dialog=new Dialog(this);
        DialogDownloadImageBinding downloadImageBinding=DialogDownloadImageBinding.inflate(getLayoutInflater());
        dialog.setContentView(downloadImageBinding.getRoot());
        downloadImageBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        downloadImageBinding.tvdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // downloadImageAPICall(url);
                new DownloadFiles().execute(url);
                dialog.cancel();
            }
        });
        dialog.show();


    }

    private void rateStylistApiCall() {

        if(binding.rbMyRatings.getRating()==0){
            Toast.makeText(nContext, "Please select star rate", Toast.LENGTH_SHORT).show();
            return;
        }
        if(binding.edtComments.getText().toString().isEmpty()){
            Toast.makeText(nContext, "Please add comments", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialog.show();
        ApiConfing.getApiClient().reviewStylistDesign(stylistReplyData.getRepRequestId(),binding.rbMyRatings.getRating(),
                binding.edtComments.getText().toString(),ConstantStrings.SavedUserID)
                .enqueue(new Callback<FormAndReviewResponse>() {
                    @Override
                    public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                        FormAndReviewResponse nResponse=response.body();
                        if (nResponse.getStatus()==1){
                            Toast.makeText(nContext,getResources().getString(R.string.reviewAddedSuccess),Toast.LENGTH_SHORT).show();
                            finish();
                        }else if(nResponse.getStatus()==0){
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.cancel();

                    }

                    @Override
                    public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                        Toast.makeText(nContext,getResources().getString(R.string.reviewAddedFailure),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void getReplyApiCall(){
        Log.e(TAG, "getReplyApiCall: "+replyId );
        loadingDialog.show();
            ApiConfing.getApiClient().getReplyFromStylist(replyId)
                    .enqueue(new Callback<ReviewStylingPhotoResponse>() {
                        @Override
                        public void onResponse(Call<ReviewStylingPhotoResponse> call, Response<ReviewStylingPhotoResponse> response) {
                            Log.e(TAG, "onResponse: "+response.body() );
                            ReviewStylingPhotoResponse mResponse=response.body();

                            if(mResponse!=null && mResponse.getStatus()==1){
                                stylistReplyData=mResponse.getResult().get(0);
                                if(mResponse.getReview().size()>0){
                                    reviewReplyData=mResponse.getReview().get(mResponse.getReview().size()-1);
                                }
                                setAPIdata();
                            }else{
                                Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                                CommonMethods.logoutDueToSession(nContext);
                            }
                            loadingDialog.cancel();


                        }

                        @Override
                        public void onFailure(Call<ReviewStylingPhotoResponse> call, Throwable t) {
                            Log.e(TAG, "onFailure: "+t.getMessage() );
                            loadingDialog.cancel();
                        }
                    });
    }

    private void setAPIdata() {
        if(stylistReplyData.getRepImage().contains(",")) {

            //loadImage(binding.civMainPic, stylistReplyData.getRepImage().substring(0, stylistReplyData.getRepImage().toString().indexOf(",")));
            //loadImage(binding.civPicToLeft, stylistReplyData.getRepImage().substring(stylistReplyData.getRepImage().toString().indexOf(",") + 1, stylistReplyData.getRepImage().toString().lastIndexOf(",")));
            //loadImage(binding.civPicToRight, stylistReplyData.getRepImage().substring(stylistReplyData.getRepImage().toString().lastIndexOf(",") + 1, stylistReplyData.getRepImage().length()));
        }else{
            loadImage(binding.civMainPic, stylistReplyData.getRepImage());
            //loadImage(binding.civPicToRight, "");
            //loadImage(binding.civPicToLeft, "");

        }
        /*if (stylistReplyData.getRepLinks() != null && stylistReplyData.getRepLinks().contains(",")) {
            for (int j = 0; j < stylistReplyData.getRepLinks().length(); j++) {
                startindex = endindex;
                if (",".equalsIgnoreCase("" + stylistReplyData.getRepLinks().charAt(j))) {
                    endindex = j;
                    if(endindex>2) {
                        if (!stylistReplyData.getRepLinks().substring(startindex).startsWith(",")) {

                            addedLinks.add(stylistReplyData.getRepLinks().substring(startindex, endindex));
                        } else {
                            addedLinks.add(stylistReplyData.getRepLinks().substring(startindex+1, endindex));
                        }
                    }
                }
            }
        }else{
            addedLinks.add(stylistReplyData.getRepLinks());
        }
        if (addedLinks.size() >= 1) {
            if(stylistReplyData.getRepLinks().contains(",")) {
                addedLinks.add(stylistReplyData.getRepLinks().substring(stylistReplyData.getRepLinks().lastIndexOf(",") + 1, stylistReplyData.getRepLinks().length()));
            }
                for (int i = 0; i < addedLinks.size(); i++) {
                addOtherLinksViews(addedLinks.get(i), i);
            }
        } else {
            addOtherLinksViews(stylistReplyData.getRepLinks(), 0);
        }*/

        if(reviewReplyData!=null){
            binding.rbMyRatings.setRating(Float.parseFloat(reviewReplyData.getRevRating()));
            binding.edtComments.setText(reviewReplyData.getRevText());
            binding.rbMyRatings.setIsIndicator(true);

            binding.edtComments.setEnabled(false);
            binding.btnSubmit.setVisibility(View.GONE);
        }
        if (ConstantStrings.userDetails!=null
                && ConstantStrings.userDetails.getUserType()!=null
                && ConstantStrings.userDetails.getUserType().equalsIgnoreCase("2")
                && !ConstantStrings.SavedUserID.equalsIgnoreCase(stylistReplyData.getRepCreateDate())){
            binding.rbMyRatings.setIsIndicator(true);

            binding.edtComments.setEnabled(false);
        }
        //binding.tvReplyText.setText(stylistReplyData.getRepText().substring(0,2));

    }
    private void addOtherLinksViews(String text, int position) {

        final NormalLinksViewBinding productLayout = NormalLinksViewBinding.inflate(getLayoutInflater());
        View view = productLayout.getRoot();
        layoutsContainner.addView(view);
        CustomTextView edtotherLinks = layoutsContainner.getChildAt(position).findViewById(R.id.edtOtherLinks);
        edtotherLinks.setText(text);

    }
    private class DownloadFiles extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... sUrl) {
            Bitmap bm;
            InputStream in;
            String URL = sUrl[0];
            //YOUR INTRESTING LOOP HERE.
            publishProgress(30);

            try {
                in = new java.net.URL(sUrl[0]).openStream();
                bm = BitmapFactory.decodeStream(new DataInputStream(in));
                String fileName = System.currentTimeMillis()+".png";
                File storage = new File(Environment.getExternalStorageDirectory()+File.separator +"Pictures"+ "/DivaismImages/");
                Log.i(TAG, "storage:" + storage);
                Log.i(TAG, "storage:" + storage.getAbsolutePath());
                if (!storage.exists()) {
                    storage.mkdirs();

                }
                //String FileName = "/" + SetConstant.CONTENT_ID + ".jpg";
                FileOutputStream fos = new FileOutputStream(storage+File.separator+fileName);

                bm.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                String newpath = MediaStore.Images.Media.insertImage(nContext.getContentResolver(), bm, "newBitmap", null);
                //String filepath = storage.getPath()+"/"+fileName;
                File filecheck = new File(newpath);
                //long fileSize = filecheck.length();
                fos.flush();
                fos.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPreExecute () {
            super.onPreExecute();
            // pDialog.show();
            loadingDialog.show();
        }

        @Override
        protected void onProgressUpdate (Integer...progress){
            super.onProgressUpdate(progress);
            // pDialog.setProgress(progress[0]);
        }

        protected void onPostExecute (String result){
            super.onPostExecute(result);
            loadingDialog.cancel();
            Toast.makeText(nContext, nContext.getResources().getString(R.string.imageDownloaded), Toast.LENGTH_SHORT).show();

            //pDialog.dismiss();
        }
    }
}