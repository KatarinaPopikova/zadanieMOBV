package sk.stu.fei.mobv.ui

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.*
import sk.stu.fei.mobv.GeofenceBroadcastReceiver
import sk.stu.fei.mobv.MainApplication
import sk.stu.fei.mobv.R
import sk.stu.fei.mobv.databinding.FragmentTagBarsListBinding
import sk.stu.fei.mobv.domain.Bar
import sk.stu.fei.mobv.domain.MyLocation
import sk.stu.fei.mobv.helpers.PreferenceData
import sk.stu.fei.mobv.ui.adapter.BarsListItemAdapter
import sk.stu.fei.mobv.ui.adapter.BarsListItemEventListener
import sk.stu.fei.mobv.ui.viewmodels.TagBarsViewModel
import sk.stu.fei.mobv.ui.viewmodels.factory.ViewModelFactory

class TagBarsListFragment : Fragment() {
    private var _binding: FragmentTagBarsListBinding? = null
    private val binding get(): FragmentTagBarsListBinding = _binding!!
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geofencingClient: GeofencingClient

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_BACKGROUND_LOCATION, false) -> {
                // Precise location access granted.
            }
            else -> {
                showShortMessage("Background location access denied.")
                // No location access granted.
            }
        }
    }

    private val tagBarsViewModel: TagBarsViewModel by lazy {
        val activity = requireNotNull(this.activity) {
            "You can only access the viewModel after onActivityCreated()"
        }
        ViewModelProvider(
            this,
            ViewModelFactory((activity.application as MainApplication).repository)
        )[TagBarsViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTagBarsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (!isUserLoggedIn()) {
            goToLoginScreen()
            return
        }

        tagBarsViewModel.apply {
            loading.observe(viewLifecycleOwner) {
                binding.refreshLayout.isRefreshing = it
            }
            message.observe(viewLifecycleOwner) {
                showShortMessage(it)
                if (!isUserLoggedIn()) {
                    goToLoginScreen()
                }
            }
            checkedIn.observe(viewLifecycleOwner) {
                if (it) {
                    tagBarsViewModel.myLocation.value?.let { myLocation ->
                        createFence(myLocation.latitude, myLocation.longitude)
                    }
                }

            }
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            tagBarsViewModel = this@TagBarsListFragment.tagBarsViewModel
            thisFragment = this@TagBarsListFragment
        }.apply {
            tagBarsListView.adapter = BarsListItemAdapter(
                BarsListItemEventListener(
                    { bar: Bar -> tagBarsViewModel!!.myBar.postValue(bar) },
                    { tagBarsListView.scrollToPosition(0) }
                )
            )
        }.apply {
            refreshLayout.setOnRefreshListener { loadData() }
            checkInBarLottie.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    checkInBarLoading.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {}

                override fun onAnimationRepeat(animation: Animator) {}
            })

        }

        if (checkPermissions()) {
            loadData()
        } else {
            goToBarListScreen()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun handleCheckInBarButtonClick() {
        if (checkBackgroundPermissions()) {
            binding.checkInBarLoading.visibility = View.VISIBLE
            binding.checkInBarLottie.playAnimation()
            tagBarsViewModel.checkMe()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                permissionDialog()
            }
        }
    }

    private fun isUserLoggedIn(): Boolean {
        val loggedUser = PreferenceData.getInstance().getUserItem(requireContext())
        return (loggedUser?.id ?: "").isNotBlank()
    }

    private fun goToLoginScreen() {
        findNavController().navigate(R.id.action_tagBarsListFragment_to_loginFragment)
    }

    private fun goToBarListScreen() {
        findNavController().navigate(R.id.action_tagBarsListFragment_to_barsListFragment)
    }


    @SuppressLint("MissingPermission")
    private fun loadData() {
        if (checkPermissions()) {
            tagBarsViewModel.loading.postValue(true)
            fusedLocationClient.getCurrentLocation(
                CurrentLocationRequest.Builder().setDurationMillis(30000)
                    .setMaxUpdateAgeMillis(60000).build(), null
            ).addOnSuccessListener {
                it?.let {
                    tagBarsViewModel.myLocation.postValue(MyLocation(it.latitude, it.longitude))
                } ?: tagBarsViewModel.loading.postValue(false)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun createFence(lat: Double, lon: Double) {
        if (!checkPermissions()) {
            showShortMessage("Geofence failed, permissions not granted.")
        }
        val geofenceIntent = PendingIntent.getBroadcast(
            requireContext(), 0,
            Intent(requireContext(), GeofenceBroadcastReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val request = GeofencingRequest.Builder().apply {
            addGeofence(
                Geofence.Builder()
                    .setRequestId("mygeofence")
                    .setCircularRegion(lat, lon, 300F)
                    .setExpirationDuration(1000L * 60 * 60 * 24)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build()
            )
        }.build()

        geofencingClient.addGeofences(request, geofenceIntent).run {
            addOnSuccessListener {
                // TODO
                showShortMessage("Geofence successfully to create.")
            }
            addOnFailureListener {
                showShortMessage("Geofence failed to create.") //permission is not granted for All times.
                it.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun permissionDialog() {
        val alertDialog: AlertDialog = requireActivity().let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Background location needed")
                setMessage("Allow background location (All times) for detecting when you leave bar.")
                setPositiveButton("OK",
                    DialogInterface.OnClickListener { _, _ ->
                        locationPermissionRequest.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            )
                        )
                    })
                setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { _, _ ->
                        // User cancelled the dialog
                    })
            }
            // Create the AlertDialog
            builder.create()
        }
        alertDialog.show()
    }

    private fun checkBackgroundPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
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

    private fun showShortMessage(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}