package com.rajesh.gallery.ui.view.album;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * 相册ViewPager
 *
 * @author zhufeng on 2017/10/22
 */
public class AlbumViewPager extends ViewPager {
    private ZoomImageView mCurrentView;
    private View.OnClickListener mOnClickListener;

    public AlbumViewPager(Context context) {
        super(context);
    }

    public AlbumViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ZoomImageView) {
            return ((ZoomImageView) v).canScroll(dx) || super.canScroll(v, checkV, dx, x, y);
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    public void setZoomView(ZoomImageView currentView) {
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
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }
}
