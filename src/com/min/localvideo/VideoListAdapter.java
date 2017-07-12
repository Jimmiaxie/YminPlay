package com.min.localvideo;

import java.util.ArrayList;
import java.util.List;

import com.min.localmusic.MusicItemInfo;
import com.min.yminplay.Other;
import com.min.yminplay.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListAdapter extends BaseAdapter {
	private Context context;
	private List<VideoItemInfo> videolist;
	public VideoListAdapter(Context context, List<VideoItemInfo> videolist) {
		super();
		this.context = context;
		this.videolist = videolist;
	}

	@Override
	public int getCount() {
		return videolist.size();
	}

	@Override
	public Object getItem(int position) {
		return videolist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		VideoItemInfo videoinfo = videolist.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.videolist_item, null);
			holder = new ViewHolder();
			holder.video_img = (ImageView) convertView
					.findViewById(R.id.video_img);
			holder.video_title = (TextView) convertView
					.findViewById(R.id.video_title);
			holder.video_time = (TextView) convertView
					.findViewById(R.id.video_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.video_title.setText(videoinfo.getName());
		holder.video_time.setText(Other.ChangeTimeFormat(Integer.valueOf(videoinfo.getTime())));
	
		return convertView;
	}


	private static class ViewHolder {
		public ImageView video_img;
		public TextView video_title;
		public TextView video_time;

	}
}
