package grch.assignment.quotes.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import grch.assignment.quotes.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)
    }

}