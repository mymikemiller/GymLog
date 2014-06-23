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

public class SimpleCounterFragment extends Fragment {

    private String mShow = "";
    private int mCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple_counter, container, false);

        TextView show = (TextView) view.findViewById(R.id.show);
        mShow = getArguments().getString("show");
        show.setText(mShow);

        Button count = (Button) view.findViewById(R.id.count);
        if (savedInstanceState!= null && savedInstanceState.containsKey("count")) {
            mCount = savedInstanceState.getInt("count");
        }

        count.setText(Integer.toString(mCount));

        count.setOnTouchListener(new View.OnTouchListener() {
            GestureDetector doubleTapDetector = new GestureDetector(new DoubleTapDetector(getActivity()));

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (doubleTapDetector.onTouchEvent(event)) {
                    // A double tap occurred.
                    increment();
                    return true;
                }
                return false;
            }
        });

        count.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                decrement();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("count", mCount);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState!= null && savedInstanceState.containsKey("count")) {
            setCount(savedInstanceState.getInt("count"));
        }
    }

    private void setCount(int count) {
        mCount = count;
        Button countButton = (Button) getView().findViewById(R.id.count);
        countButton.setText(Integer.toString(mCount));

    }

    public void increment() {
        setCount(mCount + 1);
    }
    public void decrement() {
        setCount(mCount - 1);
    }

    public static SimpleCounterFragment newInstance(String show) {

        SimpleCounterFragment f = new SimpleCounterFragment();
        Bundle b = new Bundle();
        b.putString("show", show);
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
        outState.putInt("count", mCount);
        outState.commit();
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences counter_data = getActivity().getSharedPreferences(getSharedpreferencesName(), Context.MODE_APPEND);
        setCount(counter_data.getInt("count", 0));
    }

    private String getSharedpreferencesName() {
        return "counter_data_" + mShow;
    }
}