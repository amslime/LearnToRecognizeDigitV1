package com.learn.wlnutter.learntorecognizedigit;

import android.app.Fragment;
import android.content.res.Resources;

import java.io.IOException;
import java.io.InputStream;

public class DataReader {
    private static final DataReader instance = new DataReader();

    private static final int SEG_LEN = 4096;
    private byte data_buffer[] = new byte[SEG_LEN];
    private byte label_buffer[] = new byte[SEG_LEN];
    private InputStream data, label;
    private int data_offset, label_offset, rows, cols, data_num, data_read_count, label_read_count;
    private boolean is_available;
    private DataReader() {
    }
    public static DataReader getInstance() {
        return instance;
    }

    public void init(Fragment fg, int data_id, int label_id) throws IOException {
        is_available = false;
        if (data != null) data.close();
        if (label != null) label.close();
        data = fg.getResources().openRawResource(data_id);
        label = fg.getResources().openRawResource(label_id);
        data_read_count = data.read(data_buffer);
        label_read_count = label.read(label_buffer);

        int x = byteArrayToInt(data_buffer, 0);
        System.out.println("DataMagicCode = " + x);
        int y = byteArrayToInt(label_buffer, 0);
        System.out.println("LabelMagicCode = " + y);

        data_num = byteArrayToInt(data_buffer, 4);
        int label_num = byteArrayToInt(label_buffer, 4);
        if (label_num != data_num)
            return;
        System.out.println("Total Data Set = " + data_num);

        rows = byteArrayToInt(data_buffer, 8);
        cols = byteArrayToInt(data_buffer, 12);
        System.out.println("Rows  = " + rows + ", cols = " + cols);

        data_offset = 16;
        label_offset = 8;

        is_available = true;
    }

    public ImageInfo getNextImageInfo() {
        try {
            byte[] b = nextDataBytes();
            int l = nextLabelValue();
            return new ImageInfo(b,l, rows, cols);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return  null;
        }

    }

    private int nextLabelValue() throws IOException {
        if (label_offset == label_read_count) {
            label_read_count = label.read(label_buffer);
            label_offset = 0;
        }
        int res = (label_buffer[label_offset]&0xff);
        label_offset++;
        return res;
    }

    private  byte[] nextDataBytes() throws  IOException {
        int left_bytes = rows * cols;
        int next_index = 0;
        byte[] res = new byte[left_bytes];
        while (data_offset + left_bytes > data_read_count) {
            System.arraycopy(data_buffer, data_offset, res, next_index, data_read_count - data_offset);
            left_bytes -= data_read_count - data_offset;
            next_index += data_read_count - data_offset;
            data_read_count = data.read(data_buffer);
            data_offset = 0;
        }
        System.arraycopy(data_buffer, data_offset, res, next_index, left_bytes);
        data_offset += left_bytes;
        return res;
    }

    public static int byteArrayToInt(byte[] b, int offset) {
        return b[3 + offset] & 0xFF |
                (b[2 + offset] & 0xFF) << 8 |
                (b[1 + offset] & 0xFF) << 16 |
                (b[0 + offset] & 0xFF) << 24;
    }



}
