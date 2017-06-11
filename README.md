# 本地电视剧视频播放器

这个主要是给家里老人人用的一款应用，随着智能机的普及，家里人都用上了智能机，但对于老人来说，由于不识字，很难使用手机来看视频，听音乐。为了解决看视频的问题，写了这个应用

# 截图

运行阄果如下所示：


![主界面](https://raw.githubusercontent.com/shishengyi/XVideo/master/screen/main.png) ![任务界面](https://raw.githubusercontent.com/shishengyi/XVideo/master/screen/function.png) ![同步下载电视剧](https://raw.githubusercontent.com/shishengyi/XVideo/master/screen/sync.png) ![删除电视剧](https://raw.githubusercontent.com/shishengyi/XVideo/master/screen/delete.png)


# 用法
1，进入功脂列表界面
2，点击进入同步视频，然后程序默认会从<http://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/xvideo/videos.json>下载一个json文件，json文件列出了服务器上已有的电视据信息，如截图所示，我准备了2个电视据。

后续json可能会失效，所以在这里列出json的结构，如下所示:
![Json结构](https://raw.githubusercontent.com/shishengyi/XVideo/master/screen/json.png)

示例json如下:

```
[{"name":"渴望","cover":"http://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/xvideo/%E6%B8%B4%E6%9C%9B/1_1492870703_7584106.jpg","videos":[{"key":1,"url":"http://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/xvideo/%E6%B8%B4%E6%9C%9B/1.rmvb"}]},{"name":"女儿红","cover":"http://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/xvideo/%E5%A5%B3%E5%84%BF%E7%BA%A2/nvh.jpeg","videos":[{"key":1,"url":"http://codingsky.oss-cn-hangzhou.aliyuncs.com/cdn/xvideo/%E5%A5%B3%E5%84%BF%E7%BA%A2/1.mp4"}]}]

```

3，点击下载，后台服务会根据json的信息，下载电视据。





