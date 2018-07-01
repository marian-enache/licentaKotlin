package com.marian.licenta.wallpaper

import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.PreferenceFragment
import com.marian.licenta.R
import org.jetbrains.anko.toast


/**
 * Created by Marian on 27.06.2018.
 */
class WallpaperPreferencesActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager.beginTransaction().replace(android.R.id.content, PrefsFragment()).commit()
    }

    class PrefsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.prefs)

            var circlePreference: Preference = preferenceScreen.findPreference("numberOfCircles")
            circlePreference.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { p0, newValue ->
                // check that the string is an integer
                if (newValue != null && newValue.toString().isNotEmpty() && newValue.toString().matches(Regex.fromLiteral("\\d*"))) {
                     true
                }
                // If now create a message to the user
                WallpaperPreferencesActivity@this.toast("Invalid Input")
                false
            }
        }

    }
}