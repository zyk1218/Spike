package com.imooc.miaosha.util;


import org.apache.commons.codec.digest.DigestUtils;


public class MD5Util {

    private static final String salt = "1a2b3c4d";


    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }


    public static String inputPassFromPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String formPass,String salt){
        String str = salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }


    public static String inputPassToDBPass(String input,String saltDB){
        String fromPass = inputPassFromPass(input);
        String dbPass = fromPassToDBPass(fromPass,saltDB);
        return dbPass;
    }

    public static void main(String[] args) {
        System.out.println(inputPassFromPass("12345678"));//ca96df1c6cd528a3541f80a77900272b
        System.out.println(fromPassToDBPass(inputPassFromPass("12345678"),"1a2b3cc"));
    }
}
