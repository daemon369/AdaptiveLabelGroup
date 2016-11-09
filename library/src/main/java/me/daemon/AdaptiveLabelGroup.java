package me.daemon;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import me.daemon.library.R;

/**
 * 自适应标签容器<br/>
 * Created by daemon on 2016/11/8.
 */
public class AdaptiveLabelGroup extends ViewGroup {

    /**
     * 水平间距 TODO
     */
    private int horizontalDividerSize = 0;

    /**
     * 垂直间距 TODO
     */
    private int verticalDividerSize = 0;

    public AdaptiveLabelGroup(Context context) {
        super(context);

        init(context, null, 0, 0);
    }

    public AdaptiveLabelGroup(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, 0, 0);
    }

    public AdaptiveLabelGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public AdaptiveLabelGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.AdaptiveLabelGroup, defStyleAttr, defStyleRes);

        final int hDividerSize = a.getDimensionPixelSize(R.styleable.AdaptiveLabelGroup_horizontal_divider_size, 0);
        if (hDividerSize != 0) {
            setHorizontalDividerSize(hDividerSize);
        }

        final int vDividerSize = a.getDimensionPixelSize(R.styleable.AdaptiveLabelGroup_vertical_divider_size, 0);
        if (vDividerSize != 0) {
            setVerticalDividerSize(vDividerSize);
        }

        a.recycle();
    }

    public void setHorizontalDividerSize(final int horizontalDividerSize) {
        this.horizontalDividerSize = horizontalDividerSize;
        requestLayout();
        invalidate();
    }

    public void setVerticalDividerSize(final int verticalDividerSize) {
        this.verticalDividerSize = verticalDividerSize;
        requestLayout();
        invalidate();
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new LayoutParams((MarginLayoutParams) lp);
        } else {
            return new LayoutParams(lp);
        }
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);

        final int count = getChildCount();

        // 一行View总宽度
        int width = 0;
        // 当前总高度
        int totalHeight = 0;
        // 当前行高度，以该行View中高度最高的为准
        int rowHeight = 0;

        switch (heightSpecMode) {
            case MeasureSpec.EXACTLY:
                totalHeight = heightSpecSize;
                break;

            default:
                for (int i = 0; i < count; i++) {
                    final View v = getChildAt(i);
                    final LayoutParams lp = (LayoutParams) v.getLayoutParams();
                    final int childW = v.getMeasuredWidth();
                    final int childH = v.getMeasuredHeight();

                    if (width + childW + lp.leftMargin + lp.rightMargin > widthSpecSize) {
                        // 总宽度超过容器宽度，总高度加上加上当前行高度，换行，清空行高、行宽
                        totalHeight += rowHeight;
                        rowHeight = 0;
                        width = 0;
                    }

                    // 行宽加上当前View宽度及左右margin
                    width += childW + lp.leftMargin + lp.rightMargin;

                    if (rowHeight < childH + lp.topMargin + lp.bottomMargin) {
                        // 行高取当前行中View高度+上下margin最大值
                        rowHeight = childH + lp.topMargin + lp.bottomMargin;
                    }
                }

                // 计算完成，总高度加上最后一行的高度
                totalHeight += rowHeight;
                break;
        }

        setMeasuredDimension(widthSpecSize, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = 0;
        int totalHeight = 0;
        int rowHeight = 0;

        final int measuredWidth = getMeasuredWidth();
        final int measuredHeight = getMeasuredHeight();

        final int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);
            final LayoutParams lp = (LayoutParams) v.getLayoutParams();
            final int childW = v.getMeasuredWidth();
            final int childH = v.getMeasuredHeight();

            if (width + childW + lp.leftMargin + lp.rightMargin > measuredWidth) {
                totalHeight += rowHeight;
                rowHeight = 0;
                width = 0;
            }

            v.layout(width + lp.leftMargin, totalHeight + lp.topMargin, width + lp.leftMargin + childW, totalHeight + lp.topMargin + childH);

            width += childW + lp.leftMargin + lp.rightMargin;

            if (rowHeight < childH + lp.topMargin + lp.bottomMargin) {
                rowHeight = childH + lp.topMargin + lp.bottomMargin;
            }
        }
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(LayoutParams source) {
            super(source);
        }
    }
}
