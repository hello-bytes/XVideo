package com.newjourney.video;

import android.app.Application;

import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;

/**
 * Created by shishengyi on 2017/5/16.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NoHttp.initialize(this);

        Logger.setDebug(true);// 开启NoHttp的调试模式, 配置后可看到请求过程、日志和错误信息。
        Logger.setTag("NoHttpSample");// 设置NoHttp打印Log的tag。
    }

}
