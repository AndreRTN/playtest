package com.example.play_test.ui.currentBets

import android.app.Application
import android.app.NotificationManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.play_test.data.dao.BalanceDao
import com.example.play_test.data.dao.BetHistoyDao
import com.example.play_test.data.models.BetHistory
import com.example.play_test.data.shared.NotificationUtils.sendResultNotification
import com.example.play_test.data.shared.NotificationUtils.sendScoreNotification
import com.example.play_test.domain.entity.Bets
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.random.Random

class RunningBetsViewModel(private val balanceDao: BalanceDao, val dao: BetHistoyDao, app: Application) :
    AndroidViewModel(app) {

    private val homeTeamPrediction = (0..25).toList()
    private val awayTeamPrediction = (26..51).toList()
    private var notificationManager: NotificationManager
    private var _bets: MutableStateFlow<MutableList<Bets>> = MutableStateFlow(mutableListOf())
    val itemsUpdated: MutableStateFlow<Int> = MutableStateFlow(0)
    val itemRemoved: MutableStateFlow<Bets?> = MutableStateFlow(null)

    init {
        val context = getApplication<Application>().applicationContext
        notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private val job: Job = viewModelScope.launch {
        while (true) {

            delay(1000)
            manageTime()
            viewModelScope.launch {
                delay(10000)
                manageEvents()
            }
            if (_bets.value.isEmpty()) {
                cancel()
                itemsUpdated.emit(0)
            }
        }
    }

    private fun manageTime() {
        val managedBets = _bets.value.toList()

        managedBets.forEach {
            if (it.timeToEnd > 0) {
                it.timeToEnd = it.timeToEnd.minus(1)
            } else {
                manageResults(it)
                manageBet(it)
                val betHistory = BetHistory(
                    uid = it.id.toInt(),
                    value = it.value,
                    odd = it.oddPojo.odd,
                    oddId = it.oddPojo.id,
                    period = it.date,
                    win = it.win
                )
                viewModelScope.launch(Dispatchers.IO) {
                    dao.add(betHistory)
                    if (it.win) {
                        balanceDao.getBalance().collect { balances ->
                            val balance = balances.first()
                            balanceDao.update(balance.copy(value = balance.value.plus(it.value * it.oddPojo.odd)))
                            cancel()
                        }
                    }
                }
                _bets.value.remove(it)
                viewModelScope.launch {
                    itemRemoved.emit(it)
                }
            }
        }
        viewModelScope.launch {

            itemsUpdated.emit(itemsUpdated.value.plus(1))
        }
    }

    private fun manageBet(bets: Bets) {
        val context = getApplication<Application>().applicationContext
        val winMsg =
            if (bets.win) "Você ganhou a aposta!" else "Você perdeu a aposta, não foi dessa vez."
        if (bets.draw) {
            notificationManager.sendResultNotification(
                context,
                "O jogo entre ${bets.homeTeam} e ${bets.awayTeam} terminou em empate, $winMsg"
            )
        }
        notificationManager.sendResultNotification(
            context,
            winMsg
        )
    }

    private fun manageResults(bets: Bets) {
        bets.draw = bets.awayScore == bets.homeScore
        if (bets.homeScore > bets.awayScore) {
            bets.winTeam = bets.homeTeam
            bets.loseTeam = bets.awayTeam
        }

        if (bets.awayScore > bets.homeScore) {
            bets.winTeam = bets.awayTeam
            bets.loseTeam = bets.homeTeam
        }

        when (bets.oddPojo.id) {
            "1" -> bets.win = bets.homeTeam == bets.winTeam
            "2" -> bets.win = bets.awayTeam == bets.winTeam
            else -> bets.win = bets.draw
        }
    }

    private fun manageScores(team: String, oneScore: Int, twoScore: Int) {
        val context = getApplication<Application>().applicationContext
        if (oneScore == 1 && twoScore == 0) {
            notificationManager.sendScoreNotification(
                context,
                "Gooool! o $team marca o primeiro gol e sai na frente"
            )
        }
        if (oneScore == 1 && twoScore == 1) {
            notificationManager.sendScoreNotification(context, "Gooool! o $team acaba de empatar")
        }
    }

    private fun manageEvents() {
        val managedBets = _bets.value.toList()
        managedBets.forEach {
            val scoreRandom = Random.nextInt(0, 500)
            if (homeTeamPrediction.contains(scoreRandom)) {
                it.homeScore = it.homeScore.plus(1)
                manageScores(it.homeTeam, it.homeScore, it.awayScore)
            }
            if (awayTeamPrediction.contains(scoreRandom)) {
                it.awayScore = it.awayScore.plus(1)
                manageScores(it.awayTeam, it.awayScore, it.homeScore)
            }
        }
    }

    val bets: StateFlow<MutableList<Bets>> = _bets
    fun initializeBets(list: List<Bets>) {
        viewModelScope.launch {

            if (_bets.value.isEmpty()) {

                _bets.emit(list.toMutableList())

                job.start()
            } else {
                val copyBets = _bets.value.toMutableList()
                copyBets.addAll(list)
                _bets.emit(copyBets)
            }
        }
    }

    fun resetRemovedItems() {
        viewModelScope.launch { itemRemoved.emit(null) }
    }
}

class RunningBetsViewModelFactory(
    private val balanceDao: BalanceDao,
    private val dao: BetHistoyDao,
    val app: Application
) :
    ViewModelProvider.AndroidViewModelFactory(app) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RunningBetsViewModel(balanceDao, dao, app) as T
    }
}
