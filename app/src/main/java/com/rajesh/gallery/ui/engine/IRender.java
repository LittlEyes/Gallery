package com.rajesh.gallery.ui.engine;

import android.net.Uri;

/**
 * desc
 *
 * @author zhufeng on 2017/10/23
 */
public interface IRender {
    /**
     * 图片加载完成后公布其真实宽高
     *
     * @param width
     * @param height
     */
    void onRender(int width, int height);

    /**
     * 加载图片
     *
     * @param resizeX 压缩最大宽
     * @param resizeY 压缩最大高
     * @param uri     图片Uri
     */
    void loadImage(int resizeX, int resizeY, Uri uri);
}
