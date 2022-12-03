package sk.stu.fei.mobv.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentBarsListBinding
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.domain.MyLocation
import sk.stu.fei.mobv.helpers.BarsSort
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.adapter.BarsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.BarsListItemEventListener
import sk.stu.fei.mobv.ui.viewmodels.BarsViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class BarsListFragment : Fragment() {
    private var _binding: FragmentBarsListBinding? = null
    private val binding get(): FragmentBarsListBinding = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationForCallback: () -> Unit


    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                locationForCallback()
                // Precise location access granted.
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                showShortMessage("Only approximate location access granted.")
                // Only approximate location access granted.
            }
            else -> {
                showShortMessage("Location access denied.")
                // No location access granted.
            }
        }
    }

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isUserLoggedIn()) {
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
                if (!isUserLoggedIn()) {
                    goToLoginScreen()
                }
            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            thisFragment = this@BarsListFragment
            barsViewModel = this@BarsListFragment.barsViewModel
        }.apply {
            barsListView.adapter = BarsListItemAdapter(
                BarsListItemEventListener(
                    { bar: Bar -> goToBarDetailScreen(bar.id) },
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
                        doActionAfterCheckPermission{ setMyLocationAndSort(BarsSort.DIST_ASC) }
                        return true
                    }
                    else -> {
                        doActionAfterCheckPermission{ setMyLocationAndSort(BarsSort.DIST_DESC) }
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

    fun handleShowNearbyBarsClick() {
        doActionAfterCheckPermission { goToTagBarsListScreen() }
    }

    private fun doActionAfterCheckPermission(action: () -> Unit) {
        locationForCallback = action
        if (checkPermissions()) {
            action()
        } else {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    @SuppressLint("MissingPermission")
    fun setMyLocationAndSort(sortType: BarsSort) {
        fusedLocationClient.getCurrentLocation(
            CurrentLocationRequest.Builder().setDurationMillis(30000)
                .setMaxUpdateAgeMillis(60000).build(), null
        ).addOnSuccessListener {
            it?.let {
                barsViewModel.myLocation.value = MyLocation(it.latitude, it.longitude)
                barsViewModel.sortBy(sortType)
            }
        }
    }

    fun goToMyFriendsListScreen() {
        findNavController().navigate(R.id.action_barsListFragment_to_myFriendsListFragment)
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

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

