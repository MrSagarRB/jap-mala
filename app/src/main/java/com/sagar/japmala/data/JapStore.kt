package com.sagar.japmala.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

/** Thin SharedPreferences wrapper. Counts are kept per mantra; daily totals are kept across mantras. */
class JapStore(context: Context) {
    private val prefs = context.getSharedPreferences("jap_mala", Context.MODE_PRIVATE)

    var selectedId: String
        get() = prefs.getString(KEY_SELECTED, Mantras.presets.first().id) ?: Mantras.presets.first().id
        set(value) = prefs.edit().putString(KEY_SELECTED, value).apply()

    var target: Int
        get() = prefs.getInt(KEY_TARGET, 108)
        set(value) = prefs.edit().putInt(KEY_TARGET, value).apply()

    var haptics: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS, value).apply()

    var keepScreenOn: Boolean
        get() = prefs.getBoolean(KEY_KEEP_ON, true)
        set(value) = prefs.edit().putBoolean(KEY_KEEP_ON, value).apply()

    var volumeKeys: Boolean
        get() = prefs.getBoolean(KEY_VOLUME, false)
        set(value) = prefs.edit().putBoolean(KEY_VOLUME, value).apply()

    fun customMantras(): List<Mantra> {
        val raw = prefs.getString(KEY_CUSTOM, null) ?: return emptyList()
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                Mantra(id = o.getString("id"), title = "मेरा मंत्र", text = o.getString("text"), custom = true)
            }
        }.getOrDefault(emptyList())
    }

    fun saveCustomMantras(list: List<Mantra>) {
        val arr = JSONArray()
        list.forEach { arr.put(JSONObject().put("id", it.id).put("text", it.text)) }
        prefs.edit().putString(KEY_CUSTOM, arr.toString()).apply()
    }

    fun beads(id: String) = prefs.getInt("beads_$id", 0)
    fun malas(id: String) = prefs.getInt("malas_$id", 0)
    fun total(id: String) = prefs.getLong("total_$id", 0L)

    fun setProgress(id: String, beads: Int, malas: Int, total: Long) {
        prefs.edit()
            .putInt("beads_$id", beads)
            .putInt("malas_$id", malas)
            .putLong("total_$id", total)
            .apply()
    }

    fun clear(id: String) {
        prefs.edit().remove("beads_$id").remove("malas_$id").remove("total_$id").apply()
    }

    fun today(): Int = prefs.getInt(dayKey(LocalDate.now()), 0)

    fun addToday(delta: Int) {
        val key = dayKey(LocalDate.now())
        prefs.edit().putInt(key, (prefs.getInt(key, 0) + delta).coerceAtLeast(0)).apply()
    }

    /** Consecutive days with at least one jap. A still-empty today doesn't break yesterday's streak. */
    fun streak(): Int {
        var day = LocalDate.now()
        if (prefs.getInt(dayKey(day), 0) == 0) day = day.minusDays(1)
        var n = 0
        while (prefs.getInt(dayKey(day), 0) > 0) {
            n++
            day = day.minusDays(1)
        }
        return n
    }

    private fun dayKey(date: LocalDate) = "day_$date"

    private companion object {
        const val KEY_SELECTED = "selected"
        const val KEY_TARGET = "target"
        const val KEY_HAPTICS = "haptics"
        const val KEY_KEEP_ON = "keep_screen_on"
        const val KEY_VOLUME = "volume_keys"
        const val KEY_CUSTOM = "custom_mantras"
    }
}
