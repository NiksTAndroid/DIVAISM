package com.prometteur.divaism.turboimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;


public class TurboImageView extends View implements MultiTouchObjectCanvas<MultiTouchObject> {
    private static final String TAG = "TurboImageView";

    private ArrayList<MultiTouchObject> mImages = new ArrayList<>();
    private MultiTouchController<MultiTouchObject> multiTouchController;

    private PointInfo currTouchPoint;

    private static final int UI_MODE_ROTATE = 1;
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    private static final int mUIMode = UI_MODE_ROTATE;

    private boolean isDeleteMode = false;

    private TurboImageViewListener listener;
    private int objectBorderColor = MultiTouchObject.DEFAULT_BORDER_COLOR;
    private int objectDeleteBorderColor = MultiTouchObject.DEFAULT_DELETE_BORDER_COLOR;

    private boolean selectOnObjectAdded = true;

    public TurboImageView(Context context) {
        this(context, null);
    }

    public TurboImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TurboImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setBackgroundColor(Color.TRANSPARENT);
        multiTouchController = new MultiTouchController<>(getContext(), this);
        multiTouchController.setOnDoubleTapListener(new MultiTouchController.OnDoubleTapListener() {
            @Override
            public void onDoubleTap() {
                //Timber.d("OnDoubleTapListener... Has Selected Object: %s", getSelectedObject());
                if(listener != null) {
                    final MultiTouchObject doubleTappedObject = getSelectedObject();
                    if(doubleTappedObject != null) {
                        listener.onTextImageDoubleTapped(doubleTappedObject);
                    }
                }
            }
        });
        multiTouchController.setOnDeleteListener(new MultiTouchController.OnDeleteListener() {
            @Override
            public void onDelete(MultiTouchObject multiTouchObject) {
                if(listener != null) {
                    listener.onImageDelete(multiTouchObject);
                }
            }
        });
        currTouchPoint = new PointInfo();
    }

    public ImageObject addObject(Context context, int resourceId) {
        ImageObject imageObject = new ImageObject(resourceId, context.getResources());
        return addObject(context, imageObject);
    }

    public ImageObject addObject(Context context, Drawable drawable) {
        ImageObject imageObject = new ImageObject(drawable, context.getResources());
        return addObject(context, imageObject);
    }

    public ImageObject addObject(Context context, Bitmap bitmap) {
        ImageObject imageObject = new ImageObject(bitmap, context.getResources());
        return addObject(context, imageObject);
    }

    private ImageObject addObject(Context context, ImageObject imageObject) {
        deselectAll();

        imageObject.setSelected(selectOnObjectAdded);
        imageObject.setBorderColor(objectBorderColor);
        mImages.add(imageObject);

        float cx = getX() + getWidth() / 2;
        float cy = getY() + getHeight() / 2;
        mImages.get(mImages.size() - 1).init(context, cx, cy);

        invalidate();
        return imageObject;
    }


    public void setObjectSelectedBorderColor(int borderColor) {
        this.objectBorderColor = borderColor;
        for (MultiTouchObject imageObject : mImages) {
            imageObject.setBorderColor(borderColor);
        }
        invalidate();
    }

    public int getObjectSelectedBorderColor() {
        return this.objectBorderColor;
    }

    public void setDeleteMode(boolean deleteMode) {
        isDeleteMode = deleteMode;
    }

    public boolean isDeleteMode() {
        return isDeleteMode;
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (MultiTouchObject imageObject : mImages) {
            imageObject.draw(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return multiTouchController.onTouchEvent(event);
    }

    /**
     * Get the image that is under the single-touch point, or return null
     * (canceling the drag op) if none
     */
    public MultiTouchObject getDraggableObjectAtPoint(PointInfo touchPoint) {
        float x = touchPoint.getX();
        float y = touchPoint.getY();

        for (int i = mImages.size() - 1; i >= 0; i--) {
            ImageObject imageObject = (ImageObject) mImages.get(i);
            if (imageObject.containsPoint(x, y)) {
                return imageObject;
            }
        }
        return null;
    }

    /**
     * Get the image that is under the single-touch point, or return null
     * (canceling the drag op) if none
     */
    public MultiTouchObject getDeletableObjectAtPoint(PointInfo touchPoint) {
        float x = touchPoint.getX();
        float y = touchPoint.getY();

        //Timber.d("Touch PointX: %f, PointY: %f", x, y);

        for (int i = mImages.size() - 1; i >= 0; i--) {
            ImageObject imageObject = (ImageObject) mImages.get(i);
            boolean objectDeletable = imageObject.containsDeleteButtonCoords(x, y);
            //Timber.d("Object Deletable: %s", objectDeletable);
            if (objectDeletable) {
                return imageObject;
            }
        }
        return null;
    }


    /**
     * Select an object for dragging. Called whenever an object is found to be
     * under the point (non-null is returned by getDraggableObjectAtPoint()) and
     * a drag operation is starting. Called with null when drag op ends.
     */
    public void selectObject(MultiTouchObject multiTouchObject, PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        if (multiTouchObject != null) {
            // Move image to the top of the stack when selected
            mImages.remove(multiTouchObject);
            mImages.add(multiTouchObject);
            if (listener != null) {
                listener.onImageObjectSelected(multiTouchObject);
            }
        } else {
            // Called with multiTouchObject == null when drag stops.
            if (listener != null) {
                listener.onImageObjectDropped();
            }
        }
        invalidate();
    }

    @Override
    public void deselectAll() {
        for (MultiTouchObject imageObject : mImages) {
            if(imageObject.isSelected()) {
                imageObject.setSelected(false);
                imageObject.showDeleteButton(false);
                if (listener != null) {
                    listener.onImageObjectDeselected(imageObject);
                }
            }
        }
        invalidate();
    }

    @Override
    public void canvasTouched() {
        if (listener != null) {
            listener.onCanvasTouched();
        }
    }

    public boolean removeObject() {
        boolean deleted = false;
        Iterator<MultiTouchObject> iterator = mImages.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().isSelected()) {
                iterator.remove();
                deleted = true;
            }
        }

        invalidate();
        return deleted;
    }

    public boolean removeObject(MultiTouchObject multiTouchObject) {
        boolean deleted = mImages.remove(multiTouchObject);
        invalidate();
        return deleted;
    }

    public MultiTouchObject getSelectedObject() {
        Iterator<MultiTouchObject> iterator = mImages.iterator();
        while (iterator.hasNext()) {
            MultiTouchObject multiTouchObject = iterator.next();
            if (multiTouchObject.isSelected()) {
                return multiTouchObject;
            }
        }
        return null;
    }
    public int getSelectedObjectPosition() {
        Iterator<MultiTouchObject> iterator = mImages.iterator();
        while (iterator.hasNext()) {
            MultiTouchObject multiTouchObject = iterator.next();
            if (multiTouchObject.isSelected()) {
                return mImages.indexOf(multiTouchObject);
            }
        }
        return 0;
    }

    public void selectAllObjects(boolean showDeleteButton) {
        Iterator<MultiTouchObject> iterator = mImages.iterator();
        while (iterator.hasNext()) {
            final MultiTouchObject multiTouchObject = iterator.next();
            multiTouchObject.setSelectedForDeletion(true);
            multiTouchObject.showDeleteButton(showDeleteButton);
        }
        invalidate();
    }

    public void rotate(MultiTouchObject imageObject) {
        MultiTouchObject currentObject = getSelectedObject();
        if(currentObject != null) {
            currentObject.angle += 1.0f;
        }
        invalidate();
    }

    public MultiTouchObject updateSelectedObject(MultiTouchObject imageObject) {
        MultiTouchObject currentObject = getSelectedObject();
        if(currentObject != null) {
            final int index = mImages.indexOf(currentObject);

            imageObject.setSelected(selectOnObjectAdded);
            imageObject.setBorderColor(objectBorderColor);

            float cx = currentObject.getCenterX();
            float cy = currentObject.getCenterY();
            float scaleX = currentObject.getScaleX();
            float scaleY = currentObject.getScaleY();
            float angle = currentObject.getAngle();

            imageObject.init(getContext(), cx, cy);
            imageObject.setPos(cx, cy, scaleX, scaleY, angle);
            imageObject.setSelected(true);

            //Timber.d("Index of current object: %d", index);
            mImages.set(index, imageObject);
            invalidate();
            return imageObject;
        }
        return null;
    }

    public void removeAllObjects() {
        mImages.clear();
        invalidate();
    }

    public int getSelectedObjectCount() {
        int count = 0;
        for (MultiTouchObject imageObject : mImages) {
            if (imageObject.isSelected()) {
                count++;
            }
        }
        return count;
    }

    public int getObjectCount() {
        return mImages.size();
    }

    public void setListener(TurboImageViewListener turboImageViewListener) {
        this.listener = turboImageViewListener;
    }

    /**
     * Get the current position and scale of the selected image. Called whenever
     * a drag starts or is reset.
     */
    public void getPositionAndScale(MultiTouchObject multiTouchObject, PositionAndScale objPosAndScaleOut) {
        objPosAndScaleOut.set(multiTouchObject.getCenterX(), multiTouchObject.getCenterY(),
            (mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
            (multiTouchObject.getScaleX() + multiTouchObject.getScaleY()) / 2,
            (mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, multiTouchObject.getScaleX(),
            multiTouchObject.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
            multiTouchObject.getAngle());
    }

    /** Set the position and scale of the dragged/stretched image. */
    public boolean setPositionAndScale(MultiTouchObject multiTouchObject,
                                       PositionAndScale newImgPosAndScale, PointInfo touchPoint) {
        currTouchPoint.set(touchPoint);
        boolean moved = multiTouchObject.setPos(newImgPosAndScale);
        if (moved) {
            invalidate();
        }
        return moved;
    }

    public boolean pointInObjectGrabArea(PointInfo touchPoint, MultiTouchObject multiTouchObject) {
        return false;
    }

    public boolean isSelectOnObjectAdded() {
        return selectOnObjectAdded;
    }

    public void setSelectOnObjectAdded(boolean selectOnObjectAdded) {
        this.selectOnObjectAdded = selectOnObjectAdded;
    }

    public void setFlippedHorizontallySelectedObject(boolean flipped) {
        for (MultiTouchObject imageObject : mImages) {
            if (imageObject.isSelected()) {
                imageObject.setFlippedHorizontally(flipped);
            }
        }
        invalidate();
    }

    public boolean isFlippedHorizontallySelectedObject() {
        for (MultiTouchObject imageObject : mImages) {
            if (imageObject.isSelected()) {
                return imageObject.isFlippedHorizontally();
            }
        }
        return false;
    }

    public void toggleFlippedHorizontallySelectedObject() {
        for (MultiTouchObject imageObject : mImages) {
            if (imageObject.isSelected()) {
                imageObject.setFlippedHorizontally(!imageObject.isFlippedHorizontally());
            }
        }
        invalidate();
    }
}
