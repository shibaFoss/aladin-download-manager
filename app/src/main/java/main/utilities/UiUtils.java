package main.utilities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class UiUtils {

    public static void fontBoldEffect(TextView textView, Context context) {
        //textView.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/BEBAS___.ttf"));
    }

    public static void fontBoldEffect(TextView[] textViews, Context context) {
        for (TextView textView : textViews) {
            fontBoldEffect(textView, context);
        }
    }

    public static void fillParent(Dialog dialog) {
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(params);
    }

    public static Dialog generateNewDialog(Context activity_context, int layout) {
        final Dialog dialog = new Dialog(activity_context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.setContentView(layout);
        fillParent(dialog);
        return dialog;
    }

    @Deprecated
    public static void enableBlueEffectOn(Dialog dialog) {
        WindowManager.LayoutParams attributes = dialog.getWindow().getAttributes();
        attributes.dimAmount = 0.0f;
        dialog.getWindow().setAttributes(attributes);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    }


    public static void executeFadInAnimationOn(View view) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", .0f, 1f);
        fadeIn.setDuration(200);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn);
        animatorSet.start();


    }

    public static void executeFadInAnimationOn(View view, int animationTime) {
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", .0f, 1f);
        fadeIn.setDuration(animationTime);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(fadeIn);
        animatorSet.start();
    }

    public static void executeFadOutAnimationOn(final View view) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, .0f);
        fadeOut.setDuration(200);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
            }
        });
        animatorSet.play(fadeOut);
        animatorSet.start();
    }


    public static void executeFadOutAnimationOn(final View view, AnimatorListenerAdapter animatorListener) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, .0f);
        fadeOut.setDuration(200);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);
        animatorSet.play(fadeOut);
        animatorSet.start();
    }

    public static void executeFadOutAnimationOn(final View view, int animationTime, AnimatorListenerAdapter animatorListener) {
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(view, "alpha", 1f, .0f);
        fadeOut.setDuration(animationTime);
        final AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(animatorListener);
        animatorSet.play(fadeOut);
        animatorSet.start();
    }


}
