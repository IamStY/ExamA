package testing.steven.myapplication.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StevensSimpleRecyclerPaging extends RecyclerView {
    RecyclerView.OnScrollListener mScrollListener;
    ICallbackLoadMoreData loadMore;

    boolean currentlyRequesting;

    private boolean mChildIsScrolling = false;
    private int mTouchSlop;
    private float mOriginalX;
    private float mOriginalY;


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);

        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the scroll
            mChildIsScrolling = false;
            return false; // Let child handle touch event
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mChildIsScrolling = false;
                setOriginalMotionEvent(ev);
            }
            case MotionEvent.ACTION_MOVE: {
                if (mChildIsScrolling) {
                    // Child is scrolling so let child handle touch event
                    return false;
                }

                // If the user has dragged her finger horizontally more than
                // the touch slop, then child view is scrolling

                final int xDiff = calculateDistanceX(ev);
                final int yDiff = calculateDistanceY(ev);

                // Touch slop should be calculated using ViewConfiguration
                // constants.
                if (xDiff > mTouchSlop && xDiff > yDiff) {
                    mChildIsScrolling = true;
                    return false;
                }
            }
        }

        // In general, we don't want to intercept touch events. They should be
        // handled by the child view.  Be safe and leave it up to the original definition
        return super.onInterceptTouchEvent(ev);
    }

    public void setOriginalMotionEvent(MotionEvent ev) {
        mOriginalX = ev.getX();
        mOriginalY = ev.getY();
    }

    public int calculateDistanceX(MotionEvent ev) {
        return (int) Math.abs(mOriginalX - ev.getX());
    }

    public int calculateDistanceY(MotionEvent ev) {
        return (int) Math.abs(mOriginalY - ev.getY());
    }

    public StevensSimpleRecyclerPaging(Context context) {
        super(context);
        initScrollEvent(context);
    }

    public StevensSimpleRecyclerPaging(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initScrollEvent(context);
    }

    public StevensSimpleRecyclerPaging(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initScrollEvent(context);
    }

    private void initScrollEvent(Context context) {
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
    }

    public void init(ICallbackLoadMoreData iCallbackLoadMoreData) {
        this.loadMore = iCallbackLoadMoreData;
        hookLoadMoreAvailable();
    }

    public void hookLoadMoreAvailable() {
        disableHookLoadMore();
        mScrollListener = null;
        mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int firstVisibleItem = 0;
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

                    firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                } else if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    firstVisibleItem = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                }

                int visibleThreshold = 5;
                boolean isCloseToTheEnd =
                        (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold);
                if (!currentlyRequesting && isCloseToTheEnd) {
                    loadMore.loadmoreData();
                }


            }
        };
        this.addOnScrollListener(mScrollListener);
    }

    public void disableHookLoadMore() {
        if (mScrollListener != null)
            this.removeOnScrollListener(mScrollListener);
    }

    public boolean isCurrentlyRequesting() {
        return currentlyRequesting;
    }

    public void setCurrentlyRequesting(boolean currentlyRequesting) {
        this.currentlyRequesting = currentlyRequesting;
    }

    public interface ICallbackLoadMoreData {
        void loadmoreData();
    }
}
