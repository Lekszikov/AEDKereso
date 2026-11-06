package com.example.aedkereso.data

import android.content.Context
import com.example.aedkereso.AedApiService
import com.example.aedkereso.AedElement
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class AedRepository(
    private val context: Context,
    private val apiService: AedApiService
) {
    suspend fun getDefibrillators(): List<AedElement> {
        return try {
            // Beolvassuk a helyi assets mappából a defibrillators.json fájlt
            val inputStream = context.assets.open("defibrillators.json")
            val reader = InputStreamReader(inputStream)
            val itemType = object : TypeToken<List<AedElement>>() {}.type
            val result: List<AedElement> = Gson().fromJson(reader, itemType)
            reader.close()
            result
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}