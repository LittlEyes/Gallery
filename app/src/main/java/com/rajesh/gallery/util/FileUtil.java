package com.rajesh.gallery.util;

import android.app.Activity;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import com.rajesh.gallery.MyApp;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * 文件工具类
 *
 * @author zhufeng on 2017/3/1.
 */
public class FileUtil {
    public static final String DIRECTORY_IMAGES = "images";
    public static final String DIRECTORY_ROOT = MyApp.getAppContext().getDir(DIRECTORY_IMAGES, MODE_PRIVATE).getAbsolutePath();

    /**
     * 创建文件夹
     *
     * @param path
     * @return
     */
    public static File createSDDir(String path) {
        File dir = new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        return dir;
    }

    /**
     * 获取正常运行的存储设备路径
     * @param activity
     * @return
     */
    public static List<String> getStoragePath(Activity activity) {
        List<String> storagePathList = new ArrayList<>();
        try {
            StorageManager sm = (StorageManager) activity.getSystemService(Activity.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            String[] paths = (String[]) getVolumePathsMethod.invoke(sm);
            for(int i=0;i<paths.length;i++){
                if(getStorageState(activity, paths[i]).equals(Environment.MEDIA_MOUNTED)){
                    storagePathList.add(paths[i]);
                }
            }
        } catch (Exception e) {
            Log.e("Storage", "error");
        }
        return storagePathList;
    }

    public static String getStorageState(Activity activity, String path) {
        try {
            StorageManager sm = (StorageManager) activity.getSystemService(Activity.STORAGE_SERVICE);
            Method getVolumeStateMethod = StorageManager.class.getMethod("getVolumeState", new Class[] {String.class});
            String state = (String) getVolumeStateMethod.invoke(sm, path);
            return state;
        } catch (Exception e) {
            Log.e("Storage state", "error");
        }
        return "";
    }
}
