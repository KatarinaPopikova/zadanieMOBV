package sk.stu.fei.mobv.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import sk.stu.fei.mobv.R
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.databinding.FragmentFriendsListBinding
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.adapter.FriendsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.FriendsListItemEventListener
import sk.stu.fei.mobv.ui.viewmodels.FriendsViewModel
import sk.stu.fei.mobv.ui.viewmodels.TagBarsViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class FriendsListFragment : Fragment() {
    private var _binding: FragmentFriendsListBinding? = null
    private val binding get(): FragmentFriendsListBinding = _binding!!

    private val friendsViewModel: FriendsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[FriendsViewModel::class.java]
    }

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
        if (!isUserLoggedIn()) {
            goToLoginScreen()
            return
        }

        friendsViewModel.apply {
            loading.observe(viewLifecycleOwner) {
                binding.refreshLayout.isRefreshing = it
            }
            message.observe(viewLifecycleOwner) {
                showShortMessage(it)
                if (!isUserLoggedIn()) {
                    goToLoginScreen()
                }
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            friendsViewModel = this@FriendsListFragment.friendsViewModel
        }.apply {
            friendsListView.adapter = FriendsListItemAdapter(
                FriendsListItemEventListener ({ barId: Long? ->
                    barId?.let {
                        goToBarDetailScreen(barId)
                    }
                })
            )
        }.apply {
            refreshLayout.setOnRefreshListener { friendsViewModel!!.loadFriends() }
        }

        friendsViewModel.loadFriends()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goToBarDetailScreen(barId: Long) {
        findNavController().navigate(
            FriendsListFragmentDirections.actionFriendsListFragmentToBarDetailFragment(barId)
        )
    }

    private fun isUserLoggedIn(): Boolean {
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        return (loggedUser?.id ?: "").isNotBlank()
    }

    private fun goToLoginScreen() {
        findNavController().navigate(R.id.action_friendsListFragment_to_loginFragment)
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}