package com.learn.wlnutter.learntorecognizedigit;

import android.graphics.Bitmap;

/**
 * Created by Administrator on 2018/1/23.
 */

public class ImageInfo {
    private byte[] bytes;
    private int labelNum, rows, cols;
    private Bitmap bitmap = null;

    public ImageInfo(byte[] bytes, int labelNum, int rows, int cols) {
        this.bytes = bytes;
        this.labelNum = labelNum;
        this.rows = rows;
        this.cols = cols;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getLabelNum() {
        return labelNum;
    }

    public Bitmap getBitmap() {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(cols, rows, Bitmap.Config.ARGB_8888);
            int total = cols * rows;
            int pixels[] = new int[total];

            for (int i = 0; i < total; ++i) {
                int c = 255 - (bytes[i]&0xff);
                pixels[i] = (c << 16) + (c << 8) + c + 0xFF000000;
            }
            bitmap.setPixels(pixels, 0, cols, 0,0, cols, rows);
        }
        return bitmap;
    }
}
