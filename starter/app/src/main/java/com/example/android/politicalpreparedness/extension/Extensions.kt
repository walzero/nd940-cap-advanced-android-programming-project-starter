package com.example.android.politicalpreparedness.extension

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

fun Fragment.navigateTo(navDirections: NavDirections) {
    findNavController().navigate(navDirections)
}