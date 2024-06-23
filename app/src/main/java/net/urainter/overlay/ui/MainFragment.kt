package net.urainter.overlay.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import net.urainter.overlay.R
import net.urainter.overlay.databinding.FragmentMainBinding
import net.urainter.overlay.service.OverlayService

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val activityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            // nop.
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        OverlayService.isActive.observe(viewLifecycleOwner) {
            binding.toggleButton.isChecked = it
        }

        binding.toggleButton.apply {
            setOnCheckedChangeListener { button, isChecked ->
                if (isChecked) {
                    if (Settings.canDrawOverlays(context)) {
                        OverlayService.start(context)
                    } else {
                        button.isChecked = false
                        AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.dialog_permission_required_apps))
                            .setMessage(
                                context.getString(
                                    R.string.dialog_allow_permission_display_over_other_apps,
                                    getString(R.string.app_name)
                                )
                            )
                            .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                                requestOverlayPermission()
                            }.show()
                    }
                } else {
                    OverlayService.stop(context)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestOverlayPermission() {
        if (Settings.canDrawOverlays(requireContext())) {
            return
        }
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${requireContext().packageName}")
        )
        activityLauncher.launch(intent)
    }
}
