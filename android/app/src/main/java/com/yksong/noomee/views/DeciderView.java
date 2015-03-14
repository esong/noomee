package com.yksong.noomee.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.seismic.ShakeDetector;
import com.yksong.noomee.NewEventActivity;
import com.yksong.noomee.R;
import com.yksong.noomee.model.ChiTag;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.presenter.DeciderPresenter;
import com.yksong.noomee.util.GeoProvider;
import com.yksong.noomee.widget.LoadingView;

import java.net.URI;
import java.util.Locale;

/**
 * Created by esong on 2015-01-01.
 */
public class DeciderView extends FrameLayout implements ShakeDetector.Listener {
    private DeciderPresenter mPresenter = new DeciderPresenter();
    private ChiTagView mChiTagView;
    private AlertDialog mLoadingDialog;
    private LoadingView mLoadingView;
    private boolean mRequesting;

    private GeoProvider mGeoProvider = GeoProvider.getInstance();
    private GoogleMap mMap;
    private Marker mCurrentMarker;

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

        mLoadingView = (LoadingView) findViewById(R.id.restaurant_container);
        mChiTagView = (ChiTagView) findViewById(R.id.chi_tag_view);

        findViewById(R.id.cuisine_tab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                    v.setSelected(true);
                    mLoadingDialog.show();
                    mPresenter.getTags();
            }
        });

        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestRestaurant();
            }
        });

        ((MapFragment)((FragmentActivity)getContext())
                .getFragmentManager().findFragmentById(R.id.map))
                .getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        mMap = googleMap;
                        mMap.setMyLocationEnabled(true);

                        LatLng latLng = mGeoProvider.getLatLng();
                        if (latLng != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        }
                    }
                });
    }

    public ChiTag[] getSelectedTags(){
        return mChiTagView.getSelectedTags();
    }

    public void showTagsDialog(ChiTag[] tags) {
        mLoadingDialog.dismiss();
        final ChiTagView dialogTagView = (ChiTagView)LayoutInflater.from(
                getContext()).inflate(R.layout.cuisine_dialog, null);

        dialogTagView.setTags(tags);

        new AlertDialog.Builder(getContext())
            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mChiTagView.setTags(dialogTagView.getSelectedTags());
                    dialogTagView.resetTags();
                }
            })
            .setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            })
            .setView(dialogTagView)
            .show();
    }

    private void requestRestaurant() {
        mRequesting = true;
        mLoadingView.load();
        mPresenter.getRestaurant();
    }

    public void showRestaurant(final Restaurant restaurant) {
        mRequesting = false;
        mLoadingView.finish();

        final ViewGroup restaurantView = (ViewGroup)
                mLoadingView.findViewById(R.id.restaurant_view);

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

        restaurantView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), NewEventActivity.class);

                intent.putExtra("restaurantName", restaurant.name);
                intent.putExtra("restaurantId", restaurant.id);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                getContext().startActivity(intent);
            }
        });

        if (mMap != null) {
            LatLng latLng = new LatLng(restaurant.location.coordinate.latitude,
                    restaurant.location.coordinate.longitude);

            if (mCurrentMarker != null) {
                mCurrentMarker.remove();
            }

            mCurrentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(restaurant.name));

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                    new LatLngBounds.Builder()
                            .include(latLng)
                            .include(mGeoProvider.getLatLng())
                            .build(), 80));
        }
    }

    public void showLocationPromote() {
        mLoadingDialog.dismiss();
        new AlertDialog.Builder(getContext())
                .setTitle(R.string.location_disabled)
                .setMessage(R.string.location_disalbed_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
