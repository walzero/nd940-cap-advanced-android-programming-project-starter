package com.example.android.politicalpreparedness.launch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentLaunchBinding
import com.example.android.politicalpreparedness.extension.navigateTo

class LaunchFragment : Fragment() {

    private lateinit var binding: FragmentLaunchBinding

    private val toRepresentatives by lazy { LaunchFragmentDirections.fromLaunchToRepresentatives() }
    private val toElections by lazy { LaunchFragmentDirections.fromLaunchToElections() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLaunchBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.setListeners()
        return binding.root
    }

    private fun FragmentLaunchBinding.setListeners() {
        btGoToRepresentatives.setOnClickListener { navToRepresentatives() }
        btGoToElections.setOnClickListener { navigateToElections() }
    }

    private fun navigateToElections() = navigateTo(toElections)
    private fun navToRepresentatives() = navigateTo(toRepresentatives)
}
