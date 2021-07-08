package com.prometteur.divaism.turboimageview;


public interface TurboImageViewListener {
    void onImageObjectSelected(MultiTouchObject object);

    void onImageObjectDropped();

    void onCanvasTouched();

    void onTextImageDoubleTapped(MultiTouchObject object);

    void onImageObjectDeselected(MultiTouchObject object);

    void onImageDelete(MultiTouchObject object);
}
