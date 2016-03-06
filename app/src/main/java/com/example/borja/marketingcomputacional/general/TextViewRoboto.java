package com.example.borja.marketingcomputacional.general;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by Borja on 06/03/2016.
 */
public class TextViewRoboto extends TextView {

    public TextViewRoboto(Context context) {
        super(context);

        Typeface font = Typeface.createFromAsset(context.getAssets(), "Roboto-Regular.ttf");
        setTypeface(font);
    }
}
