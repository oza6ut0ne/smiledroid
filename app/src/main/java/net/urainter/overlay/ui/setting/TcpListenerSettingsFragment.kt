package net.urainter.overlay.ui.setting

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import net.urainter.overlay.R

class TcpListenerSettingsFragment : PreferenceFragmentCompat() {

    companion object {
        private const val MIN_PORT_NUMBER = 0
        private const val MAX_PORT_NUMBER = 65535
    }

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
        setPreferencesFromResource(R.xml.preferences_tcp, rootKey)

        findPreference<EditTextPreference?>(getString(R.string.key_tcp_bind_address))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
            }
        }

        findPreference<EditTextPreference?>(getString(R.string.key_tcp_listen_port))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType =
                    InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                editText.doOnTextChanged { text, _, _, _ ->
                    val portNumber = text.toString().toIntOrNull() ?: return@doOnTextChanged
                    if (portNumber < MIN_PORT_NUMBER) {
                        editText.setText(MIN_PORT_NUMBER.toString())
                    } else if (portNumber > MAX_PORT_NUMBER) {
                        editText.setText(MAX_PORT_NUMBER.toString())
                    }
                }
            }
        }
    }
}
