package com.soorya.wealthmanager.widget

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.*
import androidx.glance.*
import androidx.glance.action.*
import androidx.glance.appwidget.*
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.layout.*
import androidx.glance.text.*
import com.soorya.wealthmanager.MainActivity

class WealthWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            WealthWidgetContent()
        }
    }

    @Composable
    private fun WealthWidgetContent() {
        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(Color(0xFF12121A))
                .appWidgetBackground()
                .cornerRadius(20.dp)
                .clickable(actionStartActivity<MainActivity>())
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "WEALTH",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFD4AF37)),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Open App",
                style = TextStyle(
                    color = ColorProvider(Color(0xFFF0F0F0)),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Tap to view balance",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF9090A0)),
                    fontSize = 10.sp
                )
            )
        }
    }
}
