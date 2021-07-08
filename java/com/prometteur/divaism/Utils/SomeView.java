package com.prometteur.divaism.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatViewInflater;
import androidx.appcompat.widget.AppCompatImageView;

import com.prometteur.divaism.Activities.ImagePickerAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SomeView extends View implements View.OnTouchListener {
    private Paint paint;
    private List<Point> points;

    int DIST = 2;
    boolean flgPathDraw = true;

    Point mfirstpoint = null;
    boolean bfirstpoint = false;

    Point mlastpoint = null;

    Bitmap bitmap;

    Context mContext;
    Rect canvasRect=new Rect();


    public SomeView(Context c, Bitmap bitmap) {
        super(c);

        mContext = c;
        this.bitmap = bitmap;
        /*if (bitmap != null) {
            int bitmapH = bitmap.getHeight();
            int bitmapW = bitmap.getWidth();


            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            final int height = displayMetrics.heightPixels;
            final int width = displayMetrics.widthPixels;

            boolean willResizePhoto = false;
            if (bitmapH > ConstantStrings.PHOTO_MAX_HEIGHT) {
                willResizePhoto = true;
                bitmap = scaleDown(bitmap, ConstantStrings.PHOTO_MAX_HEIGHT, false);
            }

            if (bitmapW > ConstantStrings.PHOTO_MAX_WIDTH) {
                willResizePhoto = true;
                bitmap = scaleDown(bitmap, ConstantStrings.PHOTO_MAX_WIDTH, false);
            }

            if (willResizePhoto) {
                bitmapH = bitmap.getHeight();
                bitmapW = bitmap.getWidth();
                Log.e("TAG", "New Bitmap Size... Height: %d, Width: %d" + bitmapH + "   " + bitmapW);
            }

            Log.e("TAG", "Screen Size... Height: %d, Width: %d" + height + "  " + width);
        }*/

        setFocusable(true);
        setFocusableInTouchMode(true);
        //setScaleType(ScaleType.FIT_XY);
        //setImageBitmap(bitmap);
        ViewGroup.LayoutParams params =new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        requestLayout();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);


        this.setOnTouchListener(this);
        points = new ArrayList<Point>();

        bfirstpoint = false;
    }

    public SomeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setFocusable(true);
        setFocusableInTouchMode(true);

        //setScaleType(ScaleType.FIT_XY);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);

        points = new ArrayList<Point>();
        bfirstpoint = false;

        this.setOnTouchListener(this);
    }

    public void onDraw(Canvas canvas) {

            /*Rect dest = new Rect(0, 0, getWidth(), getHeight());  

     paint.setFilterBitmap(true); canvas.drawBitmap(bitmap, null, dest, paint);*/
        if (bitmap != null) {
            //canvas.setBitmap(bitmap);
         float originalWidth = bitmap.getWidth(), originalHeight = bitmap.getHeight();

         float scale, xTranslation = 0.0f, yTranslation = 0.0f;
         if (originalWidth > originalHeight) {
             scale =  ConstantStrings.PHOTO_MAX_HEIGHT/originalHeight;
             xTranslation = (ConstantStrings.PHOTO_MAX_WIDTH - originalWidth * scale)/2.0f;
         }
         else {
             scale = ConstantStrings.PHOTO_MAX_WIDTH / originalWidth;
             yTranslation = (ConstantStrings.PHOTO_MAX_HEIGHT - originalHeight * scale)/2.0f;
         }
         Matrix transformation = new Matrix();
         transformation.postTranslate(xTranslation, yTranslation);
         transformation.preScale(scale, scale);
         Paint mpaint = new Paint();
         mpaint.setFilterBitmap(true);
         canvas.drawBitmap(bitmap, transformation, mpaint);
         canvasRect=canvas.getClipBounds();
            //canvas.drawBitmap(bitmap, 0, 0, null);

            Path path = new Path();
            boolean first = true;

            for (int i = 0; i < points.size(); i += 2) {
                Point point = points.get(i);
                if (first) {
                    first = false;
                    path.moveTo(point.x, point.y);
                } else if (i < points.size() - 1) {
                    Point next = points.get(i + 1);
                    path.quadTo(point.x, point.y, next.x, next.y);
                } else {
                    mlastpoint = points.get(i);
                    path.lineTo(point.x, point.y);
                }
            }
            canvas.drawPath(path, paint);


            if (cursorBitmap != null) {
                canvas.drawBitmap(cursorBitmap, cursorX, cursorY, null);
            }
        }

    }

    public boolean onTouch(View view, MotionEvent event) {

        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        cursorTouched = true;
        cursorX = point.x - cursorOffsetX;
        cursorY = point.y - cursorOffsetY;

        /**************************************************/

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if(!flgPathDraw && cursorTouched) {
                resetView();
                flgPathDraw = true;
            }
        }

        final Point cursorPoint = new Point();
        /*if(cursorBitmap != null) {
            cursorPoint.x = cursorX - CURSOR_POINTER_OFFSET;
            cursorPoint.y = cursorY - CURSOR_POINTER_OFFSET;
        } else {
            cursorPoint.x = point.x;
            cursorPoint.y = point.y;
        }*/
        cursorPoint.x = point.x - cursorOffsetX;
        cursorPoint.y = point.y - cursorOffsetY;

        if (flgPathDraw) {
            if (bfirstpoint) {
                cursorTouched = true;
                if (comparepoint(mfirstpoint, point)) {
                    // points.add(point);
                    points.add(mfirstpoint);
                    flgPathDraw = false;
                    showcropdialog();
                } else {
                    points.add(cursorPoint);
                }
            } else {
                points.add(cursorPoint);
            }

            if (!bfirstpoint) {
                mfirstpoint = cursorPoint;
                bfirstpoint = true;
            }
        }

        invalidate();

        if (event.getAction() == MotionEvent.ACTION_UP) {
            HashSet<Point> uniquePoints = new HashSet<>(points);
            for(Point pp: points) {
            }
            if(uniquePoints.size() == 1) {
                resetView();
            }
            mlastpoint = cursorPoint;
            //cursorTouched = false;

            if(cursorX>canvasRect.width() || cursorY>canvasRect.height()){
                Toast.makeText(mContext, "Width greater", Toast.LENGTH_SHORT).show();
                resetView();
                //return false;
            }


            //if (flgPathDraw) {
            //if (points.size() > 12) {
            if (!comparepoint(mfirstpoint, mlastpoint)) {
                flgPathDraw = false;
                //points.add(mfirstpoint);
                if(!flgPathDraw && cursorTouched) {
                    flgPathDraw = false;
                    points.add(mfirstpoint);
                    showcropdialog();
                }
            }
            //}
            //}
        }

        return true;
    }

    private boolean comparepoint(Point first, Point current) {
        int left_range_x = (int) (current.x - 3);
        int left_range_y = (int) (current.y - 3);

        int right_range_x = (int) (current.x + 3);
        int right_range_y = (int) (current.y + 3);

        if ((left_range_x < first.x && first.x < right_range_x)
                && (left_range_y < first.y && first.y < right_range_y)) {
            if (points.size() < 10) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }

    }

    public void fillinPartofPath() {
        Point point = new Point();
        point.x = points.get(0).x;
        point.y = points.get(0).y;

        points.add(point);
        invalidate();
    }

    public void resetView() {
        points.clear();
        paint.setColor(Color.WHITE);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        paint.setStrokeWidth(5);
        paint.setColor(Color.RED);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        points = new ArrayList<Point>();
        bfirstpoint = false;

        flgPathDraw = true;
        invalidate();
    }

    private void showcropdialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        ((ImagePickerAction) mContext).cropImage();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                            /*// No button clicked  

     intent = new Intent(mContext, DisplayCropActivity.class); intent.putExtra("crop", false); mContext.startActivity(intent);  
     bfirstpoint = false;*/
                        resetView();

                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Do you Want to save Crop or Non-crop image?")
                .setPositiveButton("Crop", dialogClickListener)
                .setNegativeButton("Non-crop", dialogClickListener).show()
                .setCancelable(false);
    }

    public List<Point> getPoints() {
        return points;
    }

    private Bitmap cursorBitmap;
    private int cursorX = 5, cursorY = 5;
    private int cursorpointerX = 250, cursorpointerY = 250;
    private int cursorW = 0, cursorH = 0;
    private int cursorOffsetX = 5, cursorOffsetY = 5;
    private boolean cursorTouched = false;

    public void setCursorDrawable(@Nullable Drawable drawable) {

        if (drawable != null) {
            cursorBitmap = ((BitmapDrawable) drawable).getBitmap();
            if (cursorBitmap != null) {
                cursorW = cursorBitmap.getWidth();
                cursorH = cursorBitmap.getHeight();

            }
        }
    }

    /*@Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setScaleType(ScaleType.FIT_XY);
        this.bitmap=bm;

    }*/

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