package com.min.localmusic;

/*
 * 封装音乐信息的类，其中包括音乐名称、演唱者、歌曲时长、文件路径和文件大小
 */
public class MusicItemInfo  {
	private String name, artist, time, path;
	private long size;

	public void setSize(long size) {
		this.size = size;
	}

	public long getSize() {
		return size;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	public String getArtist() {
		return artist;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTime() {
		return time;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
