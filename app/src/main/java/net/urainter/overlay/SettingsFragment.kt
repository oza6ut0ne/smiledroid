package net.urainter.overlay

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val MIN_PORT_NUMBER = 0
        private const val MAX_PORT_NUMBER = 65535
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
        setPreferencesFromResource(R.xml.preferences, rootKey)

        arrayOf(
            R.string.key_mqtt_url,
            R.string.key_mqtt_topic,
            R.string.key_mqtt_username,
            R.string.key_tcp_bind_address
        ).forEach { resId ->
            findPreference<EditTextPreference?>(getString(resId))?.run {
                setOnBindEditTextListener { editText ->
                    editText.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                }
            }
        }

        findPreference<EditTextPreference?>(getString(R.string.key_mqtt_password))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
        }

        findPreference<EditTextPreference?>(getString(R.string.key_tcp_listen_port))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                editText.doOnTextChanged { text, _, _, _ ->
                    val portNumber = text.toString().toIntOrNull() ?: return@doOnTextChanged
                    if (portNumber < SettingsFragment.MIN_PORT_NUMBER) {
                        editText.setText(SettingsFragment.MIN_PORT_NUMBER.toString())
                    } else if (portNumber > SettingsFragment.MAX_PORT_NUMBER) {
                        editText.setText(SettingsFragment.MAX_PORT_NUMBER.toString())
                    }
                }
            }
        }
    }
}
