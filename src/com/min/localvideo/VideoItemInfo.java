package com.min.localvideo;
/*
 * 封装视频信息的类，其中视频信息包括视频名称、视频时长、视频路径
 */
public class VideoItemInfo  {
	private String name, time, path;

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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
