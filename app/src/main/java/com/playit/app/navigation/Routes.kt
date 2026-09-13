package com.playit.app.navigation

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object Routes {
    const val SPLASH = "splash"
    const val PROFILE_SELECT = "profile_select"
    const val NAME_PROMPT = "name_prompt"
    const val MAP = "map"
    const val HEAR_IT = "hearit/{phonemeId}"
    const val SAY_IT = "sayit/{phonemeId}"
    const val FIND_IT = "findit/{phonemeId}"
    const val LETTER_COMPLETE = "letter_complete/{phonemeId}?heartsLost={heartsLost}"
    const val BLEND_IT = "blendit/{groupId}"
    const val BLEND_IT_COMPLETE = "blendit_complete/{groupId}?heartsLost={heartsLost}"
    const val PARENT_DASHBOARD = "parent_dashboard"
    const val REPORT_PREVIEW = "report_preview/{filePath}"

    fun hearIt(phonemeId: String) = "hearit/$phonemeId"
    fun sayIt(phonemeId: String) = "sayit/$phonemeId"
    fun findIt(phonemeId: String) = "findit/$phonemeId"
    fun letterComplete(phonemeId: String, heartsLost: Int = 0) = "letter_complete/$phonemeId?heartsLost=$heartsLost"
    fun blendIt(groupId: String) = "blendit/$groupId"
    fun blendItComplete(groupId: String, heartsLost: Int = 0) = "blendit_complete/$groupId?heartsLost=$heartsLost"
    fun reportPreview(filePath: String): String {
        val encoded = URLEncoder.encode(filePath, StandardCharsets.UTF_8.toString())
        return "report_preview/$encoded"
    }
}
