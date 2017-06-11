package com.newjourney.video.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.newjourney.video.R;
import com.newjourney.video.ui.function.FunctionActivity;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private ListView videoListView;
    private VideoListAdapt mVideoAdapt;
    private String _downloadedPath;
    private LocalBroadcastManager _manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mVideoAdapt = new VideoListAdapt(getLayoutInflater());
        videoListView = (ListView)findViewById(R.id.all_video_list);
        videoListView.setAdapter(mVideoAdapt);

        mVideoAdapt.loadVideoList();

        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoAlbum videoAlbum = (VideoAlbum)parent.getItemAtPosition(position);
                MainActivity.this.onVideoAlbumClick(videoAlbum);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.newjourney.video.download.finish");
        filter.addAction("com.newjourney.video.download.clear");
        _manager = LocalBroadcastManager.getInstance(this);
        _manager.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().compareToIgnoreCase("com.newjourney.video.download.finish") == 0){
                    mVideoAdapt.loadVideoList();
                }else if(intent.getAction().compareToIgnoreCase("com.newjourney.video.download.clear") == 0){
                    mVideoAdapt.loadVideoList();
                }
            }
        },filter);

        /**
         * JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
         jcVideoPlayerStandard.setUp("http://2449.vod.myqcloud.com/2449_22ca37a6ea9011e5acaaf51d105342e3.f20.mp4"
         , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "嫂子闭眼睛");
         jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");
         */
    }

    public void onVideoAlbumClick(VideoAlbum videoAlbum){
        Intent intent = new Intent(this,VideoListActivity.class);
        intent.putExtra("path",videoAlbum.fullPath);
        startActivity(intent);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_more_function:
                try
                {
                    Intent intent = new Intent(this,FunctionActivity.class);
                    startActivity(intent);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                break;
        }
        return true;
    }

    public class ViewHodler{
        private ImageView _cover;
        private TextView _videoName;

        public ViewHodler(View rootView){
            _cover = (ImageView)rootView.findViewById(R.id.video_cover_imageview);

            WindowManager wm = MainActivity.this.getWindowManager();
            int height = wm.getDefaultDisplay().getWidth() * 9 / 16;

            ViewGroup.LayoutParams param = _cover.getLayoutParams();
            param.height = height;
            _cover.setLayoutParams(param);

            _videoName = (TextView) rootView.findViewById(R.id.video_name);

        }

        public void setData(VideoAlbum album){
            _videoName.setText(album.name);

            if(!TextUtils.isEmpty(album.coverUri) && new File(album.coverUri).exists()){
                _cover.setImageBitmap(BitmapFactory.decodeFile(album.coverUri));
            }
        }
    }

    public class VideoAlbum{
        public String name;
        public String fullPath;
        public String coverUri;
        public Image coverImage;
    }

    public class VideoListAdapt extends BaseAdapter{
        public List<VideoAlbum> mVideos;
        private LayoutInflater mLayoutInflater;

        public VideoListAdapt(LayoutInflater layoutInflater){
            mVideos = new Vector<>();
            mLayoutInflater = layoutInflater;
        }

        public void loadVideoList(){
            mVideos.clear();

            _downloadedPath = getFilesDir().getPath() + "/video";
            File fileObj = new File(_downloadedPath);
            if(!fileObj.exists()){
                fileObj.mkdir();
            }

            File musicFolder = new File(_downloadedPath);
            String[] files = musicFolder.list();
            for(String file : files){
                File folderObj = new File(_downloadedPath,file);
                if(folderObj.isDirectory()){
                    VideoAlbum videoAlbumObj = new VideoAlbum();
                    videoAlbumObj.name = file;
                    videoAlbumObj.fullPath = folderObj.getAbsolutePath();
                    videoAlbumObj.coverUri = folderObj.getAbsolutePath() + "/cover.jpg";
                    mVideos.add(videoAlbumObj);
                }
            }

            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mVideos.size();
        }

        @Override
        public Object getItem(int position) {
            return mVideos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler tag = null;
            if(convertView == null){
                convertView = mLayoutInflater.inflate(R.layout.video_album, parent,false);

                tag = new ViewHodler(convertView);
                convertView.setTag(tag);
            }else{
                tag = (ViewHodler)convertView.getTag();
            }

            if(tag != null){
                tag.setData((VideoAlbum)getItem(position));
            }

            return convertView;
        }
    }
}


