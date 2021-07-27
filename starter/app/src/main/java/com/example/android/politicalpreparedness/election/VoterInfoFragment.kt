package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.extension.startForUrl
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class VoterInfoFragment : Fragment() {

    private lateinit var binding: FragmentVoterInfoBinding

    private val args: VoterInfoFragmentArgs by navArgs()

    private val viewModel: VoterInfoViewModel by viewModel {
        parametersOf(args.argElectionId, args.argDivision)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVoterInfoBinding.inflate(inflater)
        binding.voterInfo = viewModel
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
    }

    private fun setObservers() = with(viewModel) {
        navigateToUrl.observe(viewLifecycleOwner) { url -> url?.let { startForUrl(it) } }
        election?.observe(viewLifecycleOwner) { election ->
            election?.let { binding.buttonFollow.setText(getFollowButtonText(it)) }
        }
    }

}