package com.rajesh.gallery.ui.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

/**
 * 提供加载图片宽高
 *
 * @author zhufeng on 2017/10/26
 */
@SuppressLint("AppCompatCustomView")
public class PicassoImageView extends ImageView implements IRender {
    private Context mContext;
    private IRender mRender;
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
            Log.i("zhufeng_111", "Picasso("+preWidth+","+preHeight+")");
            if (preWidth != mWidth || preHeight != mHeight) {
                mWidth = preWidth;
                mHeight = preHeight;
                onRender(mWidth, mHeight);
                if (mRender != null) {
                    mRender.onRender(mWidth, mHeight);
                }
            }
            return source;
        }

        @Override
        public String key() {
            //这里不唯一的话，同一张图片只会触发一次transform()方法。
            return "PicassoImageView"+ System.currentTimeMillis();
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

    @Override
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
     * 加载1080P图片
     *
     * @param uri
     */
    public void loadImage(Uri uri) {
        loadImage(1080, 1920, uri);
    }
}
