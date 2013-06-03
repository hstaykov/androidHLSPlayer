package com.example.demovideoview;

import java.util.concurrent.TimeUnit;

public class TimeFormat {

	static public String milisecondToHMS(long miliseconds) {
		// long millis = 3600000;
		String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(miliseconds),
				TimeUnit.MILLISECONDS.toMinutes(miliseconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(miliseconds)),
				TimeUnit.MILLISECONDS.toSeconds(miliseconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(miliseconds)));
		
		return hms;
	}

}
