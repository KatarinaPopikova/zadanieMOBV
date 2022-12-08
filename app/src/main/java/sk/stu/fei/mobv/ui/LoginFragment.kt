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
import sk.stu.fei.mobv.databinding.FragmentLoginBinding
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.viewmodels.AuthenticationViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get(): FragmentLoginBinding = _binding!!


    private val authenticationViewModel: AuthenticationViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[AuthenticationViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (isUserLoggedIn()) {
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
            thisFragment = this@LoginFragment
            authenticationViewModel = this@LoginFragment.authenticationViewModel
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun login() {
        val loginName = binding.loginNameText.text.toString()
        val password = binding.passwordText.text.toString()
        if (loginName.isNotBlank() && password.isNotBlank()) {
            authenticationViewModel.login(
                loginName,
                password
            )
        } else {
            showShortMessage(getString(R.string.fill_name_password))
        }
    }

    fun goToRegisterScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun goToBarsListScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_barsListFragment)
    }

    private fun isUserLoggedIn(): Boolean {
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        return (loggedUser?.id ?: "").isNotBlank()
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}