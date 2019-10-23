package me.daemon.adaptivelabel

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewDebug
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import me.daemon.library.R

/**
 * 自适应标签容器
 *
 * [Github](https://github.com/daemon369/AdaptiveLabelGroup)
 *
 * [jcenter](https://bintray.com/beta/#/daemon336699/maven/adaptivelabelgroup?tab=overview)
 *
 * @author daemon
 * @since 2016-11-08 00:00
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class AdaptiveLabelGroup @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : ViewGroup(context, attrs, defStyleAttr, defStyleRes) {

    /**
     * 水平间距
     */
    @ViewDebug.ExportedProperty(category = "daemon")
    var horizontalDividerSize = 0
        /**
         * 设置水平间距
         *
         * @param horizontalDividerSize 水平间距
         */
        set(horizontalDividerSize) {
            if (field != horizontalDividerSize) {
                field = horizontalDividerSize
                requestLayout()
                invalidate()
            }
        }

    /**
     * 垂直间距
     */
    @ViewDebug.ExportedProperty(category = "daemon")
    var verticalDividerSize = 0
        /**
         * 设置垂直间距
         *
         * @param verticalDividerSize 垂直间距
         */
        set(verticalDividerSize) {
            if (field != verticalDividerSize) {
                field = verticalDividerSize
                requestLayout()
                invalidate()
            }
        }

    /**
     * 最大行数，默认为0不限制行数
     */
    @ViewDebug.ExportedProperty(category = "daemon")
    var maxRows = 0
        /**
         * 设置最大行数
         *
         * @param maxRows 最大行数，默认为0，为0不限制行数
         */
        set(maxRows) {
            require(maxRows >= 0) { "maxRows can't be negative" }

            if (field != maxRows) {
                field = maxRows
                requestLayout()
                invalidate()
            }
        }

    init {
        if (null != attrs) {
            val a = context.obtainStyledAttributes(
                    attrs, R.styleable.AdaptiveLabelGroup, defStyleAttr, defStyleRes)

            horizontalDividerSize = a.getDimension(R.styleable.AdaptiveLabelGroup_horizontal_divider_size, 0f).toInt()
            verticalDividerSize = a.getDimension(R.styleable.AdaptiveLabelGroup_vertical_divider_size, 0f).toInt()
            maxRows = a.getInt(R.styleable.AdaptiveLabelGroup_max_rows, 0)

            a.recycle()
        }
    }

    override fun checkLayoutParams(p: LayoutParams?): Boolean {
        return p is MarginLayoutParams
    }

    override fun generateLayoutParams(attrs: AttributeSet): MarginLayoutParams {
        return MarginLayoutParams(context, attrs)
    }

    override fun generateLayoutParams(lp: LayoutParams): MarginLayoutParams {
        return (lp as? MarginLayoutParams)?.let { MarginLayoutParams(it) } ?: MarginLayoutParams(lp)
    }

    override fun generateDefaultLayoutParams(): MarginLayoutParams {
        return MarginLayoutParams(WRAP_CONTENT, WRAP_CONTENT)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val count = childCount

        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightSpecMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSpecSize = MeasureSpec.getSize(heightMeasureSpec)

        for (i in 0 until count) {
            val v = getChildAt(i)
            if (v.visibility != View.GONE) {
                measureChildWithMargins(v, widthMeasureSpec, 0, heightMeasureSpec, 0)
            }
        }

        // 当前总高度
        var totalHeight: Int

        if (MeasureSpec.EXACTLY == heightSpecMode) {
            totalHeight = heightSpecSize
        } else {

            // 一行View总宽度，初始为容器左padding
            var width = paddingLeft

            // 当前行高度，以该行View中高度最高的为准
            var rowHeight = 0

            // 总高度初始加上容器顶部padding
            totalHeight = paddingTop

            // 行数
            var row = 0

            for (i in 0 until count) {
                val v = getChildAt(i)

                if (v.visibility != View.GONE) {
                    val lp = v.layoutParams as MarginLayoutParams
                    val childW = v.measuredWidth
                    val childH = v.measuredHeight

                    if (width + childW + lp.leftMargin + lp.rightMargin + paddingRight > widthSpecSize) {
                        // 总宽度超过容器宽度，总高度加上垂直间距及当前行高度，换行，重置行高、行宽
                        totalHeight += this.verticalDividerSize + rowHeight
                        rowHeight = 0
                        width = paddingLeft
                        row++

                        if (this.maxRows > 0 && row == this.maxRows) {
                            // 最大行数非0时，超过最大行数不显示
                            break
                        }
                    }

                    // 行宽加上垂直间距、当前View宽度及左右margin
                    width += this.horizontalDividerSize + childW + lp.leftMargin + lp.rightMargin

                    if (rowHeight < childH + lp.topMargin + lp.bottomMargin) {
                        // 行高取当前行中View高度+上下margin最大值
                        rowHeight = childH + lp.topMargin + lp.bottomMargin
                    }
                }
            }

            // 计算完成，总高度加上最后一行的高度
            totalHeight += rowHeight

            // 总高度加上容器底部padding
            totalHeight += paddingBottom
        }

        setMeasuredDimension(widthSpecSize, totalHeight)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount

        var width = paddingLeft

        var rowHeight = 0

        var totalHeight = paddingTop

        val measuredWidth = measuredWidth

        var row = 0

        for (i in 0 until count) {
            val v = getChildAt(i)

            if (v.visibility == View.GONE) {
                continue
            }

            val lp = v.layoutParams as MarginLayoutParams
            val childW = v.measuredWidth
            val childH = v.measuredHeight

            if (width + childW + lp.leftMargin + lp.rightMargin + paddingRight > measuredWidth) {
                totalHeight += this.verticalDividerSize + rowHeight
                rowHeight = 0
                width = paddingLeft
                row++

                if (this.maxRows > 0 && row == this.maxRows) {
                    break
                }
            }

            v.layout(width + lp.leftMargin, totalHeight + lp.topMargin,
                    width + lp.leftMargin + childW, totalHeight + lp.topMargin + childH)

            width += this.horizontalDividerSize + childW + lp.leftMargin + lp.rightMargin

            if (rowHeight < childH + lp.topMargin + lp.bottomMargin) {
                rowHeight = childH + lp.topMargin + lp.bottomMargin
            }
        }
    }
}
