@file:Suppress("RestrictedApi")
package com.example.aedkereso

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider

class AedWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WidgetContent(context)
        }
    }
}

@Composable
private fun WidgetContent(context: Context) {
    // Elkészítjük a pontos indító parancsot (Intentet) a MainActivity megnyitásához
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(androidx.compose.ui.graphics.Color(0xFFFFEB3B))) // Sárga háttér
            .padding(12.dp)
            .clickable(actionStartActivity(intent)), // Átadjuk a kész Intentet a Glance-nek
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "⚡", // Nemzetközi villám jelzés
                style = TextStyle(fontSize = 28.sp)
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "AED SOS",
                style = TextStyle(
                    color = ColorProvider(androidx.compose.ui.graphics.Color(0xFF000000)), // Fekete szöveg
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}