package com.min.localvideo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.min.localmusic.MusicItemInfo;
import com.min.localmusic.MusicListAdapter;
import com.min.localmusic.MusicListFragment;
import com.min.yminplay.Other;
import com.min.yminplay.R;

import android.R.integer;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class VideoListFragment extends Fragment implements OnItemClickListener {
	private Context context;
	private ListView video_listview;
	private String[] paths;
	private List<VideoItemInfo> videoinfolist;
	private VideoListAdapter videoadapet = null;
	private VideoItemInfo videoinfo = null;
	private static VideoListFragment instance;

	public VideoListFragment() {
		super();
	}

	public VideoListFragment(Context context) {
		super();
		this.context = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_videolist, container,
				false);
		instance = this;
		videoinfolist = new ArrayList<VideoItemInfo>();
		video_listview = (ListView) view.findViewById(R.id.video_listview);
		InitView();
		videoadapet = new VideoListAdapter(getActivity(), videoinfolist);
	
		video_listview.setAdapter(videoadapet);
		video_listview.setOnItemClickListener(this);
		return view;
	}

	private void InitView() {
		ContentResolver resoler = getActivity().getContentResolver();
		Cursor cursor = resoler.query(
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI, new String[] {
						MediaStore.Video.Media.TITLE,
						MediaStore.Video.Media.DURATION,
						MediaStore.Video.Media.DATA }, null, null, null);
		while (cursor.moveToNext()) {
			VideoItemInfo videoinfo = new VideoItemInfo();
			videoinfo.setName(cursor.getString(0));
			videoinfo.setTime(cursor.getString(1));
			videoinfo.setPath(cursor.getString(2));
			videoinfolist.add(videoinfo);
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		videoinfo = videoinfolist.get(position);
		File file = new File(videoinfo.getPath());
		if (file.exists()) {
			Intent intent = new Intent(getActivity(), VideoPlayActivity.class);
			intent.putExtra("videopath", videoinfo.getPath());
			startActivity(intent);
		}
	}
}
