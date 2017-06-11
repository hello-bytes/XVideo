package com.newjourney.video.ui.download;

import com.newjourney.video.model.VideoAlbum;

import java.util.Vector;

/**
 * Created by shishengyi on 2017/5/4.
 */
public class SelectInterface {

    interface View {
    }

    interface Presenter {
        void downloadFiles(Vector<VideoAlbum> files);
        void loadFileListAsync(final String url, LoadFileCallback callback);
    }

    interface LoadFileCallback{
        void onGetFileList(Vector<VideoAlbum> files);
    }

}
