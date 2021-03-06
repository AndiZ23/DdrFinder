/*
 * Copyright (c) 2018 Andrés Cordero
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
package com.andrew67.ddrfinder.arcades.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andrew67.ddrfinder.R;
import com.andrew67.ddrfinder.activity.SettingsActivity;
import com.andrew67.ddrfinder.arcades.util.LocationActions;
import com.andrew67.ddrfinder.arcades.vm.SelectedLocationModel;
import com.andrew67.ddrfinder.mylocation.MyLocationModel;
import com.google.android.gms.maps.model.LatLng;

import java.text.NumberFormat;

public class LocationActionsFragment extends Fragment {

    private SelectedLocationModel selectedLocationModel;
    private MyLocationModel myLocationModel;

    private LocationActions locationActions;

    private NumberFormat distanceFormat; // 2 decimal digits
    private NumberFormat latLngFormat; // 5 decimal digits (good for 1m precision)

    private TextView arcadeName;
    private TextView arcadeCity;
    private TextView arcadeDistance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.location_actions, container);

        arcadeName = view.findViewById(R.id.location_name);
        arcadeCity = view.findViewById(R.id.location_city);
        arcadeDistance = view.findViewById(R.id.location_distance);

        final View navigate = view.findViewById(R.id.action_navigate);
        navigate.setOnClickListener(this::onNavigateClicked);

        final View moreInfo = view.findViewById(R.id.action_moreinfo);
        moreInfo.setOnClickListener(this::onMoreInfoClicked);

        final View copyGps = view.findViewById(R.id.action_copygps);
        copyGps.setOnClickListener(this::onCopyClicked);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        selectedLocationModel = ViewModelProviders.of(requireActivity())
                .get(SelectedLocationModel.class);
        selectedLocationModel.getSelectedLocation()
                .observe(this, onSelectedLocationUpdated);

        myLocationModel = ViewModelProviders.of(requireActivity()).get(MyLocationModel.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // https://medium.com/@BladeCoder/architecture-components-pitfalls-part-1-9300dd969808
        selectedLocationModel.getSelectedLocation().removeObserver(onSelectedLocationUpdated);
    }

    /**
     * Updates the UI with name/city and requests a new location to update the distance if empty
     */
    private final Observer<SelectedLocationModel.CompositeArcade> onSelectedLocationUpdated =
            selectedLocation -> {
                if (selectedLocation == null) {
                    arcadeName.setText("");
                    arcadeCity.setText("");
                    arcadeDistance.setText("");
                } else {
                    arcadeName.setText(selectedLocation.arcadeLocation.getName());

                    // If city information is not available, show coordinates instead.
                    if (selectedLocation.arcadeLocation.getCity().isEmpty()) {
                        final LatLng coords = selectedLocation.arcadeLocation.getPosition();
                        arcadeCity.setText(getString(R.string.coordinates,
                                formatLatLng(coords.latitude), formatLatLng(coords.longitude)));
                    } else {
                        arcadeCity.setText(selectedLocation.arcadeLocation.getCity());
                    }

                    // If distance is unset, leave blank and request to update user's location.
                    // If user has location disabled, it stays blank silently.
                    // If enabled, a new location is emitted which includes distance.
                    if (selectedLocation.distanceKm == null) {
                        arcadeDistance.setText("");
                        myLocationModel.requestMyLocationSilently(requireActivity(),
                                selectedLocationModel::updateUserLocation);
                    } else {
                        arcadeDistance.setText(getString(R.string.distance_km,
                                formatDistance(selectedLocation.distanceKm)));
                    }

                    // Set up LocationActions object that enables the available actions
                    locationActions = new LocationActions(selectedLocation.arcadeLocation,
                            selectedLocation.dataSource);
                }
            };

    private String formatDistance(float distance) {
        if (distanceFormat == null) {
            distanceFormat = NumberFormat.getNumberInstance();
            distanceFormat.setMaximumFractionDigits(2);
        }
        return distanceFormat.format(distance);
    }

    private String formatLatLng(double coord) {
        if (latLngFormat == null) {
            latLngFormat = NumberFormat.getNumberInstance();
            latLngFormat.setMaximumFractionDigits(5);
        }
        return latLngFormat.format(coord);
    }

    private void onNavigateClicked(@SuppressWarnings("unused") View v) {
        if (locationActions == null) return;
        locationActions.navigate(requireActivity());
    }

    private void onMoreInfoClicked(@SuppressWarnings("unused") View v) {
        if (locationActions == null) return;
        final boolean useCustomTabs = PreferenceManager
                .getDefaultSharedPreferences(requireActivity())
                .getBoolean(SettingsActivity.KEY_PREF_CUSTOMTABS, true);
        locationActions.moreInfo(requireActivity(), useCustomTabs);
    }

    private Toast copiedToast = null;
    private void onCopyClicked(@SuppressWarnings("unused") View v) {
        if (locationActions == null) return;
        final boolean copySuccess = locationActions.copyGps(requireActivity());
        if (copySuccess) {
            if (copiedToast == null) copiedToast = Toast.makeText(requireActivity(),
                    R.string.copy_complete, Toast.LENGTH_SHORT);
            copiedToast.show();
        }
    }
}
