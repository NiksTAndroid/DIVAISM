package com.prometteur.divaism.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.prometteur.divaism.Adapters.BodyTypeListAdapter;
import com.prometteur.divaism.LocalDB.DatabaseHelper;
import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.PojoModels.FormDataToSave;
import com.prometteur.divaism.PojoModels.GetRequestFormResponse;
import com.prometteur.divaism.PojoModels.SavedFormDataPojo;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.databinding.ActivityRequestFormBinding;
import com.prometteur.divaism.databinding.DialogColorPickerBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerPopup;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.prometteur.divaism.Utils.CommonMethods.getBitmap;
import static com.prometteur.divaism.Utils.CommonMethods.loadImage;
import static com.prometteur.divaism.Utils.CommonMethods.prepareFilePart;
import static com.prometteur.divaism.Utils.CommonMethods.toRequestBody;
import static com.prometteur.divaism.Utils.ConstantStrings.NewUser;
import static com.prometteur.divaism.Utils.ConstantStrings.SavedUserID;
import static com.prometteur.divaism.Utils.ConstantStrings.stylistId;
import static com.prometteur.divaism.Utils.ConstantStrings.toSend;
import static com.prometteur.divaism.Utils.ConstantStrings.userDetails;
import static com.prometteur.divaism.Utils.ConstantStrings.usertype;

public class RequestFormActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "RequestFormActivity";
    ActivityRequestFormBinding binding;
    BodyTypeListAdapter adapter;
    Context nContext;
    ArrayList<Uri> imguris = new ArrayList<>();
    private List<File> images = new ArrayList<>();
    List<MultipartBody.Part> imageParts;
    Bundle bundle;
    static String bodyType;
    static int bodyTypeSelected = -1;
    String userType;
    String ageRange;
    String unFavroiteColor;
    String[] ages = {"Select age", "< 18", "19 to 23", "24 to 27",
            "28 to 32", "33 to 36", "37 to 40", "41 to 44",
            "45 to 48", "49 to 52", "53 to 56", "57 to 60",
            "61 to 64", "65 to 68", "69 to 72", "73 to 76",
            "77 to 80", "81 to 84", "85 to 88", "89 to 92",
            "93 to 96", "97 to 100"};
    public static int REQUEST_IMAGE = 121;
    public static int CAPTURE_REQUEST_IMAGE = 122;
    Uri imageUri;
    int picNumber = 0;
    String fileName;
    boolean toSave;
    private Gson gson;
    DatabaseHelper dbHelper;
    String STYLISTID;
    int gender = 0;
    boolean goBack;
    Dialog loadingDialog;

    List<File> savedImagesList;
    List<Bitmap> imageBitmaps = new ArrayList<>();
    List<File> imagesToUpload = new ArrayList<>();
    File imageOne;
    File imageTwo;
    File imageThree;
    MultipartBody.Part uploadimageOne;
    MultipartBody.Part uploadimageTwo;
    MultipartBody.Part uploadimageThree;
    int commaCount = 0;
    GetRequestFormResponse.Result resultRequestForm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext = this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);
        bundle = getIntent().getExtras();
        if (bundle != null) {
            userType = bundle.getString(usertype);
            if (bundle.containsKey(stylistId)) {
                STYLISTID = bundle.getString(stylistId);

            } else {
                toSave = bundle.getBoolean(ConstantStrings.toBeSaved);
            }
            if (bundle.containsKey(NewUser)) {
                goBack = false;
            }
            if (bundle.containsKey(ConstantStrings.toSend)) {
                toSave = bundle.getBoolean(toSend);
            }

        }
        if (STYLISTID != null) {
            toSave = false;
        }
        dbHelper = new DatabaseHelper(nContext);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                binding.tvPopUp.setVisibility(View.GONE);
            }
        }, 5000);
        binding.spinAge.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, ages);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        binding.spinAge.setAdapter(aa);


        binding.colorpicker.setOnClickListener(this);
        binding.ivSendForm.setOnClickListener(this);
        binding.ivUploadPicOne.setOnClickListener(this);
        binding.ivUploadPicTwo.setOnClickListener(this);
        binding.ivUploadPicThree.setOnClickListener(this);

        /*Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        permission[0] =true;
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();*/
        getRequestFormDataApi(true);
        initListAdapter();

    }

    private void initListAdapter() {

        adapter = new BodyTypeListAdapter(nContext, CommonMethods.bodyTypeUris(nContext));
        binding.recycleBodiesIcons.setLayoutManager(new LinearLayoutManager(nContext, RecyclerView.HORIZONTAL, false));
        binding.recycleBodiesIcons.setAdapter(adapter);
    }

    public static void getSelectedBodyType(int position) {
        bodyType = String.valueOf(position);
        bodyTypeSelected = position;
        Log.e(TAG, "getSelectedBodyType: " + position);
    }

    public static int setSelectedBodyType() {
        return bodyTypeSelected;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.colorpicker:
                showColorpickerdialog();
                break;

            case R.id.ivSendForm:
                if (toSave || STYLISTID == null) {
                    if (resultRequestForm != null) {
                        UpdateRequestFormApiCall(1);
                    } else {
                        storeRequestForm(1);
                    }
                } else {
                    if (resultRequestForm != null) {
                        if (resultRequestForm.getUformId() != null) {
                            UpdateRequestFormApiCall(0);
                            //sendRequestFormApiCall();
                        }
                    } else {
                        //saveFormToDataBase();
                        storeRequestForm(0);
                        sendRequestFormApiCall(true);
                    }

                }
                break;
            case R.id.ivUploadPicOne:
                picNumber = 1;
                selectImage();
                break;
            case R.id.ivUploadPicTwo:
                picNumber = 2;
                selectImage();
                break;
            case R.id.ivUploadPicThree:
                picNumber = 3;
                selectImage();
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        ageRange = ages[i];
        Log.e(TAG, "onItemSelected: " + ageRange);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void showColorpickerdialog() {

        Dialog dialog = new Dialog(this);
        DialogColorPickerBinding colorBinding = DialogColorPickerBinding.inflate(getLayoutInflater());
        dialog.setContentView(colorBinding.getRoot());
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int[] colorChoosen = new int[1];
        colorBinding.colorPicker.subscribe(new ColorObserver() {
            @Override
            public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
                colorChoosen[0] = color;
            }
        });
        colorBinding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        colorBinding.tvChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.colorpicker.setProgress(100);
                binding.colorpicker.setProgressBarColor(colorBinding.colorPicker.getColor());
                Log.e(TAG, "onClick:colorChosen " + colorChoosen[0]);
                String hexColor = String.format("#%06X", (0xFFFFFF & colorBinding.colorPicker.getColor()));
                unFavroiteColor = hexColor;
                Log.e(TAG, "onColorPicked: " + colorBinding.colorPicker.getColor());
                Log.e(TAG, "onColorPicked: hexColor" + hexColor);
                binding.edtPriceRange.requestFocus();
                dialog.cancel();
            }
        });

        dialog.show();

        /*new ColorPickerPopup.Builder(this)
                .initialColor(Color.RED) // Set initial color
                .enableBrightness(true) // Enable brightness slider or not
                .enableAlpha(true) // Enable alpha slider or not
                .okTitle("Choose")
                .cancelTitle("Cancel")
                .showIndicator(true)
                .showValue(true)
                .build()
                .show( new ColorPickerPopup.ColorPickerObserver() {
                    @Override
                    public void onColorPicked(int color) {

                        binding.colorpicker.setProgress(100);
                        binding.colorpicker.setProgressBarColor(color);

                        String hexColor = String.format("#%06X", (0xFFFFFF & color));
                        unFavroiteColor=hexColor;
                        Log.e(TAG, "onColorPicked: "+color );
                        Log.e(TAG, "onColorPicked: hexColor"+hexColor );
                    }
                });*/
    }

    private void sendRequestFormApiCall(boolean sendwithImages) {
        imageParts = new ArrayList<>();

        if (binding.rbMale.isChecked()) {
            gender = 1;
        }
        if (binding.rbFemale.isChecked()) {
            gender = 2;
        }
        if(imagesToUpload.size()>0) {
            for (File image : imagesToUpload) {
                imageParts.add(prepareFilePart("req_photo[]", image));
            }
        }
        if (bodyType == null || binding.edtHeightCM.getText().toString() == null
                || ageRange == null
                || binding.edtFavBrands.getText().toString() == null
                || unFavroiteColor == null
                || binding.edtHeightInches.getText().toString() == null
                || binding.edtFavBrands.getText().toString() == null
                || binding.edtOtherComments.getText().toString() == null
                || binding.edtPriceRange.getText().toString() == null) {
            Toast.makeText(nContext, nContext.getResources().getString(R.string.FillallDetails), Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
            return;

        }
        loadingDialog.show();
        if (sendwithImages) {
            ApiConfing.getApiClient().sendRequestForm(toRequestBody(STYLISTID),
                    toRequestBody(String.valueOf(bodyType)),
                    toRequestBody(String.valueOf(gender)),
                    toRequestBody(binding.edtHeightCM.getText().toString() + "-" + binding.edtHeightInches.getText().toString()),
                    toRequestBody(String.valueOf(ageRange)),
                    toRequestBody(binding.edtFavBrands.getText().toString()),
                    toRequestBody(String.valueOf(unFavroiteColor)),
                    toRequestBody(binding.edtPriceRange.getText().toString()),
                    toRequestBody(binding.edtOtherComments.getText().toString()),
                    imageParts,
                    toRequestBody(userDetails.getUserId())
            ).enqueue(new Callback<FormAndReviewResponse>() {
                @Override
                public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                    FormAndReviewResponse nData = response.body();
                    if (nData.getStatus() == 1) {
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.Request_Sent), Toast.LENGTH_SHORT).show();
                        if (userType.equalsIgnoreCase(ConstantStrings.client) || userType.equalsIgnoreCase(ConstantStrings.stylist)) {
                            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
                            finish();
                        }
                    }else if(nData.getStatus()==0){
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    }

                    else{
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        CommonMethods.logoutDueToSession(nContext);
                    }
                    loadingDialog.cancel();

                }

                @Override
                public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(nContext, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    loadingDialog.cancel();

                }
            });
        } else {
            Log.e(TAG, "sendRequestFormApiCall: " + resultRequestForm.getUformPhoto());
            ApiConfing.getApiClient().sendStringRequestForm(STYLISTID,
                    String.valueOf(bodyType),
                    String.valueOf(gender),
                    binding.edtHeightCM.getText().toString() + "-" + binding.edtHeightInches.getText().toString(),
                    String.valueOf(ageRange),
                    binding.edtFavBrands.getText().toString(),
                    String.valueOf(unFavroiteColor),
                    binding.edtPriceRange.getText().toString(),
                    binding.edtOtherComments.getText().toString(),
                    resultRequestForm.getUformPhoto(),
                    userDetails.getUserId()
            ).enqueue(new Callback<FormAndReviewResponse>() {
                @Override
                public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                    FormAndReviewResponse nData = response.body();
                    if (nData.getStatus() == 1) {
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.Request_Sent), Toast.LENGTH_SHORT).show();
                        if (userType.equalsIgnoreCase(ConstantStrings.client) || userType.equalsIgnoreCase(ConstantStrings.stylist)) {
                            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
                            finish();
                        }
                    }
                    loadingDialog.cancel();

                }

                @Override
                public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(nContext, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    loadingDialog.cancel();

                }
            });
        }
    }

    private void selectImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                                startActivityForResult(takePictureIntent, CAPTURE_REQUEST_IMAGE);
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
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap mBitmap = null;
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                imageUri = data.getData();
                File file = new File(CommonMethods.getPath(nContext, imageUri));
                Log.e(TAG, "onActivityResult: " + imageUri);
                if (picNumber == 1) {
                    binding.ivUploadPicOne.setImageURI(imageUri);
                    imageOne=file;
                    if(imagesToUpload.size()>0) {
                        imagesToUpload.set(0, file);
                    }else{
                        imagesToUpload.add(file);
                    }

                } else if (picNumber == 2) {
                    binding.ivUploadPicTwo.setImageURI(imageUri);
                    imageTwo=file;
                    if(imagesToUpload.size()>1) {
                        imagesToUpload.set(1, file);
                    }else{
                        imagesToUpload.add(file);
                    }

                    //mBitmap=Bitmap.createBitmap(binding.ivUploadPicOne);
                } else if (picNumber == 3) {
                    binding.ivUploadPicThree.setImageURI(imageUri);
                    imageThree=file;
                    if(imagesToUpload.size()>2) {
                        imagesToUpload.set(2, file);
                    }else{
                        imagesToUpload.add(file);
                    }

                    //mBitmap=Bitmap.createBitmap(binding.ivUploadPicOne);
                }


                Log.e(TAG, "onActivityResult: " + file.getPath());
                if (images.size() > 3) {
                    images.set(picNumber, file);
                } else {
                    images.add(file);
                }

            }

        }if (requestCode == CAPTURE_REQUEST_IMAGE) {
            Uri captureduri = getCacheImagePath(fileName);
            Log.e(TAG, "onActivityResult: "+captureduri.getPath() );
            if (captureduri != null) {

                if (picNumber == 1) {
                    binding.ivUploadPicOne.setImageURI(captureduri);
                    //binding.ivUploadPicOne.setDrawingCacheEnabled(true);
                   // mBitmap = binding.ivUploadPicOne.getDrawingCache();


                } else if (picNumber == 2) {
                    binding.ivUploadPicTwo.setImageURI(captureduri);
                    //binding.ivUploadPicTwo.setDrawingCacheEnabled(true);
                   // mBitmap = binding.ivUploadPicOne.getDrawingCache();

                    //mBitmap=Bitmap.createBitmap(binding.ivUploadPicOne);
                } else if (picNumber == 3) {
                    binding.ivUploadPicThree.setImageURI(captureduri);
                    //binding.ivUploadPicThree.setDrawingCacheEnabled(true);
                    //mBitmap=Bitmap.createBitmap(binding.ivUploadPicOne);
                    //mBitmap = binding.ivUploadPicOne.getDrawingCache();

                }
                try {
                    mBitmap=MediaStore.Images.Media.getBitmap(nContext.getContentResolver(), captureduri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (mBitmap != null) {
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                }
                String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, "newBitmap", null);
                File file = new File(CommonMethods.getPath(nContext, Uri.parse(newpath)));

                if(picNumber==1){
                    imageOne=file;
                    if(imagesToUpload.size()>0) {
                        imagesToUpload.set(0, file);
                    }else{
                        imagesToUpload.add(file);
                    }
                }
                if(picNumber==2){
                    imageTwo=file;
                    if(imagesToUpload.size()>1) {
                        imagesToUpload.set(1, file);
                    }else{
                        imagesToUpload.add(file);
                    }
                }
                if(picNumber==3){
                    imageThree=file;
                    if(imagesToUpload.size()>2) {
                        imagesToUpload.set(2, file);
                    }else{
                        imagesToUpload.add(file);
                    }
                }

                Log.e(TAG, "onActivityResult: filepath" + file.getPath());
                if (images.size() > 3) {
                    images.set(picNumber, file);
                } else {
                    images.add(file);
                }
            } else {
                Toast.makeText(nContext, "Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }
     /*public void saveFormToDataBase() {
         gson = new Gson();
         String savebodyType;
         if (bodyType!=null){
              savebodyType=bodyType;
         }else{
              savebodyType= String.valueOf(bodyTypeSelected);
         }

         Log.e(TAG, "saveFormToDataBase: "+savebodyType );
        if(savebodyType==null){
            Toast.makeText(nContext,getResources().getString(R.string.selectBodyType),Toast.LENGTH_SHORT);
        }
        String saveheightcm=binding.edtHeightCM.getText().toString();
        String saveheightInch=binding.edtHeightInches.getText().toString();
         if(binding.rbMale.isChecked()){
             gender=1;
         }
         if (binding.rbFemale.isChecked()){
             gender=2;
         }
        String savegender=String.valueOf(gender);
        String saveAge=ageRange;
        String saveFavBrands=binding.edtFavBrands.getText().toString();
        String saveUnFavColor=unFavroiteColor;
        String savePrice=binding.edtPriceRange.getText().toString();
        if(savebodyType==null || saveheightcm==null
                || saveheightInch==null
                || savegender==null
                || saveAge==null
                || saveFavBrands==null
                || saveUnFavColor==null
                || savePrice==null){
            //startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));

        }else{
            Log.e(TAG, "saveFormToDataBase: "+userDetails.getUserId() );
            dbHelper.addDataToDatabase(gson.toJson(new FormDataToSave(savebodyType,saveheightcm,saveheightInch,savegender,saveAge,saveFavBrands,saveUnFavColor,savePrice,images)),userDetails.getUserId());
            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
        }
    }*/

    /*public void UpdateDatabaseData(){
        gson = new Gson();
        String savebodyType=bodyType;
        Log.e(TAG, "saveFormToDataBase: "+savebodyType );
        if(savebodyType==null){
            Toast.makeText(nContext,getResources().getString(R.string.selectBodyType),Toast.LENGTH_SHORT);
        }
        String saveheightcm=binding.edtHeightCM.getText().toString();
        String saveheightInch=binding.edtHeightInches.getText().toString();
        if(binding.rbMale.isChecked()){
            gender=1;
        }
        if (binding.rbFemale.isChecked()){
            gender=2;
        }
        String savegender=String.valueOf(gender);
        String saveAge=ageRange;
        String saveFavBrands=binding.edtFavBrands.getText().toString();
        String saveUnFavColor=unFavroiteColor;
        String savePrice=binding.edtPriceRange.getText().toString();
        if(savebodyType==null || saveheightcm==null
                || saveheightInch==null
                || savegender==null
                || saveAge==null
                || saveFavBrands==null
                || saveUnFavColor==null
                || savePrice==null){
            //startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));

        }else{
            Log.e(TAG, "saveFormToDataBase: "+userDetails.getUserId() );
            dbHelper.updateData(gson.toJson(new FormDataToSave(savebodyType,saveheightcm,saveheightInch,savegender,saveAge,saveFavBrands,saveUnFavColor,savePrice,images)),userDetails.getUserId());
            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
        }
    }*/

    /*private void getDataFromDB() {
        if(dbHelper.getDataFromDatabase()!=null) {
            responseData.addAll(dbHelper.getDataFromDatabase());
            for (SavedFormDataPojo data : responseData) {
                Log.e(TAG, "getDataFromDB: " + data.getId());
                    savedFormData = data;
            }
        }
        if(savedFormData!=null) {

            setDataBaseData();
        }

    }*/

    private void storeRequestForm(int i) {
        String savebodyType;
        if (bodyType != null) {
            savebodyType = bodyType;
        } else {
            savebodyType = String.valueOf(bodyTypeSelected);
        }

        Log.e(TAG, "saveFormToDataBase: " + savebodyType);
        if (savebodyType == null) {
            Toast.makeText(nContext, getResources().getString(R.string.selectBodyType), Toast.LENGTH_SHORT);
        }
        String saveheightcm = binding.edtHeightCM.getText().toString();
        String saveheightInch = binding.edtHeightInches.getText().toString();
        if (binding.rbMale.isChecked()) {
            gender = 1;
        }
        if (binding.rbFemale.isChecked()) {
            gender = 2;
        }
        String savegender = String.valueOf(gender);
        String saveAge = ageRange;
        String saveFavBrands = binding.edtFavBrands.getText().toString();
        String saveUnFavColor = unFavroiteColor;
        String saveOtherComments = binding.edtOtherComments.getText().toString();
        String savePrice = binding.edtPriceRange.getText().toString();
        if (savebodyType == null || saveheightcm == null
                || saveheightInch == null
                || savegender == null
                || saveAge == null
                || saveFavBrands == null
                || saveUnFavColor == null
                || saveOtherComments == null
                || savePrice == null) {
            //startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
            loadingDialog.cancel();
            return;

        } else {
            imageParts = new ArrayList<>();
            if (imagesToUpload.size() != 0) {
                for (File image : imagesToUpload) {
                    imageParts.add(prepareFilePart("uform_photo[]", image));
                }
            }
            ApiConfing.getApiClient().storeRequestForm(toRequestBody(SavedUserID),
                    toRequestBody(savebodyType),
                    toRequestBody(saveheightcm + "-" + saveheightInch),
                    toRequestBody(saveAge),
                    toRequestBody(saveFavBrands),
                    toRequestBody(saveUnFavColor),
                    toRequestBody(savePrice),
                    toRequestBody(saveOtherComments),
                    imageParts, toRequestBody(savegender)).enqueue(new Callback<FormAndReviewResponse>() {
                @Override
                public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                    FormAndReviewResponse nResponse = response.body();
                    if (nResponse.getStatus() == 1 && i == 1) {
                        Toast.makeText(nContext, getResources().getString(R.string.formSaved), Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
                        finish();
                    }
                    loadingDialog.cancel();
                }

                @Override
                public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(nContext, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    loadingDialog.cancel();
                }
            });
        }
    }

    private void UpdateRequestFormApiCall(int i) {
        loadingDialog.show();
        String savebodyType;
        if (bodyType != null) {
            savebodyType = bodyType;
        } else {
            savebodyType = String.valueOf(bodyTypeSelected);
        }

        Log.e(TAG, "saveFormToDataBase: " + savebodyType);
        if (savebodyType == null) {
            Toast.makeText(nContext, getResources().getString(R.string.selectBodyType), Toast.LENGTH_SHORT);
        }
        String saveheightcm = binding.edtHeightCM.getText().toString();
        String saveheightInch = binding.edtHeightInches.getText().toString();
        if (binding.rbMale.isChecked()) {
            gender = 1;
        }
        if (binding.rbFemale.isChecked()) {
            gender = 2;
        }
        String savegender = String.valueOf(gender);
        String saveAge = ageRange;
        String saveFavBrands = binding.edtFavBrands.getText().toString();
        String saveOtherComments = binding.edtOtherComments.getText().toString();
        String saveUnFavColor = unFavroiteColor;
        String savePrice = binding.edtPriceRange.getText().toString();
        if (savebodyType == null || saveheightcm == null
                || saveheightInch == null
                || savegender == null
                || saveAge == null
                || saveFavBrands == null
                || saveUnFavColor == null
                || saveOtherComments == null
                || savePrice == null) {
            //startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
            loadingDialog.cancel();
            return;

        } else {
            imageParts = new ArrayList<>();
            if(imageOne!=null){
                uploadimageOne=prepareFilePart("uform_photo_1[]", imageOne);
            }else{

            }
            if(imageTwo!=null){
                uploadimageTwo=prepareFilePart("uform_photo_2[]", imageTwo);
            }else{

            }
            if(imageThree!=null){
                uploadimageThree=prepareFilePart("uform_photo_3[]", imageThree);
            }else{

            }

            Log.e(TAG, "UpdateRequestFormApiCall: "+uploadimageOne );
            Log.e(TAG, "UpdateRequestFormApiCall: "+uploadimageTwo );
            Log.e(TAG, "UpdateRequestFormApiCall: "+uploadimageThree );

            ApiConfing.getApiClient().updateNewRequestForm(toRequestBody(resultRequestForm.getUformId()),
                    toRequestBody(SavedUserID),
                    toRequestBody(savebodyType),
                    toRequestBody(saveheightcm + "-" + saveheightInch),
                    toRequestBody(saveAge),
                    toRequestBody(saveFavBrands),
                    toRequestBody(saveUnFavColor),
                    toRequestBody(savePrice),
                    toRequestBody(saveOtherComments),
                    uploadimageOne,uploadimageTwo,uploadimageThree, toRequestBody(savegender)
            ).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    CommonResponse nResponse = response.body();
                    if (nResponse.getStatus() == 1) {
                        if(i==1) {
                            Toast.makeText(nContext, getResources().getString(R.string.formUpdated), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
                            finish();
                        }else{
                            getRequestFormDataApi(false);
                        }
                    }
                    loadingDialog.cancel();

                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    Log.e(TAG, "onFailure: " + t.getMessage());
                    Toast.makeText(nContext, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                    loadingDialog.cancel();
                }
            });
        }
    }


    private void getRequestFormDataApi(boolean setData) {
        loadingDialog.show();
        ApiConfing.getApiClient().getServerRequestForm(SavedUserID)
                .enqueue(new Callback<GetRequestFormResponse>() {
                    @Override
                    public void onResponse(Call<GetRequestFormResponse> call, Response<GetRequestFormResponse> response) {
                        GetRequestFormResponse nResponse = response.body();
                        if (nResponse.getStatus() == 1 && setData) {
                            resultRequestForm = nResponse.getResult().get(0);
                            if(setData)
                            setApiData(setData);
                        }else{
                            sendRequestFormApiCall(false);
                        }
                        loadingDialog.cancel();
                    }

                    @Override
                    public void onFailure(Call<GetRequestFormResponse> call, Throwable t) {
                        Toast.makeText(nContext, getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                        loadingDialog.cancel();
                    }
                });

    }


    private void setApiData(boolean setData) {
        if(setData) {
            savedImagesList = new ArrayList<>();
            //gson=new Gson();
            int agePosition = 0;
            //savedData=gson.fromJson(savedFormData.getData(),FormDataToSave.class);
            if (resultRequestForm != null) {
                //binding.ivBodiesIcons.setImageURI(CommonMethods.bodyTypeUris(nContext).get());
                Log.e(TAG, "setApiData: " + resultRequestForm.getUformBodyType());
                if(!resultRequestForm.getUformBodyType().toString().isEmpty()) {
                    binding.recycleBodiesIcons.smoothScrollToPosition(Integer.parseInt(resultRequestForm.getUformBodyType()));
                    bodyTypeSelected = Integer.parseInt(resultRequestForm.getUformBodyType());
                    adapter.notifyDataSetChanged();
                    bodyType = resultRequestForm.getUformBodyType();
                }
                if(resultRequestForm.getUformUnfevColor().startsWith("#")) {
                    binding.colorpicker.setProgressBarColor(Color.parseColor(resultRequestForm.getUformUnfevColor()));
                }else{
                    binding.colorpicker.setProgressBarColor(Color.parseColor("#"+resultRequestForm.getUformUnfevColor()));
                }
                binding.edtFavBrands.setText(resultRequestForm.getUformBrands());
                binding.edtOtherComments.setText(resultRequestForm.getUformComment());
                if (resultRequestForm.getUformReqGender().equalsIgnoreCase("1")) {
                    binding.rbMale.setChecked(true);
                }
                if (resultRequestForm.getUformReqGender().equalsIgnoreCase("2")) {
                    binding.rbFemale.setChecked(true);
                }
                for (int i = 0; i < ages.length; i++) {
                    if (ages[i].equalsIgnoreCase(resultRequestForm.getUformAge())) {
                        agePosition = i;
                    }
                }

                binding.spinAge.setSelection(agePosition);
                unFavroiteColor = resultRequestForm.getUformUnfevColor();
                binding.edtHeightCM.setText(resultRequestForm.getUformHeight().substring(0, resultRequestForm.getUformHeight().indexOf("-")));
                binding.edtHeightInches.setText(resultRequestForm.getUformHeight().substring(resultRequestForm.getUformHeight().indexOf("-") + 1, resultRequestForm.getUformHeight().length()));
                binding.edtPriceRange.setText(resultRequestForm.getUformPrice());
                String comma = ",";
                if(!resultRequestForm.getUformPhoto().isEmpty() &&resultRequestForm.getUformPhoto().length()>0) {
                    for (int i = 0; i < resultRequestForm.getUformPhoto().length(); i++) {
                        if (comma.equalsIgnoreCase(resultRequestForm.getUformPhoto().charAt(i) + "")) {
                            commaCount = commaCount + 1;
                        }
                    }
                }
            if (commaCount >= 1) {
                //LoadImages(resultRequestForm.getUformPhoto().substring(0, resultRequestForm.getUformPhoto().toString().indexOf(",")), 1);
                loadImage(binding.ivUploadPicOne, resultRequestForm.getUformPhoto().substring(0, resultRequestForm.getUformPhoto().toString().indexOf(",")));
                if (commaCount >= 2) {
                    //LoadImages(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().indexOf(",") + 1, resultRequestForm.getUformPhoto().toString().lastIndexOf(",")), 2);
                    //LoadImages(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") + 1, resultRequestForm.getUformPhoto().length()), 3);
                    loadImage(binding.ivUploadPicTwo, resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().indexOf(",") + 1, resultRequestForm.getUformPhoto().toString().lastIndexOf(",")));
                    if(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") , resultRequestForm.getUformPhoto().length()).length()>1) {
                        loadImage(binding.ivUploadPicThree, resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") + 1, resultRequestForm.getUformPhoto().length()));
                    }else{
                        loadImage(binding.ivUploadPicThree, "");
                    }

                } else {
                    //LoadImages(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") + 1, resultRequestForm.getUformPhoto().length()), 2);
                    loadImage(binding.ivUploadPicTwo, resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") + 1, resultRequestForm.getUformPhoto().length()));
                    loadImage(binding.ivUploadPicThree, "");
                }
            } else {
                loadImage(binding.ivUploadPicOne, resultRequestForm.getUformPhoto());
                //LoadImages(resultRequestForm.getUformPhoto(), 1);
                loadImage(binding.ivUploadPicTwo, "");
                loadImage(binding.ivUploadPicThree, "");
            }
              /*  if (commaCount >= 1) {
                    if (commaCount >= 2) {
                        LoadImages(resultRequestForm.getUformPhoto().substring(0, resultRequestForm.getUformPhoto().toString().indexOf(",")), 1);
                        LoadImages(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().indexOf(",") + 1, resultRequestForm.getUformPhoto().toString().lastIndexOf(",")), 2);
                        LoadImages(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") + 1, resultRequestForm.getUformPhoto().length()), 3);
                    } else {
                        LoadImages(resultRequestForm.getUformPhoto().substring(0, resultRequestForm.getUformPhoto().toString().indexOf(",")), 1);
                        LoadImages(resultRequestForm.getUformPhoto().substring(resultRequestForm.getUformPhoto().toString().lastIndexOf(",") + 1, resultRequestForm.getUformPhoto().length()), 2);
                        loadImage(binding.ivUploadPicThree, "");
                    }
                } else {
                    LoadImages(resultRequestForm.getUformPhoto(), 1);
                    loadImage(binding.ivUploadPicTwo, "");
                    loadImage(binding.ivUploadPicThree, "");
                }*/

            /*if(savedData.getImagesAdded().size()==3) {
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
            }*/

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
            finish();

        } else {
            startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, userType));
            finish();

            //Toast.makeText(nContext, "Fill the form first.", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();

    }

    /*private void LoadImages(String url, int position) {


        Glide.with(nContext)
                .asBitmap()
                .load(url)
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.AUTOMATIC))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                        //view.setImageBitmap(bitmap); //For SubsampleImage
                        if (position == 1) {
                            binding.ivUploadPicOne.setImageBitmap(bitmap);
                            //imageBitmaps.add( bitmap);
                            Uri uri = getImageUri(bitmap);
                            imagesToUpload.add(new File(CommonMethods.getPath(nContext,uri)));
                        }
                        if (position == 2) {
                            binding.ivUploadPicTwo.setImageBitmap(bitmap);
                            //imageBitmaps.add( bitmap);
                            Uri uri = getImageUri(bitmap);
                            imagesToUpload.add(new File(CommonMethods.getPath(nContext,uri)));
                        }
                        if (position == 3) {
                            binding.ivUploadPicThree.setImageBitmap(bitmap);
                            //imageBitmaps.add( bitmap);
                            Uri uri = getImageUri(bitmap);
                            imagesToUpload.add(new File(CommonMethods.getPath(nContext,uri)));
                        }
                        
                    }
                });




    }

    public Uri getImageUri(Bitmap inImage) {

        *//*if(permission[0]) {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(nContext.getContentResolver(), inImage, "new"+UUID.randomUUID().toString() + ".png", "drawing");            return Uri.parse(path);
        }else{
            return null;
        }*//*
    }*/


}