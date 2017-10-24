package com.rajesh.gallery.ui.engine;

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
}
