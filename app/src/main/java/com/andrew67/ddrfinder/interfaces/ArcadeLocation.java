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
package com.andrew67.ddrfinder.interfaces;

import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

/**
 * Standard interface for API arcade locations.
 * When new fields are added in API versions, old models should provide new functions to compensate.
 * For example, the model.v1 ArcadeLocation class was extended to return static "ziv" source information.
 * See: https://github.com/Andrew67/ddr-finder/wiki/API-Description
 */
public interface ArcadeLocation extends Parcelable {
    int getId();
    String getSrc();
    String getSid();
    String getName();
    String getCity();
    LatLng getLocation();
    boolean hasDDR();
    boolean isClosed();
}