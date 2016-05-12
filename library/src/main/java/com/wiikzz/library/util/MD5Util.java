package com.wiikzz.library.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wiikii on 2016-5-8.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class MD5Util {
    private static final int STREAM_BUFFER_LENGTH = 1024;

    /**
     * 获取一个字符串的MD5
     * @param sourceString 源字符串
     * @return 源字符串的MD5值，获取失败返回空串"";
     */
    public static String getMD5(String sourceString) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(sourceString.getBytes("UTF-8"));
            byte[] encryption = messageDigest.digest();

            StringBuffer strBuf = new StringBuffer();
            for(int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    strBuf.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    strBuf.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return strBuf.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    /**
     * 获取一个文件的MD5值
     * @param sourceFile 源文件
     * @return 源文件的MD5值，获取失败返回空串"";
     */
    public static String getMD5(File sourceFile) {
        if (sourceFile == null || !sourceFile.isFile()) {
            return null;
        }
        MessageDigest messageDigest;
        FileInputStream fileInputStream;
        byte buffer[] = new byte[STREAM_BUFFER_LENGTH];
        int length;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(sourceFile);
            while ((length = fileInputStream.read(buffer, 0, STREAM_BUFFER_LENGTH)) != -1) {
                messageDigest.update(buffer, 0, length);
            }
            fileInputStream.close();
        } catch (Exception e) {
            return "";
        }

        BigInteger bigInt = new BigInteger(1, messageDigest.digest());
        return bigInt.toString(16);
    }
}
