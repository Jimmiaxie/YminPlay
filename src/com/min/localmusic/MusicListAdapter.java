package com.min.localmusic;

import java.util.List;

import com.min.yminplay.Other;
import com.min.yminplay.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/*
 * 文件列表适配器
 */
public class MusicListAdapter extends BaseAdapter {
	private Context context;
	private List<MusicItemInfo> musiclist;

	public MusicListAdapter(Context context, List<MusicItemInfo> musiclist) {
		super();
		this.context = context;
		this.musiclist = musiclist;
	}

	@Override
	public int getCount() {
		return musiclist.size();
	}

	@Override
	public Object getItem(int position) {
		return musiclist.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		MusicItemInfo musicinfo = musiclist.get(position);
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(
					R.layout.musiclist_item, null);
			holder = new ViewHolder();
			holder.music_image = (ImageView) convertView
					.findViewById(R.id.music_image);
			holder.music_name = (TextView) convertView
					.findViewById(R.id.music_name);
			holder.music_author = (TextView) convertView
					.findViewById(R.id.music_author);
			holder.music_time = (TextView) convertView
					.findViewById(R.id.music_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.music_name.setText(musicinfo.getName());
		holder.music_author.setText(musicinfo.getArtist());
		holder.music_time.setText(Other.ChangeTimeFormat(Integer.valueOf(musicinfo.getTime())));

		return convertView;
	}

	private static class ViewHolder {
		public ImageView music_image;
		public TextView music_name;
		public TextView music_time;
		public TextView music_author;
	}

}
