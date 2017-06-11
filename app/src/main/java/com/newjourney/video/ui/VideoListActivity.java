package com.newjourney.video.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.newjourney.video.R;
import com.newjourney.video.model.VideoFileItem;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerManager;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

/**
 * Created by shishengyi on 2017/5/16.
 */
public class VideoListActivity extends AppCompatActivity {

    private String _videoPath;
    private ListView _videoList;
    private VideoListAdapt _videoListAdapt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        _videoList = (ListView)findViewById(R.id.video_list_view);

        _videoListAdapt = new VideoListAdapt(getLayoutInflater());
        _videoList.setAdapter(_videoListAdapt);

        _videoPath = getIntent().getStringExtra("path");
        //loadAllPath(_videoPath);

        _videoListAdapt.loadVideoList(_videoPath);

        _videoList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                VideoItem vi = (VideoItem)parent.getItemAtPosition(position);

                JCVideoPlayerStandard.startFullscreen(VideoListActivity.this, JCVideoPlayerStandard.class, vi.fullPath, vi.seq + "");

                //Intent intent = new Intent(VideoListActivity.this,PlayerActivity.class);
                //intent.putExtra("video_path",vi.fullPath);
                //startActivity(intent);
                //JCFullScreenActivity.startActivity(VideoListActivity.this,
                  //      vi.fullPath,
                    //    JCVideoPlayerStandard.class, vi.seq + "");


                //JCVideoPlayerStandard.startFullscreen();
                /*JCVideoPlayerStandard jcVideoPlayerStandard = (JCVideoPlayerStandard) findViewById(R.id.videoplayer);
                jcVideoPlayerStandard.setUp(vi.fullPath
                        , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "~~~");
                //jcVideoPlayerStandard.thumbImageView.setImage("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640");*/
            }
        });
    }

    public class VideoItem{
        public int seq;
        public String fullPath;
    }

    public class ViewHodler{
        private TextView _cover;
        public ViewHodler(View rootView){
            _cover = (TextView) rootView.findViewById(R.id.video_file_seq);
        }

        public void setData(VideoItem album){
            _cover.setText("第 " + album.seq + " 集");
        }
    }

    class VideoItemComparator implements Comparator
    {

        @Override
        public int compare(Object o1, Object o2) {
            VideoItem itemLeft = (VideoItem)o1;
            VideoItem itemRight = (VideoItem)o2;

            if(itemLeft.seq < itemRight.seq){
                return -1;
            }else if(itemLeft.seq == itemRight.seq){
                return 0;
            }else{
                return 1;
            }
        }
    }

    public class VideoListAdapt extends BaseAdapter {
        public List<VideoItem> mVideos;
        private LayoutInflater mLayoutInflater;
        private String _videoFolder;

        public VideoListAdapt(LayoutInflater layoutInflater){
            mVideos = new Vector<>();
            mLayoutInflater = layoutInflater;
        }

        public void loadVideoList(String videoPath){
            _videoFolder = videoPath;
            mVideos.clear();

            File fileObj = new File(_videoFolder);
            if(!fileObj.exists()){
                fileObj.mkdir();
            }

            File musicFolder = new File(_videoFolder);
            String[] files = musicFolder.list();
            if(files == null){
                notifyDataSetChanged();
                return;
            }

            for(String file : files){
                if(!(file.endsWith(".mov") || file.endsWith(".rmvb") || file.endsWith(".mp4"))){
                    continue;
                }

                File folderObj = new File(_videoFolder,file);

                    try{
                        VideoItem videoAlbumObj = new VideoItem();
                        String seq = file.substring(0,file.lastIndexOf("."));
                        videoAlbumObj.seq = Integer.valueOf(seq);
                        videoAlbumObj.fullPath = folderObj.getAbsolutePath();

                        mVideos.add(videoAlbumObj);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                Collections.sort(mVideos,new VideoItemComparator());
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
                convertView = mLayoutInflater.inflate(R.layout.video_file_item, parent,false);

                tag = new ViewHodler(convertView);
                convertView.setTag(tag);
            }else{
                tag = (ViewHodler)convertView.getTag();
            }

            if(tag != null){
                tag.setData((VideoItem)getItem(position));
            }

            return convertView;
        }
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}
