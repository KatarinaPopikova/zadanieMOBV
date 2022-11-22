package sk.stu.fei.mobv.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get(): FragmentRegisterBinding = _binding!!

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
        binding.apply {
            thisFragment = this@RegisterFragment
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun goToLoginScreen() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    fun goToBarsListScreen() {
        findNavController().navigate(R.id.action_loginFragment_to_barsListFragment)
    }
}