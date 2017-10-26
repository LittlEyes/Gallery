package com.rajesh.gallery.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.rajesh.gallery.ui.view.album.AlbumViewPager;
import com.rajesh.gallery.ui.view.album.ZoomImageView;

import java.util.HashMap;
import java.util.List;

/**
 * 相册适配器
 *
 * @author zhufeng on 2017/10/22
 */
public class AlbumAdapter extends PagerAdapter {
    private Context mContext;
    /**
     * 展示的图片资源的URL列表
     */
    private List<Uri> imageRes;
    /**
     * 存放展示图片的容器，用于删除不用的item
     */
    private HashMap<Integer, ZoomImageView> viewCache;

    public AlbumAdapter(Context mContext, List<Uri> imageRes) {
        this.mContext = mContext;
        this.imageRes = imageRes;
        viewCache = new HashMap<>();
    }

    @Override
    public int getCount() {
        return imageRes.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        ZoomImageView zoomImage = viewCache.get(position);
        ((AlbumViewPager) container).setZoomView(zoomImage);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ZoomImageView zoomImage = viewCache.get(position);
        if (zoomImage == null) {
            zoomImage = new ZoomImageView(mContext);
            zoomImage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            zoomImage.loadImage(imageRes.get(position));
            viewCache.put(position, zoomImage);
        }
        container.addView(zoomImage);
        return zoomImage;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        ZoomImageView zoomImage = viewCache.get(position);
        if (zoomImage != null) {
            viewCache.remove(position);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
