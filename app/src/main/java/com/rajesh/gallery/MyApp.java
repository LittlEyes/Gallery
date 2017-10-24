package com.rajesh.gallery;

import android.app.Application;
import android.content.Context;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.rajesh.gallery.util.FileUtil;

/**
 * desc
 *
 * @author zhufeng on 2017/1/10.
 */
public class MyApp extends Application {
    /**
     * 默认图极低磁盘空间缓存的最大值
     */
    public static final int MAX_DISK_CACHE_VERY_LOW_SIZE = 10 * ByteConstants.MB;
    /**
     * 默认图低磁盘空间缓存的最大值
     */
    public static final int MAX_DISK_CACHE_LOW_SIZE = 30 * ByteConstants.MB;
    /**
     * 默认图磁盘缓存的最大值
     */
    public static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

        //Fresco 初始化
        DiskCacheConfig diskCacheConfig = DiskCacheConfig
                .newBuilder(this)
                // 缓存图片基路径
                .setBaseDirectoryPath(FileUtil.createSDDir(FileUtil.DIRECTORY_ROOT))
                // 文件夹名
                .setBaseDirectoryName(FileUtil.DIRECTORY_IMAGES)
                // 默认缓存的最大大小
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                // 缓存的最大大小,使用设备时低磁盘空间
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)
                // 缓存的最大大小,当设备极低磁盘空间
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERY_LOW_SIZE)
                .build();
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setMainDiskCacheConfig(diskCacheConfig)
                .build();
        Fresco.initialize(this, config);
    }

    public static Context getAppContext() {
        return mContext;
    }
}
