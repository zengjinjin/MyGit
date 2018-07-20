package com.example.floatviewdemo.service;

import com.example.floatviewdemo.R;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB) public class FloatViewService extends Service {

	private static final String TAG = "FloatViewService";
	// 定义浮动窗口布局
	private LinearLayout mFloatLayout;
	private WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	private WindowManager mWindowManager;

	private ImageButton mFloatView;
	private int screenHeight;
	private int screenWidth;
	private MyCountDownTimer myCountDownTimer;

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "onCreate");
		screenHeight = ScreenParam.getInstance().height;
		screenWidth = ScreenParam.getInstance().width;
		createFloatView();
		myCountDownTimer = new MyCountDownTimer(2500, 1000);
		myCountDownTimer.start();
	}

	@SuppressWarnings("static-access")
	@SuppressLint("InflateParams")
	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;
		wmParams.y = 150;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(
				R.layout.alert_window_menu, null);
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		// 浮动窗口按钮
		mFloatView = (ImageButton) mFloatLayout
				.findViewById(R.id.alert_window_imagebtn);

		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		// 设置监听浮动窗口的触摸移动
		mFloatView.setOnTouchListener(new OnTouchListener() {

			boolean isClick;
			private int leftDistance;
			private float rawX;
			private float rawY;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.e(TAG, "ACTION_DOWN");
					mFloatLayout.setAlpha(1.0f);
					myCountDownTimer.cancel();
					
					rawX = event.getRawX();
					rawY = event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					Log.e(TAG, "ACTION_MOVE");
					// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
					int distanceX = (int) (event.getRawX()-rawX);
					int distanceY = (int) (event.getRawY()-rawY);
					leftDistance = (int) event.getRawX()
							+ mFloatView.getMeasuredWidth() / 2;
					
					wmParams.x = wmParams.x-distanceX;
					wmParams.y = wmParams.y-distanceY;
					// 刷新
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);
					rawX = event.getRawX();
					rawY = event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					Log.e(TAG, "ACTION_UP");
					myCountDownTimer.start();
					if(wmParams.x>leftDistance){
						wmParams.x = screenWidth-mFloatView.getMeasuredWidth() / 2;
					}else{
						wmParams.x = 0;
					}
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);
					break;
				}
				return false;
			}
		});

		mFloatView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AudioManager audioManager = (AudioManager) getSystemService(Service.AUDIO_SERVICE);
		        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
		                audioManager.getStreamVolume(AudioManager.STREAM_MUSIC), AudioManager.FLAG_SHOW_UI);
//				Toast.makeText(FloatViewService.this, "hello!",
//						Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
        	mFloatLayout.setAlpha(0.4f);
        }
    }

}
