package com.rajesh.gallery.ui.view.album;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
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

import com.rajesh.gallery.ui.engine.FrescoImageView;
import com.rajesh.gallery.ui.engine.IRender;

/**
 * 可缩放图片控件(被ZoomImageView替代)
 * 实现原理：ViewGroup中一个ImageView，设置ImageView缩放，并控制其移动
 *
 * @author zhufeng on 2017/10/20
 */
public class ScaleImageView extends ViewGroup implements GestureDetector.OnGestureListener, ScaleGestureDetector.OnScaleGestureListener {
    private Context mContext;
    /**
     * 最小缩放比例
     */
    protected static final float MIN_SCALE = 1.0F;
    /**
     * 最大缩放比例
     */
    protected static final float MAX_SCALE = 3.0F;
    /**
     * 双击的时间间隔
     */
    protected static final int DOUBLE_TAP_TIME = 300;
    /**
     * 双击的时间间隔
     */
    protected static final float MIN_SCALE_RATIO = 0.01F;
    /**
     * 控件的宽
     */
    private int mWidth = 0;
    /**
     * 控件的高
     */
    private int mHeight = 0;
    /**
     * 当前缩放比例
     */
    protected float mScale = 1.0F;
    /**
     * 图片的宽
     */
    private int mDrawableWidth = 0;
    /**
     * 图片的高
     */
    private int mDrawableHeight = 0;
    /**
     * 缩放后图片的宽
     */
    private int mScaledDrawableWidth = 0;
    /**
     * 缩放后图片的高
     */
    private int mScaledDrawableHeight = 0;
    /**
     * 手势处理
     */
    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    /**
     * 是否单指操作（多指操作都交给缩放）
     */
    protected boolean isAlwaysSingleTouch = true;
    /**
     * 是否为双击缩放
     */
    private boolean isDoubleClickScale = false;
    /**
     * 缩放焦点
     */
    private int[] focusPoint = new int[2];
    /**
     * 双指捏合返回的缩放值，即将要反应在图片上
     */
    private float waitHandleScale = 1.0F;
    /**
     * 上一次的缩放值
     */
    private float preScale = 1.0F;
    /**
     * 最小缩放值，用于减少计算次数，提高性能
     */
    private boolean isValidScale = true;
    /**
     * 单双击
     */
    protected long mLastTapTime = 0;
    protected float lastTapX;
    protected float lastTapY;
    /**
     * 相对移动距离（用于ScrollBy）
     */
    protected int mXScroll = 0;
    protected int mYScroll = 0;
    /**
     * 最小惯性滑动速度
     */
    private int mMinimumVelocity;
    /**
     * 最大惯性滑动速度
     */
    private int mMaximumVelocity;
    /**
     * 惯性滑动工具
     */
    private FlingUtil mFlingUtil;
    /**
     * 控件是否加载成功
     */
    private boolean mWidgetLoaded = false;
    /**
     * 待缩放图片是否已经设置
     */
    private boolean mImageLoaded = false;
    /**
     * 控件是否开始加载
     */
    private boolean mStart = false;
    /**
     * 控件是否需要销毁
     */
    private boolean mFinished = false;
    /**
     * 图片到达左边界
     */
    private boolean mIsLeftSide = true;
    /**
     * 图片到达右边界
     */
    private boolean mIsRightSide = true;
    /**
     * 真正被缩放的ImageView，如果用的是Fresco需要改成FrescoImageView
     */
    private FrescoImageView mImageView;

    public ScaleImageView(Context context) {
        this(context, null);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScaleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mGestureDetector = new GestureDetector(context, this);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);

        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mFlingUtil = new FlingUtil();

        mImageView = new FrescoImageView(mContext);
        mImageView.setOnSizeRenderListener(new IRender() {
            @Override
            public void onRender(int width, int height) {
                Log.d("zhufeng", "**************图片加载：图片宽：" + width + ", 高：" + height);
                if (width > 0 && height > 0) {
                    mDrawableWidth = width;
                    mDrawableHeight = height;
                    mImageLoaded = true;
                    if (mWidgetLoaded) {
                        loadImageToWidget();
                    }
                }
            }
        });
        addView(mImageView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int preWidth = MeasureSpec.getSize(widthMeasureSpec);
        int preHeight = MeasureSpec.getSize(heightMeasureSpec);

        boolean hasSizeChanged = false;
        if (preWidth != mWidth || preHeight != mHeight) {
            hasSizeChanged = true;
        }
        mWidth = preWidth;
        mHeight = preHeight;
        mWidgetLoaded = true;

        Log.d("zhufeng", "*****************控件加载成功");
        //图片资源已有并且控件还没有加载 || 控件已经加载但控件尺寸发生变化
        boolean needUpdate = (!mStart && mImageLoaded) || (mStart && mImageLoaded && hasSizeChanged);
        if (needUpdate) {
            loadImageToWidget();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!mStart) {
            return;
        }
        if (finished()) {
            return;
        }
        mScaledDrawableWidth = (int) (mDrawableWidth * mScale);
        mScaledDrawableHeight = (int) (mDrawableHeight * mScale);
        //执行缩放
        mImageView.layout(0, 0, mScaledDrawableWidth, mScaledDrawableHeight);
        mImageView.invalidate();
        //计算缩放造成的移动
        boolean hasScale = (!isAlwaysSingleTouch && isValidScale) || isDoubleClickScale;
        if (hasScale) {
            mXScroll = (int) (focusPoint[0] * preScale - focusPoint[0] * mScale);
            mYScroll = (int) (focusPoint[1] * preScale - focusPoint[1] * mScale);
            isDoubleClickScale = false;
        }
        //执行位移
        scrollBy(-mXScroll, -mYScroll);
        mXScroll = mYScroll = 0;
        //检测图片是否触及边界
        checkBorder();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mStart) {
            return true;
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            isAlwaysSingleTouch = true;
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            //actionMove(event);
        }
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            //actionUp();
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
    public void scrollBy(int dx, int dy) {
        Point p = constrainScrollBy(dx, dy);
        super.scrollBy(p.x, p.y);
    }

    /**
     * 边界问题，需要约束移动大小
     *
     * @param dx
     * @param dy
     * @return
     */
    protected Point constrainScrollBy(int dx, int dy) {
        int scrollX = getScrollX();
        int scrollY = getScrollY();

        if (mScaledDrawableWidth > mWidth) {
            //right
            if (mScaledDrawableWidth - scrollX - dx < mWidth) {
                dx = mScaledDrawableWidth - scrollX - mWidth;
            }
            //left
            if (-scrollX - dx > 0) {
                dx = -scrollX;
            }
        } else {
            //center
            dx = -scrollX - (mWidth - mScaledDrawableWidth) / 2;
        }

        if (mScaledDrawableHeight > mHeight) {
            //bottom
            if (mScaledDrawableHeight - scrollY - dy < mHeight) {
                dy = mScaledDrawableHeight - scrollY - mHeight;
            }
            //top
            if (scrollY + dy < 0) {
                dy = -scrollY;
            }
        } else {
            //center
            dy = -scrollY - (mHeight - mScaledDrawableHeight) / 2;
        }

        return new Point(dx, dy);
    }

    /* GestureDetector start *****************************************************************************************/
    @Override
    public boolean onDown(MotionEvent e) {
        if (!isAlwaysSingleTouch) {
            return true;
        }
        forceFinishScroll();
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (!isAlwaysSingleTouch) {
            return;
        }
        doLongTap(e.getX(), e.getY());
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (finished()) {
            return true;
        }
        if (!isAlwaysSingleTouch) {
            return true;
        }

        mXScroll -= distanceX;
        mYScroll -= distanceY;
        requestLayout();
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        if (!isAlwaysSingleTouch) {
            return;
        }
    }

    @Override
    public boolean onSingleTapUp(final MotionEvent e) {
        if (!isAlwaysSingleTouch) {
            return false;
        }
        long now = System.currentTimeMillis();
        if (mLastTapTime != 0 && ((now - mLastTapTime) < DOUBLE_TAP_TIME)) {
            doDoubleTap(lastTapX, lastTapY);
            mLastTapTime = 0;
        } else {
            mLastTapTime = now;
            lastTapX = e.getX();
            lastTapY = e.getY();
            doSingleTap(lastTapX, lastTapY);
        }
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
    /* GestureDetector end *****************************************************************************************/

    /* ScaleGestureDetector start *****************************************************************************************/
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        int[] p = eventToScreen(detector.getFocusX(), detector.getFocusY());
        focusPoint = getRelativePoint(mImageView, p);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        waitHandleScale = Math.min(Math.max(mScale * detector.getScaleFactor(), MIN_SCALE), MAX_SCALE);
        float currScale = (float) ((int) (waitHandleScale * 100)) / 100;
        if (Math.abs(mScale - currScale) < MIN_SCALE_RATIO) {
            isValidScale = false;
            return true;
        }
        isValidScale = true;
        preScale = mScale;
        mScale = currScale;
        requestLayout();
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }
    /* ScaleGestureDetector end *****************************************************************************************/

    /**
     * 屏幕坐标系转到ScaleListView坐标系
     * 把相对控件的坐标转换为相对屏幕的坐标，以配合getGlobalVisibleRect和getLocationOnScreen使用。
     *
     * @param fx
     * @param fy
     * @return
     */
    protected int[] eventToScreen(float fx, float fy) {
        int[] position = new int[2];
        position[0] = Math.round(fx);
        position[1] = Math.round(fy);
        Rect docRect = new Rect();
        getGlobalVisibleRect(docRect);
        position[0] += docRect.left;
        position[1] += docRect.top;

        return position;
    }

    /**
     * 获取当前坐标，在没有缩放的情况下，相对与View左上角点的距离
     *
     * @param targetView
     * @param currPoint  相对屏幕左上角
     * @return
     */
    protected int[] getRelativePoint(View targetView, int[] currPoint) {
        int[] rPosition = new int[2];

        int[] loc = new int[2];
        targetView.getLocationOnScreen(loc);

        rPosition[0] = currPoint[0] - loc[0];
        rPosition[1] = currPoint[1] - loc[1];

        rPosition[0] = (int) (rPosition[0] / mScale);
        rPosition[1] = (int) (rPosition[1] / mScale);
        return rPosition;
    }

    private void startDoubleClickScale(float fx, float fy) {
        isDoubleClickScale = true;
        int[] p = eventToScreen(fx, fy);
        focusPoint = getRelativePoint(mImageView, p);
        if (mScale > MIN_SCALE) {
            preScale = mScale;
            mScale = MIN_SCALE;
            requestLayout();
        } else {
            preScale = mScale;
            mScale = MAX_SCALE;
            requestLayout();
        }
    }

    private void loadImageToWidget() {
        float imageRatio = (float) mDrawableWidth / (float) mDrawableHeight;
        float widgetRatio = (float) mWidth / (float) mHeight;
        if (imageRatio > widgetRatio) {
            mDrawableHeight = mWidth * mDrawableHeight / mDrawableWidth;
            mDrawableWidth = mWidth;
        } else {
            mDrawableWidth = mHeight * mDrawableWidth / mDrawableHeight;
            mDrawableHeight = mHeight;
        }
        preScale = 1.0F;
        mScale = 1.0F;
        //居中显示
        mXScroll = -getScrollX() - (mWidth - mDrawableWidth) / 2;
        mYScroll = -getScrollY() - (mHeight - mDrawableHeight) / 2;
        mStart = true;
        requestLayout();
    }

    /**
     * 检查图片是否触及左右边界
     */
    private void checkBorder() {
        int scrollX = getScrollX();
        Log.i("zhufeng", "" + scrollX);
        if (scrollX <= 0) {
            mIsLeftSide = true;
        } else {
            mIsLeftSide = false;
        }

        if (mScaledDrawableWidth - scrollX <= mWidth) {
            mIsRightSide = true;
        } else {
            mIsRightSide = false;
        }

        Log.i("zhufeng", "是否最小：" + isMinScaled() + ", 是否靠左：" + isLeftSide() + " ,是否靠右：" + isRightSide(), null);
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
                mYScroll = dy;
                mXScroll = dx;
                requestLayout();
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
                ViewCompat.postOnAnimation(ScaleImageView.this, this);
            }
        }
    }

    /**
     * 强制停止控件的惯性滑动
     */
    public void forceFinishScroll() {
        mFlingUtil.stop();
    }

    /**
     * 控件是否需要被销毁
     *
     * @return 是否需要被销毁
     */
    public boolean finished() {
        return mFinished;
    }

    public boolean isLeftSide() {
        return mIsLeftSide;
    }

    public boolean isRightSide() {
        return mIsRightSide;
    }

    public boolean isMinScaled() {
        return mScale == MIN_SCALE;
    }

    /**
     * 单击
     *
     * @param fx
     * @param fy
     */
    protected void doSingleTap(float fx, float fy) {
    }

    /**
     * 双击
     *
     * @param fx
     * @param fy
     */
    protected void doDoubleTap(float fx, float fy) {
        startDoubleClickScale(fx, fy);
    }

    /**
     * 长按
     *
     * @param fx
     * @param fy
     */
    protected void doLongTap(float fx, float fy) {
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

    /**
     * 获取ImageView
     * 为各大图片加载框架提供
     *
     * @return ImageView
     */
    public FrescoImageView getImageView() {
        return mImageView;
    }

}
