package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.extension.navigateTo
import com.example.android.politicalpreparedness.network.models.Election
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : Fragment() {

    private lateinit var binding: FragmentElectionBinding

    private val electionsAdapter by lazy { ElectionListAdapter { goToVoterInfo(it) } }
    private val followedElectionsAdapter by lazy { ElectionListAdapter { goToVoterInfo(it) } }

    private val viewModel: ElectionsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentElectionBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.electionsAdapter = electionsAdapter
        binding.followedElectionsAdapter = followedElectionsAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addObservers()
    }

    private fun addObservers() = with(viewModel) {
        upcomingElections.observe(viewLifecycleOwner) { elections ->
            elections?.let {
                (binding.upcomingElectionsList.adapter as ElectionListAdapter).submitList(it)
            }
        }
        followedElections.observe(viewLifecycleOwner) { followedElections ->
            followedElections?.let {
                (binding.followedElectionsList.adapter as ElectionListAdapter).submitList(it)
            }
        }
    }

    private fun goToVoterInfo(it: Election) = navigateTo(
        ElectionsFragmentDirections.fromElectionsToVoterInfo(it.id, it.division)
    )
}