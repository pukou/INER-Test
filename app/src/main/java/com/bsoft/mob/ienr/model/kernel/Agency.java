package com.bsoft.mob.ienr.model.kernel;

import android.os.Parcel;
import android.os.Parcelable;

public class Agency implements Parcelable {

	public String JGID;

	public String JGMC;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(JGID);
		dest.writeString(JGMC);

	}

	public static final Parcelable.Creator<Agency> CREATOR = new Creator<Agency>() {
		@Override
		public Agency createFromParcel(Parcel source) {

			Agency item = new Agency();
			item.JGID = source.readString();
			item.JGMC = source.readString();
			return item;
		}

		@Override
		public Agency[] newArray(int size) {
			return new Agency[size];
		}
	};

}
