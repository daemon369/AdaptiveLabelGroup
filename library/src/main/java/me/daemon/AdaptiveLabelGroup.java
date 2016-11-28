package me.daemon;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import me.daemon.library.R;

/**
 * 自适应标签容器
 * Created by daemon on 2016/11/8.
 */
public class AdaptiveLabelGroup extends ViewGroup {

    /**
     * 水平间距
     */
    @ViewDebug.ExportedProperty(category = "layout")
    private int horizontalDividerSize = 0;

    /**
     * 垂直间距
     */
    @ViewDebug.ExportedProperty(category = "layout")
    private int verticalDividerSize = 0;

    /**
     * 最大行数，默认为0不限制行数
     */
    @ViewDebug.ExportedProperty(category = "layout")
    private int maxRows = 0;

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
        if (null != attrs) {
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

            final int maxRows = a.getInt(R.styleable.AdaptiveLabelGroup_max_rows, 0);
            if (maxRows != 0) {
                setMaxRows(maxRows);
            }

            a.recycle();
        }
    }

    /**
     * 设置水平间距
     *
     * @param horizontalDividerSize 水平间距
     */
    public void setHorizontalDividerSize(final int horizontalDividerSize) {
        if (this.horizontalDividerSize != horizontalDividerSize) {
            this.horizontalDividerSize = horizontalDividerSize;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置垂直间距
     *
     * @param verticalDividerSize 垂直间距
     */
    public void setVerticalDividerSize(final int verticalDividerSize) {
        if (this.verticalDividerSize != verticalDividerSize) {
            this.verticalDividerSize = verticalDividerSize;
            requestLayout();
            invalidate();
        }
    }

    /**
     * 设置最大行数
     *
     * @param maxRows 最大行数，默认为0，为0不限制行数
     */
    public void setMaxRows(final int maxRows) {
        if (maxRows < 0) {
            throw new IllegalArgumentException("maxRows can't be negative");
        }

        if (this.maxRows != maxRows) {
            this.maxRows = maxRows;
            requestLayout();
            invalidate();
        }
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
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int count = getChildCount();

        final int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < count; ++i) {
            final View v = getChildAt(i);
            if (v.getVisibility() != GONE) {
                measureChildWithMargins(v, widthMeasureSpec, 0, heightMeasureSpec, 0);
            }
        }

        // 当前总高度
        int totalHeight;

        if (MeasureSpec.EXACTLY == heightSpecMode) {
            totalHeight = heightSpecSize;
        } else {

            // 一行View总宽度，初始为容器左padding
            int width = getPaddingLeft();

            // 当前行高度，以该行View中高度最高的为准
            int rowHeight = 0;

            // 总高度初始加上容器顶部padding
            totalHeight = getPaddingTop();

            // 行数
            int row = 0;

            for (int i = 0; i < count; i++) {
                final View v = getChildAt(i);

                if (v.getVisibility() != GONE) {
                    final LayoutParams lp = (LayoutParams) v.getLayoutParams();
                    final int childW = v.getMeasuredWidth();
                    final int childH = v.getMeasuredHeight();

                    if (width + childW + lp.leftMargin + lp.rightMargin + getPaddingRight() > widthSpecSize) {
                        // 总宽度超过容器宽度，总高度加上垂直间距及当前行高度，换行，重置行高、行宽
                        totalHeight += verticalDividerSize + rowHeight;
                        rowHeight = 0;
                        width = getPaddingLeft();
                        row++;

                        if (maxRows > 0 && row == maxRows) {
                            // 最大行数非0时，超过最大行数不显示
                            break;
                        }
                    }

                    // 行宽加上垂直间距、当前View宽度及左右margin
                    width += horizontalDividerSize + childW + lp.leftMargin + lp.rightMargin;

                    if (rowHeight < childH + lp.topMargin + lp.bottomMargin) {
                        // 行高取当前行中View高度+上下margin最大值
                        rowHeight = childH + lp.topMargin + lp.bottomMargin;
                    }
                }
            }

            // 计算完成，总高度加上最后一行的高度
            totalHeight += rowHeight;

            // 总高度加上容器底部padding
            totalHeight += getPaddingBottom();
        }

        setMeasuredDimension(widthSpecSize, totalHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();

        int width = getPaddingLeft();

        int rowHeight = 0;

        int totalHeight = getPaddingTop();

        final int measuredWidth = getMeasuredWidth();

        int row = 0;

        for (int i = 0; i < count; i++) {
            final View v = getChildAt(i);

            if (v.getVisibility() != GONE) {
                final LayoutParams lp = (LayoutParams) v.getLayoutParams();
                final int childW = v.getMeasuredWidth();
                final int childH = v.getMeasuredHeight();

                if (width + childW + lp.leftMargin + lp.rightMargin + getPaddingRight() > measuredWidth) {
                    totalHeight += verticalDividerSize + rowHeight;
                    rowHeight = 0;
                    width = getPaddingLeft();
                    row++;

                    if (maxRows > 0 && row == maxRows) {
                        break;
                    }
                }

                v.layout(width + lp.leftMargin, totalHeight + lp.topMargin,
                        width + lp.leftMargin + childW, totalHeight + lp.topMargin + childH);

                width += horizontalDividerSize + childW + lp.leftMargin + lp.rightMargin;

                if (rowHeight < childH + lp.topMargin + lp.bottomMargin) {
                    rowHeight = childH + lp.topMargin + lp.bottomMargin;
                }
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
