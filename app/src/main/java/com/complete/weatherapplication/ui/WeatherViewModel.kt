package com.complete.weatherapplication.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.complete.weatherapplication.utils.Resources
import kotlinx.coroutines.launch
import retrofit2.Response
import com.complete.weatherapplication.model.WeatherResponse
import com.complete.weatherapplication.model2.WeatherReportResponse
import com.complete.weatherapplication.model3.WeatherPastResponse

class WeatherViewModel(private val repo: WeatherRepository): ViewModel() {
    val reportList:MutableLiveData<Resources<WeatherReportResponse>> = MutableLiveData()
    var search :MutableLiveData<Resources<WeatherResponse>> = MutableLiveData()
    var pastValues:MutableLiveData<Resources<WeatherPastResponse>> = MutableLiveData()


    fun getSearch(lon:Double,lat:Double,unit:String) = viewModelScope.launch {
        search.postValue(Resources.Loading())
        val searched = repo.getSearched(lon,lat,unit)
        search.postValue(handleWeatherResponse(searched))
    }
    private fun handleWeatherResponse(response: Response<WeatherResponse>):Resources<WeatherResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }
    fun getForecast(lon:Double,lat:Double,unit:String) = viewModelScope.launch {
        reportList.postValue(Resources.Loading())
        val reports = repo.getForecast(lon,lat,unit)
        reportList.postValue(handleDailyResponse(reports))
    }
    private fun handleDailyResponse(response: Response<WeatherReportResponse>):Resources<WeatherReportResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }
    fun getPastResponse(lon:Double, lat:Double, unit:String, date:Int) = viewModelScope.launch {
        pastValues.postValue(Resources.Loading())
        val reports = repo.getPastCall(lon,lat,unit,date)
        pastValues.postValue(handleDailyPastResponse(reports))
    }
    private fun handleDailyPastResponse(response: Response<WeatherPastResponse>):Resources<WeatherPastResponse>{
        if(response.isSuccessful){
            response.body()?.let{ resultResponse->
                return Resources.Success(resultResponse)
            }
        }
        return Resources.Error(response.message())
    }


}