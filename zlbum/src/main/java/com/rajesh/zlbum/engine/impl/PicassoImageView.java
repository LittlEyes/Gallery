package com.rajesh.zlbum.engine.impl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.rajesh.zlbum.engine.IRender;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * 提供加载图片宽高
 *
 * @author zhufeng on 2017/10/26
 */
@SuppressLint("AppCompatCustomView")
public class PicassoImageView extends ImageView implements IRender {
    private static final String TAG = "PicassoImageView";
    private Context mContext;
    private int mWidth = -1;
    private int mHeight = -1;

    private Transformation transformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
            int preWidth = source.getWidth();
            int preHeight = source.getHeight();
            if (preWidth == 0 || preHeight == 0) {
                return source;
            }
            Log.i(TAG, "Picasso drawable size:(" + preWidth + "," + preHeight + ")");
            if (preWidth != mWidth || preHeight != mHeight) {
                mWidth = preWidth;
                mHeight = preHeight;
                onRender(mWidth, mHeight);
            }
            return source;
        }

        @Override
        public String key() {
            //这里不唯一的话，同一张图片只会触发一次transform()方法。
            return TAG + System.currentTimeMillis();
        }
    };

    public PicassoImageView(Context context) {
        this(context, null);
    }

    public PicassoImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PicassoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void loadImage(int resizeX, int resizeY, Uri uri) {
        Picasso.with(mContext)
                .load(uri)
                .resize(resizeX, resizeY)
                .priority(Picasso.Priority.HIGH)
                .centerInside()
                .transform(transformation)
                .into(this);
    }

    /**
     * load 1080P image
     *
     * @param uri
     */
    @Override
    public void loadImage(Uri uri) {
        loadImage(1080, 1920, uri);
    }

    @Override
    public void onRender(int width, int height) {

    }
}
