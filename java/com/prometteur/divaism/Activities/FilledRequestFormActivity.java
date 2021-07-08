package com.prometteur.divaism.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.prometteur.divaism.LocalDB.DatabaseHelper;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.PojoModels.FormDataToSave;
import com.prometteur.divaism.PojoModels.MessageResponse;
import com.prometteur.divaism.PojoModels.SavedFormDataPojo;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityFilledRequestFormBinding;
import com.prometteur.divaism.databinding.DialogDownloadImageBinding;
import com.squareup.okhttp.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.prometteur.divaism.Utils.CommonMethods.getBitmap;
import static com.prometteur.divaism.Utils.CommonMethods.loadImage;
import static com.prometteur.divaism.Utils.CommonMethods.prepareFilePart;
import static com.prometteur.divaism.Utils.CommonMethods.toRequestBody;
import static com.prometteur.divaism.Utils.ConstantStrings.ClientID;
import static com.prometteur.divaism.Utils.ConstantStrings.FORMDATA;
import static com.prometteur.divaism.Utils.ConstantStrings.FormId;
import static com.prometteur.divaism.Utils.ConstantStrings.client;
import static com.prometteur.divaism.Utils.ConstantStrings.stylist;

public class FilledRequestFormActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FilledRequestFormActivi";
    ActivityFilledRequestFormBinding binding;
    Bundle nBundle;
    String requestUserFormId;
    String userType;
    String STYLISTID;
    String replyID;
    boolean toSend;
    MessageResponse.Result result;
    SavedFormDataPojo savedFormData;
    FormDataToSave savedData;
    List<SavedFormDataPojo> responseData=new ArrayList<>();
    DatabaseHelper dbHelper;
    Context nContext;
    Gson gson;
    int commaCount=0;
    List<MultipartBody.Part> imageParts;
    List<File> savedImagesList;
    Dialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFilledRequestFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext=this;
        dbHelper=new DatabaseHelper(nContext);
        loadingDialog=LoadingDialog.getLoadingDialog(nContext);
        nBundle=getIntent().getExtras();
        if(nBundle!=null){
            requestUserFormId=nBundle.getString(FormId);

            toSend=nBundle.getBoolean(ConstantStrings.toBeSaved);
            userType=nBundle.getString(ConstantStrings.usertype);
            STYLISTID=nBundle.getString(ConstantStrings.stylistId);
            replyID=nBundle.getString(ConstantStrings.FormReplyID);

            Log.e(TAG, "onCreate: "+requestUserFormId );
            Log.e(TAG, "onCreate: "+toSend );
            Log.e(TAG, "onCreate: "+userType );

        }
        /*if(userType.equalsIgnoreCase("2")) {
            getRequestFormData();
        }
        else{
            getDataFromDB();
        }*/
        if(replyID!=null){
            binding.ivMessage.setVisibility(View.INVISIBLE);
        }
        getRequestFormData();

        binding.ivMessage.setOnClickListener(this);
        binding.ivUploadPicOne.setOnClickListener(this);
        binding.ivUploadPicTwo.setOnClickListener(this);
        binding.ivUploadPicThree.setOnClickListener(this);
    }

    private void getDataFromDB() {
        if(dbHelper.getDataFromDatabase()!=null) {
            responseData.addAll(dbHelper.getDataFromDatabase());
            for (SavedFormDataPojo data : responseData) {
                Log.e(TAG, "getDataFromDB: " + data.getId());
                if (requestUserFormId.equalsIgnoreCase(String.valueOf(data.getId()))) {
                    savedFormData = data;
                }
            }
        }
        setDataBaseData();

    }

    private void setDataBaseData() {
        savedImagesList = new ArrayList<>();
        gson=new Gson();
        savedData=gson.fromJson(savedFormData.getData(),FormDataToSave.class);
        if (savedData!=null) {
            binding.ivBodiesIcons.setImageURI(CommonMethods.bodyTypeUris(nContext).get(Integer.parseInt(savedData.getSavebodyType())));
            binding.colorpicker.setProgressBarColor(Color.parseColor(savedData.getSaveUnFavColor()));
            binding.edtFavBrands.setText(savedData.getSaveFavBrands());
            binding.edtHeightCM.setText(savedData.getSaveheightcm());
            binding.edtHeightInches.setText(savedData.getSaveheightInch());
            binding.edtPriceRange.setText(savedData.getSavePrice());
            binding.spinAge.setText(savedData.getSaveAge());
            savedImagesList.addAll(savedData.getImagesAdded());
            if(savedData.getImagesAdded().size()==3) {
                Log.e(TAG, "setDataBaseData: "+ savedData.getImagesAdded().get(0).getPath());
                Log.e(TAG, "setDataBaseData: "+ savedData.getImagesAdded().get(0).getAbsolutePath());

                    binding.ivUploadPicOne.setImageBitmap(getBitmap(savedImagesList.get(0).getPath()));
                    binding.ivUploadPicTwo.setImageBitmap(getBitmap(savedImagesList.get(1).getPath()));
                    binding.ivUploadPicThree.setImageBitmap(getBitmap(savedImagesList.get(2).getPath()));

            }
            else if(savedData.getImagesAdded().size()==2){
                binding.ivUploadPicOne.setImageBitmap(getBitmap(savedImagesList.get(0).getPath()));
                binding.ivUploadPicTwo.setImageBitmap(getBitmap(savedImagesList.get(1).getPath()));
                binding.ivUploadPicThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_dummy));
            }
            else if(savedData.getImagesAdded().size()==1){
                binding.ivUploadPicOne.setImageBitmap(getBitmap(savedImagesList.get(0).getPath()));
                binding.ivUploadPicTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_dummy));
                binding.ivUploadPicThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_dummy));

            }else{
                binding.ivUploadPicOne.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_dummy));
                binding.ivUploadPicTwo.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_dummy));
                binding.ivUploadPicThree.setImageDrawable(getResources().getDrawable(R.drawable.ic_picture_dummy));
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ivMessage:
                if(userType.equalsIgnoreCase(client) && toSend || STYLISTID!=null){
                    sendFormApiCall();

                }else if(userType.equalsIgnoreCase(client) && !toSend ){
                    Log.e(TAG, "onClick: ");
                }
                else
                 {
                    startActivity(new Intent(this, StylingPhotoActivity.class)
                            .putExtra(FormId, requestUserFormId)
                            .putExtra(ClientID, result.getReqUserId()));
                }
                break;
            case  R.id.ivUploadPicOne:
                if(userType.equalsIgnoreCase(stylist)) {
                    if (result.getReqPhoto() != null) {
                        if (commaCount>=1) {
                            showDownlaodImageDialog(result.getReqPhoto().substring(0, result.getReqPhoto().toString().indexOf(",")));
                        } else {
                            showDownlaodImageDialog(result.getReqPhoto());
                        }
                    }
                }
                break;
            case  R.id.ivUploadPicTwo:
                if(userType.equalsIgnoreCase(stylist)) {
                if(result.getReqPhoto()!=null) {
                    if(commaCount>2){
                    showDownlaodImageDialog(result.getReqPhoto().substring(result.getReqPhoto().toString().indexOf(",") + 1, result.getReqPhoto().toString().lastIndexOf(",")));
                }if(commaCount==1){
                        showDownlaodImageDialog(result.getReqPhoto().substring(result.getReqPhoto().toString().indexOf(",") + 1, result.getReqPhoto().toString().length()));
                    }
                }}
                break;
            case  R.id.ivUploadPicThree:
                if(userType.equalsIgnoreCase(stylist)) {
                if(result.getReqPhoto()!=null) {
                    if(commaCount>2) {
                        showDownlaodImageDialog(result.getReqPhoto().substring(result.getReqPhoto().toString().lastIndexOf(",") + 1, result.getReqPhoto().length()));
                    }}}break;
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

    private void getRequestFormData(){
        loadingDialog.show();
        Log.e(TAG, "getRequestFormData: "+requestUserFormId );
        ApiConfing.getApiClient().getRequestForm(requestUserFormId)
                .enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        MessageResponse nData =response.body();
                        if(nData.getStatus()==1){
                            result=nData.getResult().get(0);
                            setApiResponseData();
                        }
                        else if(nData.getStatus()==0){
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                            CommonMethods.logoutDueToSession(nContext);
                        }
                        loadingDialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Log.e(TAG, "onFailure: "+t.getMessage() );
                        loadingDialog.cancel();
                    }
                });
    }

    private void setApiResponseData() {



        String comma=",";
        for (int i=0;i<result.getReqPhoto().length();i++){
            if(comma.equalsIgnoreCase(result.getReqPhoto().charAt(i)+"")){
                commaCount=commaCount+1;
            }
        }
        if(commaCount>=1) {
            loadImage(binding.ivUploadPicOne, result.getReqPhoto().substring(0, result.getReqPhoto().toString().indexOf(",")));
            if(commaCount>=2) {
                loadImage(binding.ivUploadPicTwo, result.getReqPhoto().substring(result.getReqPhoto().toString().indexOf(",") + 1, result.getReqPhoto().toString().lastIndexOf(",")));
                if(result.getReqPhoto().substring(result.getReqPhoto().toString().lastIndexOf(",") , result.getReqPhoto().length()).length()>1) {
                    loadImage(binding.ivUploadPicThree, result.getReqPhoto().substring(result.getReqPhoto().toString().lastIndexOf(",") + 1, result.getReqPhoto().length()));
                }else{
                    loadImage(binding.ivUploadPicThree, "");
                }

                //loadImage(binding.ivUploadPicThree, result.getReqPhoto().substring(result.getReqPhoto().toString().lastIndexOf(",") + 1, result.getReqPhoto().length()));
            }else {
                loadImage(binding.ivUploadPicTwo, result.getReqPhoto().substring(result.getReqPhoto().toString().lastIndexOf(",") + 1, result.getReqPhoto().length()));
                loadImage(binding.ivUploadPicThree,"");

            }
        }else{
            loadImage(binding.ivUploadPicOne, result.getReqPhoto());
            loadImage(binding.ivUploadPicTwo, "");
            loadImage(binding.ivUploadPicThree, "");

        }
        binding.ivBodiesIcons.setImageURI(CommonMethods.bodyTypeUris(nContext).get(Integer.parseInt(result.getReqBodyType())));
        if(result.getReqUnfevColor().contains("#")) {
            binding.colorpicker.setProgressBarColor(Color.parseColor(result.getReqUnfevColor()));
        }else{
            binding.colorpicker.setProgressBarColor(Color.parseColor("#"+result.getReqUnfevColor()));
        }
        binding.edtFavBrands.setText(result.getReqBrands());
        binding.edtHeightCM.setText(result.getReqHeight().substring(0,result.getReqHeight().indexOf("-")));
        binding.edtHeightInches.setText(result.getReqHeight().substring(result.getReqHeight().indexOf("-")+1,result.getReqHeight().toString().length()));
        binding.edtPriceRange.setText(result.getReqPrice());
        binding.spinAge.setText(result.getReqAge());
    }

    private void sendFormApiCall(){
        imageParts=new ArrayList<>();
        loadingDialog.show();
            for(File image:savedData.getImagesAdded()){
                imageParts.add(prepareFilePart("req_photo[]", image));
            }
        ApiConfing.getApiClient().sendRequestForm(toRequestBody(STYLISTID),

                toRequestBody(savedData.getSavebodyType()),
                toRequestBody(savedData.getSavegender()),
                toRequestBody(savedData.getSaveheightcm()+"-"+savedData.getSaveheightInch()),
                toRequestBody(savedData.getSaveAge()),
                toRequestBody(savedData.getSaveFavBrands()),
                toRequestBody(savedData.getSaveUnFavColor()),
                toRequestBody(savedData.getSavePrice()),
                toRequestBody(""),
                imageParts,
                toRequestBody(ConstantStrings.SavedUserID)
        ).enqueue(new Callback<FormAndReviewResponse>() {
            @Override
            public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                FormAndReviewResponse nData=response.body();
                if(nData.getStatus()==1) {
                    loadingDialog.cancel();
                    if (userType.equalsIgnoreCase(ConstantStrings.client)) {
                        Toast.makeText(nContext,"Form sent successfully",Toast.LENGTH_SHORT);

                        startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
                    }

                }
            }

            @Override
            public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: "+t.getMessage() );
                loadingDialog.cancel();
            }
        });
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


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


}