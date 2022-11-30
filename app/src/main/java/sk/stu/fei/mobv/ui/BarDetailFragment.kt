package sk.stu.fei.mobv.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentBarDetailBinding
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.viewmodels.BarDetailViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class BarDetailFragment : Fragment() {
    private var _binding: FragmentBarDetailBinding? = null
    private val binding get(): FragmentBarDetailBinding = _binding!!

    private val navigationArgs: BarDetailFragmentArgs by navArgs()

    private val barDetailViewModel: BarDetailViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[BarDetailViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        if ((loggedUser?.id ?: "").isBlank()) {
            findNavController().navigate(R.id.action_barDetailFragment_to_loginFragment)
            return
        }

        barDetailViewModel.apply {
            loading.observe(viewLifecycleOwner) {
                binding.refreshLayout.isRefreshing = it
            }
            message.observe(viewLifecycleOwner) {
                showShortMessage(it)
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            barDetailViewModel = this@BarDetailFragment.barDetailViewModel
            thisFragment = this@BarDetailFragment
        }.apply {
            refreshLayout.setOnRefreshListener {
                this@BarDetailFragment.barDetailViewModel.loadBar(navigationArgs.barId)
            }
        }

        barDetailViewModel.loadBar(navigationArgs.barId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun showOnMap() {
        val bar: Bar? = barDetailViewModel.bar.value
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    "geo:0,0,?q=" +
                            "${bar?.latitude ?: 0}," +
                            "${bar?.longitude ?: 0}" +
                            "(${bar?.name ?: ""})"
                )
            )
        )
    }

}