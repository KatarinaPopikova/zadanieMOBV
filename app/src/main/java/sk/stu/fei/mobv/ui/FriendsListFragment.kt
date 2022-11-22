package sk.stu.fei.mobv.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import sk.stu.fei.mobv.R
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.databinding.FragmentFriendsListBinding

class FriendsListFragment : Fragment() {
    private var _binding: FragmentFriendsListBinding? = null
    private val binding get(): FragmentFriendsListBinding = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            thisFragment = this@FriendsListFragment
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goToAddFriendScreen() {
        findNavController().navigate(R.id.action_friendsListFragment_to_addFriendFragment)
    }

    fun goToBarDetailScreen() {
        findNavController().navigate(R.id.action_friendsListFragment_to_barDetailFragment)
    }
}