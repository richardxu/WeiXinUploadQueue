package cn.ihealthbaby.weitaixin.library.tools;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.ihealthbaby.weitaixin.library.log.LogUtil;

/**
 * Created by Think on 2015/8/14.
 */
public class DateTimeTool {
	public static String date2Str(Date date, String dateFormat) {
		if (date == null) {
			return "";
		}
		String dateFormatStr = dateFormat;
		if (TextUtils.isEmpty(dateFormat)) {
			dateFormatStr = "yyyy年MM月dd日 HH:mm:ss";
		}
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormatStr);
		return sdf.format(date);
	}

	public static String date2StrAndTime(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 HH:mm");
		return sdf.format(date);
	}

	public static String date2StrAndTime2(Date date) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		return sdf.format(date);
	}

	public static String date2St2(Date date, String formatStr) {
		if (date == null) {
			return "";
		}
		long mss = date.getTime();
		long hour = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minute = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long second = (mss % (1000 * 60)) / 1000;
		StringBuffer sb = new StringBuffer();
		if (hour > 0) {
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			SimpleDateFormat sdf = new SimpleDateFormat(formatStr);
			return sdf.format(date);
		} else if (minute > 0) {
			sb.append(minute + "分");
			if (second > 0) {
				sb.append(second + "秒");
			}
			sb.append("前");
		} else {
			sb.append(second + "秒前");
		}
		return sb.toString();
	}

	public static Date str2Date(String dateStr) {
		if (TextUtils.isEmpty(dateStr)) {
			return new Date();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getTime3(int mss) {
//        mss=mss*1000;
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		return days + "日" + hours + "小时" + minutes + "分" + seconds + "秒";
	}

	public static String getTime2(int time) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm分ss秒");
		Date dt = new Date(time);
		String sDateTime = formatter.format(dt);
		return sDateTime;
	}

	public static Date longDate2Str(long lSysTime1) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		java.util.Date dt = new Date(lSysTime1);
		String sDateTime = sdf.format(dt);
		LogUtil.d("longDate2Str", "longDate2Str" + sDateTime);
		return dt;
	}

	public static String getGestationalWeeks(Date deliveryTime) {
		if (deliveryTime == null) {
			return "";
		}
		int getGestationalDay = (int) ((new Date().getTime() / 1000
				                                - deliveryTime.getTime() / 1000 + 280 * 24 * 3600) / 3600 / 24);
		int weeks = getGestationalDay / 7;
		int days = getGestationalDay % 7;
		return weeks + "周+" + days + "天";
	}

	public static String getGestationalWeeks(Date deliveryTime, Date testTime) {
		int getGestationalDay = (int) ((testTime.getTime() / 1000 - deliveryTime.getTime() / 1000 + 280 * 24 * 3600) / 3600 / 24);
		int weeks = getGestationalDay / 7;
		int days = getGestationalDay % 7;
		return weeks + "周+" + days;
	}

	// 怀孕天数
	public static int getGestationalDays(Date deliveryTime) {
		return (int) ((new Date().getTime() / 1000 - deliveryTime.getTime()
				/ 1000 + 280 * 24 * 3600) / 3600 / 24);

	}

	// 距离预产期天数
	public static int fromDeliveryTime(Date deliveryTime) {
		return (int) ((deliveryTime.getTime() / 1000 - new Date().getTime() / 1000) / 3600 / 24);

	}


	public static void main(String[] args) {
		String s = getGestationalWeeks(new Date(new Date().getTime() + 1000 * 3600 * 24 * 6));
		System.out.println(s);
	}

	public static String million2hhmmss(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		java.util.Date dt = new Date(time);
		String hhmmss = sdf.format(dt);
		return hhmmss;
	}

	public static String million2mmss(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
		java.util.Date dt = new Date(time);
		String mmss = sdf.format(dt);
		return mmss;
	}
}



