package cx.com.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xinchang on 2017/7/3.
 */

public class FlowLayout extends ViewGroup {
    //用来保存每行View的列表
    private List<List<View>> mViewLinesList = new ArrayList<>();
    //用来保存行高的列表
    private List<Integer> mViewHightList = new ArrayList<>();

    private boolean isSelect;

    private int childWidth = 0;
    private int childHeight = 0;

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidthMeasureSpec = widthMeasureSpec;
        mHeightMeasureSpec = heightMeasureSpec;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int measureWidth = 0;
        int measureHeight = 0;
        int iCurLineW = 0;
        int iCurLineH = 0;
        int childCount = getChildCount();
        List<View> list = new ArrayList<>();
        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
            measureWidth = widthSize;
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                mesureChild(widthMeasureSpec, heightMeasureSpec, childView);
                iCurLineW += childWidth;
                //第一行放不下的时候换行
                if (iCurLineW > measureWidth) {
                    mViewLinesList.add(list);
                    list = new ArrayList<>();
                    list.add(childView);
                    mViewHightList.add(iCurLineH);
                    iCurLineW = 0;
                    iCurLineW += childWidth;
                    iCurLineH = Math.max(iCurLineH, childHeight);
                } else {
                    iCurLineH = Math.max(iCurLineH, childHeight);
                    list.add(childView);
                }
            }
            mViewLinesList.add(list);
            mViewHightList.add(iCurLineH);
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                mesureChild(widthMeasureSpec, heightMeasureSpec, childView);
                if (iCurLineW + childWidth > widthSize) {
                    //换行
                    measureWidth = Math.max(measureWidth, iCurLineW);
                    measureHeight += iCurLineH;
                    mViewLinesList.add(list);
                    mViewHightList.add(iCurLineH);
                    //新一行的赋值
                    iCurLineW = childWidth;
                    iCurLineH = childHeight;
                    list = new ArrayList<View>();
                    list.add(childView);
                } else {
                    //累加
                    iCurLineW += childWidth;
                    iCurLineH = Math.max(iCurLineH, childHeight);
                    //添加到当前行的ViewList中
                    list.add(childView);
                }
                //当最后一行需要换行
                if (i == childCount - 1) {
                    //1、记录当前行的最大宽度，高度累加
                    measureWidth = Math.max(measureWidth, iCurLineW);
                    measureHeight += iCurLineH;
                    //2、将当前行的viewList添加至总的mViewsList，将行高添加至总的行高List
                    mViewLinesList.add(list);
                    mViewHightList.add(iCurLineH);
                }
            }
        } else if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.AT_MOST) {
            measureWidth = widthSize;
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                mesureChild(widthMeasureSpec, heightMeasureSpec, childView);
                iCurLineW += childWidth;
                //第一行放不下的时候换行
                if (iCurLineW > measureWidth) {
                    measureHeight += iCurLineH;
                    mViewLinesList.add(list);
                    list = new ArrayList<>();
                    list.add(childView);
                    mViewHightList.add(iCurLineH);
                    iCurLineW = 0;
                    iCurLineW += childWidth;
                    iCurLineH = Math.max(iCurLineH, childHeight);
                } else {
                    iCurLineH = Math.max(iCurLineH, childHeight);
                    list.add(childView);
                }
            }
            //最后一行的高度要加上去
            measureHeight += iCurLineH;
            mViewLinesList.add(list);
            mViewHightList.add(iCurLineH);

        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.EXACTLY) {
            measureHeight = heightSize;
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                mesureChild(widthMeasureSpec, heightMeasureSpec, childView);
                iCurLineW += childWidth;
                //第一行放不下的时候换行
                if (iCurLineW > measureWidth) {
                    measureWidth = Math.max(measureWidth, iCurLineW - childWidth);
                    mViewLinesList.add(list);
                    list = new ArrayList<>();
                    list.add(childView);
                    mViewHightList.add(iCurLineH);
                    iCurLineW = 0;
                    iCurLineW += childWidth;
                    iCurLineH = Math.max(iCurLineH, childHeight);
                } else {
                    iCurLineH = Math.max(iCurLineH, childHeight);
                    list.add(childView);
                }
            }
            //最后一行的高度要加上去
            mViewLinesList.add(list);
            mViewHightList.add(iCurLineH);

        }

        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int lineCount = mViewLinesList.size();
        int left;
        int top;
        int right;
        int bottom;
        int currentTop = 0;
        int currentLeft = 0;
        for (int i = 0; i < lineCount; i++) {
            List<View> viewList = mViewLinesList.get(i);
            int lineViewSize = viewList.size();
            for (int j = 0; j < lineViewSize; j++) {
                View childView = viewList.get(j);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                left = currentLeft + layoutParams.leftMargin;
                top = currentTop + layoutParams.topMargin;
                right = left + childView.getMeasuredWidth();
                bottom = top + childView.getMeasuredHeight();
                childView.layout(left, top, right, bottom);
                currentLeft += childView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            }
            currentLeft = 0;
            currentTop += mViewHightList.get(i);

        }
        mViewLinesList.clear();
        mViewHightList.clear();
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int index);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            final int j = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, j);
                }
            });
        }
    }

    public void setDefaultSelect(int resource, int... position) {
        for (int i = 0; i < position.length; i++) {
            getChildAt(position[i]).setBackgroundResource(resource);
            getChildAt(position[i]).setSelected(true);
        }
    }

    public int getSelectList() {
        int selectNum = 0;
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (getChildAt(i).isSelected()) {
                selectNum++;
            }
        }
        return selectNum;
    }

    private void mesureChild(int widthMeasureSpec, int heightMeasureSpec, View childView) {
        measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
        childWidth = childView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
        childHeight = childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
    }

    public void removeChildView(int index) {
        this.removeViewAt(index);
    }

//    public void addLastChildView(View childView) {
//        if (childView != null) {
//            ViewGroup parentViewGroup = (ViewGroup) childView.getParent();
//            Log.d("tag", "addLastChildView: " + parentViewGroup);
//            if (parentViewGroup != null ) {
//                parentViewGroup.removeView(childView);
//            }
//        }
//        mesureChild(mWidthMeasureSpec, mHeightMeasureSpec, childView);
//        this.addView(childView);
//
//    }
}




























