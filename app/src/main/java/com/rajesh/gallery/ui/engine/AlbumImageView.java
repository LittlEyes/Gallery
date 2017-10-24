package com.rajesh.gallery.ui.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 相册使用的ImageView，主要目的是添加设置图片后的回调
 *
 * @author zhufeng on 2017/10/22
 */
@SuppressLint("AppCompatCustomView")
public class AlbumImageView extends ImageView implements IRender{
    private int mWidth = -1;
    private int mHeight = -1;
    private IRender mRender;

    public AlbumImageView(Context context) {
        super(context);
    }

    public AlbumImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AlbumImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        renderSize();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        renderSize();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        renderSize();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        renderSize();
    }

    private void renderSize() {
        Drawable drawable = getDrawable();
        int preWidth = drawable.getIntrinsicWidth();
        int preHeight = drawable.getIntrinsicHeight();
        if (preWidth != mWidth || preHeight != mHeight) {
            mWidth = preWidth;
            mHeight = preHeight;
            onRender(mWidth, mHeight);
            if (mRender != null) {
                mRender.onRender(mWidth, mHeight);
            }
        }
    }

    /**
     * 添加图片加载监听器
     *
     * @param render
     */
    public void setOnSizeRenderListener(IRender render) {
        this.mRender = render;
        if (mWidth != -1 && mHeight != -1 && render != null) {
            render.onRender(mWidth, mHeight);
        }
    }

    @Override
    public void onRender(int width, int height) {

    }
}
