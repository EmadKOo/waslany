package com.wasalny.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class Comforta extends android.support.v7.widget.AppCompatTextView {
    public Comforta(Context context) {
        super(context);
    }

    public Comforta(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface(Typeface.createFromAsset(context.getAssets(), "Comfortaa-SemiBold.ttf"));


    }
}
