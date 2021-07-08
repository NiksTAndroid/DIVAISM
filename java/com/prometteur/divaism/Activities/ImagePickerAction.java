package com.prometteur.divaism.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.prometteur.divaism.R;
import com.prometteur.divaism.Utils.CommonMethods;
import com.prometteur.divaism.Utils.ConstantStrings;
import com.prometteur.divaism.Utils.SomeView;
import com.prometteur.divaism.databinding.ImageAddonBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static androidx.core.content.FileProvider.getUriForFile;
import static com.prometteur.divaism.Utils.ConstantStrings.bitmap;

public class ImagePickerAction extends AppCompatActivity  {

    private static final String TAG = "ImagePickerAction";
    private Bitmap mBitmap;
    private Bitmap rotatedBitmap;
    private SomeView mSomeView;
    Bundle nBundle;
    Uri imagePath;
    Intent intent;
    Context nContext;
    public static int REQUEST_IMAGE = 121;
    public static final String INTENT_IMAGE_PICKER_OPTION = "image_picker_option";
    LinearLayout layout;
    FloatingActionButton cropButton,okButton;
    ImageView ivCrop;
    ImageView ivTempImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_picker_action);
        nContext=this;
        nBundle = getIntent().getExtras();
        if (nBundle != null) {
            if(nBundle.containsKey(bitmap) && nBundle.getParcelable(bitmap)!=null) {
                imagePath = nBundle.getParcelable(bitmap);
            }else{

            }

        }

        Log.e(TAG, "oncreate: imagepath"+imagePath );

        File file=new File(CommonMethods.getPath(this,imagePath));
        if (file != null) {

           mBitmap = BitmapFactory.decodeFile(file.getPath());
            /*try {
                mBitmap=CommonMethods.handleSamplingAndRotationBitmap(nContext,imagePath);
            } catch (IOException e) {
                Toast.makeText(nContext, "Rotation error", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }*/
        }


        cropButton=findViewById(R.id.fbCrop);
        okButton=findViewById(R.id.fbDone);
        ivCrop=findViewById(R.id.ivCrop);
        ivTempImage=findViewById(R.id.ivTempImage);
        //ivSome=findViewById(R.id.someview);
        /*if(mBitmap != null) {
            int bitmapH = mBitmap.getHeight();
            int bitmapW = mBitmap.getWidth();


            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int height = displayMetrics.heightPixels;
            final int width = displayMetrics.widthPixels;

            boolean willResizePhoto = false;
            if (bitmapH > ConstantStrings.PHOTO_MAX_HEIGHT) {
                willResizePhoto = true;
                mBitmap = scaleDown(mBitmap, ConstantStrings.PHOTO_MAX_HEIGHT, false);
            }

            if (bitmapW > ConstantStrings.PHOTO_MAX_WIDTH) {
                willResizePhoto = true;
                mBitmap = scaleDown(mBitmap, ConstantStrings.PHOTO_MAX_WIDTH, false);
            }

            if (willResizePhoto) {
                bitmapH = mBitmap.getHeight();
                bitmapW = mBitmap.getWidth();
                Log.e("TAG", "New Bitmap Size... Height: %d, Width: %d" + bitmapH + "   " + bitmapW);
            }
        }*/
        /*if(rotatedBitmap!=null) {
            ivTempImage.setImageBitmap(mBitmap);
        }else{*/

        ivTempImage.setImageBitmap(mBitmap);

        ivTempImage.setVisibility(View.VISIBLE);
        //ivSome.setVisibility(View.GONE);
        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBitmap=CommonMethods.loadBitmapFromView(ivTempImage);
                final Drawable dr = ContextCompat.getDrawable(nContext, R.drawable.ic_scissor_drawing);
                final Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
                final BitmapDrawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 75, 75, true));
                ivTempImage.setVisibility(View.GONE);
                mSomeView = new SomeView(nContext, mBitmap);
                RelativeLayout layout = findViewById(R.id.layout);
                RelativeLayout.LayoutParams lp =
                        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                /*layout.setLayoutParams(lp);
                layout.requestLayout();*/
                layout.addView(mSomeView, lp);

                mSomeView.setCursorDrawable(d);
                //mSomeView.setImageBitmap(mBitmap);
                ivCrop.setImageDrawable(d);
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setResultOk(imagePath);
            }
        });

    }
    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public void cropImage() {
        if(mBitmap!=null) {
            if (mSomeView.getHeight() > 0) {
                ImageAddonBinding ivBinding = ImageAddonBinding.inflate(getLayoutInflater());
                setContentView(ivBinding.getRoot());

                Bitmap fullScreenBitmap =
                        Bitmap.createBitmap(mSomeView.getWidth(), mSomeView.getHeight(), mBitmap.getConfig());


                Canvas canvas = new Canvas(fullScreenBitmap);

                float originalWidth = mBitmap.getWidth(), originalHeight = mBitmap.getHeight();

                Path path = new Path();
                List<Point> points = mSomeView.getPoints();
                for (int i = 0; i < points.size(); i++) {
                    path.lineTo(points.get(i).x, points.get(i).y);
                }

                // Cut out the selected portion of the image...
                Paint paint = new Paint();
                paint.setColor(Color.WHITE);

                canvas.drawPath(path, paint);
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                float scale, xTranslation = 0.0f, yTranslation = 0.0f;
                if (originalWidth > originalHeight) {

                    scale = ConstantStrings.PHOTO_MAX_HEIGHT / originalHeight;
                    xTranslation = (ConstantStrings.PHOTO_MAX_WIDTH - originalWidth * scale) / 2.0f;
                } else {

                    scale = ConstantStrings.PHOTO_MAX_WIDTH / originalWidth;
                    yTranslation = (ConstantStrings.PHOTO_MAX_HEIGHT - originalHeight * scale) / 2.0f;
                }
                Matrix transformation = new Matrix();
                transformation.postTranslate(xTranslation, yTranslation);
                transformation.preScale(scale, scale);
                Paint mpaint = new Paint();
                mpaint.setFilterBitmap(true);
                canvas.drawBitmap(mBitmap, transformation, paint);

                // Frame the cut out portion...
       /* paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10f);
        canvas.drawPath(path, paint);*/

                // Create a bitmap with just the cropped area.
                Region region = new Region();
                Region clip = new Region(0, 0, fullScreenBitmap.getWidth(), fullScreenBitmap.getHeight());
                region.setPath(path, clip);
                Rect bounds = region.getBounds();
                Bitmap croppedBitmap = null;
                if (bounds.width() > 0 && bounds.height() > 0) {
                    croppedBitmap = Bitmap.createBitmap(fullScreenBitmap, bounds.left, bounds.top,
                            bounds.width(), bounds.height());

                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                croppedBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                byte[] byteArray = bytes.toByteArray();
                setResultOk(byteArray);
            }
        }
    }





    public interface PickerOptionListener {
        void onTakeCameraSelected();

        void onChooseGallerySelected();
    }

    public static void showImagePickerOptions(Context context, final PickerOptionListener listener) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.lbl_set_profile_photo));

        // add a list
        String[] animals = {context.getString(R.string.lbl_take_camera_picture), context.getString(R.string.lbl_choose_from_gallery)};
        builder.setItems(animals, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        listener.onTakeCameraSelected();
                        break;
                    case 1:
                        listener.onChooseGallerySelected();
                        break;
                }
            }
        });
        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void setResultOk(byte[] byteArray/*Uri imagePath*/) {
        Intent intent = new Intent();
        //Log.e(TAG, "setResultOk: "+imagePath );
        intent.putExtra("path", byteArray);
        setResult(Activity.RESULT_OK, intent);
        //Toast.makeText(this, "SetResultOkPAth", Toast.LENGTH_SHORT).show();
        //layout.removeAllViews();
        finish();
    }

    private void setResultCancelled() {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResultCancelled();
        //super.onBackPressed();
    }
    public static final int REQUEST_IMAGE_CAPTURE = 0;
    String fileName;
    private void takeCameraImage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            fileName = System.currentTimeMillis() + ".jpg";
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheImagePath(fileName));
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                            }
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {

                    Uri uri = data.getData();
                    setResultOk(uri);
                    break;
                }
        }
    }
    private void setResultOk(Uri imagePath) {
        Intent intent = new Intent();
        //Log.e(TAG, "setResultOk: "+imagePath );
        intent.putExtra("paths", imagePath);
        setResult(Activity.RESULT_OK, intent);
        //layout.removeAllViews();
        //Toast.makeText(this, "SetResultOkPAths", Toast.LENGTH_SHORT).show();
        finish();
    }

        private Bitmap scaleDown(Bitmap realImage, float maxImageSize,
        boolean filter) {
            float ratio = Math.min(
                    maxImageSize / realImage.getWidth(),
                    maxImageSize / realImage.getHeight());
            int width = Math.round(ratio * realImage.getWidth());
            int height = Math.round(ratio * realImage.getHeight());

            Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                    height, filter);
            return newBitmap;
        }
}