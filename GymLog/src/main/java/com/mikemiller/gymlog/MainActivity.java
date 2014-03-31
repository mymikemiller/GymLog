package com.mikemiller.gymlog;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    SimpleCounterFragment mEpisodeFragment;

    // 1 = Sunday
    final Map<Integer[], Activity[]> mDaysOfWeekToActivity =  new HashMap<Integer[], Activity[]>(){{
        put(new Integer[]{2, 3}, new Activity[]{
                new Activity("Barbell Deadlifts", 3, 4, 6, 10, 1),
                new Activity("Assisted Chin-ups", 3, 4, 6, 10, 1),
                new Activity("Pendlay Rows", 2, 4, 6, 10, 1) });
        put(new Integer[]{4, 5}, new Activity[]{
                new Activity("Flat Dumbell Bench Press", 3, 6, 10, 10, 1),
                new Activity("Incline Dumbell Bench Press", 2, 8, 12, 10, 1),
                new Activity("Barbell Curls", 3, 0, 8, 0, 0)});
        put(new Integer[]{6, 7, 1}, new Activity[]{
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
    }

    // 1 = Sunday
    private int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private Activity[] getActivitiesForDayOfWeek(int dayOfWeek) {
        for(Integer[] key : mDaysOfWeekToActivity.keySet()) {
            if (Arrays.asList(key).contains(dayOfWeek)) {
                return mDaysOfWeekToActivity.get(key);
            }
        }
        return new Activity[]{};
    }

    private Activity[] getActivitiesForTody() {
        return getActivitiesForDayOfWeek(getDayOfWeek());
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            Activity[] activities = getActivitiesForTody();

            if (pos == 0) {
                return mEpisodeFragment;
            }

            if (pos > activities.length) pos = activities.length;

            return ActivityFragment.newInstance(activities[pos - 1]);
        }

        @Override
        public int getCount() {
            return getActivitiesForTody().length + 1;
        }
    }
}