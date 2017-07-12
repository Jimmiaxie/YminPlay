package com.min.yminplay;

import com.min.localmusic.MusicListFragment;
import com.min.localvideo.VideoListFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;

public class MainActivity extends FragmentActivity implements
		ActionBar.TabListener {
	private ViewPager mViewPager;
	private ViewPagerAdapter mViewPagerAdapter;// 自定义ViewPagerAdapter继承FragmentPagerAdapter

	// 定义几个常量
	private static int index = 2;
	private static final int zero = 0;
	private static final int first = 1;
	MusicListFragment fragment1 = new MusicListFragment();
	VideoListFragment fragment2 = new VideoListFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setUpActionBar();// 设置 ActionBar 的样式，如：无回退按键、无标题等
		setUpViewPager();
		setUpTabs(); // 设置actionbar的tab标签
	}
	/*
	 *  设置actionbar的tab标签
	 */
	private void setUpTabs() {
		ActionBar actionbar = getActionBar();
		for (int i = 0; i < mViewPagerAdapter.getCount(); i++) {
			actionbar.addTab(actionbar.newTab()
					.setText(mViewPagerAdapter.getPageTitle(i))
					.setTabListener(this)// tab设置监听
					);

		}
	}

	/*
	 * 设置viewpaper滑动式的相关操作
	 */
	@SuppressWarnings("deprecation")
	private void setUpViewPager() {
		mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.viewpager);
		mViewPager.setAdapter(mViewPagerAdapter);// 加载适配器布局
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(final int arg0) {
				final ActionBar actionBar = getActionBar();// 获取actionbar对象
				actionBar.setSelectedNavigationItem(arg0);
			}

			@Override
			public void onPageScrolled(final int arg0, final float arg1,
					final int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(final int state) {
				switch (state) {
				case ViewPager.SCROLL_STATE_IDLE:
					break;
				case ViewPager.SCROLL_STATE_DRAGGING:
					break;
				case ViewPager.SCROLL_STATE_SETTLING:
					break;
				default:
					break;
				}
			}
		});

	}
/*
 * 设置 ActionBar 的样式，如：无回退按键、无标题等
 */
	private void setUpActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);// Tab导航模式
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			switch (arg0) {
			case zero:
				return fragment1;
			case first:
				return fragment2;
			}
			throw new IllegalStateException("No fragment at position " + arg0);

		}

		@Override
		public int getCount() {
			return index;
		}
//导航Tab的文字显示
		@Override
		public CharSequence getPageTitle(int position) {
			String PageTitle = null;
			switch (position) {
			case 0:
				PageTitle = "本地音乐";
				break;
			case 1:
				PageTitle = "本地视频";
				break;
			}
			return PageTitle;
		}
	}
	@Override  // 退出程序对话框
    public boolean onKeyDown(int keyCode, KeyEvent event) {   
        if (keyCode == KeyEvent.KEYCODE_BACK) {   
            AlertDialog.Builder builder = new AlertDialog.Builder(this);   
            builder.setTitle("退出YminPlary");   
            builder.setIcon(R.drawable.rate_star_big_on_holo_dark);   
            builder.setMessage("要退出YminPlayer吗？")   
                    .setPositiveButton("确定",   
                            new DialogInterface.OnClickListener() {   
                                public void onClick(DialogInterface dialog,   
                                        int which) {   
                                    finish();   
                                }   
                            }).setNegativeButton("取消", null).show();   
        }   
        return true;   
    }   

}