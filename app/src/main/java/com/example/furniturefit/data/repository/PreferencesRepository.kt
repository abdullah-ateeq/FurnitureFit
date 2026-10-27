package com.example.furniturefit.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.furniturefit.data.model.AppSettings
import com.example.furniturefit.data.model.AppThemeMode
import com.example.furniturefit.data.model.Clearance
import com.example.furniturefit.data.model.FurnitureFitHistory
import com.example.furniturefit.data.model.Orientation
import com.example.furniturefit.data.model.UnitType
import org.json.JSONArray
import org.json.JSONObject

class PreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("furniture_fit_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SETTINGS = "app_settings"
        private const val KEY_HISTORY = "fit_history"
        private const val KEY_LAST_CALCULATION = "last_calculation"
        private const val MAX_HISTORY_ITEMS = 10
    }

    // ==========================================
    // Settings
    // ==========================================

    fun getSettings(): AppSettings {
        val raw = prefs.getString(KEY_SETTINGS, null) ?: return AppSettings()
        return try {
            val json = JSONObject(raw)
            AppSettings(
                themeMode = AppThemeMode.valueOf(json.optString("themeMode", AppThemeMode.SYSTEM.name)),
                dynamicColor = json.optBoolean("dynamicColor", true),
                defaultUnit = UnitType.valueOf(json.optString("defaultUnit", UnitType.CM.name)),
                defaultClearanceMm = json.optDouble("defaultClearanceMm", 300.0),
                defaultFrontClearanceMm = json.optDouble("defaultFrontClearanceMm", 600.0),
                tightFitThresholdMm = json.optDouble("tightFitThresholdMm", 100.0),
                defaultOrientation = Orientation.valueOf(json.optString("defaultOrientation", Orientation.AUTO.name)),
                showGrid = json.optBoolean("showGrid", true),
                gridSpacingMm = json.optDouble("gridSpacingMm", 250.0),
                showDimensions = json.optBoolean("showDimensions", true),
                showClearance = json.optBoolean("showClearance", true),
                showFurnitureLabels = json.optBoolean("showFurnitureLabels", true),
                snapToGrid = json.optBoolean("snapToGrid", true),
                liveCalculation = json.optBoolean("liveCalculation", true),
                haptics = json.optBoolean("haptics", true),
                animations = json.optBoolean("animations", true),
                confirmDelete = json.optBoolean("confirmDelete", true),
                restoreLastCalculation = json.optBoolean("restoreLastCalculation", true)
            )
        } catch (e: Exception) {
            AppSettings()
        }
    }

    fun saveSettings(settings: AppSettings) {
        val json = JSONObject().apply {
            put("themeMode", settings.themeMode.name)
            put("dynamicColor", settings.dynamicColor)
            put("defaultUnit", settings.defaultUnit.name)
            put("defaultClearanceMm", settings.defaultClearanceMm)
            put("defaultFrontClearanceMm", settings.defaultFrontClearanceMm)
            put("tightFitThresholdMm", settings.tightFitThresholdMm)
            put("defaultOrientation", settings.defaultOrientation.name)
            put("showGrid", settings.showGrid)
            put("gridSpacingMm", settings.gridSpacingMm)
            put("showDimensions", settings.showDimensions)
            put("showClearance", settings.showClearance)
            put("showFurnitureLabels", settings.showFurnitureLabels)
            put("snapToGrid", settings.snapToGrid)
            put("liveCalculation", settings.liveCalculation)
            put("haptics", settings.haptics)
            put("animations", settings.animations)
            put("confirmDelete", settings.confirmDelete)
            put("restoreLastCalculation", settings.restoreLastCalculation)
        }
        prefs.edit().putString(KEY_SETTINGS, json.toString()).apply()
    }

    fun resetSettingsToDefaults(): AppSettings {
        val defaultSettings = AppSettings()
        saveSettings(defaultSettings)
        return defaultSettings
    }

    // ==========================================
    // History (Max 10 items)
    // ==========================================

    fun getHistory(): List<FurnitureFitHistory> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<FurnitureFitHistory>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val clearanceObj = obj.optJSONObject("clearance")
                val clearance = if (clearanceObj != null) {
                    Clearance(
                        leftMm = clearanceObj.optDouble("leftMm", 300.0),
                        rightMm = clearanceObj.optDouble("rightMm", 300.0),
                        frontMm = clearanceObj.optDouble("frontMm", 600.0),
                        backMm = clearanceObj.optDouble("backMm", 300.0)
                    )
                } else Clearance()

                list.add(
                    FurnitureFitHistory(
                        id = obj.getString("id"),
                        furnitureName = obj.optString("furnitureName", "Furniture"),
                        roomLength = obj.getDouble("roomLength"),
                        roomWidth = obj.getDouble("roomWidth"),
                        furnitureLength = obj.getDouble("furnitureLength"),
                        furnitureWidth = obj.getDouble("furnitureWidth"),
                        furnitureHeight = if (obj.has("furnitureHeight") && !obj.isNull("furnitureHeight")) obj.getDouble("furnitureHeight") else null,
                        unit = UnitType.valueOf(obj.optString("unit", UnitType.CM.name)),
                        clearance = clearance,
                        orientation = Orientation.valueOf(obj.optString("orientation", Orientation.AUTO.name)),
                        fits = obj.getBoolean("fits"),
                        createdAt = obj.getLong("createdAt")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Saves a new check to history. Enforces maximum of 10 items; drops oldest if size exceeds 10.
     */
    fun saveHistoryItem(item: FurnitureFitHistory): List<FurnitureFitHistory> {
        val current = getHistory().toMutableList()
        // Add new item at the top (newest first)
        current.add(0, item)
        // Keep only top MAX_HISTORY_ITEMS
        val trimmed = if (current.size > MAX_HISTORY_ITEMS) current.take(MAX_HISTORY_ITEMS) else current

        val array = JSONArray()
        for (h in trimmed) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("furnitureName", h.furnitureName)
                put("roomLength", h.roomLength)
                put("roomWidth", h.roomWidth)
                put("furnitureLength", h.furnitureLength)
                put("furnitureWidth", h.furnitureWidth)
                if (h.furnitureHeight != null) put("furnitureHeight", h.furnitureHeight)
                put("unit", h.unit.name)
                put("clearance", JSONObject().apply {
                    put("leftMm", h.clearance.leftMm)
                    put("rightMm", h.clearance.rightMm)
                    put("frontMm", h.clearance.frontMm)
                    put("backMm", h.clearance.backMm)
                })
                put("orientation", h.orientation.name)
                put("fits", h.fits)
                put("createdAt", h.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
        return trimmed
    }

    fun deleteHistoryItem(id: String): List<FurnitureFitHistory> {
        val current = getHistory().filterNot { it.id == id }
        val array = JSONArray()
        for (h in current) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("furnitureName", h.furnitureName)
                put("roomLength", h.roomLength)
                put("roomWidth", h.roomWidth)
                put("furnitureLength", h.furnitureLength)
                put("furnitureWidth", h.furnitureWidth)
                if (h.furnitureHeight != null) put("furnitureHeight", h.furnitureHeight)
                put("unit", h.unit.name)
                put("clearance", JSONObject().apply {
                    put("leftMm", h.clearance.leftMm)
                    put("rightMm", h.clearance.rightMm)
                    put("frontMm", h.clearance.frontMm)
                    put("backMm", h.clearance.backMm)
                })
                put("orientation", h.orientation.name)
                put("fits", h.fits)
                put("createdAt", h.createdAt)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
        return current
    }

    fun clearAllHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
