package com.example.android.politicalpreparedness.extension

import android.content.Context
import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.SettingsClient
import com.google.android.material.snackbar.Snackbar

fun Fragment.navigateTo(navDirections: NavDirections) {
    findNavController().navigate(navDirections)
}

fun Fragment.showShortToast(@StringRes text: Int) = requireContext().showShortToast(text)

fun Context.showShortToast(@StringRes text: Int) =
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()

fun View.showConfirmationSnackbar(
    text: String,
    actionText: String,
    length: Int = Snackbar.LENGTH_INDEFINITE,
    action: ((Snackbar) -> Unit)? = null
): Snackbar = Snackbar.make(this, text, length).apply {
    when (action) {
        null -> setAction(actionText, null)
        else -> setAction(actionText) { action(this) }
    }
}.also { it.show() }

fun Fragment.isAllowed(permission: String): Boolean =
    requireContext().isAllowed(permission)

fun Fragment.areAllowed(permissions: Array<String>): Boolean =
    requireContext().areAllowed(permissions)

fun Context.isAllowed(permission: String): Boolean =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Context.areAllowed(permissions: Array<String>): Boolean {
    permissions.forEach { permission ->
        if (!isAllowed(permission))
            return false
    }

    return true
}

fun SettingsClient.verifyLocationSettings(
    onSuccessListener: (LocationSettingsResponse?) -> Unit,
    onFailureListener: (Exception) -> Unit
) {
    val requestBuilder = LocationSettingsRequest.Builder().apply {
        addLocationRequest(LocationRequest.create())
    }

    checkLocationSettings(requestBuilder.build()).run {
        addOnSuccessListener { response -> onSuccessListener(response) }
        addOnFailureListener { exception -> onFailureListener(exception) }
    }
}

fun String?.orEmpty() = this ?: ""