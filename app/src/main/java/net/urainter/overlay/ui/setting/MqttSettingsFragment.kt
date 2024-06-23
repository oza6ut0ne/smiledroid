package net.urainter.overlay.ui.setting

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import net.urainter.overlay.R

class MqttSettingsFragment : PreferenceFragmentCompat() {

    override fun onNavigateToScreen(preferenceScreen: PreferenceScreen) {
        super.onNavigateToScreen(preferenceScreen)
        when (preferenceScreen.key) {
            getString(R.string.key_basic_settings) -> findNavController().navigate(R.id.action_SettingsFragment_to_BasicSettingsFragment)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_settings).setVisible(false)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_mqtt, rootKey)

        arrayOf(
            R.string.key_mqtt_url,
            R.string.key_mqtt_topic,
            R.string.key_mqtt_username,
        ).forEach { resId ->
            findPreference<EditTextPreference?>(getString(resId))?.run {
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                }
            }
        }

        findPreference<EditTextPreference?>(getString(R.string.key_mqtt_password))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }
    }
}
