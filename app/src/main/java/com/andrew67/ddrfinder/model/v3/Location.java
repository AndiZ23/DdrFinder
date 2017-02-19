/*
 * Copyright (c) 2015-2016 Andrés Cordero
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

package com.andrew67.ddrfinder.model.v3;

import android.os.Parcel;
import android.os.Parcelable;

import com.andrew67.ddrfinder.interfaces.ArcadeLocation;
import com.google.android.gms.maps.model.LatLng;

/**
 * Represents the API v2 location.
 * See: https://github.com/Andrew67/ddr-finder/wiki/API-Description
 */
public class Location implements ArcadeLocation {
    private int id;
    private String src;
    private String sid;
    private String name;
    private String city;
    private double lat;
    private double lng;
    private Integer hasDDR; // optional; interface should be Boolean, but this gets fed to us as "0" or "1" (or null!)

    private Location() { }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getSrc() {
        return src;
    }

    @Override
    public String getSid() {
        return sid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getCity() {
        return city;
    }

    private LatLng position = null;
    @Override
    public LatLng getPosition() {
        return (position == null) ? position = new LatLng(lat, lng) : position;
    }

    @Override
    public boolean hasDDR() {
        return Integer.valueOf(1).equals(hasDDR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(src);
        dest.writeString(sid);
        dest.writeString(name);
        dest.writeString(city);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        // Accounts for hasDDR = null
        if (hasDDR()) dest.writeInt(1);
        else dest.writeInt(0);
    }

    private Location(Parcel in) {
        this.id = in.readInt();
        this.src = in.readString();
        this.sid = in.readString();
        this.name = in.readString();
        this.city = in.readString();
        this.lat = in.readDouble();
        this.lng = in.readDouble();
        this.hasDDR = in.readInt();
    }

    public static final Parcelable.Creator<Location> CREATOR
            = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
}
