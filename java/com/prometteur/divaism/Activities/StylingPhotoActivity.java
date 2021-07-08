package com.prometteur.divaism.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.exifinterface.media.ExifInterface;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.prometteur.divaism.BuildConfig;
import com.prometteur.divaism.PojoModels.CommonResponse;
import com.prometteur.divaism.PojoModels.FormAndReviewResponse;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Retrofit.ApiConfing;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.LoadingDialog;
import com.prometteur.divaism.Utils.SomeView;
import com.prometteur.divaism.databinding.ActivityStylingPhotoBinding;
import com.prometteur.divaism.databinding.DialogAddnewPageBinding;
import com.prometteur.divaism.databinding.DialogEntertextBinding;
import com.prometteur.divaism.databinding.DialogSampleStylingDisplayBinding;
import com.prometteur.divaism.turboimageview.ImageObject;
import com.prometteur.divaism.turboimageview.MultiTouchObject;
import com.prometteur.divaism.turboimageview.TurboImageView;
import com.prometteur.divaism.turboimageview.TurboImageViewListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Multipart;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.prometteur.divaism.Utils.CommonMethods.newInstagramProfileIntent;
import static com.prometteur.divaism.Utils.CommonMethods.prepareFilePart;
import static com.prometteur.divaism.Utils.CommonMethods.toRequestBody;
import static com.prometteur.divaism.Utils.ConstantStrings.ClientID;
import static com.prometteur.divaism.Utils.ConstantStrings.FormId;

public class StylingPhotoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "StylingPhotoActivity";
    ActivityStylingPhotoBinding binding;
    Context nContext;
    public static int REQUEST_IMAGE = 121;
    public static int REQUEST_CAMERA_IMAGE = 122;
    public static int GET_IMAGE_RESULT = 123;
    Uri imageUri;
    private Bitmap mBitmap;
    String fileName;
    String texttoPin;
    RelativeLayout tempContainer;
    File images;
    private List<Bitmap> imageBitmaps = new ArrayList<>();
    private List<String> otherLinksList = new ArrayList<>();
    boolean newClicked = false;


    Bundle nBundle;
    String userFormID;
    String clientUserId;
    Dialog loadingDialog;
    int containerChildPosition = 0;

    TurboImageView tempturboView;
    LinearLayout layoutContainer;
    HashMap<ImageObject, String> objectHashMap;
    int textColorUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStylingPhotoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        nContext = this;
        loadingDialog = LoadingDialog.getLoadingDialog(nContext);
        nBundle = getIntent().getExtras();
        if (nBundle != null) {
            clientUserId = nBundle.getString(ClientID);
            userFormID = nBundle.getString(FormId);

        }

        binding.ivCamera.setOnClickListener(this);
        binding.ivPen.setOnClickListener(this);
        binding.ivAddLinks.setOnClickListener(this);
        binding.ivDelete.setOnClickListener(this);
        binding.ivHome.setOnClickListener(this);
        binding.ivSend.setOnClickListener(this);
        binding.ivGoBack.setOnClickListener(this);
        binding.ivGoForward.setOnClickListener(this);
        binding.ivAddNewPage.setOnClickListener(this);
        binding.layoutFontSizes.setVisibility(View.GONE);
        binding.colorPallette.setVisibility(View.GONE);
        layoutContainer = binding.LinearContainer;
        addView();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivSend:
                imageBitmaps.clear();
                if(layoutContainer.getChildCount()>1) {
                    for (int i = 0; i < layoutContainer.getChildCount(); i++) {
                        RelativeLayout layout = layoutContainer.getChildAt(i).findViewById(R.id.ActualView);
                        tempturboView = layout.findViewById(R.id.ivTurboView);
                        if (tempturboView.getSelectedObjectCount() > 0) {
                            tempturboView.deselectAll();
                        }
                        Bitmap viewbitmap = loadBitmapFromView(layout);
                        imageBitmaps.add(viewbitmap);
                    }

                    Bitmap allInOne = CommonMethods.combineImageIntoOne(imageBitmaps);
                    fileName = System.currentTimeMillis() + ".jpg";
                    //images = CommonMethods.bitmapToFile(nContext, allInOne, fileName);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    allInOne.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), allInOne,fileName, null);

                    images = new File(CommonMethods.getPath(nContext,Uri.parse(newpath)));
                    if(ConstantStrings.userDetails.getUserSession()!=null) {
                        sendReplyAPICall();
                    }
                }else{
                    Bitmap viewbitmap = null;
                    RelativeLayout layout = layoutContainer.getChildAt(0).findViewById(R.id.ActualView);
                    tempturboView = layout.findViewById(R.id.ivTurboView);
                    if (tempturboView.getSelectedObjectCount() > 0) {
                        tempturboView.deselectAll();
                    }
                    viewbitmap = loadBitmapFromView(layout);
                    imageBitmaps.add(viewbitmap);
                    fileName = System.currentTimeMillis() + ".jpg";
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    viewbitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), viewbitmap, fileName, null);
                    images = new File(CommonMethods.getPath(nContext,Uri.parse(newpath)));
                    sendReplyAPICall();
                }

                break;
            case R.id.ivCamera:
                selectImage();
                break;
            case R.id.ivPen:
                showTextEnterDialog(1);
                break;
            case R.id.ivAddLinks:
                showTextEnterDialog(2);
                break;
            case R.id.ivDelete:
                tempturboView.removeObject();
                break;
            case R.id.ivHome:
                startActivity(new Intent(this, HomePage.class));
                break;
            case R.id.ivAddNewPage:
                showAddNewPageDialog();
                break;
            case R.id.ivGoBack:

                containerChildPosition = containerChildPosition - 1;
                binding.ivGoForward.setVisibility(View.VISIBLE);
                hideshowOtherLayouts();
                break;
            case R.id.ivGoForward:
                containerChildPosition = containerChildPosition + 1;
                hideshowOtherLayouts();
                break;
        }
    }

    private void addView() {
        if (containerChildPosition == 0) {
            binding.ivGoBack.setVisibility(View.GONE);
            binding.ivGoForward.setVisibility(View.GONE);
        } else {
            binding.ivGoBack.setVisibility(View.VISIBLE);
        }
        containerChildPosition = containerChildPosition + 1;
        objectHashMap = new HashMap<>();
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_sample_styling_display, null);
        v.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutContainer.addView(v);


    }

    public Bitmap loadBitmapFromView(View v) {
        if (v.getMeasuredWidth() <= 0) {
            v.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            v.buildDrawingCache(true);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels,
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(dm.heightPixels,
                            View.MeasureSpec.EXACTLY));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(b);
            c.drawColor(Color.WHITE);
            c.drawBitmap(b,0,0,null);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);

            return b;
        } else {
            v.measure(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            v.buildDrawingCache(true);
            DisplayMetrics dm = getResources().getDisplayMetrics();
            v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels,
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(dm.heightPixels,
                            View.MeasureSpec.EXACTLY));
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            //v.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);

            Canvas c = new Canvas(b);
            c.drawColor(Color.WHITE);
            c.drawBitmap(b,0,0,null);
            v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
            v.draw(c);
            return b;
        }
    }

    private void showTextEnterDialog(int i) {
        final Dialog dialog = new Dialog(nContext);
        final DialogEntertextBinding dialogBinding = DialogEntertextBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());
        if (i == 1) {
            dialogBinding.tvTitle.setText(nContext.getResources().getString(R.string.enter_text));
        } else {
            dialogBinding.tvTitle.setText(nContext.getResources().getString(R.string.enter_link));
        }
        dialogBinding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (dialogBinding.edtTextToPin.getText().length() > 0) {
                        texttoPin = dialogBinding.edtTextToPin.getText().toString();
                        mBitmap = textAsBitmap(texttoPin, 20);

                        textColorUpdate = nContext.getResources().getColor(R.color.black);
                        dialog.cancel();
                        addViewOnPage(2);
                    }
                /*else{
                    if(CommonMethods.linkValidation(dialogBinding.edtTextToPin.getText().toString())) {
                        otherLinksList.add(dialogBinding.edtTextToPin.getText().toString());
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.LinkAdded), Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }else{
                        Toast.makeText(nContext, nContext.getResources().getString(R.string.please_entervalidLink), Toast.LENGTH_SHORT).show();
                    }
                }*/
            }
        });
        dialog.show();

    }

    private void showAddNewPageDialog() {
        final Dialog dialog = new Dialog(nContext);
        final DialogAddnewPageBinding dialogBinding = DialogAddnewPageBinding.inflate(getLayoutInflater());
        dialog.setContentView(dialogBinding.getRoot());

        dialogBinding.tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                if (tempturboView != null && tempturboView.getSelectedObjectCount() > 0) {
                    tempturboView.deselectAll();
                }
                newClicked = true;
                Bitmap mbitmap = loadBitmapFromView(tempContainer);
                imageBitmaps.add(mbitmap);
                addView();
                hideshowOtherLayouts();
            }
        });
        dialogBinding.tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
        dialog.show();

    }

    private void selectImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
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
                                startActivityForResult(takePictureIntent, REQUEST_CAMERA_IMAGE);
                            }
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();



        //startActivityForResult(Intent.createChooser(intent, getString(R.string.lbl_set_profile_photo)), REQUEST_IMAGE);

    }

    private Uri getCacheImagePath(String fileName) {
        File path = new File(getExternalCacheDir(), "camera");
        if (!path.exists()) path.mkdirs();
        File image = new File(path, fileName);
        return getUriForFile(StylingPhotoActivity.this, getPackageName() + ".provider", image);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    imageUri = data.getData();
                    Log.e(TAG, "onActivityResult: if part" + imageUri);
                    Intent intent = new Intent(this, ImagePickerAction.class);
                    intent.putExtra(ConstantStrings.bitmap, imageUri);
                    startActivityForResult(intent, GET_IMAGE_RESULT);
                } else {
                    imageUri = getCacheImagePath(fileName);
                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(nContext.getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, UUID.randomUUID().toString(), null);

                    //File file = new File(CommonMethods.getPath(nContext, Uri.parse(newpath)));

                    Intent intent = new Intent(this, ImagePickerAction.class);
                    intent.putExtra(ConstantStrings.bitmap, Uri.parse(newpath));
                    startActivityForResult(intent, GET_IMAGE_RESULT);

                }
            }
        }
        if (requestCode == REQUEST_CAMERA_IMAGE) {
            Bitmap rotatedBitmap = null;
            imageUri = getCacheImagePath(fileName);
            if (imageUri != null) {
                try {

                    mBitmap = MediaStore.Images.Media.getBitmap(nContext.getContentResolver(), imageUri);
                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), mBitmap, UUID.randomUUID().toString(), null);
                    Intent intent = new Intent(this, ImagePickerAction.class);
                    intent.putExtra(ConstantStrings.bitmap, Uri.parse(newpath));
                    startActivityForResult(intent, GET_IMAGE_RESULT);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /*if (imageUri != null || mBitmap != null) {

                ExifInterface ei = null;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();
                ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.e(TAG, "onActivityResult: "+imageUri );
                        ei = new ExifInterface(bs);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (ei != null) {
                    int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                            ExifInterface.ORIENTATION_NORMAL);


                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                rotatedBitmap = rotateImage(mBitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                rotatedBitmap = rotateImage(mBitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                rotatedBitmap = rotateImage(mBitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:

                            default:
                                rotatedBitmap = mBitmap;
                        }
                }*/

                /*if (rotatedBitmap != null) {

                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                    String newpath = MediaStore.Images.Media.insertImage(this.getContentResolver(), rotatedBitmap, "newBitmap", null);
                    Intent intent = new Intent(this, ImagePickerAction.class);
                    intent.putExtra(ConstantStrings.bitmap, Uri.parse(newpath));
                    startActivityForResult(intent, GET_IMAGE_RESULT);
                } else {*/

        }
        if (requestCode == GET_IMAGE_RESULT) {
            if (data != null) {
                if (data.getExtras() != null) {
                    if (data.getExtras().containsKey("paths")) {
                        Log.e(TAG, "onActivityResult: full image path" + data.getParcelableExtra("paths"));
                        File file = new File(CommonMethods.getPath(this, data.getParcelableExtra("paths")));
                        if (file != null) {

                            mBitmap = BitmapFactory.decodeFile(file.getPath());
                            addViewOnPage(1);
                        }
                    }
                    //transformedImage = data.getParcelableExtra("path");

                    //setImgBitmap=BitmapFactory.decodeFile(imageUri.getPath());
                    //File file = new File(CommonMethods.getPath(this, transformedImage));
                    else {
                        if (data != null && data.getByteArrayExtra("path") != null) {
                            byte[] byteArray = data.getByteArrayExtra("path");
                            if (byteArray != null) {
                                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                                Log.e(TAG, "onActivityResult: byteArray" + byteArray);
                                mBitmap = bmp;
                                addViewOnPage(1);
                            }
                        }
                    }
                }
            }
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    float textViewSize = 20;
    Bitmap textBitmap;

    private void addViewOnPage(int i) {


        RelativeLayout container = layoutContainer.getChildAt(containerChildPosition - 1).findViewById(R.id.ActualView);
        tempturboView = container.findViewById(R.id.ivTurboView);
        tempContainer = container;
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                binding.layoutFontSizes.setVisibility(View.GONE);
                binding.colorPallette.setVisibility(View.GONE);
                return true;
            }
        });
        if (i == 1) {

            ImageView ivImage = container.findViewById(R.id.ivImageAddOn);


            tempturboView.addObject(nContext, mBitmap);

        }
        if (i == 2) {
            textBitmap = textAsBitmap(texttoPin, 20);

            objectHashMap.put(tempturboView.addObject(nContext, textBitmap), texttoPin);


        }
        clickListeners();


    }

    private void clickListeners() {
        tempturboView.setListener(new TurboImageViewListener() {
            @Override
            public void onImageObjectSelected(MultiTouchObject object) {
                Log.e(TAG, "onImageObjectSelected: " + tempturboView.getSelectedObjectPosition());
                if (objectHashMap.containsKey(object)) {
                    binding.layoutFontSizes.setVisibility(View.VISIBLE);
                    binding.colorPallette.setVisibility(View.VISIBLE);
                    String text = getTextOfImage(object);
                    textViewSize = object.getHeight();
                    Log.e(TAG, "onImageObjectSelected: " + text);
                    Log.e(TAG, "onImageObjectSelected: " + getTextOfImage(object));
                    binding.layoutColorPallette.ibWhite.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //updateTextBitmap()
                            textColorUpdate = nContext.getResources().getColor(R.color.white);

                            Log.e(TAG, "onClick: " + text);
                            Bitmap newBitmap = updateTextBitmap(text, (object.getHeight() + object.getWidth()), textColorUpdate);

                            Log.e(TAG, "onClick: height " + object.getHeight());
                            Log.e(TAG, "onClick: width" + object.getWidth());
                            Log.e(TAG, "onClick: addition" + (object.getHeight() + object.getWidth()) / 2);
                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tempturboView.updateSelectedObject(createTextImageObject(text,textColorUpdate));

                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                objectHashMap.replace(tempturboView.addObject(nContext,newBitmap),text);
                            }
                            updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);*/
                            //tvText.setTextColor(getResources().getColor(R.color.white));
                        }
                    });
                    binding.layoutColorPallette.ibBlack.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.black);


                            Log.e(TAG, "onClick: " + text);
                            Bitmap newBitmap = updateTextBitmap(text, (object.getHeight() + object.getWidth()), textColorUpdate);
                            //tempturboView.updateSelectedObject(createTextImageObject(text,textColorUpdate));

                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //tvText.setTextColor(getResources().getColor(R.color.black));
                        }
                    });
                    binding.layoutColorPallette.ibBrown.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.brown);


                            Log.e(TAG, "onClick: " + text);
                            //tempturboView.updateSelectedObject(createTextImageObject(text,textColorUpdate));

                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //tvText.setTextColor(getResources().getColor(R.color.brown));
                        }
                    });
                    binding.layoutColorPallette.ibBlue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.blue);
                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));

                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextColor(getResources().getColor(R.color.blue));
                        }
                    });
                    binding.layoutColorPallette.ibGreen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.green);

                            Log.e(TAG, "onClick: " + text);
                            Bitmap newBitmap = updateTextBitmap(text, (object.getHeight() + object.getWidth()), textColorUpdate);
                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));
                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextColor(getResources().getColor(R.color.green));
                        }
                    });
                    binding.layoutColorPallette.ibYellow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.yellow);

                            Log.e(TAG, "onClick: " + text);
                            Bitmap newBitmap = updateTextBitmap(text, (object.getHeight() + object.getWidth()), textColorUpdate);
                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));
                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextColor(getResources().getColor(R.color.yellow));
                        }
                    });
                    binding.layoutColorPallette.ibOrange.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.orange);

                            Log.e(TAG, "onClick: " + text);
                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));

                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextColor(getResources().getColor(R.color.orange));
                        }
                    });
                    binding.layoutColorPallette.ibRed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textColorUpdate = nContext.getResources().getColor(R.color.red);

                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));
                            updateImageStringObject(createTextImageObject(text, textColorUpdate), text);
                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextColor(getResources().getColor(R.color.red));
                        }
                    });
                    binding.ibDecreaseFontSize.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            textViewSize = textViewSize - 1;
                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));

                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextSize(textViewSize);
                        }
                    });
                    binding.ibIncreaseFontSize.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            textViewSize = textViewSize + 1;
                            tempturboView.updateSelectedObject(createTextImageObject(text, textColorUpdate));

                            //updateImageStringObject(tempturboView.addObject(nContext,newBitmap),text);
                            //tvText.setTextSize(textViewSize);
                        }
                    });

                } else {
                    binding.layoutFontSizes.setVisibility(View.GONE);
                    binding.colorPallette.setVisibility(View.GONE);
                }
            }

            @Override
            public void onImageObjectDropped() {

            }

            @Override
            public void onCanvasTouched() {
                tempturboView.deselectAll();
                binding.layoutFontSizes.setVisibility(View.GONE);
                binding.colorPallette.setVisibility(View.GONE);
            }

            @Override
            public void onTextImageDoubleTapped(MultiTouchObject object) {

            }

            @Override
            public void onImageObjectDeselected(MultiTouchObject object) {

            }

            @Override
            public void onImageDelete(MultiTouchObject object) {

            }
        });
    }

    private void hideshowOtherLayouts() {

        for (int i = 0; i < containerChildPosition; i++) {
            Log.e(TAG, "hideshowOtherLayouts: childcount " + layoutContainer.getChildCount());
            layoutContainer.getChildAt(i).setVisibility(View.GONE);
        }
        //this.currentLayout = Layout;
        Log.e(TAG, "hideshowOtherLayouts:containerChildPosition " + containerChildPosition);
        if (containerChildPosition == 0 || containerChildPosition <= 0) {
            binding.ivGoBack.setVisibility(View.GONE);
        }
        if (containerChildPosition == layoutContainer.getChildCount()) {
            binding.ivGoForward.setVisibility(View.GONE);
        }
        layoutContainer.getChildAt(containerChildPosition - 1).setVisibility(View.VISIBLE);


    }


    public Bitmap textAsBitmap(String text, float textSize) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColorUpdate);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void sendReplyAPICall() {
        loadingDialog.show();
        /*String otherlinks="";
        if(otherLinksList.size()>=1) {
            otherlinks=otherLinksList.get(0);
        }

        for(int i=1;i<otherLinksList.size();i++){
            otherlinks=otherlinks+","+otherLinksList.get(i);
        }*/
        MultipartBody.Part imageParts = prepareFilePart("rep_image[]", images);

        ApiConfing.getApiClient().sendStylingPhoto(toRequestBody(clientUserId),
                imageParts,
                toRequestBody(""),
                toRequestBody(""),
                toRequestBody(userFormID),
                toRequestBody(ConstantStrings.SavedUserID)
        ).enqueue(new Callback<FormAndReviewResponse>() {
            @Override
            public void onResponse(Call<FormAndReviewResponse> call, Response<FormAndReviewResponse> response) {
                FormAndReviewResponse cResponse = response.body();
                Log.e(TAG, "onResponse: " + cResponse.getResult());
                Log.e(TAG, "onResponse: "+cResponse.getStatus() );
                if (cResponse.getStatus() == 1) {

                    Toast.makeText(nContext, nContext.getResources().getString(R.string.messageSent), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(nContext, HomePage.class).putExtra(ConstantStrings.usertype, 2));
                    finish();
                    loadingDialog.cancel();
                }else if(cResponse.getStatus()==0){
                    Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                }else{

                }

            }

            @Override
            public void onFailure(Call<FormAndReviewResponse> call, Throwable t) {
                loadingDialog.cancel();
                Toast.makeText(nContext, nContext.getResources().getString(R.string.somethingwentwrong), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }


    private Bitmap updateTextBitmap(String text, float textSize, int textColor) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }

    private void updateImageStringObject(ImageObject imageObject, String text) {
        tempturboView.updateSelectedObject(imageObject);
        objectHashMap.remove(tempturboView.getSelectedObject());
        objectHashMap.put(imageObject, text);
    }

    private ImageObject createTextImageObject(String text, int color) {
        Bitmap bitmap = textAsBitmap(text, textViewSize);
        return new ImageObject(bitmap, getResources());
    }

    private String getTextOfImage(MultiTouchObject object) {
        return objectHashMap.get(object);
    }

    @Override
    public void onBackPressed() {
        // Toast.makeText(nContext, "OnBackPressed", Toast.LENGTH_SHORT).show();
        finish();
        super.onBackPressed();
    }
}