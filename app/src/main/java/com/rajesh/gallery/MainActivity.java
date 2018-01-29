package com.rajesh.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.rajesh.zlbum.ui.AlbumActivity;
import com.rajesh.zlbum.widget.PhotoView;

import java.util.ArrayList;

/**
 * 主页面
 *
 * @author zhufeng on 2017/10/22
 */
public class MainActivity extends AppCompatActivity {
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
        contentView.loadImage(Uri.parse(ImageData.picUrls[0]));
        isFirstImage = true;
    }

    private void initEvents() {
        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add(ImageData.getUriForFresco(R.mipmap.bg1));
                imageUrls.add(ImageData.getUriForFresco(R.mipmap.bg2));
                imageUrls.add(ImageData.picUrls[0]);
                imageUrls.add(ImageData.picUrls[1]);
                imageUrls.add(ImageData.getUri(ImageData.picPaths[0]));
                imageUrls.add(ImageData.getUri(ImageData.picPaths[1]));
                imageUrls.add(ImageData.getUriForFresco(R.mipmap.bg1));
                imageUrls.add(ImageData.getUriForFresco(R.mipmap.bg2));
                imageUrls.add(ImageData.picUrls[0]);
                imageUrls.add(ImageData.picUrls[1]);
                imageUrls.add(ImageData.getUri(ImageData.picPaths[0]));
                imageUrls.add(ImageData.getUri(ImageData.picPaths[1]));

                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra(AlbumActivity.INTENT_IMAGE, imageUrls);
                startActivity(intent);
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFirstImage) {
                    contentView.loadImage(Uri.parse(ImageData.picUrls[1]));
                    isFirstImage = false;
                } else {
                    contentView.loadImage(Uri.parse(ImageData.picUrls[0]));
                    isFirstImage = true;
                }
            }
        });
    }
}
