package com.rajesh.gallery;

import android.content.ContentResolver;
import android.content.res.Resources;

/**
 * desc
 *
 * @author zhufeng on 2018/1/29
 */
public class ImageData {
    public static String[] picUrls = new String[]{
            "http://a.hiphotos.baidu.com/image/pic/item/6609c93d70cf3bc7176dd658db00baa1cd112a10.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbf1c48af8e13d8bc3eb0354189.jpg"
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
