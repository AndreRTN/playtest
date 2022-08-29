package com.example.play_test.ui.home

import android.os.Parcelable
import androidx.lifecycle.*
import com.example.play_test.data.api.ResultApi
import com.example.play_test.data.dao.BalanceDao
import com.example.play_test.data.dto.PredictionResponse
import com.example.play_test.data.enums.Federations
import com.example.play_test.data.models.Balance
import com.example.play_test.data.repository.PredictionsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class HomeViewModel(val dao: BalanceDao, private val repository: PredictionsRepository) :
    ViewModel() {
    private val dtf: DateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd")
    private var _visibility: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val money: MutableStateFlow<Double> = MutableStateFlow(0.0)
    private lateinit var balance: Balance

    var currentFederation: Int = 0
    val visibility: StateFlow<Boolean> get() = _visibility
    private var _predictions: MutableStateFlow<ResultApi<List<PredictionResponse>>> =
        MutableStateFlow(ResultApi.Loading)
    val predictions: StateFlow<ResultApi<List<PredictionResponse>>> get() = _predictions

    private var predictionList: List<PredictionResponse> = emptyList()
    private var _chipScrollPosition: MutableStateFlow<Int> = MutableStateFlow(0)
    val chipScrollPosition: StateFlow<Int> get() = _chipScrollPosition

    var plantListStateParcel: Parcelable? = null

    fun updatePredictionList(predictions: List<PredictionResponse>) {
        predictionList = predictions
    }

    fun savePlanetListState(parcel: Parcelable) {
        plantListStateParcel = parcel
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dao.getBalance().collect {
                if (it.isEmpty()) {
                    val balance = Balance(value = 2000.0, id = 0)
                    dao.addBalance(balance)
                    money.emit(balance.value)
                } else if (it.first().value < 500) {
                    dao.update(it.first().copy(value = 2000.0))
                    money.emit(2000.0)
                    balance = it.first().copy(value = 2000.0)
                } else {
                    money.emit(it.first().value)
                    balance = it.first()
                }
            }
        }
        searchPredicitons(Federations.UEFA, 0)
    }

    fun setChipScrollPosition(value: Int) {
        _chipScrollPosition.value = value
    }

    fun addMoney(value: Double) {
        val newMoney = money.value.plus(value)
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(balance.copy(value = newMoney))
            money.emit(newMoney)
        }
    }

    fun subtractMoney(value: Double) {
        val newMoney = money.value.minus(value)
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(balance.copy(value = newMoney))
            money.emit(newMoney)
        }
    }

    fun searchPredicitons(federation: Federations, chipId: Int) {
        currentFederation = chipId
        val nowDate = LocalDate.now()
        val isoDate = nowDate.format(dtf)
        viewModelScope.launch {
            _predictions.emit(ResultApi.Loading)
            val predictions =
                repository.getPredictions(isoDate = isoDate, federation = federation)
            _predictions.emit(predictions)
        }
    }

    fun convertToRecyclerList(): List<PredictionsRecyclerList> {
        val predictionsList = predictions.value as ResultApi.Success
        val recyclerList = mutableListOf<PredictionsRecyclerList>()
        val groupingPredictions = predictionsList.data?.groupBy { it.competitionCluster }
        groupingPredictions?.forEach {
            val cluster = it.key
            val items = it.value
            val predictionRecycler = PredictionsRecyclerList(cluster, items)
            recyclerList.add(predictionRecycler)
        }
        return recyclerList.toList()
    }

    fun changeVisibility() {
        viewModelScope.launch {
            _visibility.emit(!_visibility.value)
        }
    }
}

class HomeViewModelFactory(
    private val dao: BalanceDao,
    private val predictionsRepository: PredictionsRepository
) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(dao, predictionsRepository) as T
    }
}
