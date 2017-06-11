package com.newjourney.video.ui.download;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.text.TextUtilsCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.newjourney.video.R;


/**
 * Created by shishengyi on 2017/5/4.
 */
public class SelectActivity extends AppCompatActivity {

    SelectView _selctView;
    SelectPresenter _selectPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_select);

        String url = null;
        if(getIntent() != null){
            url = getIntent().getStringExtra("url");
        }

        if(TextUtils.isEmpty(url)){
            finish();
            return;
        }

        _selectPresenter = new SelectPresenter(this);

        _selctView = new SelectView(this);
        _selctView.setUrl(url);
        _selctView.setPresenter(_selectPresenter);
        _selctView.setView(findViewById(R.id.select_root_view));
        _selctView.start();

        setTitle("需要下载的文件");

    }

}
