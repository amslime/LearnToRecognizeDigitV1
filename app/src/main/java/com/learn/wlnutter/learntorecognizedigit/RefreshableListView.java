package com.learn.wlnutter.learntorecognizedigit;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by Administrator on 2018/1/24.
 */

public class RefreshableListView extends ListView implements AbsListView.OnScrollListener {
    private View footerView;
    private int footerHeight;
    private boolean isLoadEnabled = true;
    private boolean isLoadable = false;
    private int startY, offsetY;
    private int firstItemIndex;
    private int visibleItemCount;
    private int totalItemCount;
    private int firstItemTopPadding;
    private boolean isScrollIdle;
    private OnRefreshListener onRefreshListener;


    public RefreshableListView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public RefreshableListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }


    private void initView(Context context) {
        footerView = LayoutInflater.from(context).inflate(R.layout.load_footer, this, false);
        this.post(new Runnable() {
            @Override
            public void run() {
                System.out.println("Refresh able view set padding");
                footerHeight = footerView.getMeasuredHeight();
                setViewPadding(-footerHeight);
            }
        });
        this.addFooterView(footerView);
        this.setOnScrollListener(this);
    }

    private void setViewPadding(int bottomPadding) {
        this.setPadding(footerView.getPaddingLeft(), 0, footerView.getPaddingRight(), bottomPadding);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        isScrollIdle = scrollState == OnScrollListener.SCROLL_STATE_IDLE;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstItemIndex = firstVisibleItem;
        this.visibleItemCount = visibleItemCount;
        this.totalItemCount = totalItemCount;
        View firstView = this.getChildAt(firstVisibleItem);
        if (firstView != null) {
            this.firstItemTopPadding = firstView.getTop();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            // 手指按下时，判断是否可以上拉加载
            case MotionEvent.ACTION_DOWN:
                if (isLoadEnabled && isScrollIdle && firstItemIndex + visibleItemCount == totalItemCount) {
                    isLoadable = true;
                }
                startY = (int) ev.getY();
                break;
            // 手指移动时，判断是否在下拉刷新或上拉加载，如果是，则动态改变头部布局或底部布局的状态
            case MotionEvent.ACTION_MOVE:
                offsetY = (int) ev.getY() - startY;
                if (isLoadEnabled && isLoadable && offsetY < 0) {
                    setViewPadding( -footerHeight - offsetY);
                }
                break;
            // 手指抬起时，判断是否下拉或上拉到可以刷新或加载的程度，如果达到程度，则进行刷新或加载
            case MotionEvent.ACTION_UP:
              if (isLoadEnabled && isLoadable && offsetY < 0) {
                    if (offsetY >= -footerHeight) {
                        setViewPadding(-footerHeight);
                    } else {
                        setViewPadding(0);
                        System.out.println("To load more!");
                        onRefreshListener.onLoading();
                    }
                }
                isLoadable = false;
                break;
        }
        return super.onTouchEvent(ev);
    }

    public void onRefreshComplete() {
        setViewPadding(-footerHeight);
    }

    public void doNotLoadMore() {
        isLoadEnabled = false;
        this.removeFooterView(footerView);
        setViewPadding(0);
    }

    /**
     * 监听下拉刷新的接口
     */
    interface OnRefreshListener {
        void onLoading(); // 在上拉加载的时候回调的方法
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }
}
