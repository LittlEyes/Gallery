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
     * 使用示例：
     * loadImage(1080, 1920, Uri.parse("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg2));
     * loadImage(1080, 1920, Uri.parse("http://a.hiphotos.baidu.com/image/pic/item/6609c93d70cf3bc7176dd658db00baa1cd112a10.jpg"));
     *
     * @param resizeX 压缩最大宽
     * @param resizeY 压缩最大高
     * @param uri     图片Uri
     */
    void loadImage(int resizeX, int resizeY, Uri uri);
}
