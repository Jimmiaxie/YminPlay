package com.min.localmusic;

import com.min.yminplay.Other;
import com.min.yminplay.R;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MusicPlayActivity extends Activity implements OnClickListener {

	private static MusicPlayActivity instance = null;
	private TextView playing_name, playing_artist;
	private TextView playing_currenttime, playing_alltime;
	private SeekBar playing_seekbar;
	private ImageButton playing_previous, playing_play, playing_next,
			playing_pause, playing_order, playing_cycle, playing_single;

	private MusicService musicservice;
	public static Handler musichandler;

	// 生成ServiceConnection对象，用于Service绑定
	private ServiceConnection conn = new MyConnection();
	private int musicPosition; // 标识当前音乐的位置
	private Intent intent;
	private int musiclength = 0; // 音乐时长

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_musicplaying);
		// actionbar的操作
		getActionBar().setDisplayHomeAsUpEnabled(false);
		getActionBar().setDisplayShowHomeEnabled(false);
		getActionBar().hide();
		playing_name = (TextView) findViewById(R.id.playing_name);
		playing_artist = (TextView) findViewById(R.id.playing_artist);
		playing_currenttime = (TextView) findViewById(R.id.playing_currenttime);
		playing_alltime = (TextView) findViewById(R.id.playing_alltime);
		playing_seekbar = (SeekBar) findViewById(R.id.playing_seekbar);
		playing_previous = (ImageButton) findViewById(R.id.playing_previous);
		playing_play = (ImageButton) findViewById(R.id.playing_play);
		playing_pause = (ImageButton) findViewById(R.id.playing_pause);
		playing_order = (ImageButton) findViewById(R.id.playing_order);
		playing_cycle = (ImageButton) findViewById(R.id.playing_cycle);
		playing_single = (ImageButton) findViewById(R.id.playing_single);
		playing_next = (ImageButton) findViewById(R.id.playing_next);

		playing_previous.setOnClickListener(this);
		playing_next.setOnClickListener(this);
		playing_play.setOnClickListener(this);
		playing_pause.setOnClickListener(this);
		playing_single.setOnClickListener(this);
		playing_cycle.setOnClickListener(this);
		playing_order.setOnClickListener(this);
		instance = this;
		// 得到MusicListFragment传过来的要播放的音乐在列表的位置
		musicPosition = getIntent().getIntExtra("musicPosition", 0);
		// 进度条的一系列操作
		playing_seekbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					@Override
					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						if (fromUser) {
							musicservice.Skip(progress);
						}
					}
				});
		// 接受子线程发送的数据，并配合主线程更新UI界面
		musichandler = new Handler() {

			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				// 正在播放，改变进度条和当前播放的时间
				case MusicService.PLAYING:
					int currenttime = musicservice.getMusicDuration();
					playing_seekbar.setProgress(currenttime);
					playing_currenttime.setText(Other
							.ChangeTimeFormat(currenttime));
					break;
				// 播放完成
				case MusicService.FINISH:
					playing_play.setVisibility(View.VISIBLE);
					playing_pause.setVisibility(View.GONE);
					break;
				case MusicService.START:
					musiclength = msg.arg1;
					playing_alltime
							.setText(Other.ChangeTimeFormat(musiclength));
					playing_seekbar.setMax(musiclength);
					InitView();
					break;
				default:
					break;
				}
			}
		};
		// 绑定服务
		intent = new Intent(MusicPlayActivity.this, MusicService.class);
		bindService(intent, conn, Context.BIND_AUTO_CREATE);
		startService(intent);
	}

	// 初始化工作
	private void InitView() {
		playing_name.setText(musicservice.getMusicName());
		playing_artist.setText(musicservice.getMysicArtist());

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();  //解除服务
		unbindService(conn);
		musicservice = null;
	}

	/*
	 * 选择播放模式的时候，初始为顺序播放，然后循环播放--单一循环 (non-Javadoc) 播放与暂停的图标相互切换
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.playing_previous: // 上一首
			musicservice.Previous();
			playing_pause.setVisibility(View.VISIBLE);
			playing_play.setVisibility(View.GONE);
			break;
		case R.id.playing_next: // 下一首
			musicservice.Next();
			playing_pause.setVisibility(View.VISIBLE);
			playing_play.setVisibility(View.GONE);
			break;
		case R.id.playing_play: // 播放
			playing_pause.setVisibility(View.VISIBLE);
			playing_play.setVisibility(View.GONE);
			musicservice.Play();
			break;
		case R.id.playing_pause: // 暂停
			playing_pause.setVisibility(View.GONE);
			playing_play.setVisibility(View.VISIBLE);
			musicservice.Pause();
			break;
		case R.id.playing_order: // 顺序播放
			playing_order.setVisibility(View.GONE);
			playing_single.setVisibility(View.GONE);
			playing_cycle.setVisibility(View.VISIBLE);
			musicservice.setPlayMode(MusicService.CYCLE_PLAY);
			Toast.makeText(this, "顺序播放", 1000).show();
			break;
		case R.id.playing_single: // 单曲循环
			playing_single.setVisibility(View.GONE);
			playing_cycle.setVisibility(View.GONE);
			playing_order.setVisibility(View.VISIBLE);
			musicservice.setPlayMode(MusicService.ORDER_PLAY);
			Toast.makeText(this, "单曲循环", 1000).show();
			break;
		case R.id.playing_cycle: // 循环播放
			playing_cycle.setVisibility(View.GONE);
			playing_order.setVisibility(View.GONE);
			playing_single.setVisibility(View.VISIBLE);
			musicservice.setPlayMode(MusicService.SINGLE_PLAY);
			Toast.makeText(this, "循环播放", 1000).show();
			break;
		default:
			break;
		}
	}

	// 连接服务
	class MyConnection implements ServiceConnection {

		/***
		 * 被绑定时，该方法将被调用 本例通过Binder对象获得Service对象本身
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			musicservice = ((MusicService.LocalBinder) service).getService();
			if (musicservice != null) {
				musicservice.PlayOtherMusic(musicPosition);
			}

		}

		/***
		 * 绑定非正常解除时，如Service服务被异外销毁时，该方法将被调用 将Service对象置为空
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {

			musicservice = null;
		}

	}
}
