package com.example.aedkereso

import com.google.gson.annotations.SerializedName
import java.util.Locale

data class AedElement(
    @SerializedName("id") val id: Long,
    @SerializedName("telepules") val telepules: String?,
    @SerializedName("helyszin") val helyszin: String?,
    @SerializedName("cim") val cim: String?,
    @SerializedName("lat") val lat: Double?,
    @SerializedName("lng") val lng: Double?,
    @SerializedName("elerhetoseg") val elerhetoseg: String?
) {
    val name: String
        get() = helyszin ?: "Ismeretlen életmentő pont"

    val locationDetails: String
        get() = "${telepules ?: ""}, ${cim ?: ""}".trim().trimStart(',')

    val operatorName: String
        get() = elerhetoseg ?: "Nincs információ"
}

data class AedUiItem(
    val element: AedElement,
    val distanceInMeters: Float? = null
) {
    val formattedDistance: String
        get() {
            val dist = distanceInMeters ?: return "Távolság: --"
            return if (dist < 1000) {
                "${dist.toInt()} m"
            } else {
                String.format(Locale.getDefault(), "%.1f km", dist / 1000f)
            }
        }
}