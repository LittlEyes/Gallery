# 图片缩放以及相册的实现

## 一、效果图
![image](https://raw.githubusercontent.com/zhufeng1222/Image/master/gallery/gallery.gif)

## 二、支持功能
- 图片缩放
- 放大后的图片惯性滑动
- 可在ViewPager中使用
- 可用于各大图片加载框架（Fresco，Glide，Picasso）

## 三、核心实现方法
### 3.1 缩放 Matrix.postScale(float sx, float sy, float px, float py)
参数解析：

- sx: 目标宽度 / 现有宽度
- sy: 目标高度 / 现有高度
- (px,py): 缩放焦点坐标

使用示例：

```java
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
        }
        return true;
    }
};
```

### 3.2 移动 Matrix.postTranslate(float dx, float dy)
参数解析：

- dx: 目标位置X坐标 - 当前位置X坐标
- sy: 目标位置Y坐标 - 当前位置Y坐标

使用示例：

```java
/**
 * 简单手势监听
 */
private GestureDetector.SimpleOnGestureListener mOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

    ...

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (!isAlwaysSingleTouch) {
            return true;
        }
        mMatrix.postTranslate(-distanceX, -distanceY);
        invalidate();
        return false;
    }

    ...
};
```

### 3.3 将Matrix的操作关联到ImageView上
View提供onDraw的方法，可以操作到Canvas，Canvas提供concat的方法来关联Matrix。每次针对Matrix有操作之后调用```invalidate()```刷新一下```onDraw()```即可。这就是个操作配置，而且是View早就提供好了的配置。</br>
调用示例：

```java
@Override
protected void onDraw(Canvas canvas) {
    int saveCount = canvas.save();
    canvas.concat(mMatrix);
    super.onDraw(canvas);
    canvas.restoreToCount(saveCount);
}
```

### 3.4 惯性滑动 OverScroller.fling(int startX, int startY, int velocityX, int velocityY,int minX, int maxX, int minY, int maxY)
参数解析：

- (startX, startY): 初始位置坐标
- (velocityX, velocityY): XY方向的初始速度
- (minX, maxX, minY, maxY): 限定了移动后的位置边界

使用示例：

```java
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
```

Scroller只提供在基于已有位置和已有速度下的位置计算，需要主动调用```scroller.getCurrY()```和```scroller.getCurrX()```方法去获取位置信息。</br>
这里使用的是RecyclerView中的惯性滑动的实现方式。

## 四、三个必要的细节处理
在有了上面的4个方法，基本上一个可缩放的ImageView所需要的功能都可以实现了。但是，一些细节方面的问题也不可忽视，比如说：

- 移动不能超过图片的边缘
- 在ImageView的ScaleType为FIT_CENTER时，获取真实的图片内容的宽高，毕竟需要缩放的是图片的内容
- 图片是否移动到最左侧或最右侧，用于ViewPager中的滑动判断

### 4.1 边缘处理
在移动前，校验此次的移动是否会造成图片内容是否会移动超出边界。Canvas关联的Matrix是针对整个ImageView的，我们需要知道ImageView中图片部分在ImageView中的初始位置信息,如图：

![image](https://raw.githubusercontent.com/zhufeng1222/Image/master/gallery/pic.jpg)

在得到图片内容在初始状态下的展示区域Rect(a,b,c,d)后，使用Matrix提供的```Matrix.mapRect(Rect)```方法，可以得到经历缩放后的展示区域。得到内容缩放后的展示区域后，与ImageView的展示区域Rect(0,0,W,H)作比较便可得出是否超出边界。

示例方法：

```java
/**
 * 获得缩放移动后的图片内容的位置区域
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
```

### 4.2 获取ImageView中内容的宽高
针对不同的网络加载框架有不同的操作方式，这里一Fresco位示例：
PipelineDraweeControllerBuilder提供setControllerListener方法，可以设置一个图片加载的监听。
示例代码：

```java
private ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
    @Override
    public void onFinalImageSet(String id, @Nullable ImageInfo imageInfo, @Nullable Animatable anim) {
        if (imageInfo == null) {
            return;
        }
        int preWidth = imageInfo.getWidth();
        int preHeight = imageInfo.getHeight();
        if (preWidth != mWidth || preHeight != mHeight) {
            //获取到最新的图片内容的宽高
            mWidth = preWidth;
            mHeight = preHeight;
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

public void loadImage(int resizeX, int resizeY, Uri uri) {
    ImageRequest request = ImageRequestBuilder
            .newBuilderWithSource(uri)
            .setResizeOptions(new ResizeOptions(resizeX, resizeY))
            .build();
    PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setControllerListener(controllerListener).setOldController(getController()).setImageRequest(request).build();
    setController(controller);
}
```

### 4.3 处理与ViewPager的滑动冲突
需要明确：

- 左滑时，当图片内容到达右侧边界，进行图片切换的处理（事件交由ViewPager处理）
- 右滑时，当图片内容到达左侧边界，进行图片切换的处理（事件交由ViewPager处理）
- 剩下的ImageView自己处理

ImageView中的处理：</br>
在约束移动的时候标记图片是否已经触及左右边界。并提供方法：

```java
/**
 * 用于ViewPager滑动拦截
 *
 * @param direction
 * @return
 */
public boolean canScroll(int direction) {
    return !((direction < 0 && isRightSide()) || (direction > 0 && isLeftSide()));
}
```

ViewPager中的处理：</br>
在canScroll方法中进行状态判断。重写ViewPager：

```java
/**
 * 相册ViewPager
 *
 * @author zhufeng on 2017/10/22
 */
public class AlbumViewPager extends ViewPager {

    ...

    @Override
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ZoomImageView) {
            return ((ZoomImageView) v).canScroll(dx) || super.canScroll(v, checkV, dx, x, y);
        }
        return super.canScroll(v, checkV, dx, x, y);
    }

    ...

}
```

## 五、源码下载
https://github.com/zhufeng1222/Gallery
