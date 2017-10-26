package com.rajesh.gallery.ui.view.album;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.OverScroller;

import com.rajesh.gallery.ui.engine.GlideImageView;

/**
 * 可缩放图片控件
 * 实现原理：通过Matrix操控ImageView中内容的缩放和移动
 *
 * @author zhufeng on 2016/6/13.
 */
public class ZoomImageView extends GlideImageView {
    private Context mContext;
    /**
     * 最小缩放比例
     */
    private static final float MIN_SCALE = 0.9F;
    /**
     * 最大缩放比例
     */
    private static final float MAX_SCALE = 3.0F;
    /**
     * 初始比例
     */
    private static final float ORIGINAL_SCALE = 1.0f;
    /**
     * 初始状态下控件的宽高
     */
    private int mWidth = 0;
    private int mHeight = 0;
    /**
     * 初始状态下图片内容的宽高
     */
    private int mImageWidth = 0;
    private int mImageHeight = 0;
    /**
     * 图片内容在ImageView中的显示区域（包括缩放后）
     */
    private RectF mImageRectF;
    /**
     * 控件是否加载成功
     */
    private boolean mWidgetLoaded = false;
    /**
     * 待缩放图片是否已经设置
     */
    private boolean mImageLoaded = false;
    /**
     * 手势监听
     */
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    /**
     * 最小惯性滑动速度
     */
    private int mMinimumVelocity;
    /**
     * 最大惯性滑动速度
     */
    private int mMaximumVelocity;
    /**
     * 缩放焦点-X坐标
     */
    private float focusX;
    /**
     * 缩放焦点-Y坐标
     */
    private float focusY;
    /**
     * 是否单指操作（多指操作都交给缩放）
     */
    protected boolean isAlwaysSingleTouch = true;
    /**
     * 惯性滑动工具
     */
    private FlingUtil mFlingUtil;
    /**
     * ZoomImageView的状态
     */
    private Matrix mMatrix;
    /**
     * 当前缩放比例
     */
    private float mScale = ORIGINAL_SCALE;
    /**
     * 是否触及左边界
     */
    private boolean mIsLeftSide = true;
    /**
     * 是否触及右边界
     */
    private boolean mIsRightSide = true;
    /**
     * 单击监听
     */
    private View.OnClickListener mOnClickListener;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        //使用Fresco时需要设置ScaleType，ImageView默认使用FIT_CENTER
        //getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(mContext, mOnScaleGestureListener);
        mGestureDetector = new GestureDetector(mContext, mOnGestureListener);
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mFlingUtil = new FlingUtil();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int saveCount = canvas.save();
        canvas.concat(mMatrix);
        super.onDraw(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            isAlwaysSingleTouch = true;
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
            pointerUp();
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            pointerUp();
        }
        if (event.getPointerCount() > 1) {
            mScaleGestureDetector.onTouchEvent(event);
            isAlwaysSingleTouch = false;
        } else {
            if (!mScaleGestureDetector.isInProgress() && isAlwaysSingleTouch) {
                mGestureDetector.onTouchEvent(event);
            }
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int preWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        int preHeight = View.MeasureSpec.getSize(heightMeasureSpec);

        boolean hasSizeChanged = false;
        if (preWidth != mWidth || preHeight != mHeight) {
            hasSizeChanged = true;
        }
        mWidth = preWidth;
        mHeight = preHeight;
        mWidgetLoaded = true;

        Log.d("zhufeng", "*****************控件加载成功");
        //图片资源已有并且控件还没有加载 || 控件已经加载但控件尺寸发生变化
        boolean needUpdate = mImageLoaded && hasSizeChanged;
        if (needUpdate) {
            setDrawableToView();
        }
    }

    /**
     * 获得缩放移动后的图片的位置区域
     *
     * @param matrix
     * @return RectF
     */
    private RectF getScaledRect(Matrix matrix) {
        RectF rectF = new RectF(mImageRectF);
        //转化为缩放后的相对位置
        matrix.mapRect(rectF);
        return rectF;
    }

    private void marginView(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    /**
     * 有手指离开，检查当前缩放值，并规范
     */
    private void pointerUp() {
        if (mScale < ORIGINAL_SCALE) {
            reset();
        } else if (mScale > MAX_SCALE) {
            //超出最大后增加回弹
            float scaleFactor = MAX_SCALE / mScale;
            mScale = MAX_SCALE;
            mMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
            invalidate();
        }
        checkBorder();
    }

    /**
     * 缩放时检查图片边缘，并添加相应的移动做调整
     */
    private void constrainScale() {
        float dx = 0;
        float dy = 0;
        RectF rectF = getScaledRect(mMatrix);
        float scaleImageWidth = rectF.width();
        float scaleImageHeight = rectF.height();

        if (scaleImageWidth > mWidth) {
            //right
            if (rectF.right < mWidth) {
                dx = -rectF.right + mWidth;
            }
            //left
            if (rectF.left > 0) {
                dx = -rectF.left;
            }
        } else {
            //center
            dx = -rectF.left + ((float) mWidth - scaleImageWidth) / 2;
        }

        if (scaleImageHeight > mHeight) {
            //bottom
            if (rectF.bottom < mHeight) {
                dy = -rectF.bottom + mHeight;
            }
            //top
            if (rectF.top > 0) {
                dy = -rectF.top;
            }
        } else {
            //center
            dy = -rectF.top + ((float) mHeight - scaleImageHeight) / 2;
        }

        mMatrix.postTranslate(dx, dy);
        invalidate();
        checkBorder();
    }

    /**
     * 针对边缘问题，约束移动
     *
     * @param dx
     * @param dy
     */
    private void constrainScrollBy(float dx, float dy) {
        RectF rectF = getScaledRect(mMatrix);
        float scaleImageWidth = rectF.width();
        float scaleImageHeight = rectF.height();

        if (scaleImageWidth > mWidth) {
            //right
            if (rectF.right + dx < mWidth) {
                dx = -rectF.right + mWidth;
            }
            //left
            if (rectF.left + dx > 0) {
                dx = -rectF.left;
            }
        } else {
            //center
            dx = -rectF.left + ((float) mWidth - scaleImageWidth) / 2;
        }

        if (scaleImageHeight > mHeight) {
            //bottom
            if (rectF.bottom + dy < mHeight) {
                dy = -rectF.bottom + mHeight;
            }
            //top
            if (rectF.top + dy > 0) {
                dy = -rectF.top;
            }
        } else {
            //center
            dy = -rectF.top + ((float) mHeight - scaleImageHeight) / 2;
        }

        mMatrix.postTranslate(dx, dy);
        invalidate();
        checkBorder();
    }

    /**
     * 检查图片边界
     */
    private void checkBorder() {
        RectF rectF = getScaledRect(mMatrix);
        if (rectF.left >= 0) {
            mIsLeftSide = true;
        } else {
            mIsLeftSide = false;
        }
        if (rectF.right <= mWidth) {
            mIsRightSide = true;
        } else {
            mIsRightSide = false;
        }
        printStatusLog();
    }

    /**
     * 打印图片的状态信息
     */
    private void printStatusLog() {
        RectF rectF = getScaledRect(mMatrix);
        Log.i("zhufeng", "位置：(" + rectF.left + "," + rectF.top + "," + rectF.right + "," + rectF.bottom + ")");
        Log.i("zhufeng", "是否原始大小：" + isZoomToOriginalSize() + ", 是否靠左：" + isLeftSide() + " ,是否靠右：" + isRightSide());
    }

    /**
     * 缩放手势监听
     */
    private ScaleGestureDetector.OnScaleGestureListener mOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            float wantScale = mScale * scaleFactor;
            if (wantScale >= MIN_SCALE) {
                mScale = wantScale;
                focusX = detector.getFocusX();
                focusY = detector.getFocusY();
                mMatrix.postScale(scaleFactor, scaleFactor, focusX, focusY);
                invalidate();
                constrainScale();
            }
            return true;
        }
    };

    /**
     * 简单手势监听
     */
    private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            if (!isAlwaysSingleTouch) {
                return true;
            }
            forceFinishScroll();
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mOnClickListener != null) {
                mOnClickListener.onClick(ZoomImageView.this);
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (!isAlwaysSingleTouch) {
                return true;
            }
            float x = e.getX();
            float y = e.getY();
            if (mScale == ORIGINAL_SCALE) {
                float scaleFactor = MAX_SCALE / mScale;
                mScale = MAX_SCALE;
                mMatrix.postScale(scaleFactor, scaleFactor, x, y);
                invalidate();
            } else {
                reset();
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isAlwaysSingleTouch) {
                return true;
            }
            constrainScrollBy(-distanceX, -distanceY);
            checkBorder();
            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (!isAlwaysSingleTouch) {
                return true;
            }
            float absVelocityX = Math.abs(velocityX);
            if (absVelocityX < mMinimumVelocity) {
                absVelocityX = 0F;
            } else {
                absVelocityX = Math.max(mMinimumVelocity, Math.min(absVelocityX, mMaximumVelocity));
            }
            float absVelocityY = Math.abs(velocityY);
            if (absVelocityY < mMinimumVelocity) {
                absVelocityY = 0F;
            } else {
                absVelocityY = Math.max(mMinimumVelocity, Math.min(absVelocityY, mMaximumVelocity));
            }

            if (absVelocityX != 0 || absVelocityY != 0) {
                mFlingUtil.fling((int) (velocityX > 0 ? absVelocityX : -absVelocityX), (int) (velocityY > 0 ? absVelocityY : -absVelocityY));
            }
            return true;
        }
    };

    /**
     * 强制停止控件的惯性滑动
     */
    private void forceFinishScroll() {
        mFlingUtil.stop();
    }

    /**
     * 惯性滑动工具类
     * 使用fling方法开始滑动
     * 使用stop方法停止滑动
     */
    private class FlingUtil implements Runnable {
        private int mLastFlingX = 0;
        private int mLastFlingY = 0;
        private OverScroller mScroller;
        private boolean mEatRunOnAnimationRequest = false;
        private boolean mReSchedulePostAnimationCallback = false;

        /**
         * RecyclerView使用的惯性滑动插值器
         * f(x) = (x-1)^5 + 1
         */
        private final Interpolator sQuinticInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        };

        public FlingUtil() {
            mScroller = new OverScroller(getContext(), sQuinticInterpolator);
        }

        @Override
        public void run() {
            disableRunOnAnimationRequests();
            final OverScroller scroller = mScroller;
            if (scroller.computeScrollOffset()) {
                final int y = scroller.getCurrY();
                int dy = y - mLastFlingY;
                final int x = scroller.getCurrX();
                int dx = x - mLastFlingX;
                mLastFlingY = y;
                mLastFlingX = x;
                constrainScrollBy(dx, dy);
                postOnAnimation();
            }
            enableRunOnAnimationRequests();
        }

        public void fling(int velocityX, int velocityY) {
            mLastFlingX = 0;
            mLastFlingY = 0;
            mScroller.fling(0, 0, velocityX, velocityY, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
            postOnAnimation();
        }

        public void stop() {
            removeCallbacks(this);
            mScroller.abortAnimation();
        }

        private void disableRunOnAnimationRequests() {
            mReSchedulePostAnimationCallback = false;
            mEatRunOnAnimationRequest = true;
        }

        private void enableRunOnAnimationRequests() {
            mEatRunOnAnimationRequest = false;
            if (mReSchedulePostAnimationCallback) {
                postOnAnimation();
            }
        }

        void postOnAnimation() {
            if (mEatRunOnAnimationRequest) {
                mReSchedulePostAnimationCallback = true;
            } else {
                removeCallbacks(this);
                ViewCompat.postOnAnimation(ZoomImageView.this, this);
            }
        }
    }

    @Override
    public void onRender(int width, int height) {
        super.onRender(width, height);
        mImageWidth = width;
        mImageHeight = height;
        mImageLoaded = true;
        if (mWidgetLoaded) {
            setDrawableToView();
        }
    }

    //公开方法

    private void setDrawableToView() {
        float imageRatio = (float) mImageWidth / (float) mImageHeight;
        float widgetRatio = (float) mWidth / (float) mHeight;
        if (imageRatio > widgetRatio) {
            mImageHeight = mWidth * mImageHeight / mImageWidth;
            mImageWidth = mWidth;
        } else {
            mImageWidth = mHeight * mImageWidth / mImageHeight;
            mImageHeight = mHeight;
        }
        mImageRectF = new RectF((float) (mWidth - mImageWidth) / 2, (float) (mHeight - mImageHeight) / 2, (float) (mWidth + mImageWidth) / 2, (float) (mHeight + mImageHeight) / 2);
        Log.i("zhufeng_2", "控件宽高：（" + mWidth + " ," + mHeight + "）");
        Log.i("zhufeng_2", "图片宽高：（" + mImageWidth + " ," + mImageHeight + "）");
        Log.i("zhufeng_2", "图片区域：（" + mImageRectF.left + " ," + mImageRectF.top + " ," + mImageRectF.right + " ," + mImageRectF.bottom + "）");
    }

    public void reset() {
        mMatrix.reset();
        mScale = ORIGINAL_SCALE;
        mIsLeftSide = true;
        mIsRightSide = true;
        invalidate();
    }

    public boolean isZoomToOriginalSize() {
        return mScale == ORIGINAL_SCALE;
    }

    public boolean isLeftSide() {
        return mIsLeftSide;
    }

    public boolean isRightSide() {
        return mIsRightSide;
    }

    /**
     * 用于ViewPager滑动拦截
     *
     * @param direction
     * @return
     */
    public boolean canScroll(int direction) {
        return !((direction < 0 && isRightSide()) || (direction > 0 && isLeftSide()));
    }

    @Override
    public void setOnClickListener(View.OnClickListener listener) {
        this.mOnClickListener = listener;
    }
}