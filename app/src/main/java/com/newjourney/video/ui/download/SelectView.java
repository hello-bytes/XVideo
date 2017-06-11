package com.newjourney.video.ui.download;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import com.newjourney.video.R;
import com.newjourney.video.model.VideoAlbum;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Vector;

/**
 * Created by shishengyi on 2017/5/4.
 */
public class SelectView implements SelectInterface.View {

    private String _url;
    private View _rootView;
    private ListView _functionListView;
    private WeakReference<Activity> _host;

    private SelectInterface.Presenter _presenter;

    private SelectableAdapt _selectableAdapt;

    public SelectView(Activity host){
        _host = new WeakReference<Activity>(host);
        _selectableAdapt = new SelectableAdapt();
    }

    public void setView(View rootView){
        _rootView = rootView;
        _functionListView = (ListView) _rootView.findViewById(R.id.select_list_view);
        _functionListView.setAdapter(_selectableAdapt);

        _rootView.findViewById(R.id.download_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_presenter != null){
                    _presenter.downloadFiles(_selectableAdapt.getFiles());
                }
            }
        });
    }

    public void setUrl(String url){
        _url = url;
    }

    public void setPresenter(SelectInterface.Presenter presenter){
        _presenter = presenter;
    }

    public void start(){
        if(_presenter != null){
            _presenter.loadFileListAsync(_url, new SelectInterface.LoadFileCallback() {
                @Override
                public void onGetFileList(Vector<VideoAlbum> files) {
                    final Vector<VideoAlbum> _files = files;
                    _host.get().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            _selectableAdapt.setFiles(_files);
                        }
                    });

                }
            });
        }
    }

    public class ViewHodler{
        private ImageView _imageView;
        private TextView _textView;
        public ViewHodler(View convertView){
            _imageView = (ImageView)convertView.findViewById(R.id.file_icon_image);
            _textView = (TextView)convertView.findViewById(R.id.file_url_text);
        }

        public void initWithData(VideoAlbum item){
            _textView.setText(item.name);
        }
    }

    public class FileItem{
        public String url;
        public int fileIcon;
    }

    public class SelectableAdapt extends BaseAdapter{
        private LayoutInflater _ayoutInflater;
        private Vector<VideoAlbum> _items;
        public SelectableAdapt(){
            _items = new Vector<>();
            _ayoutInflater = _host.get().getLayoutInflater();
        }

        public Vector<VideoAlbum> getFiles(){
            /*Vector<String> result = new Vector<>();

            if(_items != null){
                for(FileItem item : _items){
                    result.add(item.url);
                }
            }

            return result;*/

            return _items;
        }

        public void setFiles(Vector<VideoAlbum> files){
            _items.clear();
            if(files != null){
                for(VideoAlbum file : files){
                    //FileItem fi = new FileItem();
                    //fi.url = file.name;
                    _items.add(file);
                }
            }

            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return _items.size();
        }

        @Override
        public Object getItem(int position) {
            return _items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler tag = null;
            if(convertView == null){
                convertView = _ayoutInflater.inflate(R.layout.selectable_item,parent,false);

                tag = new ViewHodler(convertView);
                convertView.setTag(tag);
            }else{
                tag = (ViewHodler)convertView.getTag();
            }

            if(tag != null){
                tag.initWithData((VideoAlbum)getItem(position));
            }

            return convertView;
        }
    }
}
