package com.prometteur.divaism.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.LockOnGetVariable;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.LoginResponse;
import com.prometteur.divaism.PojoModels.UserProfileResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.CustomEditText;
import com.prometteur.divaism.Utils.CustomTextView;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.Utils.UpdateProfileResponse;
import com.prometteur.divaism.databinding.ActivityStylistDetailsBinding;
import com.prometteur.divaism.databinding.NormalLinksViewBinding;
import com.prometteur.divaism.databinding.OtherLinksViewBinding;
import com.prometteur.divaism.databinding.UserDetailsFormBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.prometteur.divaism.Activities.SignUpActivity.logintype;
import static com.prometteur.divaism.Utils.CommonMethods.loadImage;
import static com.prometteur.divaism.Utils.CommonMethods.prepareFilePart;
import static com.prometteur.divaism.Utils.CommonMethods.toRequestBody;
import static com.prometteur.divaism.Utils.ConstantStrings.fromHome;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;


public class StylistDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StylistDetailsActivity";
    ActivityStylistDetailsBinding binding;
    UserDetailsFormBinding userBinding;
    Bundle bundle;
    String userType;
    int endindex = 0;
    int startindex = 0;
    boolean fromHomepage;
    Context nContext;
    private LinearLayout layoutsContainner;
    List<String> toAddLink;
    List<String> addedLinks;
    UserProfileResponse.Result responseData;
    public static int REQUEST_IMAGE = 121;
    public static int GET_IMAGE_RESULT = 123;
    Uri imageUri;
    String fileName;
    String otherLinks = "";
    List<String> otherLinkList = new ArrayList<>();
    File file;
    Dialog loadingDialog;
    boolean isImageSelected=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStylistDetailsBinding.inflate(getLayoutInflater());
        userBinding = binding.form;
        setContentView(binding.getRoot());
        nContext = this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);
        layoutsContainner = userBinding.llLinksContainer;
        addedLinks = new ArrayList<>();
        toAddLink = new ArrayList<>();
        Log.e(TAG, "onCreate: " + ConstantStrings.usersavedtype);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            userType = bundle.getString(usertype);
            fromHomepage = bundle.getBoolean(fromHome);

        }
        userBinding.ivSave.setOnClickListener(this);
        binding.ivAdd.setOnClickListener(this);
        binding.rivProfile.setOnClickListener(this);


        getStylistInfoApiCall();


        //addedLinks.add(true);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, HomePage.class).putExtra(ConstantStrings.usertype, userType));
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSave:
                UpdateProfileApiCall();
                break;
            case R.id.ivAdd:
                selectImage();
                break;
            case R.id.rivProfile:
                selectImage();
                break;
        }
    }

    private void addOtherLinksView(String text, int position) {

        final OtherLinksViewBinding productLayout = OtherLinksViewBinding.inflate(getLayoutInflater());
        View view = productLayout.getRoot();
        layoutsContainner.addView(view);
        toAddLink.add("1");

        for (int i = 0; i < layoutsContainner.getChildCount(); i++) {
            CustomEditText edtotherLinks = layoutsContainner.getChildAt(i).findViewById(R.id.edtOtherLinks);



            int finalI = i;
            edtotherLinks.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
                @Override
                public void onClick(DrawablePosition target) {
                    switch (target) {
                        case RIGHT:
                            if (addedLinks.size() < finalI) {
                                addedLinks.add(edtotherLinks.getText().toString());
                            }

                            if (toAddLink.get(finalI).equalsIgnoreCase("1")) {
                                if (CommonMethods.linkValidation(edtotherLinks.getText().toString())) {
                                    toAddLink.add(finalI, "0");
                                    if (addedLinks.size() > finalI) {
                                        addedLinks.set(finalI, edtotherLinks.getText().toString());
                                    } else {
                                        addedLinks.add(edtotherLinks.getText().toString());
                                    }
                                    edtotherLinks.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(nContext, R.drawable.ic_remove_black), null);

                                    addOtherLinksView("", 0);
                                } else {
                                    Toast.makeText(nContext, "Please enter valid url link.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                //addedLinks.remove(finalI);
                                //layoutsContainner.removeView(layoutsContainner.getChildAt(finalI));
                              /*  if(addedLinks.size()>finalI) {
                                    addedLinks.remove(finalI);
                                }*/
                                layoutsContainner.removeView(layoutsContainner.getChildAt(finalI));
                            }
                    }
                }
            });
        }


    }


    private void UpdateProfileApiCall() {
        loadingDialog.show();
        addedLinks.clear();
        otherLinks="";
        Log.e(TAG, "UpdateProfileApiCall: "+layoutsContainner.getChildCount() );
        for(int i=0;i<layoutsContainner.getChildCount();i++) {
            CustomEditText edtotherLinks = layoutsContainner.getChildAt(i).findViewById(R.id.edtOtherLinks);
            if(CommonMethods.linkValidation(edtotherLinks.getText().toString())) {
                addedLinks.add(edtotherLinks.getText().toString());
            }
        }

        for (int i = 0; i < addedLinks.size(); i++) {
            otherLinks = addedLinks.get(i) + "," + otherLinks;
            Log.e(TAG, "UpdateProfileApiCall: " + otherLinks);
        }
        if (!isImageSelected){
            if( responseData.getUserImgurl() == null || responseData.getUserImgurl().length() == 0) {
            Toast.makeText(nContext, "Please select Image", Toast.LENGTH_SHORT).show();
            loadingDialog.cancel();
            return;
        }}
        if (userBinding.edtName.getText().toString().isEmpty()){

                Toast.makeText(nContext, "Please enter name", Toast.LENGTH_SHORT).show();
                userBinding.edtName.requestFocus();
                loadingDialog.cancel();
                return;
            }

        if (userBinding.edtCountry.getText().toString().isEmpty()){

            Toast.makeText(nContext, "Please enter country", Toast.LENGTH_SHORT).show();
            userBinding.edtCountry.requestFocus();
            loadingDialog.cancel();
            return;
        }
        if (userBinding.edtBackground.getText().toString().isEmpty()){

            Toast.makeText(nContext, "Please enter background", Toast.LENGTH_SHORT).show();
            userBinding.edtBackground.requestFocus();
            loadingDialog.cancel();
            return;
        }
        if (userBinding.edtSkills.getText().toString().isEmpty()){

            Toast.makeText(nContext, "Please enter skills", Toast.LENGTH_SHORT).show();
            userBinding.edtSkills.requestFocus();
            loadingDialog.cancel();
            return;
        }
        if (userBinding.edtGoodAt.getText().toString().isEmpty()){

            Toast.makeText(nContext, "Please enter GoodAt", Toast.LENGTH_SHORT).show();
            userBinding.edtGoodAt.requestFocus();
            loadingDialog.cancel();
            return;
        }
        if (userBinding.edtFavBrands.getText().toString().isEmpty()){

            Toast.makeText(nContext, "Please enter Favorite Brands", Toast.LENGTH_SHORT).show();
            userBinding.edtFavBrands.requestFocus();
            loadingDialog.cancel();
            return;
        }
        if (userBinding.edtHashTag.getText().toString().isEmpty()){

            Toast.makeText(nContext, "Please enter HashTag", Toast.LENGTH_SHORT).show();
            userBinding.edtHashTag.requestFocus();
            loadingDialog.cancel();
            return;
        }
        String hashTag;
        if(!userBinding.edtHashTag.getText().toString().startsWith("#")){
            hashTag="#"+userBinding.edtHashTag.getText().toString();
        }else{
            hashTag=userBinding.edtHashTag.getText().toString();
        }
        MultipartBody.Part imagepart = prepareFilePart("user_imgurl[]", file);

        ApiConfing.getApiClient().UpdateUserProfile(toRequestBody(ConstantStrings.SavedUserID),
                imagepart,
                toRequestBody(userBinding.edtName.getText().toString()),
                toRequestBody(""),
                toRequestBody(userBinding.edtCountry.getText().toString()),
                toRequestBody(userBinding.edtBackground.getText().toString()),
                toRequestBody(userBinding.edtSkills.getText().toString()),
                toRequestBody(userBinding.edtGoodAt.getText().toString()),
                toRequestBody(userBinding.edtFavBrands.getText().toString()),
                toRequestBody(userBinding.edtInstragramID.getText().toString()),
                toRequestBody(otherLinks),
                toRequestBody(hashTag)
        ).enqueue(new Callback<UpdateProfileResponse>() {
            @Override
            public void onResponse(Call<UpdateProfileResponse> call, Response<UpdateProfileResponse> response) {
                UpdateProfileResponse nData = response.body();
                loadingDialog.cancel();
                if (nData.getStatus() == 1) {
                    Toast.makeText(nContext, getResources().getString(R.string.profileUpdate), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
                }

            }

            @Override
            public void onFailure(Call<UpdateProfileResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(nContext, t.getMessage(), Toast.LENGTH_SHORT);
                loadingDialog.cancel();
            }
        });
    }


    private void getStylistInfoApiCall() {
        loadingDialog.show();
        ApiConfing.getApiClient().UserProfile(ConstantStrings.SavedUserID).enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                UserProfileResponse data = response.body();
                if (data.getStatus() == 1) {
                    responseData = data.getResult().get(0);
                    setData();
                }
                loadingDialog.cancel();
                ;
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                loadingDialog.cancel();
            }
        });
    }

    private void setData() {
        loadImage(binding.rivProfile, responseData.getUserImgurl());
        if (responseData.getUserImgurl().length() != 0) {
            binding.ivAdd.setVisibility(View.GONE);
        }
        userBinding.edtCountry.setText(responseData.getUserCountry());
        userBinding.edtBackground.setText(responseData.getUserBackground());
        userBinding.edtSkills.setText(responseData.getUserSkills());
        userBinding.edtFavBrands.setText(responseData.getUserBrands());
        userBinding.edtGoodAt.setText(responseData.getUserGoodAt());
        userBinding.edtHashTag.setText(responseData.getUserHashtag());
        userBinding.edtInstragramID.setText(responseData.getUserInstagramId());
        userBinding.edtName.setText(responseData.getUserName());
        Log.e(TAG, "setData: "+responseData.getUserOtherLinks() );
        if (responseData.getUserOtherLinks() != null && responseData.getUserOtherLinks().contains(",")) {
            for (int j = 0; j < responseData.getUserOtherLinks().length(); j++) {
                startindex = endindex;
                if (",".equalsIgnoreCase("" + responseData.getUserOtherLinks().charAt(j))) {
                    endindex = j;
                    if(endindex>2) {
                        if (!responseData.getUserOtherLinks().substring(startindex).startsWith(",")) {
                            otherLinkList.add(responseData.getUserOtherLinks().substring(startindex, endindex));
                            addedLinks.add(responseData.getUserOtherLinks().substring(startindex, endindex));
                        } else {
                            otherLinkList.add(responseData.getUserOtherLinks().substring(startindex+1, endindex));
                            addedLinks.add(responseData.getUserOtherLinks().substring(startindex+1, endindex));
                        }
                    }
                }
            }
        }else{
            otherLinkList.add(responseData.getUserOtherLinks());
        }
        if (otherLinkList.size() >= 1) {

            for (int i = 0; i < otherLinkList.size(); i++)
                addOtherLinksViews(otherLinkList.get(i), i);

        }
        if(otherLinkList.size()==0 && responseData.getUserOtherLinks().length()>0) {
            addOtherLinksViews(responseData.getUserOtherLinks(), 1);
        }else{
            addOtherLinksView("", 0);
        }
    }


    private void selectImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        } else {
                            // TODO - handle permission denied case
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();
    }

    private void showImagePickerOptions() {

        ImagePickerAction.showImagePickerOptions(this, new ImagePickerAction.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void launchGalleryIntent() {

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, REQUEST_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(nContext, getPackageName() + ".provider", image);
    }

    Bitmap mBitmap;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    Log.e(TAG, "onActivityResult: " + imageUri);
                    binding.rivProfile.setImageURI(imageUri);
                    isImageSelected=true;
                    file = new File(CommonMethods.getPath(nContext, imageUri));
                    Log.e(TAG, "onActivityResult: " + file.getPath());
                } else {
                    imageUri = getCacheImagePath(fileName);
                    isImageSelected=true;
                    binding.rivProfile.setImageURI(imageUri);
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(nContext.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                    String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, "newBitmap", null);

                    file = new File(CommonMethods.getPath(nContext, Uri.parse(newpath)));
                }
                binding.ivAdd.setVisibility(View.GONE);

            }
        }
    }

    private void addOtherLinksViews(String text, int position) {

        final OtherLinksViewBinding productLayout = OtherLinksViewBinding.inflate(getLayoutInflater());
        View view = productLayout.getRoot();
        layoutsContainner.addView(view);
        CustomEditText edtotherLinks = layoutsContainner.getChildAt(position).findViewById(R.id.edtOtherLinks);
        edtotherLinks.setText(text);
        edtotherLinks.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(nContext, R.drawable.ic_remove_black), null);
        for(int i=0;i<layoutsContainner.getChildCount();i++) {
            int finalI = i;
            toAddLink.add("0");
            edtotherLinks.setDrawableClickListener(new CustomEditText.DrawableClickListener() {
                @Override
                public void onClick(DrawablePosition target) {
                    switch (target) {
                        case RIGHT:

                            //addedLinks.remove(finalI);
                            //layoutsContainner.removeView(layoutsContainner.getChildAt(finalI));
                            if (addedLinks.size() >= finalI) {
                                addedLinks.remove(position);
                            }
                            layoutsContainner.getChildAt(finalI).setVisibility(View.GONE);

                    }
                }
            });
        }

    }


}