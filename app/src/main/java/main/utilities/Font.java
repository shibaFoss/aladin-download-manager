package main.utilities;

import android.graphics.Typeface;
import android.widget.TextView;

public class Font {

    public static void titleFont(TextView  textView){
        textView.setTypeface(Typeface.createFromAsset(textView.getContext().getAssets(), "fonts/Harabara.ttf"));
    }
}
