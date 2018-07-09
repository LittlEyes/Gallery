package com.rajesh.zlbum.engine.impl;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.AutoRotateDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.rajesh.zlbum.R;
import com.rajesh.zlbum.engine.IRender;

/**
 * 提供加载图片宽高
 *
 * @author zhufeng on 2017/10/22
 */
public class FrescoImageView extends SimpleDraweeView implements IRender {
    private static final String TAG = "FrescoImageView";
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
            Log.i(TAG, "Fresco drawable size:(" + preWidth + "," + preHeight + ")");
            if (preWidth != mWidth || preHeight != mHeight) {
                mWidth = preWidth;
                mHeight = preHeight;
                onRender(mWidth, mHeight);
            }
        }

        @Override
        public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            Log.d(TAG, "Intermediate image received");
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
        getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
    }

    public void loadImage(int resizeX, int resizeY, Uri uri) {
        Drawable roundingParams = getResources().getDrawable(R.mipmap.ic_loading);
        AutoRotateDrawable progressDrawable = new AutoRotateDrawable(roundingParams, 1000);

        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(200)
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setProgressBarImage(progressDrawable, ScalingUtils.ScaleType.CENTER_INSIDE)
                .setFailureImage(R.mipmap.ic_load_error, ScalingUtils.ScaleType.CENTER_INSIDE)
                .build();
        setHierarchy(hierarchy);

        ImageRequest request = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(resizeX, resizeY))
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener).setOldController(getController()).setImageRequest(request).build();
        setController(controller);
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
