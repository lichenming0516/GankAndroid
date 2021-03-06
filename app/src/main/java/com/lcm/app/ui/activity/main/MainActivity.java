package com.lcm.app.ui.activity.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.feedback.FeedbackAgent;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.SnackbarUtils;
import com.bumptech.glide.Glide;
import com.lcm.app.R;
import com.lcm.app.base.MvpActivity;
import com.lcm.app.dagger.component.AppComponent;
import com.lcm.app.dagger.component.DaggerActivityComponent;
import com.lcm.app.data.Contract;
import com.lcm.app.ui.activity.feedback.FeedBackActivity;
import com.lcm.app.ui.activity.login.LoginActivity;
import com.lcm.app.ui.activity.search.SearchActivity;
import com.lcm.app.ui.activity.web.WebActivity;
import com.lcm.app.ui.fragment.allgank.AllGankFragment;
import com.lcm.app.ui.fragment.funny.FunnyFragment;
import com.lcm.app.ui.fragment.recent.RecentFragment;
import com.lcm.app.ui.fragment.welfare.WelfareFragment;
import com.lcm.app.ui.fragment.zhihu.ZhihuFragment;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import jp.wasabeef.glide.transformations.BlurTransformation;


public class MainActivity extends MvpActivity<MainPresenter> implements MainView, NavigationView.OnNavigationItemSelectedListener, Toolbar.OnMenuItemClickListener, View.OnClickListener {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appBarLayout)
    AppBarLayout appBarLayout;

    @BindView(R.id.navigation)
    NavigationView navigation;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.frame_layout)
    FrameLayout frameLayout;
    @BindView(R.id.floating_button)
    FloatingActionButton floatingButton;


    private List<Fragment> fragmentList;
    private ActionBarDrawerToggle mDrawerToggle;
    private int currentIndex = 0;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private String imgUrl;
    private ImageView ivHeaderBg, ivUserIcon;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout layoutUser;

    private boolean isLogin;

    @Override
    protected int rootView() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        imgUrl = getIntent().getStringExtra("imgUrl");

        fragmentList = new ArrayList<>();
        fragmentList.add(RecentFragment.newInstance());
        fragmentList.add(AllGankFragment.newInstance());
        fragmentList.add(WelfareFragment.newInstance());
        fragmentList.add(ZhihuFragment.newInstance());

        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.inflateMenu(R.menu.menu_base_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //创建返回键，并实现打开关/闭监听
        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.syncState();
        drawerLayout.addDrawerListener(mDrawerToggle);

        View headerView = navigation.getHeaderView(0);
        ivHeaderBg = (ImageView) headerView.findViewById(R.id.iv_header_bg);
        ivUserIcon = (ImageView) headerView.findViewById(R.id.iv_user_icon);
        tvUserName = (TextView) headerView.findViewById(R.id.tv_user_name);
        tvUserEmail = (TextView) headerView.findViewById(R.id.tv_user_email);
        layoutUser = (LinearLayout) headerView.findViewById(R.id.layout_user);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.frame_layout, fragmentList.get(currentIndex)).commit();

        if (imgUrl != null) {
            Glide.with(this).load(imgUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_launcher)
                    .bitmapTransform(new BlurTransformation(this, 80))
                    .into(ivHeaderBg);
        }


    }


    @Override
    protected void initData() {
        EventBus.getDefault().register(this);
        isLogin = SPUtils.getInstance(Contract.SPNAME).getBoolean(Contract.ISLOGIN, false);
        updateUserInfo();

        navigation.setNavigationItemSelectedListener(this);
        toolbar.setOnMenuItemClickListener(this);
        layoutUser.setOnClickListener(this);
    }

    private void updateUserInfo() {
        tvUserEmail.setVisibility(isLogin ? View.VISIBLE : View.GONE);
        if (isLogin) {
            tvUserEmail.setText(SPUtils.getInstance(Contract.SPNAME).getString(Contract.USEREMAIL));
            tvUserName.setText(SPUtils.getInstance(Contract.SPNAME).getString(Contract.USERNAME));
        } else {
            tvUserName.setText("登录／注册");
        }
    }


    @OnClick(R.id.floating_button)
    public void onFloatingButtonCLick() {
        EventBus.getDefault().post("chooseDaily", "chooseDaily");
    }

    @Override
    protected void setupActivityComponent(AppComponent appComponent) {
        DaggerActivityComponent.builder()
                .appComponent(appComponent)
                .build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_base_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Subscriber(tag = "Login")
    public void login(String str) {
        isLogin = true;
        updateUserInfo();
    }


    public void setFragment(int targetIndex) {
        if (targetIndex == currentIndex) {
            return;
        }
        Fragment targetFragment = fragmentList.get(targetIndex);
        Fragment currentFragment = fragmentList.get(currentIndex);
        fragmentTransaction = fragmentManager.beginTransaction();

        if (!targetFragment.isAdded()) {
            fragmentTransaction.add(R.id.frame_layout, targetFragment).hide(currentFragment).commitAllowingStateLoss();
        } else {
            fragmentTransaction.show(targetFragment).hide(currentFragment).commitAllowingStateLoss();
        }
        currentIndex = targetIndex;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_recent_gank:
                toolbar.setTitle("干货集中营");
                setFragment(0);
                drawerLayout.closeDrawers();
                floatingButton.setVisibility(View.VISIBLE);
                break;

            case R.id.menu_all_gank:
                toolbar.setTitle("分类阅读");
                setFragment(1);
                drawerLayout.closeDrawers();
                floatingButton.setVisibility(View.GONE);
                break;

            case R.id.menu_welfare:
                toolbar.setTitle("福利满满");
                setFragment(2);
                drawerLayout.closeDrawers();
                floatingButton.setVisibility(View.GONE);
                break;

//            case R.id.menu_funny:
//                setFragment(3);
//                drawerLayout.closeDrawers();
//                floatingButton.setVisibility(View.GONE);
//                break;

            case R.id.menu_zhihu:
                toolbar.setTitle("知乎日报");
                setFragment(3);
                drawerLayout.closeDrawers();
                floatingButton.setVisibility(View.GONE);
                break;


            case R.id.menu_feedback:
                drawerLayout.closeDrawers();
                if (!isLogin) {
                    startActivity(new Intent(this, LoginActivity.class));
                } else {
                    startActivity(new Intent(this, FeedBackActivity.class));
                }
                break;

            case R.id.menu_about_me:
                drawerLayout.closeDrawers();
                Intent intent = new Intent(this, WebActivity.class);
                intent.putExtra("url", "http://lichenming.com");
                startActivity(intent);
                break;

            case R.id.menu_logout:
                drawerLayout.closeDrawers();
                if (!isLogin) {
                    SnackbarUtils.with(getSankBarRootView())
                            .setDuration(SnackbarUtils.LENGTH_LONG)
                            .setMessage("你没登录 乱点个啥 ヽ(‘⌒´メ)ノ")
                            .setMessageColor(getResources().getColor(R.color.white))
                            .setBgColor(getResources().getColor(R.color.colorPrimary))
                            .show();
                } else {
                    isLogin = false;
                    SnackbarUtils.with(getSankBarRootView())
                            .setDuration(SnackbarUtils.LENGTH_LONG)
                            .setMessage("退出登录咯 ┐(´-｀)┌")
                            .setMessageColor(getResources().getColor(R.color.white))
                            .setBgColor(getResources().getColor(R.color.colorPrimary))
                            .show();

                    SPUtils.getInstance(Contract.SPNAME).put(Contract.ISLOGIN, false);
                    EventBus.getDefault().post("Logout", "Logout");
                    updateUserInfo();
                }
                break;


        }
        return false;
    }

    private boolean prepareExit = false;

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawers();
        if (!prepareExit) {
            prepareExit = true;
            SnackbarUtils.with(getSankBarRootView())
                    .setDuration(SnackbarUtils.LENGTH_LONG)
                    .setMessage("再按一次退出程序～")
                    .setMessageColor(getResources().getColor(R.color.white))
                    .setBgColor(Color.parseColor("#aa000000"))
                    .show();

            Observable.timer(3000, TimeUnit.MILLISECONDS)
                    .subscribe(aLong -> prepareExit = false, Throwable::printStackTrace);
        } else {
            finish();
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_user:
                drawerLayout.closeDrawers();
                if (!isLogin) {
                    startActivity(new Intent(this, LoginActivity.class));
                }
                break;
        }
    }
}
