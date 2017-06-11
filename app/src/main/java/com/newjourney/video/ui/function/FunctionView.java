package com.newjourney.video.ui.function;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.newjourney.video.R;
import com.newjourney.video.ui.download.SelectActivity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Created by shishengyi on 2017/5/4.
 */
public class FunctionView {
    private String _deleteFolder;

    private View _rootView;
    private ListView _functionListView;
    private FunctionAdapt _functionAdapt;

    private static final int SCAN_TO_ADDMUSIC = 1;
    private static final int CLEAR_ALL_MUSIC = 2;
    private static final int DELETE_VIDEO = 3;

    private static final int ACTIVITY_RESULTCODE_SCAN = 100;

    private WeakReference<Activity> _host;

    public FunctionView(Activity host){
        _host = new WeakReference<Activity>(host);
    }

    public void initView(View rootView){
        _rootView = rootView;
        _functionAdapt = new FunctionAdapt();

        _functionListView = (ListView) _rootView.findViewById(R.id.func_list_view);
        _functionListView.setAdapter( _functionAdapt );
        _functionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onFunctionClick((FuncitonItem)parent.getItemAtPosition(position));
            }
        });
    }

    public void onFunctionClick(FuncitonItem item){
        switch ((int)item.functionId){
            case SCAN_TO_ADDMUSIC:
                Intent intent = new Intent(_host.get(),SelectActivity.class);
                intent.putExtra("url","https://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/xvideo/videos_example.json");
                _host.get().startActivity(intent);
                break;
            case DELETE_VIDEO:
                onDeleteVideo();
                break;
            case CLEAR_ALL_MUSIC:
                clearAll();
                break;
        }
    }

    public void onDeleteVideo(){
        ArrayList<String> folders = new ArrayList<>();

        String path  = _host.get().getFilesDir().getPath() + "/video/";
        File[] files = new File(path).listFiles();
        if(files != null){
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isFile()) {
                    folders.add(files[i].getName());
                }
            }
        }

        if(folders.size() == 0){
            Toast.makeText(_host.get(),"没有视频",Toast.LENGTH_LONG).show();
            return;
        }


        final CharSequence[] items = folders.toArray(new CharSequence[folders.size()]);
        _deleteFolder =  items[0].toString();
        AlertDialog.Builder builder = new AlertDialog.Builder(_host.get());
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                _deleteFolder =  items[which].toString();
                //Toast.makeText(_host.get(), items[which], Toast.LENGTH_SHORT).show();
            }
        });
        builder.setTitle("请选择要删除的目录");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(_host.get(), _deleteFolder, Toast.LENGTH_SHORT).show();
                clearFolder(_deleteFolder);
                dialog.dismiss();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        if(files != null){
            //遍历删除文件夹下的所有文件(包括子目录)
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    //删除子文件
                    flag = deleteFile(files[i].getAbsolutePath());
                    if (!flag) break;
                } else {
                    //删除子目录
                    flag = deleteDirectory(files[i].getAbsolutePath());
                    if (!flag) break;
                }
            }
        }

        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }

    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.isFile() && file.exists()) {
            return file.delete();
        }
        return false;
    }

    private void clearFolder(String folder){
        String path  = _host.get().getFilesDir().getPath() + "/video/" + folder;
        deleteDirectory(path);
        Toast.makeText(_host.get(),"已删除",Toast.LENGTH_LONG).show();
    }

    private void clearAll(){
        String path  = _host.get().getFilesDir().getPath() + "/video";
        File fileObj = new File(path);
        String[] files = fileObj.list();
        for(String file : files){
            File currentDeleteFile = new File(path,file);
            if(currentDeleteFile.isFile()){
                currentDeleteFile.delete();
            }else{
                deleteDirectory(path + "/" + file);
            }
        }

        Intent startIntent = new Intent("com.newjourney.video.download.clear");
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(_host.get());
        manager.sendBroadcast(startIntent);

        Toast.makeText(_host.get(),"已清除",Toast.LENGTH_LONG).show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_RESULTCODE_SCAN){
            if (data != null) {
                final String token = data.getStringExtra("result");
                if(TextUtils.isEmpty(token) || !token.startsWith("http")){
                    //DialogUtil.showTips(this, "扫描异常", "没有扫描到任何内容");
                }else{
                    Intent intent = new Intent(_host.get(),SelectActivity.class);
                    intent.putExtra("url",token);
                    _host.get().startActivity(intent);
                }
            }
        }
    }

    public class FuncitonItem{
        public String name;
        public int iconResourceId;
        public long functionId;

        public FuncitonItem(long id, int iconResourceId ,String name){
            this.functionId = id;
            this.name = name;
            this.iconResourceId = iconResourceId;
        }
    }

    public class ViewHodler{
        private ImageView _imageView;
        private TextView _textView;
        private long _functionId;
        public ViewHodler(View rootView){
            _imageView = (ImageView) rootView.findViewById(R.id.function_image);
            _textView = (TextView)rootView.findViewById(R.id.function_text);
        }

        public void initWithData(FuncitonItem item){
            _textView.setText(item.name);
            _imageView.setImageResource(item.iconResourceId);
            _functionId = item.functionId;
        }
    }

    public class FunctionAdapt extends BaseAdapter {
        private LayoutInflater _ayoutInflater;
        private Vector<FuncitonItem> _items;
        public FunctionAdapt(){
            _items = new Vector<>();
            _items.add(new FuncitonItem(SCAN_TO_ADDMUSIC, R.mipmap.add, "同步视频"));
            _items.add(new FuncitonItem(DELETE_VIDEO, R.mipmap.delete, "删除视频"));
            _items.add(new FuncitonItem(CLEAR_ALL_MUSIC, R.mipmap.clear ,"清除本地所有视频"));

            _ayoutInflater = _host.get().getLayoutInflater();
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
            return _items.get(position).functionId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler tag = null;
            if(convertView == null){
                convertView = _ayoutInflater.inflate(R.layout.function_item,parent,false);

                tag = new ViewHodler(convertView);
                convertView.setTag(tag);
            }else{
                tag = (ViewHodler)convertView.getTag();
            }

            if(tag != null){
                tag.initWithData((FuncitonItem)getItem(position));
            }

            return convertView;
        }
    }
}
