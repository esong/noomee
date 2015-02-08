package com.yksong.noomee.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
public class ChiTagView extends LinearLayout {

    private LayoutInflater mInflater;
    private ChiTag[] mTags;

    private int curWidth;
    private LinearLayout curLine;
    private List<LinearLayout> lines;
    private boolean mSelectedOnly;

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

        mInflater = LayoutInflater.from(context);
    }

    public void setTags(ChiTag... tags) {
        if (tags == null) {
            return;
        }

        mTags = tags;
        lines = new ArrayList<>();
        createNewLine();

        removeAllViews();
        for (ChiTag tag : mTags) {
            TextView tagView = (TextView) mInflater.inflate(R.layout.chi_tag_layout,
                    curLine, false);
            StringBuilder sb = new StringBuilder(tag.getName())
                    .append(" (").append(tag.getCount()).append(')');
            tagView.setText(sb.toString());
            tagView.setTag(tag);

            tagView.measure(0, 0);

            curWidth += tagView.getMeasuredWidth();
            if (curWidth > getMeasuredWidth() - 20) {
                flush();
                createNewLine();
                curWidth += tagView.getMeasuredWidth();
            }

            curLine.addView(tagView);

            if (mSelectedOnly) {
                tagView.setSelected(true);
                tagView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((ViewGroup)v.getParent()).removeView(v);
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
        }
        flush();
    }

    public void flush() {
        lines.add(curLine);
        this.addView(curLine);
    }

    public void createNewLine() {
        curLine = (LinearLayout) mInflater.inflate(R.layout.chi_line_layout, this, false);
        curWidth = 0;
    }

    public @NonNull ChiTag[] getSelectedTags() {
        if (lines == null) {
            return new ChiTag[0];
        }

        List<ChiTag> selectedTags = new ArrayList<>();
        for (LinearLayout line : lines) {
            for(int i = 0; i < line.getChildCount(); ++i) {
                View tagView = line.getChildAt(i);

                if (tagView.isSelected()) {
                    selectedTags.add((ChiTag)tagView.getTag());
                }
            }
        }

        ChiTag[] res = new ChiTag[selectedTags.size()];
        return selectedTags.toArray(res);
    }

    public void resetTags(){
        for (LinearLayout line : lines) {
            for(int i = 0; i < line.getChildCount(); ++i) {
                View tagView = line.getChildAt(i);
                tagView.setSelected(false);
                tagView.invalidate();
            }
        }
    }

}
