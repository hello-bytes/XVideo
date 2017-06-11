package com.newjourney.video.ui.download;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.newjourney.video.model.VideoAlbum;
import com.newjourney.video.model.VideoFileItem;
import com.newjourney.video.services.DownloadService;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Vector;

/**
 * Created by shishengyi on 2017/5/4.
 */
public class SelectPresenter implements SelectInterface.Presenter {
    private WeakReference<Activity> _host;

    public SelectPresenter(Activity activity){
        _host = new WeakReference<Activity>(activity);
    }

    public void loadFileListAsync(final String url,SelectInterface.LoadFileCallback callback){
        final SelectInterface.LoadFileCallback _callback = callback;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Request<JSONArray> request = NoHttp.createJsonArrayRequest(url);
                Response<JSONArray> response = NoHttp.startRequestSync(request);
                if (response.isSucceed()){
                    try
                    {
                        Vector<VideoAlbum> videoAlubms = new Vector<VideoAlbum>();

                        JSONArray allFiles = response.get();
                        for(int i = 0;i < allFiles.length();i++){
                            VideoAlbum album = new VideoAlbum();
                            JSONObject videoAlbumJson = (JSONObject)allFiles.get(i);
                            album.name = (String) videoAlbumJson.get("name");

                            album.cover = (String) videoAlbumJson.get("cover");

                            JSONArray videoFilesJson = videoAlbumJson.getJSONArray("videos");
                            for(int j = 0;j < videoFilesJson.length();j++){
                                VideoFileItem fileItem = new VideoFileItem();

                                JSONObject vieoFileJson = (JSONObject)videoFilesJson.get(j);
                                int jsonKey = vieoFileJson.getInt("key");
                                String url = vieoFileJson.getString("url");

                                fileItem.key = jsonKey;
                                fileItem.url = url;

                                album.videos.add(fileItem);
                            }

                            videoAlubms.add(album);
                        }
                        if(_callback != null){
                            _callback.onGetFileList(videoAlubms);
                        }
                    }catch (Exception ex){
                        ex.printStackTrace();
                        if(_callback != null){
                            _callback.onGetFileList(null);
                        }
                    }
                }else{
                    if(_callback != null){
                        _callback.onGetFileList(null);
                    }
                }
            }
        }).start();
    }

    public void downloadFiles(Vector<VideoAlbum> albums){
        Toast.makeText(_host.get(),"已加到下载队列",Toast.LENGTH_LONG).show();

        for(VideoAlbum album : albums){
            String path = _host.get().getFilesDir() + "/video";

            String thisPath = path + "/" + album.name;
            File fileObj = new File(thisPath);
            if(!fileObj.exists()){
                fileObj.mkdirs();
            }

            if(!TextUtils.isEmpty(album.cover)){
                Intent intent = new Intent(_host.get(), DownloadService.class);
                intent.setAction(DownloadService.ACTION_DOWNLOAD);
                intent.putExtra("extra_path",thisPath + "/cover.jpg");
                intent.putExtra("extra_url",album.cover);
                _host.get().startService(intent);
            }


            for(int i = 0;i < album.videos.size();i++){
                String url = album.videos.get(i).url;
                String ext = ".mp4";
                if(url.lastIndexOf(".") > 0){
                    ext = url.substring(url.lastIndexOf("."));
                    if(ext.length() > 10){
                        ext = ".mp4";
                    }
                }

                Intent intent = new Intent(_host.get(), DownloadService.class);
                intent.setAction(DownloadService.ACTION_DOWNLOAD);
                intent.putExtra("extra_path",thisPath + "/" + album.videos.get(i).key + ext);
                intent.putExtra("extra_url",url);

                _host.get().startService(intent);
            }
        }
    }
}
