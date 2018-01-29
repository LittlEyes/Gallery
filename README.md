# 图片缩放以及相册的实现

## 一、效果图
![image](https://raw.githubusercontent.com/zhufeng1222/Image/master/gallery/example.gif)

## 二、支持功能
- 图片缩放
- 下拉退出
- 可用于各大图片加载框架（Fresco，Glide，Picasso）

## 三、使用方式

### 3.1 AndroidManifest中注册
```java
<activity android:name="com.rajesh.zlbum.ui.AlbumActivity" />
```

### 3.2 跳转
```java
//数据需要能被转换为URI，用于展示
ArrayList<String> imageUri = new ArrayList<>();

Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
intent.putExtra(AlbumActivity.INTENT_IMAGE, imageUri);
intent.putExtra(AlbumActivity.INTENT_INDEX, 0);
startActivity(intent);
```

## 四、不同网络加载框架使用
不同的网络加载框架，需要PhotoView继承不同的engine。

- 使用Fresco时，PhotoView extends FrescoImageView
- 使用Glide时，PhotoView extends GlideImageView
- 使用Picasso时，PhotoView extends PicassoImageView

## 五、注意事项
Fresco加载drawable资源时的方式和Glide、Picasso有所不同。
