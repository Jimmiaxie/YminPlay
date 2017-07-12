package com.min.localmusic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.min.yminplay.Other;
import com.min.yminplay.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
/*
 * 音乐列表显示界面，包括本地音乐的搜索、列表的初始化，以及点击跳转到播放界面
 */
public class MusicListFragment extends Fragment implements OnItemClickListener {
	private Context context;
	private ListView music_listview;
	private List<MusicItemInfo> musicinfolist;
	private MusicListAdapter musicadapet = null;
	private MusicItemInfo musicinfo = null;
	private static MusicListFragment instance;

	public MusicListFragment() {
		super();
	}

	public MusicListFragment(Context context) {
		super();
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_musiclist, container,
				false);
		instance = this;
		musicinfolist = new ArrayList<MusicItemInfo>();
		music_listview = (ListView) view.findViewById(R.id.music_listview);
		InitView();
		Log.v("Length",String.valueOf(musicinfolist.size()));
		musicadapet = new MusicListAdapter(getActivity(), musicinfolist);
		music_listview.setAdapter(musicadapet);
		music_listview.setOnItemClickListener(this);
		return view;
	}
/*
 * 得到本地的音乐视频，小于1M就忽略掉,分别获取音乐名称、演唱者、歌曲时长、文件路径和文件大小
 * 符合要求就增加到musicinfolist（存放音乐信息的list）中
 */
	public void InitView() {
		new Thread(){
		public void run() {
			ContentResolver resoler = getActivity().getContentResolver();
		Cursor cursor = resoler.query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.Audio.Media.TITLE,
						MediaStore.Audio.Media.ARTIST,
						MediaStore.Audio.Media.DURATION,
						MediaStore.Audio.Media.DATA,
						MediaStore.Audio.Media.SIZE }, null, null, null);
		while (cursor.moveToNext()) {
			long size = cursor.getLong(4);
			if (size > 2 * 1024 * 1024) {
				MusicItemInfo musicinfo = new MusicItemInfo();
				musicinfo.setName(cursor.getString(0));
				String artist = cursor.getString(1);
				if (artist.equals("<unknown>")) {
					musicinfo.setArtist("未知艺术家");
				} else {
					musicinfo.setArtist(artist);
				}
				musicinfo.setTime(cursor.getString(2));
				musicinfo.setPath(cursor.getString(3));
				musicinfo.setSize(size);
				musicinfolist.add(musicinfo);
			}
		}
		};	
		}.start();	
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		musicinfo = musicinfolist.get(position);
		File file = new File(musicinfo.getPath());
		//文件存在就惊醒界面跳转
		if (file.exists()) {
			Intent intent = new Intent(getActivity(), MusicPlayActivity.class);
			intent.putExtra("filepath", musicinfo.getPath());
			intent.putExtra("artist", musicinfo.getArtist());
			intent.putExtra("musicPosition", position);
			startActivity(intent);
		} else {
			Toast.makeText(getActivity(), "该文件不存在！", Toast.LENGTH_LONG).show();
		}
	}

}
