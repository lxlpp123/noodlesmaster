/*
 ******************************************************************************
 * Parts of this code sample are licensed under Apache License, Version 2.0   *
 * Copyright (c) 2009, Android Open Handset Alliance. All rights reserved.    *
 *                                                                            *                                                                         *
 * Except as noted, this code sample is offered under a modified BSD license. *
 * Copyright (C) 2010, Motorola Mobility, Inc. All rights reserved.           *
 *                                                                            *
 * For more details, see MOTODEV_Studio_for_Android_LicenseNotices.pdf        * 
 * in your installation folder.                                               *
 ******************************************************************************
 */

package me.evis.mobile.noodle;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;

/***
 * PreferenceActivity is a built-in Activity for preferences management
 * 
 * To retrieve the values stored by this activity in other activities use the
 * following snippet:
 * 
 * SharedPreferences sharedPreferences =
 * PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
 * <Preference Type> preferenceValue = sharedPreferences.get<Preference
 * Type>("<Preference Key>",<default value>);
 */
public class Preference extends PreferenceActivity {
    private static final String TAG = "Preference";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference);
        
        PreferenceScreen psAbout = (PreferenceScreen) getPreferenceScreen().findPreference("about");
        Intent aboutIntent = new Intent(this, About.class);
        psAbout.setIntent(aboutIntent);
        try {
            String appVer = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
            psAbout.setSummary(getString(R.string.about_version_label) + appVer);
        }
        catch (NameNotFoundException e) {
            Log.v(TAG, e.getMessage());
        }
    }
}