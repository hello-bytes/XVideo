package com.newjourney.video.model;

import com.newjourney.video.model.VideoFileItem;

import java.util.Vector;

/**
 * Created by shishengyi on 2017/5/16.
 */
public class VideoAlbum {

    public String name;
    public String cover;
    public Vector<VideoFileItem> videos;

    public VideoAlbum(){
        videos = new Vector<>();
    }

}
