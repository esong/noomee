package com.yksong.noomee.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.widget.ProfilePictureView;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.yksong.noomee.NewEventActivity;
import com.yksong.noomee.R;
import com.yksong.noomee.model.EatingEvent;
import com.yksong.noomee.model.FacebookUser;
import com.yksong.noomee.model.Restaurant;
import com.yksong.noomee.network.NoomeeClient;
import com.yksong.noomee.presenter.ActivitiesPresenter;
import com.yksong.noomee.util.NoomeeAPI;
import com.yksong.noomee.util.ParseAPI;
import com.yksong.noomee.util.YelpUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by esong on 2015-01-01.
 */
public class ActivitiesView extends FrameLayout implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView mRecList;
    private ActivitiesPresenter mPresenter = new ActivitiesPresenter();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mStarted;

    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private static int sVisibleThreshold = 20;

    private NoomeeAPI mNoomeeAPI = NoomeeClient.getApi();

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
        final LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecList.setLayoutManager(llm);

        EventAdapter ca = new EventAdapter(new ArrayList<EatingEvent>());
        mRecList.setAdapter(ca);

        mRecList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = llm.getItemCount();
                int firstVisibleItem = llm.findFirstVisibleItemPosition();

                if (mLoading) {
                    if (totalItemCount > mPreviousTotal) {
                        mLoading = false;
                        mPreviousTotal = totalItemCount;
                    }
                }
                if (!mLoading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + sVisibleThreshold)/2) {

                    getEventListAsync(mPreviousTotal, sVisibleThreshold);

                    mLoading = true;
                }
            }
        });

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

        getEventListAsync(0, sVisibleThreshold);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !mStarted) {
            mSwipeRefreshLayout.setRefreshing(true);
            mStarted = true;
        }
    }

    public void createList(List<EatingEvent> result, int skip){
        mSwipeRefreshLayout.setRefreshing(false);
        mStarted = true;

        EventAdapter eventAdapter;
        // if reload the list
        if (skip == 0) {
            EatingEvent currentFirst = ((EventAdapter)mRecList.getAdapter()).getFirstItem();
            if ( currentFirst == null || !currentFirst.equals(result.get(0))) {
                mPreviousTotal = 0;
                eventAdapter = new EventAdapter(result);
                mRecList.setAdapter(eventAdapter);
            } else {
                mRecList.getAdapter().notifyItemRangeChanged(skip, sVisibleThreshold);
            }
        } else {
            ((EventAdapter)mRecList.getAdapter()).appendList(result);
        }
    }

    @Override
    public void onRefresh() {
        getEventListAsync(0, sVisibleThreshold);
    }

    private void getEventListAsync(int skip, int limit) {
        mPresenter.getEventList(skip, limit);
    }

    public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ContactViewHolder> {

        private List<EatingEvent> eventList;
        SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("MMM dd' at 'HH:mm");

        public EventAdapter(List<EatingEvent> eventList) {
            this.eventList = eventList;
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }

        public EatingEvent getFirstItem() {
            if (eventList != null && eventList.size() > 0) {
                return eventList.get(0);
            }

            return null;
        }

        public void appendList(List<EatingEvent> newList) {
            eventList.addAll(eventList.size(), newList);
            notifyItemRangeInserted(eventList.size(), newList.size());
        }

        private void loadPicture(String url, ImageView view) {
            Picasso.with(getContext())
                    .load(YelpUtil.switchToLsImageUrl(url))
                    .into(view);
        }

        @Override
        public void onBindViewHolder(final ContactViewHolder contactViewHolder, int i) {
            final EatingEvent event = eventList.get(i);

            Picasso.with(getContext()).load(R.drawable.empty_plate)
                    .into(contactViewHolder.mRestaurantPicture);

            if (event.restaurantId != null) {
                if (event.restaurant == null) {
                    mNoomeeAPI.business(event.restaurantId, new Callback<Restaurant>() {
                        @Override
                        public void success(Restaurant restaurant, Response response) {
                            event.restaurant = restaurant;
                            loadPicture(event.restaurant.image_url,
                                    contactViewHolder.mRestaurantPicture);
                        }

                        @Override
                        public void failure(RetrofitError error) {

                        }
                    });
                } else {
                    loadPicture(event.restaurant.image_url,
                            contactViewHolder.mRestaurantPicture);
                }
            }

            contactViewHolder.mEventInfo.setText(event.location);
            contactViewHolder.mEventDate.setText(mSimpleDateFormat.format(event.time) + "\n");

            contactViewHolder.mProfilePicture.setProfileId(event.users.get(0).mId);
            contactViewHolder.mUserName.setText(event.users.get(0).mName);
            contactViewHolder.mTimeText.setText(
                    DateUtils.getRelativeTimeSpanString(event.createdTime.getTime()));
            contactViewHolder.mIsJoin=false;

            final boolean past = event.time.getTime() < System.currentTimeMillis();
            if (!past) {
                contactViewHolder.mButton.setVisibility(VISIBLE);
                contactViewHolder.mButton.setText("Join");

                for(int count=0;count<event.users.size();count++){
                    FacebookUser user = event.users.get(count);

                    if( user.mId.compareTo((String)(ParseUser.getCurrentUser().get("fbId")))==0 ){
                        contactViewHolder.mButton.setText("Unjoin");
                        if(count==0){
                            contactViewHolder.mButton.setText("Cancel");
                        }
                        contactViewHolder.mIsJoin = true;
                    }
                }
            } else {
                contactViewHolder.mButton.setVisibility(GONE);
            }


            final List<FacebookUser> list = event.users;
            contactViewHolder.mButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if(contactViewHolder.mButton.getText().toString().compareTo("Cancel")==0){
                        ParseAPI.removeEvent(ParseUser.getCurrentUser(), event.eventId);
                        contactViewHolder.remove();
                    } else {
                        ParseUser curUser = ParseUser.getCurrentUser();
                        String name = curUser.getString("firstName") + " "
                                + curUser.getString("lastName");
                        String curId = curUser.getString("fbId");
                        if (!contactViewHolder.mIsJoin) {

                            list.add(new FacebookUser(curId, name));
                            ParseAPI.joinEvent(ParseUser.getCurrentUser(), event.eventId);
                            contactViewHolder.mIsJoin = true;
                            contactViewHolder.mButton.setText("Unjoin");
                            contactViewHolder.mListPeople.setText(
                                    "You and " + (list.size()-1) + " others" +
                                            (past ? " went." : " are going."));
                        } else {
                            list.remove(new FacebookUser(curId,name));
                            ParseAPI.leaveEvent(ParseUser.getCurrentUser(), event.eventId);
                            contactViewHolder.mIsJoin = false;
                            contactViewHolder.mButton.setText("Join");
                            contactViewHolder.mListPeople.setText(event.users.get(0).mName +
                                    " and "+(list.size()-1)+" others" +
                                    (past ? " went." : " are going."));
                        }
                    }
                }
            });

            String mListPeople = "";
            if( contactViewHolder.mIsJoin ){
                mListPeople = "You and " + (event.users.size()-1) + " others" +
                        (past ? " went." : " are going.");
            } else{
                mListPeople = event.users.get(0).mName+" and "
                        + (event.users.size()-1) + " others" + (past ? " went." : " are going.");
            }
            contactViewHolder.mListPeople.setText(mListPeople);
            contactViewHolder.mListPeople.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = "";
                    for(FacebookUser user : list){
                        text += "    "+user.mName+"\n";
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(text).show();
                }
            });
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout, viewGroup, false);

            return new ContactViewHolder(itemView);
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder{
            private TextView mEventInfo;
            private TextView mEventDate;
            private ProfilePictureView mProfilePicture;
            private ImageView mRestaurantPicture;
            private TextView mUserName;
            private TextView mTimeText;
            private TextView mListPeople;
            private Button mButton;
            private boolean mIsJoin;

            public ContactViewHolder(View v) {
                super(v);
                mEventInfo = (TextView) v.findViewById(R.id.txtEvent);
                mEventDate = (TextView) v.findViewById(R.id.event_time_text);
                mProfilePicture = (ProfilePictureView) v.findViewById(R.id.profile_picture);
                mRestaurantPicture = (ImageView) v.findViewById(R.id.restaurant_picture);
                mUserName = (TextView) v.findViewById(R.id.user_name);
                mTimeText = (TextView) v.findViewById(R.id.time_text);
                mListPeople = (TextView) v.findViewById(R.id.people_list);
                mButton = (Button) v.findViewById(R.id.going_button);
                mIsJoin = false;
            }

            public void remove() {
                eventList.remove(getPosition());
                notifyItemRemoved(getPosition());
                notifyItemRangeChanged(getPosition(), eventList.size());
            }
        }
    }
}

