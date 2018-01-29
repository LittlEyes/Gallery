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
            "http://a.hiphotos.baidu.com/image/pic/item/6609c93d70cf3bc7176dd658db00baa1cd112a10.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbf1c48af8e13d8bc3eb0354189.jpg"
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
