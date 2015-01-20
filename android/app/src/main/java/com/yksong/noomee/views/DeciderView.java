package com.yksong.noomee.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.yksong.noomee.R;
import com.yksong.noomee.model.ChiTag;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.presenter.DeciderPresenter;

/**
 * Created by esong on 2015-01-01.
 */
public class DeciderView extends FrameLayout {
    private DeciderPresenter mPresenter = new DeciderPresenter();
    private ChiTagView mChiTagView;
    private AlertDialog mDialog;

    public DeciderView(Context context) {
        this(context, null);
    }

    public DeciderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeciderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onFinishInflate() {
        mPresenter.setView(this);

        mChiTagView = (ChiTagView) findViewById(R.id.chi_tag_view);

        findViewById(R.id.cuisine_tab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    v.setSelected(true);
                    mPresenter.getTags();
            }
        });

        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.getRestaurant();
            }
        });
    }

    public ChiTag[] getSelectedTags(){
        return mChiTagView.getSelectedTags();
    }
    public AlertDialog getDialog() {return mDialog;}

    public void showTagsDialog() {
        if (mDialog == null) {
            mDialog = new AlertDialog.Builder(getContext())
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ChiTagView tagView = (ChiTagView)
                                    mDialog.findViewById(R.id.chi_tag_view);

                            mChiTagView.setTags(tagView.getSelectedTags());
                            tagView.resetTags();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setView(LayoutInflater.from(
                            getContext()).inflate(R.layout.cuisine_dialog, null))
                    .show();
        } else {
            mDialog.show();
        }
    }

    public void showRestaurant(Restaurant restaurant) {
        new AlertDialog.Builder(getContext())
                .setMessage(restaurant.name)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .show();
    }
}
