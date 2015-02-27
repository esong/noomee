package com.yksong.noomee.views;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.yksong.noomee.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

import com.yksong.noomee.NewEventActivity;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.presenter.ActivitiesPresenter;

/**
 * Created by esong on 2015-01-01.
 */
public class ActivitiesView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecList;
    private ActivitiesPresenter mPresenter = new ActivitiesPresenter();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mStarted;

    public ActivitiesView(Context context) {
        this(context, null);
    }

    public ActivitiesView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ActivitiesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        mPresenter.setView(this);

        mRecList = (RecyclerView) findViewById(R.id.cardList);
        mRecList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecList.setLayoutManager(llm);

        ContactAdapter ca = new ContactAdapter(new ArrayList<EatingEvent>());
        mRecList.setAdapter(ca);

        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(
                        new Intent(getContext(), NewEventActivity.class));
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));

        mPresenter.getEventList();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !mStarted) {
            mSwipeRefreshLayout.setRefreshing(true);
            mStarted = true;
        }
    }

    public void createList(List<EatingEvent> result){
        mSwipeRefreshLayout.setRefreshing(false);
        ContactAdapter ca = new ContactAdapter(result);
        mRecList.setAdapter(ca);
    }

    @Override
    public void onRefresh() {
        mPresenter.getEventList();
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private List<EatingEvent> eventList;
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MM-dd'-'HH:mm");

        public ContactAdapter(List<EatingEvent> eventList) {
            this.eventList = eventList;
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        @Override
        public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
            EatingEvent ci = eventList.get(i);

            contactViewHolder.mEventInfo.setText(TextUtils.join(", ", ci.users) +
                    " going to eat at " + ci.location + " at "
                    + mSimpleDateFormat.format(ci.time));

            contactViewHolder.mProfilePicture.setProfileId(ci.users.get(0).id);
            contactViewHolder.mUserName.setText(ci.users.get(0).name);
            contactViewHolder.mTimeText.setText(
                    DateUtils.getRelativeTimeSpanString(ci.time.getTime()));
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout, viewGroup, false);

            return new ContactViewHolder(itemView);
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {
            private TextView mEventInfo;
            private ProfilePictureView mProfilePicture;
            private TextView mUserName;
            private TextView mTimeText;

            public ContactViewHolder(View v) {
                super(v);
                mEventInfo = (TextView) v.findViewById(R.id.txtEvent);
                mProfilePicture = (ProfilePictureView) v.findViewById(R.id.profile_picture);
                mUserName = (TextView) v.findViewById(R.id.user_name);
                mTimeText = (TextView) v.findViewById(R.id.time_text);
            }
        }
    }
}


