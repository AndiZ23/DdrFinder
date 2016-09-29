/*
 * Copyright (c) 2013-2016 Andrés Cordero
 * Web: https://github.com/Andrew67/DdrFinder
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.andrew67.ddrfinder.model.v1;

import android.os.Parcel;
import android.os.Parcelable;

import com.andrew67.ddrfinder.interfaces.ArcadeLocation;
import com.google.android.gms.maps.model.LatLng;

/**
 * An immutable class that represents an arcade location from the database.
 */
public class ArcadeLocationV1 implements ArcadeLocation {
	private final int id;
	private final String name;
	private final String city;
	private final LatLng location;
	private final boolean hasDDR;
	private final boolean closed;

	public static final ArcadeLocationV1 EMPTY_LOCATION =
			new ArcadeLocationV1(0, "", "", new LatLng(0,0), false, false);
	
	public ArcadeLocationV1(int id, String name, String city, LatLng location,
							boolean hasDDR, boolean closed) {
		this.id = id;
		this.name = name;
		this.city = city;
		this.location = location;
		this.hasDDR = hasDDR;
		this.closed = closed;
	}

	public int getId() {
		return id;
	}

	public String getSrc() {
		return "ziv";
	}

	public String getSid() {
		return Integer.toString(id);
	}

	public String getName() {
		return name;
	}

	public String getCity() {
		return city;
	}

	public LatLng getPosition() {
		return location;
	}
	
	public boolean hasDDR() {
		return hasDDR;
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof ArcadeLocationV1 && this.id == ((ArcadeLocationV1) o).id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(name);
		out.writeString(city);

		// Workaround for getting BadParcelableException on LatLng objects
		//out.writeParcelable(location, flags);
		out.writeDouble(location.latitude);
		out.writeDouble(location.longitude);

		final boolean[] bools = new boolean[2];
		bools[0] = hasDDR;
		bools[1] = closed;
		out.writeBooleanArray(bools);
	}
	
	private ArcadeLocationV1(Parcel in) {
		id = in.readInt();
		name = in.readString();
		city = in.readString();

		// Workaround for getting BadParcelableException on LatLng objects
		//location = in.readParcelable(null);
		final double latitude = in.readDouble();
		final double longitude = in.readDouble();
		location = new LatLng(latitude, longitude);

		final boolean[] bools = new boolean[2];
		in.readBooleanArray(bools);
		hasDDR = bools[0];
		closed = bools[1];
	}
	
	public static final Parcelable.Creator<ArcadeLocationV1> CREATOR
		= new Parcelable.Creator<ArcadeLocationV1>() {
		public ArcadeLocationV1 createFromParcel(Parcel in) {
			return new ArcadeLocationV1(in);
		}
		
		public ArcadeLocationV1[] newArray(int size) {
			return new ArcadeLocationV1[size];
		}
	};

}
