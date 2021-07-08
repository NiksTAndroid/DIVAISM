package com.prometteur.divaism.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.prometteur.divaism.R;


public class CustomTextView extends TextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCustomTextView(context,attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCustomTextView(context,attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCustomTextView(context,attrs);
    }

   /* private void initCustomTextView(Context context) {

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Light.otf");
//            Typeface typeface = Typeface.createFromFile("font/poppins_light.ttf");//.createFromAsset(context.getAssets(), "poppins_light.ttf");
        setTypeface(typeface);
//        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "font/poppins_light.ttf");
//        Typeface typeface = FontCache.getTypeface("Poppins-Light.otf", context);

    }*/


    private void initCustomTextView(Context context, AttributeSet attrs) {


        try {


            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.CustomeFont,

                    0, 0);

            try {
                Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ARIAL.TTF");
                setTypeface(typeface);
                int value = a.getInt(R.styleable.CustomeFont_textFont, 0);

                switch (value) {
                    case 0:
                        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/ARIAL.TTF");
                        setTypeface(typeface);
                        break;
                    /*case 1:
                        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-Light.otf");
                        setTypeface(typeface1);
                        break;
                    case 2:
                        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/Poppins-SemiBold.otf");
                        setTypeface(typeface2);
                        break;*/
                }

            } finally {
                a.recycle();
            }


        } catch (Exception e) {
        }

    }

}