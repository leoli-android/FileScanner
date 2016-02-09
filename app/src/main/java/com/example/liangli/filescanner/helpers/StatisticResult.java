package com.example.liangli.filescanner.helpers;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liangli on 2/9/16.
 *
 * Format the output.
 * Implement parcelable to make them available to be delivered by bundles
 */
public class StatisticResult implements Parcelable{

    private String colume_1, colume_2;

    public StatisticResult(String str1, String str2) {
        colume_1 = str1;
        colume_2 = str2;
    }

    public String getColume_1() {
        return colume_1;
    }

    public String getColume_2() {
        return colume_2;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(colume_1);
        dest.writeString(colume_2);
    }

    public static final Parcelable.Creator<StatisticResult> CREATOR
            = new Parcelable.Creator<StatisticResult>() {
        public StatisticResult createFromParcel(Parcel in) {
            return new StatisticResult(in);
        }

        public StatisticResult[] newArray(int size) {
            return new StatisticResult[size];
        }
    };

    public StatisticResult (Parcel in) {
        colume_1 = in.readString();
        colume_2 = in.readString();
    }
}
