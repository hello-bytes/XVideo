package com.newjourney.video.ui.function;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.newjourney.video.R;


/**
 * Created by shishengyi on 2017/5/4.
 */
public class FunctionActivity extends AppCompatActivity {

    private  FunctionView _view;
    private  FunctionPresenter _presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);

        _view = new FunctionView(this);
        _view.initView(findViewById(R.id.function_root_view));

        _presenter = new FunctionPresenter();

        setTitle("功能大全");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        _view.onActivityResult(requestCode, resultCode, data);
        /*if(requestCode == ACTIVITY_RESULTCODE_SCAN){
            if (data != null) {
                final String token = data.getStringExtra("result");
                if(TextUtils.isEmpty(token)){
                    DialogUtil.showTips(this, "扫描异常", "没有扫描到任何内容");
                }else{
                    Intent intent = new Intent(this,ScanResultActivity.class);
                    intent.putExtra("scanresult",token);
                    intent.putExtra("forceinsatll",true);
                    startActivity(intent);
                }
            }
        }*/

    }
}
