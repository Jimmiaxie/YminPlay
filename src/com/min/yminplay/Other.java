package com.min.yminplay;

public class Other {
	// 改变时间格式
	public static String ChangeTimeFormat(int time) {
		
		time = time / 1000;
	
		String minutes = String.valueOf(time / 60);
		String second = String.valueOf(time % 60);
		if (second.length() == 1) {
			second = "0" + second;
		}		
		
			return minutes + ":" + second;
	}
}
