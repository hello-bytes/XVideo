package com.newjourney.video.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;

import com.yanzhenjie.nohttp.Headers;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.download.DownloadListener;
import com.yanzhenjie.nohttp.download.DownloadRequest;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.channels.FileChannel;
import java.util.Vector;

/**
 * Created by shishengyi on 2017/5/4.
 */
public class DownloadService extends Service {
    public static final String ACTION_DOWNLOAD = "com.newjourney.video.services.download:action_download";
    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_PATH = "extra_path";

    public class DownloadItem{
        public String url;
    }

    private Vector<DownloadItem> _downloadItems;
    private String _downloadedPath;
    private String _downloadedTempPath;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();

        _downloadedPath = this.getFilesDir().getPath() + "/video";
        File fileObj = new File(_downloadedPath);
        if(!fileObj.exists()){
            fileObj.mkdir();
        }

        _downloadedTempPath = this.getCacheDir().getPath();
        _downloadItems = new Vector<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        switch (action){
            case ACTION_DOWNLOAD:
                String url = intent.getStringExtra(EXTRA_URL);
                String path = intent.getStringExtra(EXTRA_PATH);
                downloadUrl(url,path);
                break;
        }
        return 0;
    }

    private String getFileName(String url){
        if(TextUtils.isEmpty(url)){
            return "";
        }

        int index = url.lastIndexOf("/");
        String fileName = url.substring(index);
        fileName = URLDecoder.decode(fileName);
        return fileName;
    }

    private void nioTransferCopy(File source, File target) {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(inStream);
            close(in);
            close(outStream);
            close(out);
        }
    }

    private void close(Closeable closeable){
        if(closeable != null){
            try
            {
                closeable.close();
            }catch (Exception ex){

            }
        }
    }

    public void downloadUrl(String url,String path){
        if(TextUtils.isEmpty(url)){
            return;
        }

        /*File savedFile = new File(_downloadedPath, getFileName(url));
        if(savedFile.exists()){
            //存在就不下
            return;
        }*/

        File savedFile = new File(path);
        if(savedFile.exists()){
            //存在就不下
            return;
        }


        final String downloadFile = savedFile.getAbsolutePath();


        DownloadItem item = new DownloadItem();
        item.url = url;
        _downloadItems.add(item);
        DownloadRequest downloadRequest = NoHttp.createDownloadRequest(url, _downloadedTempPath, savedFile.getName(), false, true);
        NoHttp.getDownloadQueueInstance().add(0, downloadRequest, new DownloadListener() {
            @Override
            public void onDownloadError(int what, Exception exception) {

            }

            @Override
            public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {

            }

            @Override
            public void onProgress(int what, int progress, long fileCount, long speed) {

            }

            @Override
            public void onFinish(int what, String filePath) {
                nioTransferCopy(new File(filePath),new File(downloadFile));

                Intent startIntent = new Intent("com.newjourney.video.download.finish");
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(DownloadService.this);
                manager.sendBroadcast(startIntent);
            }

            @Override
            public void onCancel(int what) {

            }
        });
    }
}
