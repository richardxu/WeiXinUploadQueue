package cn.ihealthbaby.weitaixin;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.android.volley.RequestQueue;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import java.util.Date;
import java.util.UUID;

import cn.ihealthbaby.client.ApiManager;
import cn.ihealthbaby.client.form.AdviceForm;
import cn.ihealthbaby.client.model.AdviceSetting;
import cn.ihealthbaby.client.model.User;
import cn.ihealthbaby.weitaixin.library.data.database.dao.Record;
import cn.ihealthbaby.weitaixin.library.data.database.dao.RecordBusinessDao;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.AbstractHttpClientAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.VolleyAdapter;
import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;
import cn.ihealthbaby.weitaixin.library.util.Constants;
import cn.ihealthbaby.weitaixin.library.util.SPUtil;

/**
 * @author by liuhongjian on 15/7/23 14:09.
 */
public class WeiTaiXinApplication extends Application {


    public static WeiTaiXinApplication app;
//    public static String accountToken;
//    public static String phone_number;
//    public static User user;
    public static AdviceSetting adviceSetting;
    public static AdviceForm adviceForm = new AdviceForm();

//        public boolean isLogin = false;
//public boolean isLogin = true;
    public LocalProductData localProductData=new LocalProductData(); //保存商品
    public VolleyAdapter mAdapter;
    private AbstractHttpClientAdapter adapter;

    public static WeiTaiXinApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        app = this;

        initUniversalImageLoader();

        initApiManager();

        RecordBusinessDao recordBusinessDao = RecordBusinessDao.getInstance(this);

        for (int i = 0; i <30 ; i++) {
            Record record=new Record();
    //      record.setId(Long.parseLong(33233 + ""));
    //      record.setGestationalWeeks(Long.parseLong(1003333+""));
            record.setRecordStartTime(new Date());
            record.setDuration(Long.parseLong(1003333 + ""));
            record.setUploadState(Record.UPLOAD_STATE_LOCAL);
            record.setLocalRecordId(UUID.randomUUID().toString());
            record.setPurposeString("目的好啊");
            record.setFeelingString("心情好");

            record.setUserId(SPUtil.getUserID(this));
            record.setSerialNumber("DSDDSFSFSF");
            record.setUserName("13798988787");

            try {
                long index = recordBusinessDao.insert(record);
                LogUtil.d("recordBusinessDao", "recordBusinessDao==> " + index);
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.d("recordBusinessDao", "recordBusinessDao==> " + e.toString());
            }
        }

    }



    public AbstractHttpClientAdapter getAdapter() {
        return adapter;
    }

    public void initApiManager() {
        RequestQueue requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());
        mAdapter = new VolleyAdapter(getApplicationContext(), Constants.SERVER_URL, requestQueue);
        User user = SPUtil.getUser(this);
        if(user!=null){
            String accountToken = user.getAccountToken();
            if (!TextUtils.isEmpty(accountToken)) {
                mAdapter.setAccountToken(accountToken+"");
            }
            LogUtil.d("mAdapter.setAccountToken","mAdapter.setAccountToken==> "+accountToken);
        }
        LogUtil.d("UserAccountToken","UserAccountToken==> "+user);
        ApiManager.init(mAdapter);
    }


    public void putValue(String key, String value) {
        SharedPreferences sp = getSharedPreferences("weitaixin.data", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }


    public String getValue(String key, String defValue) {
        SharedPreferences sp = getSharedPreferences("weitaixin.data", Context.MODE_PRIVATE);
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
