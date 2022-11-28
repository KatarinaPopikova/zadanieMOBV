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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isNotBlank()) {
            goToBarsListScreen()
            return
        }

        authenticationViewModel.message.observe(this.viewLifecycleOwner) {
            showShortMessage(it)
        }

        authenticationViewModel.user.observe(viewLifecycleOwner){
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(), it)
                goToBarsListScreen()
            }
        }


        binding.apply {
            thisFragment = this@LoginFragment
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
            //it.findNavController().popBackStack(R.id.bars_fragment,false)
            authenticationViewModel.login(
                loginName,
                password
            )
        } else {
            showShortMessage("Fill password and username")
        }
    }

    fun goToRegisterScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    private fun goToBarsListScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_barsListFragment)
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}