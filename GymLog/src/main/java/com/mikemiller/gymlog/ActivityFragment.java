package com.mikemiller.gymlog;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ActivityFragment extends Fragment {

    private Activity mActivity;
    private int mWeight = 0;
    private int mReps = 0;
    private int mLastWeight = 0;
    private int mLastReps = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);

        TextView name = (TextView) view.findViewById(R.id.name);
        mActivity = (Activity) getArguments().getSerializable("activity");
        name.setText(mActivity.getName());

        Button summary = (Button) view.findViewById(R.id.summary);
        if (savedInstanceState!= null && savedInstanceState.containsKey("count")) {
            mWeight = savedInstanceState.getInt("count");
        }

        summary.setText(Integer.toString(mWeight));
        summary.setOnTouchListener(new View.OnTouchListener() {
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

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("weight", mWeight);
        outState.putInt("reps", mReps);
    }

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

    private void setWeight(int weight) {
        mWeight = weight;
        refreshButton();
    }
    private void setReps(int reps) {
        mReps = reps;
        refreshButton();
    }
    private void setLastWeight(int weight) {
        mLastWeight = weight;
        refreshButton();
    }
    private void setLastReps(int reps) {
        mLastReps = reps;
        refreshButton();
    }

    private void refreshButton() {
        Button summaryButton = (Button) getView().findViewById(R.id.summary);
        summaryButton.setText(mActivity.getSummary(mWeight, mReps, mLastWeight, mLastReps));
    }


    public void incrementWeight() {
        setWeight(mWeight + 5);
    }
    public void decrementWeight() {
        setWeight(mWeight - 5);
    }
    public void incrementReps() {
        setReps(mReps + 1);
    }
    public void decrementReps() {
        setReps(mReps - 1);
    }

    public static ActivityFragment newInstance(Activity activity) {

        ActivityFragment f = new ActivityFragment();
        Bundle b = new Bundle();
        b.putSerializable("activity", activity);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData();
    }

    private void saveData() {
        SharedPreferences.Editor outState = getActivity().getSharedPreferences(mActivity.getSharedPreferencesName(), Context.MODE_APPEND).edit();
        outState.putInt("weight", mWeight);
        outState.putInt("reps", mReps);
        outState.putLong("dateSaved", Calendar.getInstance().getTimeInMillis());
        outState.commit();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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

    }
}