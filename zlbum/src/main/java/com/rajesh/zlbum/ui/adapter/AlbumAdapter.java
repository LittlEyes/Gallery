package com.rajesh.zlbum.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.rajesh.zlbum.widget.AlbumViewPager;
import com.rajesh.zlbum.widget.PhotoView;

import java.util.HashMap;
import java.util.List;

/**
 * 相册适配器
 *
 * @author zhufeng on 2017/10/22
 */
public class AlbumAdapter extends PagerAdapter {
    private Context mContext;
    private List<Uri> mDataList;
    private HashMap<Integer, PhotoView> mViewCache;

    public AlbumAdapter(Context mContext, List<Uri> mDataList) {
        this.mContext = mContext;
        this.mDataList = mDataList;
        mViewCache = new HashMap<>();
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        PhotoView photoView = mViewCache.get(position);
        ((AlbumViewPager) container).setCurrPhotoView(photoView);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = mViewCache.get(position);
        if (photoView == null) {
            photoView = new PhotoView(mContext);
            photoView.openPullToFinish();
            photoView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            photoView.loadImage(mDataList.get(position));
            mViewCache.put(position, photoView);
        }
        container.addView(photoView);
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        PhotoView photoView = mViewCache.get(position);
        if (photoView != null) {
            mViewCache.remove(position);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
