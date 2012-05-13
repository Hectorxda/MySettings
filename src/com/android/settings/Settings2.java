package com.android.settings;

import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings2 extends ActivityGroup implements OnClickListener {
    private static final String TAG = "MainActivity";
    public static final String FIRST_INTENT_TAG = "first";
    public static final String SECOND_INTENT_TAG = "second";
    public static final String THIRD_INTENT_TAG = "third";
    public static final int FIRST_VIEW = 0;
    public static final int SECOND_VIEW = 1;
    public static final int THIRD_VIEW = 2;
    ScrollLayout mRoot;
    private View mFirstView, mSecondView, mThirdView;
    private TextView mFirstButton;
    private TextView mSecondButton;
    private TextView mThirdButton;

    /**
     * This field should be made private, so it is hidden from the SDK. {@hide
     * 
	 * }
     */
    protected LocalActivityManager mLocalActivityManager;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            switch (msg.what) {
            case FIRST_VIEW:
                mFirstButton.setSelected(true);
                mSecondButton.setSelected(false);
                mThirdButton.setSelected(false);
                break;
            case SECOND_VIEW:
                mFirstButton.setSelected(false);
                mSecondButton.setSelected(true);
                mThirdButton.setSelected(false);
                break;

            case THIRD_VIEW:
                mFirstButton.setSelected(false);
                mSecondButton.setSelected(false);
                mThirdButton.setSelected(true);
                break;

            default:
                break;
            }
        }
    };

    public Settings2() {
        this(true);
    }

    public Settings2(boolean singleActivityMode) {

        // mLocalActivityManager = new LocalActivityManager(this,
        // singleActivityMode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLocalActivityManager = getLocalActivityManager();
        setContentView(R.layout.setting2);
        mRoot = (ScrollLayout) findViewById(R.id.root);
        mFirstButton = (TextView) findViewById(R.id.first_button);
        mSecondButton = (TextView) findViewById(R.id.second_button);
        mThirdButton = (TextView) findViewById(R.id.third_button);
        mFirstButton.setOnClickListener(this);
        mSecondButton.setOnClickListener(this);
        mThirdButton.setOnClickListener(this);
        initView();
    }

    public void initView() {

        mRoot.removeAllViews();
        Intent firstIntent = new Intent(this, Settings.class);
        firstIntent.putExtra("TYPE", FIRST_VIEW);
        mFirstView = activityToView(this, firstIntent, FIRST_INTENT_TAG);
        mFirstView.setTag(FIRST_INTENT_TAG);
        mRoot.addView(mFirstView);

        Intent secondIntent = new Intent();
        secondIntent.setClassName("com.cyanogenmod.cmparts",
                "com.cyanogenmod.cmparts.activities.MainActivity");
        mSecondView = activityToView(this, secondIntent, SECOND_INTENT_TAG);
        mSecondView.setTag(SECOND_INTENT_TAG);
        mRoot.addView(mSecondView);

        Intent thirdIntent = new Intent();
        thirdIntent.setClassName("com.cyanogenmod.cmparts",
                "com.cyanogenmod.cmparts.activities.MainActivity");
        mThirdView = activityToView(this, thirdIntent, THIRD_INTENT_TAG);
        mThirdView.setTag(THIRD_INTENT_TAG);
        mRoot.addView(mThirdView);

    }

    public View activityToView(Context parent, Intent intent, String tag) {

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Window w = mLocalActivityManager.startActivity(tag, intent);
        View wd = w != null ? w.getDecorView() : null;
        if (wd != null) {
            wd.setVisibility(View.VISIBLE);
            wd.setFocusableInTouchMode(true);
            ((ViewGroup) wd)
                    .setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        }
        return wd;

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.d(TAG, "onRestart");

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return mRoot.getNowScreen().dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        int index;
        switch (v.getId()) {
        case R.id.first_button:
            index = mRoot.indexOfChild(mFirstView);

            mRoot.snapToScreen(index);
            mFirstButton.setSelected(true);
            mSecondButton.setSelected(false);
            mThirdButton.setSelected(false);

            break;
        case R.id.second_button:
            index = mRoot.indexOfChild(mSecondView);

            mRoot.snapToScreen(index);
            mFirstButton.setSelected(false);
            mSecondButton.setSelected(true);
            mThirdButton.setSelected(false);
            break;
        case R.id.third_button:
            index = mRoot.indexOfChild(mThirdView);

            mRoot.snapToScreen(index);
            mFirstButton.setSelected(false);
            mSecondButton.setSelected(false);
            mThirdButton.setSelected(true);
            break;
        default:
            break;
        }

    }

}
