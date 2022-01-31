package com.complete.weatherapplication.ui.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.complete.weatherapplication.adapters.WeatherReportAdapter
import com.complete.weatherapplication.model2.Daily
import com.complete.weatherapplication.R
import com.complete.weatherapplication.databinding.FragmentReportBinding
import com.complete.weatherapplication.utils.Resources
import com.complete.weatherapplication.utils.Utils
import com.complete.weatherapplication.utils.Utils.Companion.SHARED
import com.complete.weatherapplication.utils.Utils.Companion.TAG
import com.complete.weatherapplication.ui.WeatherRepository
import com.complete.weatherapplication.ui.WeatherViewModel
import com.complete.weatherapplication.ui.WeatherViewmodelFactory

class ReportFragment : Fragment(R.layout.fragment_report) {

     private lateinit var rvAdapter:WeatherReportAdapter
     private lateinit var list:ArrayList<Daily>
     private var _binding: FragmentReportBinding? = null
     private val binding : FragmentReportBinding get() = _binding!!
    private var latitude : Double = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("latitude","0.0")?.toDouble()?:28.6128
    private var longitude : Double = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("longitude","0.0")?.toDouble()?:77.2311


    private lateinit var viewModel: WeatherViewModel

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentReportBinding.inflate(inflater,container,false)
        //creating a empty list
        list = ArrayList()

        binding.username.text = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("name","")
        val unit = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("unit","metric")
        binding.forLocation.text = activity?.getSharedPreferences(SHARED,Context.MODE_PRIVATE)?.getString("cityName","")

        //creating object for repository
        val repo = WeatherRepository()
        //creating object for viewModelFactory
        val factory = WeatherViewmodelFactory(repo)
        //initialising the view model
        viewModel = ViewModelProvider(this,factory).get(WeatherViewModel::class.java)
        //Checking Internet connection
        if(Utils.isOnline(requireActivity())){
            viewModel.getForecast(longitude,latitude,unit.toString())
            observing()
        }else{
            Toast.makeText(activity,"No Internet Connection!", Toast.LENGTH_SHORT).show()
        }
        //check on click
        binding.backButton.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_reportfragment_to_currentFragment)
        }

        return binding.root
    }
    //hide progress bar
    private fun hideProgressBar(){
        binding.progressbar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.progressbar.visibility = View.VISIBLE
    }

    private fun observing(){
        viewModel.reportList.observe(viewLifecycleOwner, { response->
            when(response){
                is Resources.Success ->{
                    hideProgressBar()
                    response.data.let{
                        for(i in it!!.daily){
                            list.add(i)
                        }
                        setupRecView(list)
                    }
                }
                is Resources.Error ->{
                    hideProgressBar()
                    Log.d(TAG,"error")
                }
                is Resources.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun setupRecView(listY:ArrayList<Daily>){
        rvAdapter = WeatherReportAdapter(listY,requireContext())
        binding.rv.apply {
            adapter = rvAdapter
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            rvAdapter.setData(listY)
        }
    }
    //null the binding
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}