package grch.assignment.stonks.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import grch.assignment.stonks.R
import grch.assignment.stonks.ui.main.MainFragment

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment())
                .commitNow()
        }
    }
}