package com.rajesh.zlbum.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.rajesh.zlbum.widget.pull.OnPullProgressListener;

/**
 * 相册ViewPager
 *
 * @author zhufeng on 2017/10/22
 */
public class AlbumViewPager extends ViewPager {
    private PhotoView mCurrentView;
    private OnClickListener mOnClickListener;
    /**
     * 下拉监听
     */
    private OnPullProgressListener mProgressListener;

    public AlbumViewPager(Context context) {
        super(context);
    }

    public AlbumViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof PhotoView) {
            return ((PhotoView) v).canScroll(dx) || super.canScroll(v, checkV, dx, x, y);
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    public void setCurrPhotoView(PhotoView currentView) {
        this.mCurrentView = null;
        this.mCurrentView = currentView;
        mCurrentView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnClickListener != null) {
                    mOnClickListener.onClick(v);
                }
            }
        });
        mCurrentView.setOnPullProgressListener(new OnPullProgressListener() {
            @Override
            public void startPull() {
                if (mProgressListener != null) {
                    mProgressListener.startPull();
                }
            }

            @Override
            public void onProgress(float progress) {
                if (mProgressListener != null) {
                    mProgressListener.onProgress(progress);
                }
            }

            @Override
            public void stopPull(boolean isFinish) {
                if (mProgressListener != null) {
                    mProgressListener.stopPull(isFinish);
                }
            }
        });
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        this.mOnClickListener = l;
    }

    public void setOnPullProgressListener(OnPullProgressListener l) {
        this.mProgressListener = l;
    }
}
