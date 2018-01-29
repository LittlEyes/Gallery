package com.rajesh.gallery;

import android.content.ContentResolver;
import android.content.res.Resources;

/**
 * 图片数据
 *
 * @author zhufeng on 2018/1/29
 */
public class ImageData {
    /**
     * image from url
     */
    public static String[] picUrls = new String[]{
            "http://a3.topitme.com/5/27/a3/111614045060aa3275l.jpg",
            "http://img4.duitang.com/uploads/item/201406/20/20140620155726_ch85t.jpeg"
    };

    /**
     * image from local storage
     */
    public static String[] picPaths = new String[]{
            "/storage/emulated/0/shihuo/image/1517121488541.png",
            "/storage/emulated/0/MIUI/wallpaper/international_bird_&_f0b46b78-6401-44fd-99b0-e8b71d1c7660.jpg"
    };

    public static String getUriForFresco(int res) {
        return "res://" + MyApp.getAppContext().getPackageName() + "/" + res;
    }

    public static String getUri(int res) {
        Resources resources = MyApp.getAppContext().getResources();
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                + resources.getResourcePackageName(res) + "/"
                + resources.getResourceTypeName(res) + "/"
                + resources.getResourceEntryName(res);
    }

    public static String getUri(String path) {
        return "file://" + path;
    }
}
