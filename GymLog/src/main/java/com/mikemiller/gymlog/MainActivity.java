package com.mikemiller.gymlog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    SimpleCounterFragment mEpisodeFragment;

    ArrayList<Integer> mMonday = new ArrayList<Integer>() {{
        add(2);
        add(3);
    }};
    ArrayList<Integer> mWednesday = new ArrayList<Integer>() {{
        add(4);
        add(5);
    }};
    ArrayList<Integer> mFriday = new ArrayList<Integer>() {{
        add(6);
        add(7);
        add(1);
    }};

    // 1 = Sunday
    final Map<ArrayList<Integer>, Activity[]> mDaysOfWeekToActivity =  new HashMap<ArrayList<Integer>, Activity[]>(){{
        put(mMonday, new Activity[]{
                new Activity("Barbell Deadlifts", 3, 4, 6, 10, 1),
                new Activity("Pendlay Rows", 2, 4, 6, 10, 1),
                new Activity("Assisted Chin-ups", 3, 4, 6, 10, 1) });
        put(mWednesday, new Activity[]{
                new Activity("Flat Barbell Bench Press", 3, 6, 10, 10, 1),
                new Activity("Incline Barbell Bench Press", 2, 8, 12, 10, 1),
                new Activity("Barbell Curls", 3, 0, 8, 0, 0)});
        put(mFriday, new Activity[]{
                new Activity("Barbell Squats", 2, 6, 8, 10, 1),
                new Activity("Widowmaker Squats", 1, 20, 20, 0, 0),
                new Activity("Stiff Legged Deadlifts", 2, 15, 20, 10, 1),
                new Activity("Tricep Pushdown", 3, 0, 8, 0, 0),
                new Activity("Weighted Ab Cable Crunches", 1, 10, 20, 0, 0)});
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getFragmentManager()));

        mEpisodeFragment = SimpleCounterFragment.newInstance("Whose Line");

        //String summary = getWeekSummary();
        //Log.d("Summary", summary);
    }

    // 1 = Sunday
    private int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private Activity[] getActivitiesForDayOfWeek(int dayOfWeek) {
        for(ArrayList<Integer> key : mDaysOfWeekToActivity.keySet()) {
            if (key.contains(dayOfWeek)) {
                return mDaysOfWeekToActivity.get(key);
            }
        }
        return new Activity[]{};
    }

    private Activity[] getActivitiesForToday() {
        return getActivitiesForDayOfWeek(getDayOfWeek());
    }

    public String getWeekSummary() {
        ArrayList<Activity> allActivities = new ArrayList<Activity>();

        for (Activity activity : mDaysOfWeekToActivity.get(mMonday)) {
            allActivities.add(activity);
        }
        for (Activity activity : mDaysOfWeekToActivity.get(mWednesday)) {
            allActivities.add(activity);
        }
        for (Activity activity : mDaysOfWeekToActivity.get(mFriday)) {
            allActivities.add(activity);
        }


        Activity mondayActivity = mDaysOfWeekToActivity.get(mMonday)[0];
        SharedPreferences activity_data = getSharedPreferences(mondayActivity.getSharedPreferencesName(), Context.MODE_APPEND);
        long dateSavedMillis = activity_data.getLong("dateSaved", 0);
        Calendar c = Util.getMostRecentMondayFrom(dateSavedMillis);
        //System.out.println("Date " + c.getTime());

        SimpleDateFormat simpleDate =  new SimpleDateFormat("MM/dd/yyyy");
        String mondayDateString = simpleDate.format(c.getTime());


        String summary = mondayDateString + "\t";

        for(Activity activity : allActivities) {
            activity_data = getSharedPreferences(activity.getSharedPreferencesName(), Context.MODE_APPEND);
            int weight = activity_data.getInt("weight", 0);
            int reps = activity_data.getInt("reps", 0);

            summary += weight + "x";
            summary += reps + "\t";
        }


        return summary;
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Activity[] activities = getActivitiesForToday();

            /*if (pos == 0) {
                return mEpisodeFragment;
            }*/

            if (pos >= activities.length) pos = activities.length - 1;

            return ActivityFragment.newInstance(activities[pos]);
        }

        @Override
        public int getCount() {
            return getActivitiesForToday().length;
        }
    }
}