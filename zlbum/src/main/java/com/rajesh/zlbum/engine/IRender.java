package com.rajesh.zlbum.engine;

import android.net.Uri;

/**
 * desc
 *
 * @author zhufeng on 2017/10/23
 */
public interface IRender {
    /**
     * publish its width and height after image loaded
     *
     * @param width
     * @param height
     */
    void onRender(int width, int height);

    /**
     * load image
     *
     * @param uri     图片Uri
     */
    void loadImage(Uri uri);
}
