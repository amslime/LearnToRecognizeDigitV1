package com.learn.wlnutter.learntorecognizedigit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayFragment extends Fragment {

    private DataReader dataReader;
    private List<ImageInfo> imageInfoList;
    private ListView imageViews;
    private BaseAdapter imageViewAdapter;

    public DisplayFragment() {

    }

    public static DisplayFragment newInstance() {
        DisplayFragment fragment = new DisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (dataReader == null) {
            try {
                dataReader = DataReader.getInstance();
                dataReader.init(this, R.raw.data_ubyte, R.raw.label_ubyte);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_display, container, false);
        if (imageViews == null) {
            imageInfoList = new ArrayList<>();
            for (int i = 0; i < 500; ++i) {
                imageInfoList.add(dataReader.getNextImageInfo());
            }
            imageViews =  view.findViewById(R.id.display_list);
            imageViewAdapter = new ImageViewAdapter(inflater);
            imageViews.setAdapter(imageViewAdapter);
        }
        return view;
    }

    private class ImageViewAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        public ImageViewAdapter(LayoutInflater inflater) {
            super();
            this.inflater = inflater;
        }

        @Override
        public int getCount() {
            return imageInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = inflater.inflate(R.layout.display_list_info_layout, parent, false);
            ImageView iv = layout.findViewById(R.id.num_img);
            TextView val = layout.findViewById(R.id.num_val);

            ImageInfo imf = imageInfoList.get(position);
            iv.setImageBitmap(imf.getBitmap());
            val.setText("" + imf.getLabelNum());
            return layout;
        }
    }
}
