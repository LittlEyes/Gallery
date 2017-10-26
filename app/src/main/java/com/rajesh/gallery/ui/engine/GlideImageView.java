package com.rajesh.gallery.ui.engine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * 提供加载图片宽高
 *
 * @author zhufeng on 2017/10/26
 */
@SuppressLint("AppCompatCustomView")
public class GlideImageView extends ImageView implements IRender {
    private Context mContext;
    private IRender mRender;
    private int mWidth = -1;
    private int mHeight = -1;

    private RequestListener<Uri, GlideDrawable> mUriRequestListener = new RequestListener<Uri, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            if (resource == null) {
                return false;
            }
            int preWidth = resource.getIntrinsicWidth();
            int preHeight = resource.getIntrinsicHeight();
            Log.i("zhufeng_111", "Picasso("+preWidth+","+preHeight+")");
            if (preWidth != mWidth || preHeight != mHeight) {
                mWidth = preWidth;
                mHeight = preHeight;
                onRender(mWidth, mHeight);
                if (mRender != null) {
                    mRender.onRender(mWidth, mHeight);
                }
            }
            return false;
        }
    };

    public GlideImageView(Context context) {
        this(context, null);
    }

    public GlideImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GlideImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
        Glide.with(mContext)
                .load(uri)
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .listener(mUriRequestListener)
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
