package com.yksong.noomee.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.seismic.ShakeDetector;
import com.yksong.noomee.R;
import com.yksong.noomee.model.ChiTag;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.presenter.DeciderPresenter;

import java.net.URI;

/**
 * Created by esong on 2015-01-01.
 */
public class DeciderView extends FrameLayout implements ShakeDetector.Listener {
    private DeciderPresenter mPresenter = new DeciderPresenter();
    private ChiTagView mChiTagView;
    private AlertDialog mDialog;
    private AlertDialog mLoadingDialog;
    private boolean mRequesting;

    public DeciderView(Context context) {
        this(context, null);
    }

    public DeciderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DeciderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mLoadingDialog = new ProgressDialog(getContext());
        mLoadingDialog.setMessage(context.getString(R.string.Loading));

        /*
         * Shake Detection
         */
        SensorManager sensorManager = (SensorManager)
                getContext().getSystemService(Context.SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);
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
                requestRestaurant();
            }
        });
    }

    private void requestRestaurant() {
        mRequesting = true;
        mLoadingDialog.show();
        mPresenter.getRestaurant();
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

    public void showRestaurant(final Restaurant restaurant) {
        mRequesting = false;
        mLoadingDialog.dismiss();

        final ViewGroup restaurantView = (ViewGroup) LayoutInflater.from(getContext())
                .inflate(R.layout.restaurant_card_view, null);

        if (restaurant.image_url != null) {
            Picasso.with(getContext())
                    .load(restaurant.image_url)
                    .into((ImageView) restaurantView.findViewById(R.id.restaurant_image));

            Picasso.with(getContext())
                    .load(restaurant.rating_img_url)
                    .into((ImageView) restaurantView.findViewById(R.id.restaurant_rating_image));
        }

        TextView nameTextView = (TextView) restaurantView.findViewById(R.id.restaurant_name);
        nameTextView.setText(restaurant.name);
        nameTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(restaurant.mobile_url));
                getContext().startActivity(browserIntent);
            }
        });

         new AlertDialog.Builder(getContext())
                .setView(restaurantView)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                .setNeutralButton("RETRY",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                requestRestaurant();
                            }
                        })
                .show();
    }

    @Override
    public void hearShake() {
        if (!mRequesting) {
            requestRestaurant();
        }
    }
}
