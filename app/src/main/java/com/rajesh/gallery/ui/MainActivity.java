package com.rajesh.gallery.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.rajesh.gallery.R;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private Button picBtn;
    private Button galleryBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvents();
    }

    private void initView(){
        picBtn = (Button) findViewById(R.id.pic_btn);
        galleryBtn = (Button) findViewById(R.id.gallery_btn);
    }

    private void initEvents(){
        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ImageZoomActivity.class);
                intent.putExtra("image", "图片资源");
                intent.putExtra("type", "url");
                intent.putExtra("name", "图片");
                startActivity(intent);
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add("图片资源");
                imageUrls.add("图片资源");
                imageUrls.add("图片资源");
                imageUrls.add("图片资源");
                imageUrls.add("图片资源");
                imageUrls.add("图片资源");
                imageUrls.add("图片资源");
                Intent intent = new Intent(mContext, GalleryActivity.class);
                intent.putExtra("imageUrl", imageUrls);
                intent.putExtra("name", "相册");

                startActivity(intent);
            }
        });
    }
}
