package main.utilities;

import android.app.ActionBar;
import android.app.Dialog;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.view.WindowManager;

@SuppressWarnings("UnusedDeclaration")
public class ViewUtility {

    public static void setActionBarBackgroundByOffset(ActionBar actionBar, float offset) {
        float value = CustomMath.formatTheOffset(offset);
        if (value < 0.05f) {
            actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ff455a64")));
        }

    }

    public static Bitmap getScaleImage(Bitmap bitmap, int[] size) {
        return Bitmap.createScaledBitmap(bitmap, size[0], size[1], false);
    }

    public static void dialog_matchParent(Dialog dialog) {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
    }


    public static Bitmap circleCropBitmap(Bitmap bitmap) {
        if (bitmap == null)
            throw new NullPointerException("bitmap is null.");

        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circleBitmap);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return circleBitmap;
    }


}
