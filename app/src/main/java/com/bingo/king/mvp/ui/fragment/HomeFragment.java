package com.bingo.king.mvp.ui.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.bingo.king.R;
import com.bingo.king.app.base.BaseFragment;
import com.bingo.king.app.utils.CategoryType;
import com.bingo.king.app.utils.ViewBinder;
import com.bingo.king.di.component.DaggerHomeComponent;
import com.bingo.king.di.module.HomeModule;
import com.bingo.king.mvp.contract.HomeContract;
import com.bingo.king.mvp.presenter.HomePresenter;
import com.bingo.king.mvp.ui.adapter.MianViewPagerAdapter;
import com.bingo.king.mvp.ui.widget.LoadingPage;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * <首页>
 * Created by adou on 2017/11/6:20:26.
 */

public class HomeFragment extends BaseFragment<HomePresenter> implements HomeContract.View
{
    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.tabs)
    TabLayout tabs;
    @BindView(R.id.mainPager)
    ViewPager mainPager;
    private List<Fragment> mFragments;


    @Override
    protected int getContentLayoutId()
    {
        return R.layout.fragment_home;
    }

    @Override
    public void initComponent()
    {
        DaggerHomeComponent.builder()
                .appComponent(getAppComponent())
                .homeModule(new HomeModule(this))
                .build()
                .inject(this);
    }


    @Override
    public void initData(Bundle savedInstanceState)
    {
        ViewBinder.setTextView(toolbar_title,"首页");
        if (mFragments == null)
        {
            mFragments = new ArrayList<>();
            mFragments.add(CategoryFragment.newInstance(CategoryType.IOS_STR));
            mFragments.add(CategoryFragment.newInstance(CategoryType.QIAN_STR));
            mFragments.add(CategoryFragment.newInstance(CategoryType.ANDROID_STR));
            mFragments.add(CategoryFragment.newInstance(CategoryType.IOS_STR));
            mFragments.add(CategoryFragment.newInstance(CategoryType.QIAN_STR));
        }
        mainPager.setOffscreenPageLimit(3);
        mainPager.setAdapter(new MianViewPagerAdapter(getChildFragmentManager(), mFragments));
        tabs.setupWithViewPager(mainPager);
    }

    @Override
    protected void onFragmentFirstVisible()
    {
        super.onFragmentFirstVisible();
        setState(LoadingPage.STATE_SUCCESS);
    }


    @Override
    public void hidePullLoading()
    {

    }

    @Override
    public void showMessage(String message)
    {
        showSnackbar(message);
    }

    @Override
    protected void retryRequestData()
    {

    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mainPager = null;
        tabs = null;
    }

}
