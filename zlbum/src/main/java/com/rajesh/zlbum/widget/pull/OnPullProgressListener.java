package com.rajesh.zlbum.widget.pull;

/**
 * 下拉退出进度监听
 *
 * @author zhufeng on 2018/1/26
 */
public interface OnPullProgressListener {
    /**
     * 开始下拉
     */
    void startPull();

    /**
     * 下拉中
     *
     * @param progress
     */
    void onProgress(float progress);

    /**
     * 结束下拉
     *
     * @param isFinish
     */
    void stopPull(boolean isFinish);
}
