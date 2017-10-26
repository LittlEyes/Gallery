package com.rajesh.gallery.ui;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rajesh.gallery.MyApp;
import com.rajesh.gallery.R;
import com.rajesh.gallery.ui.adapter.AlbumAdapter;
import com.rajesh.gallery.ui.view.album.AlbumViewPager;

import java.util.ArrayList;

/**
 * 相册
 *
 * @author zhufeng on 2017/10/22
 */
public class AlbumActivity extends AppCompatActivity {
    private LinearLayout actionBar;
    private ImageView backBtn;
    private TextView titleTv;
    private AlbumViewPager mAlbum;
    private AlbumAdapter mAdapter;
    private ArrayList<Uri> imageRes = null;
    private int curr = 0;
    private int total = 0;
    private boolean isActionBarShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        initView();
        initData();
        initEvents();
    }

    private void initView() {
        actionBar = (LinearLayout) findViewById(R.id.action_bar_album);
        backBtn = (ImageView) findViewById(R.id.back);
        titleTv = (TextView) findViewById(R.id.title);
        mAlbum = (AlbumViewPager) findViewById(R.id.content);
        translucentStatusBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            actionBar.setPadding(0, dp2Px(20), 0, 0);
        } else {
            actionBar.setPadding(0, 0, 0, 0);
        }

    }

    private void translucentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }


    private void initData() {
        imageRes = (ArrayList<Uri>) getIntent().getSerializableExtra("res");
        curr = getIntent().getIntExtra("index", 0);
        total = imageRes.size();
        if (imageRes == null || total == 0) {
            finish();
            return;
        }
        titleTv.setText(String.format(getString(R.string.index), curr + 1, total));
        mAdapter = new AlbumAdapter(MyApp.getAppContext(), imageRes);
        mAlbum.setPageMargin(30);
        mAlbum.setAdapter(mAdapter);
        mAlbum.setOffscreenPageLimit(1);
        mAlbum.setCurrentItem(curr, false);
    }

    private void initEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mAlbum.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curr = position;
                titleTv.setText(String.format(getString(R.string.index), curr + 1, total));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionBarShow) {
                    hideActionBar();
                } else {
                    showActionBar();
                }
            }
        });
    }

    /**
     * 动画展示actionbar
     */
    private void showActionBar() {
        isActionBarShow = true;
        actionBar.setVisibility(View.VISIBLE);
        Animation startAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_show);
        startAnim.setFillAfter(true);
        actionBar.startAnimation(startAnim);
    }

    /**
     * 动画隐藏actionbar
     */
    private void hideActionBar() {
        isActionBarShow = false;
        Animation hideAnim = AnimationUtils.loadAnimation(this, R.anim.action_bar_hide);
        hideAnim.setFillAfter(true);
        hideAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                actionBar.setVisibility(View.GONE);
                actionBar.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        actionBar.startAnimation(hideAnim);
    }

    public int dp2Px(int dp) {
        DisplayMetrics dm = MyApp.getAppContext().getResources().getDisplayMetrics();
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, dm);
        return px;
    }

}
