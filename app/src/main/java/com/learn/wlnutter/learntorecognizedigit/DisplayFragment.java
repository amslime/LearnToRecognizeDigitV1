package com.learn.wlnutter.learntorecognizedigit;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link DisplayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayFragment extends Fragment {

    private DataHandler dataHandler;
    private List<ImageInfo> imageInfoList;
    private RefreshableListView imageViews;
    private BaseAdapter imageViewAdapter;

    // Handler更新UI界面

    private static class RefreshHandler extends Handler {
        private final WeakReference<DisplayFragment> fragment;

        public RefreshHandler(DisplayFragment fg) {
            fragment = new WeakReference<DisplayFragment>(fg);
        }

        @Override
        public void handleMessage(Message msg) {
            DisplayFragment fg = fragment.get();
            if (fg != null) {
                System.out.println("Get message = " + msg.what);
                if (msg.what > 0) {
                    // ListView的数据适配器更新数据集
                    // 列表刷新和notify要放在同一个ui处理线程中，不然可能导致异步错误
                    int res = fg.getDataHandler().updateImageInfoList();
                    if (res == 0) {
                        fg.getImageViews().doNotLoadMore();
                    }
                    System.out.println("list size = " + fg.getImageInfoList().size());
                    fg.getImageViewAdapter().notifyDataSetChanged();
                    // 必须调用这个方法，重置头部布局或底部布局的视图
                    fg.getImageViews().onRefreshComplete();
                }
            }
        }
    }
    private final Handler refreshHandler = new RefreshHandler(this);

    public DisplayFragment() {

    }

    public static DisplayFragment newInstance() {
        DisplayFragment fragment = new DisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //缓存,不随activity的销毁而销毁
        setRetainInstance(true);

        if (dataHandler == null) {
            try {
                dataHandler = DataHandler.getInstance();
                dataHandler.init(this, R.raw.data_ubyte, R.raw.label_ubyte);
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
        System.out.println("Creating display view");
        if (imageInfoList == null) {
            dataHandler.updateImageInfoList();
            imageInfoList = dataHandler.getImageInfoList();
        }
        imageViews =  (RefreshableListView)view.findViewById(R.id.display_list);
        imageViewAdapter = new ImageViewAdapter(inflater);
        imageViews.setAdapter(imageViewAdapter);
        imageViews.setOnRefreshListener(
                new RefreshableListView.OnRefreshListener() {
            @Override
            public void onLoading() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("prepare to update data");
                        refreshHandler.sendEmptyMessage(1);
                    }
                }).start();
            }
        });
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

    public List<ImageInfo> getImageInfoList() {
        return imageInfoList;
    }

    public RefreshableListView getImageViews() {
        return imageViews;
    }

    public BaseAdapter getImageViewAdapter() {
        return imageViewAdapter;
    }

    public DataHandler getDataHandler() {
        return dataHandler;
    }
}
