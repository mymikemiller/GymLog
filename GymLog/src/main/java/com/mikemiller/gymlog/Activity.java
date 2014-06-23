package com.mikemiller.gymlog;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;

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
    public String getSharedPreferencesName() { return "activity_data_" + mName; }
    public int getSets() { return mSets; }
    public int getRepsLow() { return mRepsLow; }
    public int getRepsHigh() { return mRepsHigh; }
    public int getWeightForSet(int set, int setOneWeight) {
        double val =  (int)(setOneWeight - (set - 1) * setOneWeight * (mWeightDecrement_percent / 100.0));
        return (int) (5*(Math.round(val/5))); // Round to the nearest 5 lbs
    }
    public int getRepIncrement() { return mRepIncrement; }

    public String getSummary(int setOneWeight, int currentReps, int lastWeight, int lastReps) {
        String summary = "";
        String reps = getRepsLow() + (getRepsLow() == getRepsHigh() ? "" : "-" + getRepsHigh());
        summary += reps + " reps ("+ lastWeight + "x"  + lastReps + ")\n\n";
        ArrayList<String> sideWeightPerSet = getSideWeightPerSet(45, setOneWeight, getSets());
        for (int i = 1; i <= getSets(); i++) {
            summary += i + ": " + getWeightForSet(i, setOneWeight) + "x" + (currentReps + (i-1) * getRepIncrement()) + " (" + sideWeightPerSet.get(i-1) + ")";
            if (i < getSets()) summary += "\n";
        }
        return summary;
    }

    static double[] sAvailableWeights = new double[]{45, 35, 25, 10, 5, 2.5};

    private ArrayList<String> getSideWeightPerSet(int barWeight, int setOneWeight, int sets) {
        ArrayList<String> weightPerSide = new ArrayList<String>();

        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);

        ArrayList<Double> lastWeights = new ArrayList<Double>();

        for(int i = sets; i >= 1; i--) {
            int weight = getWeightForSet(i, setOneWeight);
            double sideTotal = (weight - barWeight) / 2.0;
            ArrayList<Double> subWeights = getSubWeights(sideTotal, lastWeights);
            String weightPerSideForThisSet = "";
            for(Double subWeight : subWeights) {
                weightPerSideForThisSet = weightPerSideForThisSet + format.format(subWeight) + " ";
            }
            if (weightPerSideForThisSet.length() > 0) {
                weightPerSideForThisSet = weightPerSideForThisSet.substring(0, weightPerSideForThisSet.length() - 1);
            }
            weightPerSide.add(0, weightPerSideForThisSet);
            lastWeights = subWeights;
        }

        return weightPerSide;
    }

    private ArrayList<Double> getSubWeights(double total, ArrayList<Double> lastWeights) {
        ArrayList<Double> subWeights = new ArrayList<Double>();

        // We allow removing the last weight and replacing it with a larger weight. This method is meant to be called in ascending total weight order (i.e. backwards from the inverted pyramid training, so start with the final, lightest rep).
        if (lastWeights.size() > 0) {
            lastWeights.remove(lastWeights.size() - 1);
        }
        for (double weight : lastWeights) {
            total -= weight;
            subWeights.add(weight);
        }

        int i = 0;
        while (i < sAvailableWeights.length) {
            double weight = sAvailableWeights[i];
            if (weight <= total) {
                subWeights.add(weight);
                total -= weight;
            } else {
                i++;
            }
        }

        return subWeights;
    }
}
