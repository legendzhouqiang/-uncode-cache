package cn.uncode.cache.framework.util;

import java.io.UnsupportedEncodingException;

public class ByteUtil {

	public static byte[] stringToByte(String str) {
		try {
			return str.getBytes("ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String byteToString(byte[] bytes) {
		try {
			return new String(bytes,"ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
