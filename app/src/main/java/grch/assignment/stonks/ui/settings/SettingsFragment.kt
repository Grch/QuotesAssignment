package grch.assignment.stonks.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import grch.assignment.stonks.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

}