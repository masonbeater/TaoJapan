package net.shopxx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**
	* @Title: dateToString
	* @Description: TODO(时间转换为字符串格式)
	* @param @param date
	* @param @param format yyMMddHHmmss
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	* @author Administrator 
	*/ 
	public static String dateToString(Date date,String format){
		if ((date == null) || (format == null)) return null;
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    String str = sdf.format(date);
	    return str;
	}
}
