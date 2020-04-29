package com.linkflow.fitt360sdk.helper;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

public class TimerHelper {
    private static final int MSG_WRITE_TIME = 10, MSG_START_TIMER = 11;
    private static TimerHelper mInstance;
    private TimerThread mTimerThread;

    public static TimerHelper getInstance() {
        if (mInstance == null) {
            mInstance = new TimerHelper();
        }
        return mInstance;
    }

    public TimerHelper() {

    }

    public TimerHelper init(Looper mainLooper, final Listener listener) {
        Handler mainHandler = new Handler(mainLooper) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MSG_WRITE_TIME) {
                    listener.updateTime((String) msg.obj);
                }
            }
        };

        mTimerThread = new TimerThread("timer_thread", mainHandler);
        mTimerThread.start();
        return mInstance;
    }

    public String getCurrentTime() {
        return mTimerThread.getCurrentTime();
    }

    public void start() {
        mTimerThread.doStart();
    }

    public void stop() {
        mTimerThread.doStop();
    }

    public void destroy() {
        mTimerThread.doStop();
        mTimerThread.quit();
    }

    private class TimerThread extends HandlerThread {
        private DecimalFormat mFormat = new DecimalFormat("00");

        private Handler mMainHandler, mHandler;

        private String mCurrentTime = "00:00:00";
        private boolean mIsStop;
        private long mTime = 0;

        TimerThread(String name, Handler mainHandler) {
            super(name);
            mMainHandler = mainHandler;
        }

        void doStart() {
            mIsStop = false;
            mHandler.sendEmptyMessage(MSG_START_TIMER);
        }

        public void doStop() {
            mIsStop = true;
        }

        public String getCurrentTime() {
            return mCurrentTime;
        }

        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            mHandler = new Handler(this.getLooper()) {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    if (msg.what == MSG_START_TIMER) {
                        while (!mIsStop) {
                            mCurrentTime = makeTime(mTime, 0);
                            Message message = new Message();
                            message.what = MSG_WRITE_TIME;
                            message.obj = mCurrentTime;
                            mMainHandler.sendMessage(message);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mTime++;
                        }
                        mIsStop = false;
                        mTime = 0;
                    }
                }
            };
        }

        private String makeTime(long value, int cnt) {
            if (value / 60 > 0) {
                return makeTime(value / 60, ++cnt) + ":" + mFormat.format(value % 60);
            } else {
                String fill = "";
                if (cnt == 0) {
                    fill = "00:00:";
                } else if (cnt == 1) {
                    fill = "00:";
                }
                return fill + mFormat.format(value);
            }
        }
    }

    public interface Listener {
        void updateTime(String time);
    }
}
