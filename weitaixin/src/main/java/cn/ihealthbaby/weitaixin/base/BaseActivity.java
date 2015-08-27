package cn.ihealthbaby.weitaixin.base;

import android.app.Activity;
import android.os.Bundle;

import com.android.volley.RequestQueue;
import com.orhanobut.logger.Logger;

import cn.ihealthbaby.weitaixin.library.data.net.adapter.volley.manager.ConnectionManager;
import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by liuhongjian on 15/7/23 14:58.
 */
public abstract class BaseActivity extends Activity {
    protected RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestQueue = ConnectionManager.getInstance().getRequestQueue(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.i("hello");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.d(this.getClass().getName(), "requestQueue.cancelAll:%s", getRequestTag());
        requestQueue.cancelAll(getRequestTag());
    }

    protected Object getRequestTag() {
        return this;
    }
}
