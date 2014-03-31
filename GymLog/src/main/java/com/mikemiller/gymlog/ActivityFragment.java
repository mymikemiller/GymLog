package com.mikemiller.gymlog;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class ActivityFragment extends Fragment {

    private Activity mActivity;
    private int mWeight = 0;
    private int mReps = 0;

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
            GestureDetector flingDetector = new GestureDetector(getActivity(), new GestureDetector.OnGestureListener() {
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
                    final float minFlingVelocity = 300;
                    if (velocityY > minFlingVelocity && Math.abs(velocityX) < minFlingVelocity) {
                        decrementReps();
                    } else if (velocityY < -minFlingVelocity && Math.abs(velocityX) < minFlingVelocity ) {
                        incrementReps();
                    }
                    return true;
                }
            });

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (flingDetector.onTouchEvent(event)) {
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
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState!= null && savedInstanceState.containsKey("weight")) {
            setWeight(savedInstanceState.getInt("weight"));
        }
    }

    private void setWeight(int weight) {
        mWeight = weight;
        Button summaryButton = (Button) getView().findViewById(R.id.summary);
        summaryButton.setText(getSummary());
    }
    private void setReps(int reps) {
        mReps = reps;
        Button summaryButton = (Button) getView().findViewById(R.id.summary);
        summaryButton.setText(getSummary());
    }

    private String getSummary() {
        return mActivity.getSummary(mWeight, mReps);
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
        SharedPreferences.Editor outState = getActivity().getSharedPreferences(getSharedpreferencesName(), Context.MODE_APPEND).edit();
        outState.putInt("weight", mWeight);
        outState.putInt("reps", mReps);
        outState.commit();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences activity_data = getActivity().getSharedPreferences(getSharedpreferencesName(), Context.MODE_APPEND);
        setWeight(activity_data.getInt("weight", 0));
        setReps(activity_data.getInt("reps", 0));
    }

    private String getSharedpreferencesName() {
        return "activity_data_" + mActivity.getName();
    }
}