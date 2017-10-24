package com.rajesh.gallery.ui.engine;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * 添加图片加载完成后的尺寸监听
 *
 * @author zhufeng on 2017/10/22
 */
public class FrescoImageView extends SimpleDraweeView implements IRender {
    private PipelineDraweeControllerBuilder mControllerBuilder;
    private IRender mRender;
    private int mWidth = -1;
    private int mHeight = -1;

    private ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
        @Override
        public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
            if (imageInfo == null) {
                return;
            }
            int preWidth = imageInfo.getWidth();
            int preHeight = imageInfo.getHeight();
            if (preWidth != mWidth || preHeight != mHeight) {
                mWidth = preWidth;
                mHeight = preHeight;
                onRender(mWidth, mHeight);
                if (mRender != null) {
                    mRender.onRender(mWidth, mHeight);
                }
            }
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            Log.d("zhufeng", "Intermediate image received");
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            throwable.printStackTrace();
        }
    };

    public FrescoImageView(Context context) {
        this(context, null);
    }

    public FrescoImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FrescoImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mControllerBuilder = Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener);
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

    public PipelineDraweeControllerBuilder getFrescoControllerBuilder() {
        return mControllerBuilder;
    }

    @Override
    public void loadImage(int resizeX, int resizeY, Uri uri) {
        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(resizeX, resizeY))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) getFrescoControllerBuilder().setOldController(getController()).setImageRequest(request).build();
        setController(controller);
    }

    /**
     * 加载1080P图片
     *
     * @param uri
     */
    public void loadImage(Uri uri) {
        loadImage(1080, 1920, uri);
    }

    @Override
    public void onRender(int width, int height) {

    }
}
