package com.smart.himalaya.config;

public class Constants {

    //获取推荐列表的专辑数量
    public static int COUNT_RECOMMEND = 50;

    //默认列表请求数量
    public static int COUNT_DEFAULT = 50;

    //热词的数量
    public static int COUNT_HOT_WORD = 10;

    //数据库相关的常量
    public static final String DB_NAME = "ximalaya.db";
    //订阅最多个数
    public static final int MAX_SUB_COUNT = 100;
    //最大的历史记录数
    public static final int MAX_HISTORY_COUNT = 100;

    public static final String STR_TITLE = "title";
    public static final String STR_URL = "url";
    public static final String MAKE_COMPLAINTS_URL = "https://support.qq.com/product/167872?d-wx-push=" + (Integer.MAX_VALUE - 1);

    //插件换肤相关
    public static final String SKIN_PREFIX = "skin_";
    public static final String PREF_NAME = "skin_plugin";
    public static final String KEY_PLUGIN_PATH = "plugin_path";
    public static final String KEY_PLUGIN_PKG = "plugin_pkg";
    public static final String KEY_PLUGIN_SUFFIX = "plugin_suffix";

    //播放器通知栏控制相关
    //播放或暂停
    public static final String PLAY_OR_PAUSE = "com.smart.himalaya.android.ACTION_CONTROL_PLAY_OR_PAUSE";
    //上一首
    public static final String PLAY_PRE = "com.smart.himalaya.android.ACTION_CONTROL_PLAY_PRE";
    //下一首
    public static final String PLAY_NEXT = "com.smart.himalaya.android.ACTION_CONTROL_PLAY_NEXT";
    //关闭通知栏并停止播放
    public static final String PLAY_CLOSE = "com.smart.himalaya.android.ACTION_CLOSE";
    //收藏
    public static final String STAR = "com.smart.himalaya.android.ACTION_STAR";
    //打开首页
    public static final String OPEN_HOME = "com.smart.himalaya.android.ACTION_OPEN_HOME";
}
