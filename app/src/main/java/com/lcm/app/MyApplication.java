package com.lcm.app;

import com.blankj.utilcode.utils.NetworkUtils;
import com.blankj.utilcode.utils.Utils;
import com.lcm.android.base.BaseApplication;
import com.lcm.app.dagger.component.AppComponent;
import com.lcm.app.dagger.component.DaggerAppComponent;
import com.lcm.app.dagger.module.CacheModule;
import com.lcm.app.dagger.module.ServiceModule;

/**
 * ****************************************************************
 * Author: LCM
 * Date: 2017/5/23 下午12:58
 * Desc:
 * *****************************************************************
 */

public class MyApplication extends BaseApplication {
    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(getApplicationContext());
        mAppComponent = DaggerAppComponent.builder()
                .appModule(getAppModule())
                .clientModule(getClientModule())
                .serviceModule(new ServiceModule())
                .cacheModule(new CacheModule())
                .build();
    }

    @Override
    protected String getBaseUrl() {
        return "http://gank.io/api/";
    }

    /**
     * 将AppComponent返回出去,供其它地方使用, AppComponent接口中声明的方法返回的实例, 在getAppComponent()拿到对象后都可以直接使用
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return mAppComponent;
    }


}