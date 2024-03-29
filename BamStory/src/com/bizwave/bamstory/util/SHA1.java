package com.bizwave.bamstory.util;

import java.security.MessageDigest;

public class SHA1 {
	public static String convertToHex(byte[] data){
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<data.length; i++){
			int halfbyte = (data[i] >>> 4) & 0X0F;
			int two_halfs = 0;
			do {
				if((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0X0F;
			}while(two_halfs++ < 1);
		}
		return buf.toString();
	}
	public static String digest(String text){
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] sha1hash = new byte[40];
			md.update(text.getBytes("UTF-8"),0,text.length());
			sha1hash = md.digest();
			return convertToHex(sha1hash);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
}
