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

package com.andrew67.ddrfinder.adapters;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.andrew67.ddrfinder.BuildConfig;
import com.andrew67.ddrfinder.activity.SettingsActivity;
import com.andrew67.ddrfinder.interfaces.ApiResult;
import com.andrew67.ddrfinder.interfaces.ArcadeLocation;
import com.andrew67.ddrfinder.interfaces.DataSource;
import com.andrew67.ddrfinder.interfaces.MessageDisplay;
import com.andrew67.ddrfinder.interfaces.ProgressBarController;
import com.andrew67.ddrfinder.model.v3.Result;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.util.List;
import java.util.Map;

public class MapLoaderV3 extends MapLoader {

	public MapLoaderV3(GoogleMap map, Map<Marker, ArcadeLocation> markers,
					   ProgressBarController pbc, MessageDisplay display,
					   List<LatLngBounds> areas, Map<String, DataSource> sources,
					   SharedPreferences sharedPref, String apiUrl) {
		super(map, markers, pbc, display, areas, sources, sharedPref, apiUrl);
	}

	@Override
	protected ApiResult doInBackground(LatLngBounds... boxes) {
		ApiResult result = null;
		try {
			if (boxes.length == 0) throw new IllegalArgumentException("No boxes passed to doInBackground");
			final LatLngBounds box = boxes[0];

            String datasrc = sharedPref.getString(SettingsActivity.KEY_PREF_API_SRC, "");
            if (SettingsActivity.API_SRC_CUSTOM.equals(datasrc)) {
                datasrc = sharedPref.getString(SettingsActivity.KEY_PREF_API_SRC_CUSTOM, "");
            }

			final OkHttpClient client = new OkHttpClient();
			final HttpUrl requestURL = HttpUrl.parse(apiUrl).newBuilder()
					.addQueryParameter("version", "" + SettingsActivity.API_V30)
                    .addQueryParameter("datasrc", datasrc)
					.addQueryParameter("latupper", "" + box.northeast.latitude)
					.addQueryParameter("lngupper", "" + box.northeast.longitude)
					.addQueryParameter("latlower", "" + box.southwest.latitude)
					.addQueryParameter("lnglower", "" + box.southwest.longitude)
					.build();

			Log.d("api", "Request URL: " + requestURL);
			final Request get = new Request.Builder()
					.header("User-Agent", BuildConfig.APPLICATION_ID + " " + BuildConfig.VERSION_NAME
                            + "/Android?SDK=" + Build.VERSION.SDK_INT)
					.url(requestURL)
					.build();

			final Response response = client.newCall(get).execute();
			final int statusCode = response.code();
			Log.d("api", "Status code: " + statusCode);

			// Data/error loaded OK
			if (statusCode == 200 || statusCode == 400) {
                final Gson gson = new Gson();
                result = gson.fromJson(response.body().charStream(), Result.class);
                result.setBounds(box);
                Log.d("api", "Raw API result: " + gson.toJson(result, Result.class));
			}
			// Unexpected error code
			else {
				throw new RuntimeException("Unexpected HTTP status code: " + statusCode);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return result;
	}

}