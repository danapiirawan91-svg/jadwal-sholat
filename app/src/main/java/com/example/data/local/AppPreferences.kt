package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

object AppPreferences {
    private const val PREFS_NAME = "panduan_islam_prefs"
    private const val KEY_SELECTED_TAB = "key_selected_tab"
    private const val KEY_TASBIH_COUNT = "key_tasbih_count"
    private const val KEY_TASBIH_TARGET = "key_tasbih_target"
    private const val KEY_CALC_METHOD = "key_calc_method"
    private const val KEY_LOGGED_IN_USER_ID = "key_logged_in_user_id"
    private const val KEY_GUIDE_CATEGORY = "key_guide_category"
    private const val KEY_COMMUNITY_CATEGORY = "key_community_category"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSelectedTab(context: Context): Int {
        return getPrefs(context).getInt(KEY_SELECTED_TAB, 0)
    }

    fun setSelectedTab(context: Context, tabIndex: Int) {
        getPrefs(context).edit().putInt(KEY_SELECTED_TAB, tabIndex).apply()
    }

    fun getTasbihCount(context: Context): Int {
        return getPrefs(context).getInt(KEY_TASBIH_COUNT, 0)
    }

    fun setTasbihCount(context: Context, count: Int) {
        getPrefs(context).edit().putInt(KEY_TASBIH_COUNT, count).apply()
    }

    fun getTasbihTarget(context: Context): Int {
        return getPrefs(context).getInt(KEY_TASBIH_TARGET, 33)
    }

    fun setTasbihTarget(context: Context, target: Int) {
        getPrefs(context).edit().putInt(KEY_TASBIH_TARGET, target).apply()
    }

    fun getCalcMethod(context: Context): String {
        return getPrefs(context).getString(KEY_CALC_METHOD, "KEMENAG") ?: "KEMENAG"
    }

    fun setCalcMethod(context: Context, method: String) {
        getPrefs(context).edit().putString(KEY_CALC_METHOD, method).apply()
    }

    fun getLoggedInUserId(context: Context): Long {
        return getPrefs(context).getLong(KEY_LOGGED_IN_USER_ID, -1L)
    }

    fun setLoggedInUserId(context: Context, userId: Long) {
        getPrefs(context).edit().putLong(KEY_LOGGED_IN_USER_ID, userId).apply()
    }

    fun getGuideCategory(context: Context): String {
        return getPrefs(context).getString(KEY_GUIDE_CATEGORY, "Semua") ?: "Semua"
    }

    fun setGuideCategory(context: Context, category: String) {
        getPrefs(context).edit().putString(KEY_GUIDE_CATEGORY, category).apply()
    }

    fun getCommunityCategory(context: Context): String {
        return getPrefs(context).getString(KEY_COMMUNITY_CATEGORY, "Semua") ?: "Semua"
    }

    fun setCommunityCategory(context: Context, category: String) {
        getPrefs(context).edit().putString(KEY_COMMUNITY_CATEGORY, category).apply()
    }
}
