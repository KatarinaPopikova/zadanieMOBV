package sk.stu.fei.mobv.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentBarsListBinding
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.adapter.BarsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.BarsListItemEventListener
import sk.stu.fei.mobv.ui.viewmodels.AuthenticationViewModel
import sk.stu.fei.mobv.ui.viewmodels.BarsViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class BarsListFragment : Fragment() {
    private var _binding: FragmentBarsListBinding? = null
    private val binding get(): FragmentBarsListBinding = _binding!!

    private val barsViewModel: BarsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[BarsViewModel::class.java]
    }

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
        val user = PreferenceData.getInstance().getUserItem(requireContext())
        if ((user?.id ?: "").isBlank()) {
            goToLoginScreen()
            return
        }

        barsViewModel.refreshData()

        configureMenuBar()

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            thisFragment = this@BarsListFragment
            barsViewModel = this@BarsListFragment.barsViewModel
        }.apply {
            barsListView.adapter = BarsListItemAdapter(
                BarsListItemEventListener {
                    Log.d("AHOJ", "Ano som tu")
                }
            )
        }.apply {
            refreshLayout.setOnRefreshListener {
                this@BarsListFragment.barsViewModel.refreshData()
            }
        }

        barsViewModel.loading.observe(viewLifecycleOwner) {
            binding.refreshLayout.isRefreshing = it
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun configureMenuBar() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tag_bars_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.logout_action -> {
                        logout()
                        return true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun logout() {
        PreferenceData.getInstance().clearData(requireContext())
        findNavController().navigate(R.id.action_barsListFragment_to_loginFragment)
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

    fun goToLoginScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_loginFragment)
    }
}

