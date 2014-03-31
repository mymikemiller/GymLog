package com.mikemiller.gymlog;

import java.io.Serializable;

/**
 * Created by Mike on 3/29/14. COPYRIGHT OLIO.
 */
public class Activity implements Serializable {
    private String mName;
    private int mSets;
    private int mRepsLow;
    private int mRepsHigh;
    private int mWeightDecrement_percent;
    private int mRepIncrement;

    public Activity (String name, int sets, int repsLow, int repsHigh, int weightDecrement_percent, int repIncrement) {
        mName = name;
        mSets = sets;
        mRepsLow = repsLow;
        mRepsHigh = repsHigh;
        mWeightDecrement_percent = weightDecrement_percent;
        mRepIncrement = repIncrement;
    }

    public String getName() { return mName; }
    public int getSets() { return mSets; }
    public int getRepsLow() { return mRepsLow; }
    public int getRepsHigh() { return mRepsHigh; }
    public int getWeightForSet(int set, int setOneWeight) {
        double val =  (int)(setOneWeight - (set - 1) * setOneWeight * (mWeightDecrement_percent / 100.0));
        return (int) (5*(Math.round(val/5))); // Round to the nearest 5 lbs
    }
    public int getRepIncrement() { return mRepIncrement; }

    public String getSummary(int setOneWeight, int currentReps) {
        String summary = "";
        String reps = getRepsLow() + (getRepsLow() == getRepsHigh() ? "" : " - " + getRepsHigh());
        summary += reps + " reps ("+ currentReps + ")\n\n";
        for (int i = 1; i <= getSets(); i++) {
            summary += i + ": " + getWeightForSet(i, setOneWeight);
            if (i > 1 && getRepIncrement() > 0) summary += " (+" + getRepIncrement() + " rep)";
            if (i < getSets()) summary += "\n";
        }
        return summary;
    }
}
