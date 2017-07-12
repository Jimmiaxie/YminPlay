package com.min.localmusic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.min.yminplay.Other;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.preference.Preference;
import android.provider.MediaStore;
import android.util.Log;

public class MusicService extends Service {

	public static final int START = 0x0001;
	public static final int PAUSE = 0x0002;
	public static final int RESUME = 0x0003;
	public static final int PLAYING = 0x0004;
	public static final int FINISH = 0x0005;

	public static final String ACTION_START = "ACTION_STAR";
	public static final String ACTION_PAUSE = "ACTION_PAUSE";
	public static final String ACTION_RESUME = "ACTION_RESUME";
	public static final String ACTION_PREVIOUS = "ACTION_PREVIOUS";
	public static final String ACTION_NEXT = "ACTION_NEXT";
	public static final String ACTION_SKIP = "ACTION_SKIP";

	private LocalBinder binder; // 用于与调用者通信的Binder
	private MusicItemInfo musicinfo;
	private MediaPlayer mediaplayer; // 媒体播放器对象
	private SharedPreferences share; // 用来保存选择的播放模式
	// Preferece机制操作的文件名
	public static final String PREFERENCE_NAME = "com.min.SaveContent";
	// Preferece机制的操作模式
	public static int MODE = Context.MODE_PRIVATE;
	private Thread thread = getThread();
	private String filepath;
	private List<MusicItemInfo> musicinfolist = new ArrayList<MusicItemInfo>();
	public static MusicService instance = null; // 实例
	private int currentposition; // 音乐所在列表的当前位置
	private boolean isfinish = false;
	public static int ORDER_PLAY = 0; // 顺序播放（默认模式）
	public static int SINGLE_PLAY = 1; // 单曲循环
	public static int CYCLE_PLAY = 2; // 循环播放
	private int playMode = ORDER_PLAY; // 默认播放形式

	public class LocalBinder extends Binder {
		public MusicService getService() {
			return MusicService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		// thread.start();
		return binder;
	}

	@Override
	public void onCreate() {
		binder = new LocalBinder();
		instance = this;
		// 获取播放歌曲模式
		share = getSharedPreferences(PREFERENCE_NAME, MODE);
		playMode = share.getInt("playmode", ORDER_PLAY);
		getMusicList();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		filepath = intent.getStringExtra("filepath");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mediaplayer != null) {
			mediaplayer.stop();
			mediaplayer.release();

		}

	}

	@Override
	public boolean onUnbind(Intent intent) {
		stopSelf();
		return super.onUnbind(intent);
	}

	// 得到当前播放的歌曲的歌曲名称
	public String getMusicName() {
		if (musicinfo != null) {
			return musicinfo.getName();
		}
		return "";
	}

	// 得到当前播放歌曲的演唱者
	public String getMysicArtist() {
		if (musicinfo != null)
			return musicinfo.getArtist();
		return "";
	}

	// 获取当前歌曲的时长
	public int getMusicDuration() {
		if (mediaplayer != null) {
			return mediaplayer.getCurrentPosition();
		}
		return 0;
	}

	// 播放
	public void Play() {
		if (mediaplayer != null) {
			if (thread == null) {
				thread = getThread();
			}
			mediaplayer.start();
			thread.start();
		}

	}

	// 歌曲总时长
	public int getDuration() {
		if (mediaplayer != null)
			return mediaplayer.getDuration();
		return 0;
	}


	// 暂停
	public void Pause() {
		if (mediaplayer != null && mediaplayer.isPlaying()) {
			mediaplayer.pause();

		}
	}

	// 继续播放音乐
	public void Resume() {
		if (mediaplayer != null) {
			mediaplayer.start();

		}
	}

	// 移动进度条处理
	public void Skip(int progress) {
		if (mediaplayer != null) {
			mediaplayer.seekTo(progress);
		}
	}

	// 设置播放模式
	public void setPlayMode(int mode) {
		playMode = mode;
		share = getSharedPreferences(PREFERENCE_NAME, MODE);
		Editor editor = share.edit();
		editor.putInt("playmode", mode);
		editor.commit();
	}

	// 下一曲
	public void Next() {
		if (playMode == ORDER_PLAY) { // 顺序播放
			currentposition = currentposition + 1;
			if (currentposition > musicinfolist.size() - 1)
				currentposition = musicinfolist.size() - 1;

		} else if (playMode == SINGLE_PLAY) { // 单曲循环
		} else if (playMode == CYCLE_PLAY) {
			currentposition = currentposition + 1;
			if (currentposition > musicinfolist.size() - 1)
				currentposition = 0; // 第一首歌
		}
		PlayOtherMusic(currentposition);
	}

	// 上一曲
	public void Previous() {
		if (playMode == ORDER_PLAY) { // 顺序播放
			currentposition = currentposition - 1;
			if (currentposition < 0)
				currentposition = 0;
		} else if (playMode == SINGLE_PLAY) { // 单曲循环
		} else if (playMode == CYCLE_PLAY) {
			currentposition = currentposition - 1;
			if (currentposition < 0)
				currentposition = musicinfolist.size() - 1; // 最后一首歌
		}
		PlayOtherMusic(currentposition);
	}

	// 播放第position位的歌曲
	public void PlayOtherMusic(int position) {

		currentposition = position;
		musicinfo = musicinfolist.get(position);
		if (mediaplayer != null) {
			mediaplayer.release();
			mediaplayer = null;
			Thread.interrupted();
			thread = null;
		}
		mediaplayer = new MediaPlayer();
		try {
			mediaplayer.setDataSource(musicinfo.getPath());
			mediaplayer.prepareAsync();
			mediaplayer.setOnPreparedListener(prListener);
			mediaplayer.setOnErrorListener(errorListener);
			mediaplayer.setOnCompletionListener(comlistener);
			filepath = musicinfo.getPath();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 注册一个回调函数，音乐播放完毕后调用
	private OnCompletionListener comlistener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			Message msg = new Message();
			msg.what = FINISH;
			MusicPlayActivity.musichandler.sendMessage(msg);
			isfinish = true;
			Next();
		}
	};
	// 回调监听器，在异步操作调用过程中发生错误时调用，比如音乐打开失败
	private OnErrorListener errorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			return false;
		}
	};
	// 回调监听，在做预处理完成后调用
	private OnPreparedListener prListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			Message msg = new Message();
			msg.what = START;
			msg.arg1 = mediaplayer.getDuration();
			MusicPlayActivity.musichandler.sendMessage(msg);
			isfinish = false;
			Play();

		}
	};

	// 得到SD卡中的符合条件的音乐信息
	public void getMusicList() {
		ContentResolver resoler = getContentResolver();
		Cursor cursor = resoler.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.SIZE }, null, null, null);
		while (cursor.moveToNext()) {
			long size = cursor.getLong(4);
			if (size > 2 * 1024 * 1024) { // 文件大小大于1M的音乐才获取
				MusicItemInfo musicinfo = new MusicItemInfo();
				musicinfo.setName(cursor.getString(0));
				String artist = cursor.getString(1);
				if (artist.equals("<unknown>")) {
					musicinfo.setArtist("未知艺术家");
				} else {
					musicinfo.setArtist(artist);
				}
				musicinfo.setTime(Other.ChangeTimeFormat(cursor.getInt(2)));
				musicinfo.setPath(cursor.getString(3));
				musicinfo.setSize(size);
				musicinfolist.add(musicinfo);
			}

		}
	}

	// 计算进度条进度的线程，通过Handler向MusicPlayActivity发送消息

	public Thread getThread() {
		Thread thread = new Thread() {
			public void run() {
				while (!interrupted() && mediaplayer != null) {
					try {
						int pisiton = mediaplayer.getCurrentPosition();
						Log.v("position", String.valueOf(pisiton));
						Message msg = new Message();
						msg.what = PLAYING;
						msg.arg1 = pisiton;
						MusicPlayActivity.musichandler.sendMessage(msg);
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}
			};
		};
		return thread;
	}

}