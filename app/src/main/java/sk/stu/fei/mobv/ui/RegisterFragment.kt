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
import sk.stu.fei.mobv.databinding.FragmentRegisterBinding
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.viewmodels.AuthenticationViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get(): FragmentRegisterBinding = _binding!!

    private val authenticationViewModel: AuthenticationViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[AuthenticationViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        if ((loggedUser?.id ?: "").isNotBlank()) {
            goToBarsListScreen()
            return
        }

        authenticationViewModel.apply {
            message.observe(viewLifecycleOwner) {
                showShortMessage(it)
            }
            user.observe(viewLifecycleOwner) {
                it?.let {
                    PreferenceData.getInstance().putUserItem(requireContext(), it)
                    goToBarsListScreen()
                }
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            thisFragment = this@RegisterFragment
            authenticationViewModel = this@RegisterFragment.authenticationViewModel
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun register() {
        val loginName = binding.loginNameText.text.toString()
        val password = binding.passwordText.text.toString()
        val passwordAgain = binding.passwordAgainText.text.toString()
        if (loginName.isNotBlank() && password.isNotBlank() && password.compareTo(passwordAgain) == 0) {
            authenticationViewModel.signup(
                loginName,
                password
            )
        } else if (loginName.isBlank() || password.isBlank()
        ) {
            showShortMessage(getString(R.string.fill_name_password))
        } else {
            showShortMessage(getString(R.string.passwords_be_same))
        }
    }

    fun goToLoginScreen() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun goToBarsListScreen() {
        findNavController().navigate(R.id.action_registerFragment_to_barsListFragment)
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}