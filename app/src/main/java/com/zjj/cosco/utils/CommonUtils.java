package com.zjj.cosco.utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by administrator on 2018/8/2.
 */

public class CommonUtils {
    /**
     * 将bitmap转换成base64二进制流
     *
     * @return
     */
    public static String bitMapToString(Bitmap bitmap) {
        if (null != bitmap) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            return Base64.encodeToString(b, Base64.NO_WRAP);
        }
        return "";
    }
}
