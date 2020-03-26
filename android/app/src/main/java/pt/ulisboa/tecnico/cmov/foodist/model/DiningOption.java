package pt.ulisboa.tecnico.cmov.foodist.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DiningOption implements Parcelable {

    private String name;
    private String openingHours;

    public static final Creator<DiningOption> CREATOR = new Creator<DiningOption>() {
        @Override
        public DiningOption createFromParcel(Parcel in) {
            return new DiningOption(in);
        }

        @Override
        public DiningOption[] newArray(int size) {
            return new DiningOption[size];
        }
    };

    private DiningOption(Parcel in) {
        name = in.readString();
        openingHours = in.readString();
    }

    public DiningOption(String name, String openingHours) {
        this.name = name;
        this.openingHours = openingHours;
    }

    public String getName() {
        return name;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(openingHours);
    }
}
