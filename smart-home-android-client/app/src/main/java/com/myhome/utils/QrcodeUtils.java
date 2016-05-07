package com.myhome.utils;

import android.graphics.Bitmap;

import com.google.zxing.WriterException;

/**
 * Created by Administrator on 2016/5/7 0007.
 */
public class QrcodeUtils {

    public static Bitmap getQrcode(String content) throws WriterException {
        return EncodingHandler.createQRCode(content, 400);
    }
}
