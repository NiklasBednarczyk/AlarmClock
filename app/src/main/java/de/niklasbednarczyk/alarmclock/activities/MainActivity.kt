package de.niklasbednarczyk.alarmclock.activities

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import dagger.hilt.android.AndroidEntryPoint
import de.niklasbednarczyk.alarmclock.R

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        val preferenceKeyTheme =
            applicationContext.resources.getString(R.string.preference_key_theme)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPreferencesListener =
            SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
                when (key) {
                    preferenceKeyTheme -> setTheme(sharedPreferences)
                }
            }
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
        setTheme(sharedPreferences)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.main_nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp()
    }

    override fun onPause() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesListener)
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesListener)
    }

    private fun setTheme(sharedPreferences: SharedPreferences) {
        val preferenceKeyTheme =
            applicationContext.resources.getString(R.string.preference_key_theme)
        val preferenceDefaultValueTheme =
            applicationContext.resources.getString(R.string.preference_default_value_theme)
        val theme = sharedPreferences.getString(preferenceKeyTheme, preferenceDefaultValueTheme)
        theme?.let {
            val useSystemSettingsValue =
                applicationContext.resources.getString(R.string.preference_value_theme_use_system_setting)
            val lightThemeValue =
                applicationContext.resources.getString(R.string.preference_value_theme_light_theme)
            val darkThemeValue =
                applicationContext.resources.getString(R.string.preference_value_theme_dark_theme)

            when (theme) {
                useSystemSettingsValue -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
                lightThemeValue -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
                darkThemeValue -> AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
        }
    }
}