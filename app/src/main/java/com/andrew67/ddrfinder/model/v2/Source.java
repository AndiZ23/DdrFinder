/*
 * Copyright (c) 2015 Andrés Cordero
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

package com.andrew67.ddrfinder.model.v2;

import android.os.Parcel;
import android.os.Parcelable;

import com.andrew67.ddrfinder.interfaces.DataSource;

/**
 * Represents the API v2 source.
 * See: https://github.com/Andrew67/ddr-finder/wiki/API-Description
 * Note: the infoURL field is ignored, mInfoURL is used in its place.
 */
public class Source implements DataSource {
    private String shortName;
    private String name;
    private String mInfoURL;
    private boolean hasDDR;

    @Override
    public String getShortName() {
        return shortName;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getInfoURL() {
        return mInfoURL;
    }

    @Override
    public boolean hasDDR() {
        return hasDDR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(mInfoURL);
        dest.writeBooleanArray(new boolean[] { hasDDR });
    }

    private Source(Parcel in) {
        this.name = in.readString();
        this.mInfoURL = in.readString();
        boolean[] hasDDRArray = new boolean[1];
        in.readBooleanArray(hasDDRArray);
        this.hasDDR = hasDDRArray[0];
    }

    public static final Parcelable.Creator<Source> CREATOR
            = new Parcelable.Creator<Source>() {
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        public Source[] newArray(int size) {
            return new Source[size];
        }
    };
}
