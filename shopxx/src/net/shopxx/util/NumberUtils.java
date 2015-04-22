package net.shopxx.util;

public class NumberUtils {

	 public static boolean isInteger(String num) {
	    if ((num == null) || (num.length() == 0)) {
	      return false;
	    }
	    int len = num.length();
	    char ch = num.charAt(0);
	    for (int i = (ch == '+') || (ch == '-') ? 1 : 0; i < len; i++) {
	      ch = num.charAt(i);
	      if ((ch < '0') || (ch > '9')) {
	        return false;
	      }
	    }
	    return true;
	  }
}
