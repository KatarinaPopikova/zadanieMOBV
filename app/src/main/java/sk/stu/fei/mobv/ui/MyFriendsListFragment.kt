package sk.stu.fei.mobv.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentMyFriendsListBinding
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.adapter.FriendsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.FriendsListItemEventListener
import sk.stu.fei.mobv.ui.viewmodels.FriendsViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class MyFriendsListFragment : Fragment() {
    private var _binding: FragmentMyFriendsListBinding? = null
    private val binding get(): FragmentMyFriendsListBinding = _binding!!

    private val friendsViewModel: FriendsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[FriendsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyFriendsListBinding.inflate(inflater, container, false)
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
                if (it.isNotEmpty()) {
                    showShortMessage(it)
                    message.value = ""
                }
                if (!isUserLoggedIn()) {
                    goToLoginScreen()
                }
            }
        }

        binding.apply {
            thisFragment = this@MyFriendsListFragment
            lifecycleOwner = viewLifecycleOwner
            friendsViewModel = this@MyFriendsListFragment.friendsViewModel
        }.apply {
            friendsListView.adapter = FriendsListItemAdapter(
                FriendsListItemEventListener (
                    { },
                    {
                        friendsViewModel!!.deleteFriend(it)
                    }
                )
            )
        }.apply {
            refreshLayout.setOnRefreshListener { friendsViewModel!!.refreshMyFriends() }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isUserLoggedIn(): Boolean {
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        return (loggedUser?.id ?: "").isNotBlank()
    }

    fun goToAddFriendScreen() {
        findNavController().navigate(R.id.action_myFriendsListFragment_to_addFriendFragment)
    }

    fun goToFriendListScreen() {
        findNavController().navigate(R.id.action_myFriendsListFragment_to_friendsListFragment)
    }

    private fun goToLoginScreen() {
        findNavController().navigate(R.id.action_myFriendsListFragment_to_loginFragment)
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }




}