package com.salud.admin.administradorcubrebocas;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by FPAZOS390 on 15/12/2015.
 */
public class FontManager {

    public static final String ROOT = "fonts/",
            FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }


}