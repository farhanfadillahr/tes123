package com.example.perludilindungi.checkin

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.perludilindungi.R
import com.example.perludilindungi.databinding.CheckInFragmentBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val CAMERA_REQUEST_CODE = 101

private const val LOCATION_REQUEST_CODE = 102


class CheckInFragment : Fragment(), SensorEventListener {

    companion object {
        fun newInstance() = CheckInFragment()
    }

//    private val viewModel: CheckInViewModel by viewModels()

    private lateinit var viewModel: CheckInViewModel

    private lateinit var _binding : CheckInFragmentBinding
    private val binding
        get() = _binding

    private lateinit var codeScanner: CodeScanner

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var longitude : Double = 1.0

    private var latitude : Double = 1.0

    private lateinit var sensorManager: SensorManager
    private var temperature: Sensor? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = DataBindingUtil.inflate<CheckInFragmentBinding>(
            inflater, R.layout.check_in_fragment, container, false)

        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
//        return inflater.inflate(R.layout.check_in_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CheckInViewModel::class.java)

        /*Temp sensor*/
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        /*Location*/
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        /*Observer*/
        viewModel.checkInStatus.observe(viewLifecycleOwner,
            {
                newStatus ->
                changeStatus(newStatus, viewModel.checkInReason.value);
            }
        )

        viewModel.checkInReason.observe(viewLifecycleOwner,
            {
                    newReason ->
                changeStatus(viewModel.checkInStatus.value, newReason);
            }
        )

        viewModel.responseContents.observe(viewLifecycleOwner,
            {
                binding.scanLink.text = it
            })

        /*OnClickListener*/
        val navController = view.findNavController()
        val bottomNavigationView = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val checkInButton = requireActivity().findViewById<FloatingActionButton>(R.id.toCheckInButton)

        binding.backButton.setOnClickListener {
            checkInButton.visibility = View.VISIBLE
            bottomNavigationView.visibility = View.VISIBLE
            navController.popBackStack()
        }

        setupPermission()
        codeScanner()
        changeStatus("black", "terlalu panas")

//        val temperatureBackgroundCircle : Drawable = binding.temperatureTextView.background
//        temperatureBackgroundCircle.colorFilter = BlendModeColorFilter(ContextCompat.getColor(requireContext(),R.color.accent_yellow), BlendMode.SRC_ATOP)

    }

    private fun codeScanner(){
        codeScanner = CodeScanner(requireContext(), binding.scannerView)

        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                activity?.runOnUiThread{

//                    binding.scanLink.text = it.text
                    viewModel.scannedValue = it.text
                    viewModel.longitude = longitude
                    viewModel.latitude = latitude

                    binding.scanLink.text = longitude.toString()
                }
            }

            errorCallback = ErrorCallback {
                activity?.runOnUiThread {
                    Log.e("CheckInFragment", "Camera initialization error: ${it.message}")
                }
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }



    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
        sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL)

    }

    override fun onPause() {
        super.onPause()
        codeScanner.releaseResources()
        sensorManager.unregisterListener(this)

    }

    private fun setupPermission(){
        val permission = ContextCompat.checkSelfPermission(requireContext(),
            android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }

        val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener{
                                location : Location? ->
                            if (location !== null){
                                longitude = location.longitude
                                latitude = location.latitude
                            }
                        }
                } else {
                    Toast.makeText(context, "Turn on the location to use the app", Toast.LENGTH_SHORT).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {

            }
            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        }

    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA),
            CAMERA_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){

                    Toast.makeText(context, "Turn on the camera to use the app", Toast.LENGTH_SHORT).show()
                }
                else{
                    //sucsessful
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val celciusOfTemperature = event.values[0]
        changeTemperature(celciusOfTemperature)
        // Do something with this sensor data.
    }

    private fun changeStatus(userStatus: String?){
        when (userStatus?.lowercase()) {
            "green" -> {
                binding.checkInSuccessImageView.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_success,null))
                binding.checkInStatus.text = getString(R.string.check_in_status_success)
                binding.checkInReason.text = null
                DrawableCompat.setTint(binding.checkInSuccessImageView.background, ContextCompat.getColor(requireContext(),R.color.accent_green))
            }
            "yellow" -> {
                binding.checkInSuccessImageView.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_success,null))
                binding.checkInStatus.text = getString(R.string.check_in_status_success)
                binding.checkInReason.text = null
                DrawableCompat.setTint(binding.checkInSuccessImageView.background, ContextCompat.getColor(requireContext(),R.color.accent_yellow))
            }
            "red" -> {
                binding.checkInSuccessImageView.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_fail,null))
                binding.checkInStatus.text = getString(R.string.check_in_status_fail)
                DrawableCompat.setTint(binding.checkInSuccessImageView.background, ContextCompat.getColor(requireContext(),R.color.accent_red))
            }
            "black" -> {
                binding.checkInSuccessImageView.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_fail,null))
                binding.checkInStatus.text = getString(R.string.check_in_status_fail)
                DrawableCompat.setTint(binding.checkInSuccessImageView.background, ContextCompat.getColor(requireContext(),R.color.black))
            }
            else -> {
                binding.checkInSuccessImageView.setImageDrawable(ResourcesCompat.getDrawable(resources,R.drawable.ic_fail,null))
                binding.checkInStatus.text = null
                binding.checkInReason.text = null
                DrawableCompat.setTint(binding.checkInSuccessImageView.background, ContextCompat.getColor(requireContext(),R.color.white))
            }
        }
    }

    private fun changeStatus(userStatus: String?, reason: String?){
        binding.checkInReason.text = reason
        changeStatus(userStatus)
    }

    private fun changeTemperature(temperature: Number){
        binding.temperatureTextView.text = getString(R.string.room_temperature_fromat, temperature.toInt().toString())
    }
}