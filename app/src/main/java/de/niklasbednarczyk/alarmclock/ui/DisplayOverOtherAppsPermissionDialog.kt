package de.niklasbednarczyk.alarmclock.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.DialogFragment
import de.niklasbednarczyk.alarmclock.R

class DisplayOverOtherAppsPermissionDialog : DialogFragment() {

    companion object {
        const val DISPLAY_OVER_OTHER_APPS_INTENT_REQUEST_CODE = 1337
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let { fragmentActivity ->
            val builder = AlertDialog.Builder(fragmentActivity)
            builder
                .setTitle(R.string.display_over_other_apps_permission_dialog_title)
                .setMessage(R.string.display_over_other_apps_permission_dialog_message)
                .setPositiveButton(R.string.display_over_other_apps_permission_dialog_positive_button) { _, _ ->
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${fragmentActivity.packageName}")
                    )
                    activity?.startActivityForResult(
                        intent,
                        DISPLAY_OVER_OTHER_APPS_INTENT_REQUEST_CODE
                    )
                }
            isCancelable = false
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}