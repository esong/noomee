package com.yksong.noomee.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yksong.noomee.R;
import com.yksong.noomee.model.ChiTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by esong on 2015-01-13.
 * Copied from Chi/NomNomNom project
 *
 */
public class ChiTagView extends ViewGroup {

    private LayoutInflater mInflater;
    private ChiTag[] mTags;
    private List<TextView> mTagViews;

    private boolean mSelectedOnly;
    private float mElementPadding;
    private float mRowPadding;

    public ChiTagView(Context context) {
        this(context, null);
    }

    public ChiTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChiTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChiTagView);

        mSelectedOnly = a.getBoolean(R.styleable.ChiTagView_selected_only, false);
        mElementPadding = getResources().getDimension(R.dimen.chi_tag_element_padding);
        mRowPadding = getResources().getDimension(R.dimen.chi_tag_row_padding);

        mInflater = LayoutInflater.from(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int currentHeight = (int) mRowPadding;

        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int currentLineWidth = (int) mElementPadding;
        if (mTagViews!= null && mTagViews.size() != 0) {
            for (TextView tagView : mTagViews) {
                int viewWidth = tagView.getMeasuredWidth();

                if (currentLineWidth + viewWidth > parentWidth - 20) {
                    // next line
                    currentHeight += tagView.getMeasuredHeight() + mRowPadding;
                    currentLineWidth = (int) mElementPadding;
                }

                currentLineWidth += viewWidth + mElementPadding;
            }

            currentHeight += mTagViews.get(mTagViews.size() - 1).getMeasuredHeight() + mRowPadding;
        }

        setMeasuredDimension(parentWidth, currentHeight);
    }

        @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){
        top = (int) mRowPadding;
        left = (int) mElementPadding;
        int currentHeight = top;
        int parentWidth = right - left;
        int currentLineWidth = left;
        if (mTagViews!= null && mTagViews.size() != 0) {
            for (TextView tagView : mTagViews) {
                int viewWidth = tagView.getMeasuredWidth();
                int viewHeight = tagView.getMeasuredHeight();

                if (currentLineWidth + viewWidth > parentWidth - 20) {
                    // next line
                    currentHeight += tagView.getMeasuredHeight() + mRowPadding;
                    currentLineWidth = left;
                }

                tagView.layout(currentLineWidth, currentHeight, currentLineWidth + viewWidth,
                        currentHeight + viewHeight);
                currentLineWidth += viewWidth + mElementPadding;
            }
        }
    }

    public void setTags(ChiTag... tags) {
        mTags = tags;
        mTagViews = new ArrayList<TextView>(tags.length);

        removeAllViews();
        for (int i = 0; i < tags.length; ++i) {
            TextView tagView = (TextView) mInflater.inflate(R.layout.chi_tag_layout, null);
            StringBuilder sb = new StringBuilder(tags[i].getName())
                    .append(" (").append(tags[i].getCount()).append(')');
            tagView.setText(sb.toString());
            tagView.setTag(tags[i]);

            tagView.measure(0,0);

            mTagViews.add(tagView);

            if (mSelectedOnly) {
                tagView.setSelected(true);
                tagView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mTagViews.remove(v);
                        ((ViewGroup)v.getParent()).removeView(v);
                        ChiTagView.this.invalidate();
                    }
                });
            } else {
                tagView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            v.setSelected(false);
                        } else {
                            v.setSelected(true);
                        }
                    }
                });
            }

            addView(tagView);
        }

        invalidate();
    }

    public @NonNull ChiTag[] getSelectedTags() {
        List<ChiTag> selectedTags = new ArrayList<>();
        if (mTagViews == null) {
            return new ChiTag[0];
        }

        for (TextView tagView : mTagViews) {
            if (tagView.isSelected()) {
                selectedTags.add((ChiTag)tagView.getTag());
            }
        }

        ChiTag[] res = new ChiTag[selectedTags.size()];
        return selectedTags.toArray(res);
    }

    public void resetTags(){
        for (TextView tagView : mTagViews) {
                tagView.setSelected(false);
                tagView.invalidate();
        }
    }

    @Override
    public Parcelable onSaveInstanceState() {
        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);

        ss.stateToSave = this.mTags;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());

        if (ss.stateToSave != null) {
            setTags(ss.stateToSave);
        }
    }

    static class SavedState extends BaseSavedState {
        ChiTag[] stateToSave;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.stateToSave = in.createTypedArray(ChiTag.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeArray(this.stateToSave);
        }

        //required field that makes Parcelables from a Parcel
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
