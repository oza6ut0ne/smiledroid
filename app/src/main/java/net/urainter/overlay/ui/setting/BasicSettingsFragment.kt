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
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import net.urainter.overlay.R

class BasicSettingsFragment : PreferenceFragmentCompat() {
    companion object {
        private const val MIN_DURATION = 1
        private const val MAX_DURATION = Int.MAX_VALUE
        private const val MIN_MAX_COMMENT_ON_DISPLAY = 0
        private const val MAX_MAX_COMMENT_ON_DISPLAY = Int.MAX_VALUE
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
        setPreferencesFromResource(R.xml.preferences_basic, rootKey)

        arrayOf(
            R.string.key_basic_text_color_style,
            R.string.key_basic_text_stroke_style,
        ).forEach { resId ->
            findPreference<EditTextPreference?>(getString(resId))?.run {
                setOnBindEditTextListener { editText ->
                    editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
                }
            }
        }

        findPreference<EditTextPreference?>(getString(R.string.key_basic_duration))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                editText.doOnTextChanged { text, _, _, _ ->
                    val duration = text.toString().toLongOrNull() ?: return@doOnTextChanged
                    if (duration < MIN_DURATION) {
                        editText.setText("$MIN_DURATION")
                    } else if (duration > MAX_DURATION) {
                        editText.setText("$MAX_DURATION")
                    }
                }
            }
        }

        findPreference<EditTextPreference?>(getString(R.string.key_basic_max_comments_on_display))?.run {
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
                editText.doOnTextChanged { text, _, _, _ ->
                    val duration = text.toString().toLongOrNull() ?: return@doOnTextChanged
                    if (duration < MIN_MAX_COMMENT_ON_DISPLAY) {
                        editText.setText("$MIN_MAX_COMMENT_ON_DISPLAY")
                    } else if (duration > MAX_MAX_COMMENT_ON_DISPLAY) {
                        editText.setText("$MAX_MAX_COMMENT_ON_DISPLAY")
                    }
                }
            }
        }
    }
}
