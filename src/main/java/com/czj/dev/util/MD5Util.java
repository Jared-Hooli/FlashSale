package com.czj.dev.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
	
	public static String md5(String src) {
		return DigestUtils.md5Hex(src);
	}

	// 对指定密码执行加盐加密
	public static String passToDbPass(String formPass, String randSalt) {
		String str = "" + randSalt.charAt(0) + randSalt.charAt(2) 
			+ formPass + randSalt.charAt(5) + randSalt.charAt(4);
		return md5(str);
	}

	public static void main(String[] args) {
		// 加盐加密后的密码
		System.out.println(passToDbPass("123456", "0p9o8i"));
	}
}