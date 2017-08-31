package com.shuyu.bind;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

/**
 * 实现分割线
 */
public class BindItemDecoration extends RecyclerView.ItemDecoration {

    private BindRecyclerAdapter bindSuperAdapter;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int space = 50;

    private int spanCount;

    private int orientation;

    private int dataSize;

    private int offsetPosition;

    private int endDataPosition;

    public BindItemDecoration(BindRecyclerAdapter bindRecyclerAdapter) {
        this(bindRecyclerAdapter, Color.BLACK);
    }

    public BindItemDecoration(BindRecyclerAdapter bindRecyclerAdapter, int color) {
        this(bindRecyclerAdapter, new Paint(Paint.ANTI_ALIAS_FLAG));
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
    }

    public BindItemDecoration(BindRecyclerAdapter bindRecyclerAdapter, Paint paint) {
        this.bindSuperAdapter = bindRecyclerAdapter;
        this.paint = paint;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getLayoutManager() != null) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

            //获取下拉刷新和头的偏移位置
            initOffsetPosition();

            if (layoutManager instanceof GridLayoutManager || layoutManager instanceof StaggeredGridLayoutManager) {
                orientation = getOrientation(layoutManager);
                spanCount = getSpanCount(layoutManager);
                gridRect(parent, view, outRect);
            } else if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                orientation = linearLayoutManager.getOrientation();
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    outRect.right = space;
                } else {
                    outRect.bottom = space;
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (parent.getLayoutManager() != null) {
            RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
            //根据布局管理器绘制边框
            if (layoutManager instanceof LinearLayoutManager && !(layoutManager instanceof GridLayoutManager)) {
                //线性的
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                if (linearLayoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    drawLineVertical(c, parent);
                } else {
                    drawLineHorizontal(c, parent);
                }
            } else {
                drawGridVertical(c, parent);
            }
        }
    }

    /**
     * 初始化偏移位置，不包含刷新、头部、上拉等item
     */
    void initOffsetPosition() {
        //获取下拉刷新和头的偏移位置
        if (bindSuperAdapter instanceof BindSuperAdapter) {
            offsetPosition = ((BindSuperAdapter) bindSuperAdapter).absFirstPosition();
        }
        dataSize = (bindSuperAdapter.getDataList() != null ? bindSuperAdapter.getDataList().size() : 0);
        endDataPosition = offsetPosition + dataSize;
        if (endDataPosition < 0) {
            endDataPosition = 0;
        }
    }

    /**
     * 配置grid模式的item rect
     */
    void gridRect(RecyclerView parent, View view, Rect outRect) {
        int currentPosition = parent.getChildAdapterPosition(view);
        int spanIndex = getSpanIndex(parent, view, (RecyclerView.LayoutParams) view.getLayoutParams());
        if (currentPosition >= offsetPosition && currentPosition < endDataPosition) {
            if (spanIndex == (spanCount - 1)) {
                outRect.bottom = space;

                outRect.right = space;
            } else if (spanIndex == 0) {
                outRect.right = space;
                outRect.bottom = space;

                outRect.left = space;
            }
        }
    }

    void drawLineVertical(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + space;
            if (paint != null) {
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    void drawLineHorizontal(Canvas canvas, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + layoutParams.bottomMargin;
            int bottom = top + space;
            if (paint != null) {
                canvas.drawRect(left, top, right, bottom, paint);
            }
        }
    }

    void drawGridVertical(Canvas canvas, RecyclerView parent) {
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            int currentPosition = parent.getChildAdapterPosition(child);
            int spanIndex = getSpanIndex(parent, child, (RecyclerView.LayoutParams) child.getLayoutParams());
            if (currentPosition >= offsetPosition && currentPosition < endDataPosition) {
                if (spanIndex == (spanCount - 1)) {
                    int top = child.getTop();
                    int bottomTop = child.getBottom();
                    int bottom = bottomTop + space;
                    int left = child.getLeft();
                    int right = child.getRight();
                    int rightRight = right + space;

                    if (paint != null) {
                        canvas.drawRect(left, bottomTop, right, bottom, paint);
                    }

                    if (paint != null) {
                        canvas.drawRect(right, top, rightRight, bottom, paint);
                    }

                } else if (spanIndex == 0) {
                    int top = child.getTop();
                    int bottomTop = child.getBottom();
                    int bottom = bottomTop + space;
                    int left = child.getLeft();
                    int leftRight = left + space;
                    int right = child.getRight();
                    int rightRight = right + space;

                    if (paint != null) {
                        canvas.drawRect(left, bottomTop, right, bottom, paint);
                    }

                    if (paint != null) {
                        canvas.drawRect(right, top, rightRight, bottom, paint);
                    }

                    if (paint != null) {
                        canvas.drawRect(left - space, top, leftRight - space, bottom, paint);
                    }
                }
            }

        }
    }


    public int getSpanIndex(RecyclerView parent, View view, RecyclerView.LayoutParams layoutParams) {
        if (layoutParams instanceof GridLayoutManager.LayoutParams) {
            int currentPosition = parent.getChildAdapterPosition(view);
            return ((currentPosition - offsetPosition) % spanCount);
        } else if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            return ((StaggeredGridLayoutManager.LayoutParams) layoutParams).getSpanIndex();
        }
        return 0;
    }

    public int getOrientation(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getOrientation();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }
        return 0;
    }

    public int getSpanCount(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return 0;
    }

}