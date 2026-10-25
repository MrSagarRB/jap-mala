package com.sagar.japmala

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sagar.japmala.data.JapStore
import com.sagar.japmala.data.Mantra
import com.sagar.japmala.data.Mantras
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JapState(
    val mantras: List<Mantra>,
    val selected: Mantra,
    val beads: Int,
    val malas: Int,
    val total: Long,
    val totals: Map<String, Long>,
    val today: Int,
    val streak: Int,
    val target: Int,
    val haptics: Boolean,
    val keepScreenOn: Boolean,
    val volumeKeys: Boolean,
    val showCompleted: Boolean = false,
)

class JapViewModel(app: Application) : AndroidViewModel(app) {
    private val store = JapStore(app)
    private val haptics = Haptics(app)

    private val _state = MutableStateFlow(load())
    val state: StateFlow<JapState> = _state.asStateFlow()

    private var bannerJob: Job? = null

    private fun load(): JapState {
        val mantras = Mantras.presets + store.customMantras()
        val selected = mantras.firstOrNull { it.id == store.selectedId } ?: mantras.first()
        val target = store.target
        return JapState(
            mantras = mantras,
            selected = selected,
            beads = store.beads(selected.id).coerceAtMost(target - 1),
            malas = store.malas(selected.id),
            total = store.total(selected.id),
            totals = mantras.associate { it.id to store.total(it.id) },
            today = store.today(),
            streak = store.streak(),
            target = target,
            haptics = store.haptics,
            keepScreenOn = store.keepScreenOn,
            volumeKeys = store.volumeKeys,
        )
    }

    fun increment() {
        val s = _state.value
        val completed = s.beads + 1 >= s.target
        val beads = if (completed) 0 else s.beads + 1
        val malas = if (completed) s.malas + 1 else s.malas
        val total = s.total + 1

        store.setProgress(s.selected.id, beads, malas, total)
        store.addToday(1)

        _state.update {
            it.copy(
                beads = beads,
                malas = malas,
                total = total,
                totals = it.totals + (s.selected.id to total),
                today = store.today(),
                // Streak can only change when today goes from 0 to 1
                streak = if (s.today == 0) store.streak() else it.streak,
            )
        }

        if (s.haptics) if (completed) haptics.malaComplete() else haptics.bead()
        if (completed) flashCompleted()
    }

    fun undo() {
        val s = _state.value
        if (s.total == 0L) return
        var beads = s.beads - 1
        var malas = s.malas
        if (beads < 0) {
            if (malas == 0) return
            malas--
            beads = s.target - 1
        }
        val total = s.total - 1
        store.setProgress(s.selected.id, beads, malas, total)
        store.addToday(-1)
        _state.update {
            it.copy(
                beads = beads,
                malas = malas,
                total = total,
                totals = it.totals + (s.selected.id to total),
                today = store.today(),
                streak = store.streak(),
            )
        }
    }

    fun selectMantra(mantra: Mantra) {
        store.selectedId = mantra.id
        _state.update {
            it.copy(
                selected = mantra,
                beads = store.beads(mantra.id).coerceAtMost(it.target - 1),
                malas = store.malas(mantra.id),
                total = store.total(mantra.id),
            )
        }
    }

    fun addMantra(text: String) {
        val clean = text.trim()
        if (clean.isEmpty()) return
        val mantra = Mantra(id = "custom_${System.currentTimeMillis()}", title = "मेरा मंत्र", text = clean, custom = true)
        store.saveCustomMantras(store.customMantras() + mantra)
        _state.update { it.copy(mantras = it.mantras + mantra, totals = it.totals + (mantra.id to 0L)) }
        selectMantra(mantra)
    }

    fun deleteMantra(mantra: Mantra) {
        if (!mantra.custom) return
        store.saveCustomMantras(store.customMantras().filterNot { it.id == mantra.id })
        store.clear(mantra.id)
        val wasSelected = _state.value.selected.id == mantra.id
        _state.update { it.copy(mantras = it.mantras.filterNot { m -> m.id == mantra.id }, totals = it.totals - mantra.id) }
        if (wasSelected) selectMantra(_state.value.mantras.first())
    }

    /** Clears the progress of the selected mantra (daily history is kept). */
    fun resetCurrent() {
        val s = _state.value
        store.clear(s.selected.id)
        _state.update { it.copy(beads = 0, malas = 0, total = 0, totals = it.totals + (s.selected.id to 0L)) }
    }

    fun setTarget(target: Int) {
        store.target = target
        _state.update { it.copy(target = target, beads = it.beads.coerceAtMost(target - 1)) }
        val s = _state.value
        store.setProgress(s.selected.id, s.beads, s.malas, s.total)
    }

    fun setHaptics(on: Boolean) {
        store.haptics = on
        _state.update { it.copy(haptics = on) }
    }

    fun setKeepScreenOn(on: Boolean) {
        store.keepScreenOn = on
        _state.update { it.copy(keepScreenOn = on) }
    }

    fun setVolumeKeys(on: Boolean) {
        store.volumeKeys = on
        _state.update { it.copy(volumeKeys = on) }
    }

    private fun flashCompleted() {
        bannerJob?.cancel()
        _state.update { it.copy(showCompleted = true) }
        bannerJob = viewModelScope.launch {
            delay(2800)
            _state.update { it.copy(showCompleted = false) }
        }
    }
}
