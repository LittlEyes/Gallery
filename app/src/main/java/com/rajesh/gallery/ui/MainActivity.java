package com.rajesh.gallery.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.rajesh.gallery.MyApp;
import com.rajesh.gallery.R;
import com.rajesh.zlbum.PhotoView;

import java.util.ArrayList;

/**
 * 主页面
 *
 * @author zhufeng on 2017/10/22
 */
public class MainActivity extends AppCompatActivity {
    private final String[] picRes = new String[]{
            "http://a.hiphotos.baidu.com/image/pic/item/6609c93d70cf3bc7176dd658db00baa1cd112a10.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/b2de9c82d158ccbf1c48af8e13d8bc3eb0354189.jpg"
    };
    private Button albumBtn;
    private Button changeBtn;
    private PhotoView contentView;
    /**
     * 是否是显示的第一个图片，仅用于contentView的图片点击切换
     */
    private boolean isFirstImage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvents();
    }

    private void initView() {
        contentView = (PhotoView) findViewById(R.id.content);
        albumBtn = (Button) findViewById(R.id.album);
        changeBtn = (Button) findViewById(R.id.change);
        contentView.loadImage(Uri.parse(picRes[0]));
        isFirstImage = true;
    }

    private void initEvents() {
        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Uri> imageUrls = new ArrayList<>();
                imageUrls.add(getUriFromRes(R.mipmap.bg1));
                imageUrls.add(getUriFromRes(R.mipmap.bg2));
                imageUrls.add(Uri.parse(picRes[0]));
                imageUrls.add(Uri.parse(picRes[1]));
                imageUrls.add(getUriFromRes(R.mipmap.bg1));
                imageUrls.add(getUriFromRes(R.mipmap.bg2));
                imageUrls.add(Uri.parse(picRes[0]));
                imageUrls.add(Uri.parse(picRes[1]));
                imageUrls.add(getUriFromRes(R.mipmap.bg1));
                imageUrls.add(getUriFromRes(R.mipmap.bg2));
                imageUrls.add(Uri.parse(picRes[0]));
                imageUrls.add(Uri.parse(picRes[1]));

                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra("res", imageUrls);

                startActivity(intent);
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstImage) {
                    contentView.loadImage(Uri.parse(picRes[1]));
                    isFirstImage = false;
                } else {
                    contentView.loadImage(Uri.parse(picRes[0]));
                    isFirstImage = true;
                }
            }
        });
    }

    /**
     * 得到资源文件中图片的Uri
     *
     * @param id
     * @return
     */
    public Uri getUriFromRes(int id) {
        boolean isFresco = true;
        if (isFresco) {
            //Fresco针对res的处理方式有所不同
            return Uri.parse("res://" + MyApp.getAppContext().getPackageName() + "/" + id);
        } else {
            Resources resources = MyApp.getAppContext().getResources();
            String path = ContentResolver.SCHEME_ANDROID_RESOURCE + "://"
                    + resources.getResourcePackageName(id) + "/"
                    + resources.getResourceTypeName(id) + "/"
                    + resources.getResourceEntryName(id);
            return Uri.parse(path);
        }
    }
}
