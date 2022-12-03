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
import sk.stu.fei.mobv.databinding.FragmentAddFriendBinding
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.viewmodels.FriendsViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class AddFriendFragment : Fragment() {
    private var _binding: FragmentAddFriendBinding? = null
    private val binding get(): FragmentAddFriendBinding = _binding!!

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
        _binding = FragmentAddFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isUserLoggedIn()) {
            goToLoginScreen()
            return
        }

        friendsViewModel.apply {
            message.observe(viewLifecycleOwner) {
                showShortMessage(it)
                if (!isUserLoggedIn()) {
                    goToLoginScreen()
                }
            }
        }

        binding.apply {
            thisFragment = this@AddFriendFragment
            lifecycleOwner = viewLifecycleOwner
            friendsViewModel = this@AddFriendFragment.friendsViewModel
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

    fun addFriend(){
        val friendName: String = binding.loginNameText.text.toString()
        if (friendName.isNotEmpty()) {
            friendsViewModel.addFriend(friendName)
        } else {
            showShortMessage("Vyplnte meno kamarata")
        }
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun goToLoginScreen(){
        findNavController().navigate(R.id.action_addFriendFragment_to_loginFragment)
    }

}