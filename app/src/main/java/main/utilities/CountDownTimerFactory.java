package main.utilities;

import android.os.CountDownTimer;

public class CountDownTimerFactory {

    public static CountDownTimer getCustomCountDown(int interval, int totalTime, final OnRunningListener onRunningListenerListener) {
        return new CountDownTimer(interval, totalTime) {

            @Override
            public void onTick(long intervalGap) {
                if (onRunningListenerListener != null)
                    onRunningListenerListener.onTick();
            }

            @Override
            public void onFinish() {
                if (onRunningListenerListener != null)
                    onRunningListenerListener.onFinish();
            }
        };
    }

    public interface OnRunningListener {
        void onTick();

        void onFinish();
    }
}
