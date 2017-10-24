package com.rajesh.gallery.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.rajesh.gallery.MyApp;
import com.rajesh.gallery.R;
import com.rajesh.gallery.ui.view.album.ZoomImageView;

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
    private ZoomImageView contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvents();
    }

    private void initView() {
        contentView = (ZoomImageView) findViewById(R.id.content);
        albumBtn = (Button) findViewById(R.id.album);
        changeBtn = (Button) findViewById(R.id.change);
        contentView.loadImage(1080, 1920, Uri.parse(picRes[0]));
        contentView.setTag(false);
    }

    private void initEvents() {
        albumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imageUrls = new ArrayList<>();
                imageUrls.add("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg1);
                imageUrls.add("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg2);
                imageUrls.add(picRes[0]);
                imageUrls.add(picRes[1]);
                imageUrls.add("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg1);
                imageUrls.add("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg2);
                imageUrls.add(picRes[0]);
                imageUrls.add(picRes[1]);
                imageUrls.add("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg1);
                imageUrls.add("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg2);
                imageUrls.add(picRes[0]);
                imageUrls.add(picRes[1]);

                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                intent.putExtra("res", imageUrls);
                intent.putExtra("title", "相册");

                startActivity(intent);
            }
        });
        changeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((boolean) contentView.getTag()) {
                    contentView.loadImage(1080, 1920, Uri.parse(picRes[0]));
                    contentView.setTag(false);
                } else {
                    //contentView.getImageView().loadImage(1080, 1920, Uri.parse("res://"+ MyApp.getAppContext().getPackageName()+"/" + R.mipmap.bg2))
                    contentView.loadImage(1080, 1920, Uri.parse(picRes[1]));
                    contentView.setTag(true);
                }
            }
        });
    }
}
