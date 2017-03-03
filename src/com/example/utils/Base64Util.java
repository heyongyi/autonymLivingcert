package com.example.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Base64Util {


    public static String getBase64(byte[] base) {

        String base64 = null;
        try {
            base64 = Base64.encodeToString(base, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64;
    }

    /**
     * base64����
     * 
     * @param s
     * @return String �������ֽ�����
     */
    public static byte[] getFromBASE64(String s) {

        if (s == null) {
            return null;
        }
        try {
            byte b[] = Base64.decode(s, Base64.NO_WRAP);
            return b;
        } catch (Exception ie) {
            System.out.println("****** ��Ƭ�������************");
            ie.printStackTrace();
            return null;
        }
    }

    /**
     * base64����
     * 
     * @param s
     * @return String �������ֽ�����
     */
    public static byte[] getFromBASE64(String s,
                                       int type) {

        if (s == null) {
            return null;
        }
        try {
            byte b[] = Base64.decode(s, type);
            return b;
        } catch (Exception ie) {
            System.out.println("****** ��Ƭ�������************");
            ie.printStackTrace();
            return null;
        }
    }

    /**
     * ������Ƭ
     * 
     * @param filename ��Ƭ�ļ�����
     * @param base64String �ӽӿڻ����Ƭ��base64����
     * @return
     */
    @SuppressWarnings("finally")
    public static boolean savePhoto(String filename,
                                    String base64String) {

        boolean f = false;
        FileOutputStream myFileStream = null;
        try {
            myFileStream = new FileOutputStream(filename);
            myFileStream.write(getFromBASE64(base64String));
            f = true;
            return f;
        } catch (java.io.IOException ie) {
            System.out.println("****** ������Ƭ��");
            ie.printStackTrace();
            return f;
        } catch (Exception ex) {
            System.out.println("****** ������Ƭ��");
            ex.printStackTrace();
            return f;
        } finally {
            try {
                myFileStream.close();
                return f;
            } catch (IOException e) {
                System.out.println("****** ������Ƭ��");
                e.printStackTrace();
                return f;
            }
        }
    }

}
