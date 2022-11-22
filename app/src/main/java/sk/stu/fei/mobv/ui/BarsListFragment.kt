package sk.stu.fei.mobv.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentBarsListBinding

class BarsListFragment : Fragment() {
    private var _binding: FragmentBarsListBinding? = null
    private val binding get(): FragmentBarsListBinding = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            thisFragment = this@BarsListFragment
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goToFriendsListScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_friendsListFragment)
    }

    fun goToBarDetailScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_barDetailFragment)
    }

    fun goToTagBarsListScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_tagBarsListFragment)
    }
}

