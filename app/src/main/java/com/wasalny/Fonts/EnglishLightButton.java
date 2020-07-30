package com.wasalny.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class EnglishLightButton extends android.support.v7.widget.AppCompatButton {
    public EnglishLightButton(Context context) {
        super(context);
    }

    public EnglishLightButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "Cairo-SemiBold.ttf"));


    }
}
