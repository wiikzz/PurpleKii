package com.wiikzz.library.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by wiikii on 16/4/8.
 * Copyright (C) 2014 wiikii. All rights reserved.
 */
public class AESCryptTool {
    /**
     * 加解密
     *  Do not copy to anywhere. Very important.
     */
    private static final String sIvParamString = "wiikii_parstring";
    private static final String sSecretKeyString = "secret_keystring";

    /**
     * AES加密
     * @param content 要加密的字串
     * @param ivParamString 偏移量字串
     * @param secretKeyString 密钥
     * @return 加密后的字节数组，return null if any exception occur.
     */
    public static byte[] encrypt(String content, String ivParamString, String secretKeyString) {
        if(TextUtils.isEmpty(content)) {
            //throw new Exception("CryptTool encrypt content is empty.");
            return null;
        }

        if ((TextUtils.isEmpty(ivParamString) || (ivParamString.length() != 16))
                || (TextUtils.isEmpty(secretKeyString) || (secretKeyString.length() != 16 && secretKeyString.length() != 24 && secretKeyString.length() != 32))) {
            // SecretKeySpec AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即16bytes。
            // IvParameterSpec AES为16bytes. DES为8bytes.
            //throw new Exception("CryptTool encrypt ivParameterSpec invalid or secretKeySpec invalid.");
            return null;
        }

        byte [] encryptByteData = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyString.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParamString.getBytes());

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding"); // 可以使用"AES/ECB/PKCS5Padding"
            //初始化，此方法可以采用三种方式，按服务器要求来添加。
            // （1）无第三个参数
            // （2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
            // （3）采用此代码中的IvParameterSpec
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

            encryptByteData = cipher.doFinal(paddingString(content).getBytes());
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return encryptByteData;
    }

    /**
     * AES解密
     * @param content 要解密的字节数组
     * @param ivParamString 偏移量字串
     * @param secretKeyString 密钥
     * @return 解密后的字节数组，return null if any exception occur.
     */
    public static byte[] decrypt(byte[] content, String ivParamString, String secretKeyString) {
        if(content == null) {
            //throw new Exception("CryptTool decrypt content is null.");
            return null;
        }

        if ((TextUtils.isEmpty(ivParamString) || (ivParamString.length() != 16))
                || (TextUtils.isEmpty(secretKeyString) || (secretKeyString.length() != 16 && secretKeyString.length() != 24 && secretKeyString.length() != 32))) {
            // SecretKeySpec AES固定格式为128/192/256 bits.即：16/24/32bytes。DES固定格式为128bits，即16bytes。
            // IvParameterSpec AES为16bytes. DES为8bytes.
            //throw new Exception("CryptTool decrypt ivParameterSpec invalid or secretKeySpec invalid.");
            return null;
        }

        byte [] decryptByteData = null;
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKeyString.getBytes(), "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivParamString.getBytes());

            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            //初始化，此方法可以采用三种方式，按服务器要求来添加。
            // （1）无第三个参数
            // （2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)
            // （3）采用此代码中的IvParameterSpec
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            decryptByteData = cipher.doFinal(content);
        } catch (NoSuchAlgorithmException | IllegalBlockSizeException | BadPaddingException
                | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            e.printStackTrace();
        }

        return decryptByteData;
    }

    // 加密主接口
    public static String encrypt(String sourceString) {
        byte [] resultBytes = encrypt(sourceString, sIvParamString, sSecretKeyString);
        if(resultBytes == null) {
            return null;
        }

        String resultString = parseByte2HexStr(resultBytes);
        return resultString;
    }

    // 解密主接口
    public static String decrypt(String sourceString) {
        if(TextUtils.isEmpty(sourceString)) {
            return null;
        }

        byte [] resultBytes = decrypt(parseHexStr2Byte(sourceString), sIvParamString, sSecretKeyString);
        if(resultBytes == null) {
            return null;
        }

        try {
            String resultString = new String(resultBytes, "UTF-8");
            return resultString;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }

    // 为字串添加padding
    private static String paddingString(String source) {
        char paddingChar = ' ';
        int size = 16;
        int x = source.length() % size;
        int padLength = size - x;

        for (int i = 0; i < padLength; i++) {
            source += paddingChar;
        }

        return source;
    }
}
