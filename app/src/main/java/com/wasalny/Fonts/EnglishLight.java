package com.wasalny.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class EnglishLight extends android.support.v7.widget.AppCompatTextView {
    public EnglishLight(Context context) {
        super(context);
    }

    public EnglishLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "english_light.TTF"));


    }
}
