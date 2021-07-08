package com.prometteur.divaism.turboimageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.prometteur.divaism.R;


public class ImageObject extends MultiTouchObject {

    private static final double INITIAL_SCALE_FACTOR = 0.15;
    private final int DELETE_BUTTON_WIDTH = 50;
    private final int DELETE_BUTTON_HEIGHT = 50;

    private final int DELETE_BUTTON_WIDTH_TAP_AREA = DELETE_BUTTON_WIDTH + 10;
    private final int DELETE_BUTTON_HEIGHT_TAP_AREA = DELETE_BUTTON_HEIGHT + 10;

    private transient Drawable drawable;
    private transient Bitmap deleteBitmap;
    private Rect deleteBitmapRect;

    public ImageObject(int resourceId, Resources res) {
        super(res);
        drawable = res.getDrawable(resourceId);
        this.deleteBitmap = ((BitmapDrawable)res.getDrawable(R.drawable.ic_close_search, null)).getBitmap();
        final BitmapDrawable d = new BitmapDrawable(res, Bitmap.createScaledBitmap(deleteBitmap, DELETE_BUTTON_WIDTH, DELETE_BUTTON_HEIGHT, true));
        this.deleteBitmap = d.getBitmap();
        initPaint();
    }

    public ImageObject(Drawable drawable, Resources res) {
        super(res);
        this.drawable = drawable;
        this.deleteBitmap = ((BitmapDrawable)res.getDrawable(R.drawable.ic_close_search, null)).getBitmap();
        final BitmapDrawable d = new BitmapDrawable(res, Bitmap.createScaledBitmap(deleteBitmap, DELETE_BUTTON_WIDTH, DELETE_BUTTON_HEIGHT, true));
        this.deleteBitmap = d.getBitmap();
        initPaint();
    }

    public ImageObject(Bitmap bitmap, Resources res) {
        super(res);
        this.drawable = new BitmapDrawable(res, bitmap);
        this.deleteBitmap = ((BitmapDrawable)res.getDrawable(R.drawable.ic_close_search, null)).getBitmap();
        final BitmapDrawable d = new BitmapDrawable(res, Bitmap.createScaledBitmap(deleteBitmap, DELETE_BUTTON_WIDTH, DELETE_BUTTON_HEIGHT, true));
        this.deleteBitmap = d.getBitmap();
        initPaint();
    }

    public void initPaint() {
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(borderColor);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(3.0f);
        borderPaint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
    }

    private float dx = 0;
    private float dy = 0;
    private Rect r;
    private float degrees = 0;
    public void draw(Canvas canvas) {
        canvas.save();

        dx = (maxX + minX) / 2;
        dy = (maxY + minY) / 2;

        drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
        if (flippedHorizontally) {
            canvas.scale(-1f, 1f, dx, dy);
        }
        //canvas.translate(dx, dy);
        if (flippedHorizontally) {
            degrees = -angle * 180.0f / (float) Math.PI;
        } else {
            degrees = angle * 180.0f / (float) Math.PI;
        }
        canvas.rotate(degrees, dx, dy);
        //canvas.translate(-dx, -dy);

        drawable.draw(canvas);

        if (isLatestSelected) {
            canvas.drawRect((int) minX, (int) minY, (int) maxX, (int) maxY, borderPaint);
            /*Ready to show an X button to delete the view but impossible to detect when that X button is touched*/
            //canvas.drawBitmap(cancelBitmap, minX - (cancelBitmap.getWidth() / 2), minY - (cancelBitmap.getHeight() / 2), new Paint());
        }

        if(showDeleteButton && deleteBitmap != null) {
            deleteBitmapRect = new Rect((int)minX, (int)minY, (int) minX + deleteBitmap.getWidth(), (int) minY + deleteBitmap.getHeight());
            canvas.drawBitmap(deleteBitmap, null, deleteBitmapRect, null);
            //canvas.drawBitmap(deleteBitmap, minX, minY, null);
        }

        r = canvas.getClipBounds();
        canvas.restore();
    }

    /**
     * Called by activity's onPause() method to free memory used for loading the images
     */
    @Override
    public void unload() {
        this.drawable = null;
    }

    /** Called by activity's onResume() method to init the images */
    @SuppressWarnings("deprecation")
    @Override
    public void init(Context context, float startMidX, float startMidY) {
        Resources res = context.getResources();
        init(res);

        this.startMidX = startMidX;
        this.startMidY = startMidY;

        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();

        float centerX;
        float centerY;
        float scaleX;
        float scaleY;
        float angle;
        if (firstLoad) {
            centerX = startMidX;
            centerY = startMidY;

            float scaleFactor = (float) (Math.max(displayWidth, displayHeight) /
                (float) Math.max(width, height) * INITIAL_SCALE_FACTOR);
            scaleX = scaleY = scaleFactor;
            angle = 0.0f;

            firstLoad = false;
        } else {
            centerX = this.centerX;
            centerY = this.centerY;
            scaleX = this.scaleX;
            scaleY = this.scaleY;
            angle = this.angle;
        }
        setPos(centerX, centerY, scaleX, scaleY, angle);
    }

    public boolean containsDeleteButtonCoords(float touchX, float touchY) {
        //Timber.d("TouchX: %f, TouchY: %f MinX: %f MinY: %f dX: %f dY: %f", touchX, touchY, minX, minY, dx, dy);
        int top = deleteBitmapRect.top;
        int left = deleteBitmapRect.left;

        double x1 = left - centerX;
        double y1 = top - centerY;
        double nX = centerX + x1 * Math.cos(angle) - y1 * Math.sin(angle);
        double nY = centerY + y1 * Math.cos(angle) + x1 * Math.sin(angle);


        float touchXOffset = touchX - 10;
        float touchYOffset = touchY - 10;

        boolean isDeletable = false;
        float localDegrees = degrees % 360;
        if(localDegrees < 0) {
            localDegrees = 360 + localDegrees;
        }

        /*final RectF deleteCopy = new RectF(deleteBitmapRect);
        Matrix m = new Matrix();
        m.setRotate(localDegrees, deleteBitmapRect.centerX(), deleteBitmapRect.centerY());
        m.mapRect(deleteCopy);

        Timber.d("RectF x: %f, y: %f, width: %f, height: %f", deleteCopy.left, deleteCopy.top, deleteCopy.width(), deleteCopy.height());

        if(deleteCopy.contains(touchX, touchY)) {
            isDeletable = true;
        }*/

        if(localDegrees >= 0 && localDegrees < 90) {
            if(localDegrees >= 15 && localDegrees <= 60) {
                isDeletable = (touchXOffset >= (nX - (DELETE_BUTTON_WIDTH_TAP_AREA / 2)) && touchXOffset <= (nX + (DELETE_BUTTON_WIDTH_TAP_AREA / 2)) && touchYOffset >= nY && touchYOffset <= (nY + DELETE_BUTTON_HEIGHT_TAP_AREA));
            } else if(localDegrees > 60) {
                isDeletable = (touchXOffset >= (nX - DELETE_BUTTON_WIDTH_TAP_AREA) && touchXOffset <= (nX + DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= nY && touchYOffset <= (nY + DELETE_BUTTON_HEIGHT_TAP_AREA));
            } else {
                // Original formula!!!
                isDeletable = (touchXOffset >= nX && touchXOffset <= (nX + DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= nY && touchYOffset <= (nY + DELETE_BUTTON_HEIGHT_TAP_AREA));
            }
        } else if (localDegrees >= 90 && localDegrees < 180) {
            if(localDegrees > 135) {
                isDeletable = (touchXOffset >= (nX - DELETE_BUTTON_WIDTH_TAP_AREA) && touchXOffset <= (nX + DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= (nY-DELETE_BUTTON_HEIGHT_TAP_AREA) && touchYOffset <= (nY + DELETE_BUTTON_HEIGHT_TAP_AREA));
            } else {
                isDeletable = (touchXOffset >= (nX - DELETE_BUTTON_WIDTH_TAP_AREA) && touchXOffset <= (nX + DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= nY && touchYOffset <= (nY + DELETE_BUTTON_HEIGHT_TAP_AREA));
            }
        } else if (localDegrees > 180 && localDegrees < 270) {
            if(localDegrees > 225) {
                isDeletable = (touchXOffset >= (nX-DELETE_BUTTON_WIDTH_TAP_AREA) && touchXOffset <= (nX+DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= (nY-DELETE_BUTTON_HEIGHT_TAP_AREA) && touchYOffset <= (nY+DELETE_BUTTON_HEIGHT_TAP_AREA));
            } else {
                isDeletable = (touchXOffset >= nX && touchXOffset <= (nX+DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= nY && touchYOffset <= (nY+DELETE_BUTTON_HEIGHT_TAP_AREA));
            }
        } else if (localDegrees > 270 && localDegrees <= 360) {
            isDeletable = (touchXOffset >= nX && touchXOffset <= (nX+DELETE_BUTTON_WIDTH_TAP_AREA) && touchYOffset >= (nY-DELETE_BUTTON_HEIGHT_TAP_AREA) && touchYOffset <= (nY+DELETE_BUTTON_HEIGHT_TAP_AREA));
        }

        return isDeletable;
    }
}
