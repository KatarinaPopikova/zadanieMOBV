package sk.stu.fei.mobv.ui

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentBarsListBinding
import sk.stu.fei.mobv.helpers.BarsSort
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.adapter.BarsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.BarsListItemEventListener
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
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        if ((loggedUser?.id ?: "").isBlank()) {
            goToLoginScreen()
            return
        }

        configureMenuBar()

        barsViewModel.apply {
            loading.observe(viewLifecycleOwner) {
                binding.refreshLayout.isRefreshing = it
            }
            message.observe(viewLifecycleOwner) {
                showShortMessage(it)
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            thisFragment = this@BarsListFragment
            barsViewModel = this@BarsListFragment.barsViewModel
        }.apply {
            barsListView.adapter = BarsListItemAdapter(
                BarsListItemEventListener(
                    { barId: Long -> goToBarDetailScreen(barId) },
                    { barsListView.scrollToPosition(0) }
                )
            )
        }.apply {
            refreshLayout.setOnRefreshListener {
                this@BarsListFragment.barsViewModel.refreshData()
            }
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
                when (menuItem.itemId) {
                    R.id.logout_action -> {
                        logout()
                        return true
                    }
                    R.id.sort_bars_name_asc_action -> {
                        barsViewModel.sortBy(BarsSort.NAME_ASC)
                        return true
                    }
                    R.id.sort_bars_name_desc_action -> {
                        barsViewModel.sortBy(BarsSort.NAME_DESC)
                        return true
                    }
                    R.id.sort_users_count_asc_action -> {
                        barsViewModel.sortBy(BarsSort.VISIT_ASC)
                        return true
                    }
                    R.id.sort_users_count_desc_action -> {
                        barsViewModel.sortBy(BarsSort.VISIT_DESC)
                        return true
                    }
                    R.id.sort_bars_distance_asc_action -> {
                        barsViewModel.sortBy(BarsSort.DIST_ASC)
                        return true
                    }
                    else -> {
                        barsViewModel.sortBy(BarsSort.DIST_DESC)
                        return false
                    }
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

    fun goToBarDetailScreen(barId: Long) {
        findNavController().navigate(
            BarsListFragmentDirections.actionBarsListFragmentToBarDetailFragment(barId)
        )
    }

    fun goToTagBarsListScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_tagBarsListFragment)
    }

    fun goToLoginScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_loginFragment)
    }

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}

