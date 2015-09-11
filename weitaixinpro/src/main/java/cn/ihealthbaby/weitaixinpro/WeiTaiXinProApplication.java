package cn.ihealthbaby.weitaixinpro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.AbstractHttpClientAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.util.Constants;

/**
 * @author by kang on 2015/9/9.
 */
public class WeiTaiXinProApplication extends Application {


    public static WeiTaiXinProApplication app;
    public static User user;
    public boolean isLogin = true;
    public VolleyAdapter mAdapter;
    private AbstractHttpClientAdapter adapter;

    public static WeiTaiXinProApplication getInstance() {
        if (app == null) {
            app = new WeiTaiXinProApplication();
        }
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initUniversalImageLoader();
        initApiManager();

    }

    public AbstractHttpClientAdapter getAdapter() {
        return adapter;
    }

    public void initApiManager() {
        RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
        mAdapter = new VolleyAdapter(getApplicationContext(), Constants.SERVER_URL, requestQueue);
        ApiManager.init(mAdapter);
    }


    public void putValue(String key, String value) {
        SharedPreferences sp = getSharedPreferences("weitaixinpro.data", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getValue(String key, String defValue) {
        SharedPreferences sp = getSharedPreferences("weitaixinpro.data", Context.MODE_PRIVATE);
        String value = sp.getString(key, defValue);
        return value;
    }


    public void initUniversalImageLoader() {
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800)
                .discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100)
                .writeDebugLogs()
                .build();

        ImageLoader.getInstance().init(config);
    }


    public DisplayImageOptions setDisplayImageOptions() {
        DisplayImageOptions options = null;
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.button_monitor_helper)
                .showImageForEmptyUri(R.drawable.button_monitor_helper)
                .showImageOnFail(R.drawable.button_monitor_helper)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new SimpleBitmapDisplayer())
//				.displayer(new RoundedBitmapDisplayer(20))
//				.displayer(new FadeInBitmapDisplayer(100))
                .build();
        return options;
    }


}
