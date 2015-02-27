package com.yksong.noomee.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.ParseException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.yksong.noomee.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.yksong.noomee.util.APICallback;
import com.yksong.noomee.util.ParseAPI;

/**
 * Created by esong on 2015-01-01.
 */
public class ActivitiesView extends FrameLayout {
    private RecyclerView mRecList;

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
        findViewById(R.id.fab).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ( (Activity) getContext() ).startActivity(
                                new Intent(getContext(), NewEventActivity.class));
            }
        });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


        mRecList = (RecyclerView) findViewById(R.id.cardList);
        mRecList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecList.setLayoutManager(llm);

        ContactAdapter ca = new ContactAdapter(new ArrayList<EatingEvent>());
        mRecList.setAdapter(ca);
        getEventList();
    }

    private void createList(List<EatingEvent> result){
        ContactAdapter ca = new ContactAdapter(result);
        mRecList.setAdapter(ca);
    }


    private void getEventList() {
        ParseAPI.getMyAndFriendsEvents( ParseUser.getCurrentUser(),
                new APICallback<List<ParseObject>>() {
                    @Override
                    public void run(List<ParseObject> parseObj) {
                        for(int i=0;i<parseObj.size();i++)
                        {
                            ArrayList<ParseObject> users = (ArrayList<ParseObject>)parseObj.get(i).get("users");
                            for(int j=0;j<users.size();j++)
                                System.out.println("user "+j+": "+users.get(j));
                            System.out.println("cnm2: "+parseObj.get(i).get("scheduledAt"));
                        }

                        List<EatingEvent> result = new ArrayList<EatingEvent>();
                        for (int i=1; i <= 20; i++) {
                            EatingEvent ci = new EatingEvent();

                            ci.location="Fucking Tomato";
                            result.add(ci);
                        }
                        createList(result);
                    }
                },
                20);
    }

    public class EatingEvent {
        protected String creator;
        protected String location;
        protected SimpleDateFormat time;
        protected ArrayList<String> users;
    }

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

        private List<EatingEvent> eventList;

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
            ci.time=new SimpleDateFormat("yyyy-MM-dd'-'HH:mm");
            String date="";
            try {
                Calendar c = Calendar.getInstance();
                date = ci.time.format(c.getTime());
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            contactViewHolder.eventInfo.setText("I am going to eat at "+ci.location+" at "+date);
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_layout, viewGroup, false);

            return new ContactViewHolder(itemView);
        }

        public class ContactViewHolder extends RecyclerView.ViewHolder {

            protected TextView eventInfo;

            public ContactViewHolder(View v) {
                super(v);
                eventInfo =  (TextView) v.findViewById(R.id.txtEvent);
            }
        }
    }
}


