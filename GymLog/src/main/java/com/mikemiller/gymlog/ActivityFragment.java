package com.mikemiller.gymlog;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ActivityFragment extends Fragment {

    public static String STAT_UPDATED = "STAT_UPDATED";

    private class ActivityStats {
        public ActivityStats(int weight, int reps) {
            this.weight = weight;
            this.reps = reps;
        }
        public ActivityStats(String val) {
            int xLocation = val.indexOf('x');
            this.weight = Integer.valueOf(val.substring(0, xLocation));
            this.reps = Integer.valueOf(val.substring(xLocation + 1));
        }

        @Override
        public String toString() {
            return weight + "x" + reps;
        }

        public int weight;
        public int reps;
    }

    private Activity mActivity;
    private Map<String, String> mStats = new HashMap<String, String>();
    // Key: the date in millis (as a string so it can be stored with properties.putAll)
    // Value: ActivityStats (as a string so it can be stored with properties.putAll)

    private Button mSummaryButton;

//    private int mWeight = 0;
//    private int mReps = 0;
//    private int mLastWeight = 0;
//    private int mLastReps = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        mActivity = (Activity) getArguments().getSerializable("activity");
        name.setText(mActivity.getName());

        mSummaryButton = (Button) view.findViewById(R.id.summary);

//        if (savedInstanceState!= null && savedInstanceState.containsKey("count")) {
//            mStats.put(Util.getMostRecentMonday(), savedInstanceState.getInt("count"));
//        }

        //summary.setText(Integer.toString(mWeight));
        mSummaryButton.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector doubleTapDetector = new GestureDetector(new DoubleTapDetector(getActivity()));
            GestureDetector flingAndLongPressDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
                @Override
                public boolean onDown(MotionEvent e) {
                    return false;
                }

                @Override
                public void onShowPress(MotionEvent e) {

                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return false;
                }

                @Override
                public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    decrementWeight();
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    final float minFlingVelocity = 1000;
                    if (Math.abs(velocityX) < Math.abs(velocityY)) { // make sure we're flinging more up than sideways
                        if (velocityY > minFlingVelocity) {
                            decrementReps();
                        } else if (velocityY < -minFlingVelocity) {
                            incrementReps();
                        }
                    }
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (flingAndLongPressDetector.onTouchEvent(event)) {
                    return true;
                }
                if (doubleTapDetector.onTouchEvent(event)) {
                    // A double tap occurred.
                    incrementWeight();
                    return true;
                }
                return false;
            }
        });


        /*
        count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incrementWeight();
            }
        });*/

        loadFromFile();
        refreshButton();

        return view;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

//        outState.putInt("weight", mWeight);
//        outState.putInt("reps", mReps);
    }
/*
    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState!= null && savedInstanceState.containsKey("weight")) {
            setWeight(savedInstanceState.getInt("weight"));
        }
        if (savedInstanceState!= null && savedInstanceState.containsKey("reps")) {
            setWeight(savedInstanceState.getInt("reps"));
        }
    }

*/
    private ActivityStats getActivityStats(Calendar date) {
        Calendar mostRecentMonday = Util.getMostRecentMondayFrom(date.getTimeInMillis());
        String key = String.valueOf(mostRecentMonday.getTimeInMillis());
        if (mStats.containsKey(key)) {
            return new ActivityStats(mStats.get(key));
        } else {
            return new ActivityStats(-5, -5);
        }
    }
    private ActivityStats getActivityStats() {
        Calendar c = Calendar.getInstance();
        return getActivityStats(c);
    }
    private ActivityStats getLastActivityStats() {
        Calendar c = Calendar.getInstance();
        ActivityStats lastActivityStats = new ActivityStats(-5, -5);
        while(lastActivityStats.reps < 0 || lastActivityStats.weight < 0) {
            c.add(Calendar.DAY_OF_MONTH, -7); // subtract a week
            lastActivityStats = getActivityStats(c);
        }
        return lastActivityStats;
    }
    private void setActivityStats(ActivityStats stats) {
        mStats.put(String.valueOf(Util.getMostRecentMonday().getTimeInMillis()), stats.toString());
        refreshButton();
    }
    public int getWeight() {
        if (getActivityStats().weight == -5) {
            return getLastWeight();
        }

        return getActivityStats().weight;
    }
    public int getReps() {
        if (getActivityStats().reps == -5) {
            return getLastReps();
        }

        return getActivityStats().reps;
    }
    private int getLastWeight() {
        return getLastActivityStats().weight;
    }
    private int getLastReps() {
        return getLastActivityStats().reps;
    }
    private void setWeight(int weight) {
        ActivityStats stats = getActivityStats();
        stats.weight = weight;
        setActivityStats(stats);
        saveToFile();

        // Update the weekly log file
        getActivity().sendBroadcast(new Intent(STAT_UPDATED));
    }
    private void setReps(int reps) {
        ActivityStats stats = getActivityStats();
        stats.reps = reps;
        setActivityStats(stats);
        saveToFile();

        // Update the weekly log file
        getActivity().sendBroadcast(new Intent(STAT_UPDATED));
    }

    private boolean refreshingButton = false;
    private void refreshButton() {
        if (!refreshingButton && mSummaryButton != null) {
            refreshingButton = true;
            int weight = getWeight();
            int reps = getReps();
            mSummaryButton.setText(mActivity.getSummary(weight, reps, getLastWeight(), getLastReps()));
            refreshingButton = false;
        }
    }


    public void incrementWeight() {
        setWeight(getWeight() + 5);
    }
    public void decrementWeight() {
        setWeight(getWeight() - 5);
    }
    public void incrementReps() {
        setReps(getReps() + 1);
    }
    public void decrementReps() {
        setReps(getReps() - 1);
    }

    public static ActivityFragment newInstance(Activity activity) {

        ActivityFragment f = new ActivityFragment();
        Bundle b = new Bundle();
        b.putSerializable("activity", activity);
        f.setArguments(b);

        f.mActivity = activity;
        f.loadFromFile();

        return f;
    }

    @Override
    public void onStop() {
        super.onStop();
        //saveData();
    }

    //private void saveData() {
    //    saveToFile();
//        SharedPreferences.Editor outState = getActivity().getSharedPreferences(mActivity.getSharedPreferencesName(), Context.MODE_APPEND).edit();
//        outState.putInt("weight", mWeight);
//        outState.putInt("reps", mReps);
//        outState.putLong("dateSaved", Calendar.getInstance().getTimeInMillis());
//        outState.commit();
    //}
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*
        SharedPreferences activity_data = getActivity().getSharedPreferences(mActivity.getSharedPreferencesName(), Context.MODE_APPEND);
        setWeight(activity_data.getInt("weight", 0));
        setReps(activity_data.getInt("reps", 0));

        long dateSavedMillis = activity_data.getLong("dateSaved", 0);
        if (dateSavedMillis > 0) {
            Date dateSaved = new Date(dateSavedMillis);

            long diff = Math.abs(Calendar.getInstance().getTime().getTime() - dateSaved.getTime());
            long diffDays = diff / (24 * 60 * 60 * 1000);
            if (diffDays > 2) {
                setLastWeight(mWeight);
                setLastReps(mReps);
            }
        }
*/
    }

    public File getSaveFile() {
        // External storage is not always available (i.e. when mounting via adb). Consider using a different medium for saving.
        File dir = new File(Environment.getExternalStorageDirectory() + File.separator + "GymLog"+ File.separator);
        // have the object build the directory structure, if needed.
        dir.mkdirs();
        // create a File object for the output file
        File file = new File(dir, "Activity (" + mActivity.getName() + ").txt");
        return file;
    }

    private void saveToFile() {
        Properties properties = new Properties();
//        properties.setProperty("dateSaved", String.valueOf(Calendar.getInstance().getTimeInMillis()));
//        properties.setProperty("weight", String.valueOf(getWeight()));
//        properties.setProperty("reps", String.valueOf(getReps()));
        properties.putAll(mStats);

        if (getWeight() == 0 || getReps() == 0) {
            Log.d("writing", "0");
            mSummaryButton.setTextColor(Color.RED);
        } else if (getWeight() == -5 || getReps() == -5) {
            Log.d("writing", "-5");
            mSummaryButton.setTextColor(Color.MAGENTA);
        } else {
            mSummaryButton.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
        }

        try {
            FileOutputStream os = new FileOutputStream(getSaveFile());
            properties.store(os, null);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFromFile() {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(getSaveFile()));
            this.mStats = new HashMap(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}