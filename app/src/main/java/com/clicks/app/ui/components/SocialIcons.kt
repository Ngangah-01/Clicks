package com.clicks.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun GoogleIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        val center = Offset(w / 2f, h / 2f)
        val strokeWidth = w * 0.18f
        val radius = (w - strokeWidth) / 2f

        // Blue right bar
        drawArc(
            color = Color(0xFF4285F4),
            startAngle = -35f,
            sweepAngle = 75f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Green bottom
        drawArc(
            color = Color(0xFF34A853),
            startAngle = 40f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Yellow left
        drawArc(
            color = Color(0xFFFBBC05),
            startAngle = 140f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )
        // Red top
        drawArc(
            color = Color(0xFFEA4335),
            startAngle = 230f,
            sweepAngle = 95f,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth)
        )

        // Center blue cross bar
        drawLine(
            color = Color(0xFF4285F4),
            start = Offset(center.x, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun AppleIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()

        val path = Path().apply {
            moveTo(w * 0.72f, h * 0.38f)
            cubicTo(w * 0.72f, h * 0.26f, w * 0.82f, h * 0.20f, w * 0.83f, h * 0.19f)
            cubicTo(w * 0.77f, h * 0.10f, h * 0.67f, h * 0.09f, w * 0.64f, h * 0.09f)
            cubicTo(w * 0.52f, h * 0.08f, w * 0.43f, h * 0.15f, w * 0.37f, h * 0.15f)
            cubicTo(w * 0.31f, h * 0.15f, w * 0.23f, h * 0.09f, w * 0.14f, h * 0.09f)
            cubicTo(w * 0.02f, h * 0.09f, -0.07f, h * 0.22f, -0.07f, h * 0.39f)
            cubicTo(-0.07f, h * 0.56f, w * 0.09f, h * 0.82f, w * 0.20f, h * 0.98f)
            cubicTo(w * 0.26f, h * 1.06f, w * 0.32f, h * 1.15f, w * 0.41f, h * 1.15f)
            cubicTo(w * 0.50f, h * 1.15f, w * 0.53f, h * 1.09f, w * 0.64f, h * 1.09f)
            cubicTo(w * 0.75f, h * 1.09f, w * 0.78f, h * 1.15f, w * 0.87f, h * 1.15f)
            cubicTo(w * 0.96f, h * 1.15f, w * 1.02f, h * 1.06f, w * 1.07f, h * 0.98f)
            cubicTo(w * 1.14f, h * 0.89f, w * 1.17f, h * 0.80f, w * 1.17f, h * 0.79f)
            cubicTo(w * 1.16f, h * 0.79f, w * 0.96f, h * 0.71f, w * 0.96f, h * 0.49f)
            cubicTo(w * 0.96f, h * 0.38f, w * 1.02f, h * 0.31f, w * 1.07f, h * 0.26f)
            close()
        }

        val leafPath = Path().apply {
            moveTo(w * 0.57f, h * 0.16f)
            cubicTo(w * 0.62f, h * 0.10f, w * 0.66f, h * 0.01f, w * 0.64f, -0.07f)
            cubicTo(w * 0.56f, -0.06f, w * 0.46f, -0.01f, w * 0.41f, h * 0.05f)
            cubicTo(w * 0.36f, h * 0.11f, w * 0.31f, h * 0.20f, w * 0.33f, h * 0.28f)
            cubicTo(w * 0.42f, h * 0.29f, w * 0.51f, h * 0.23f, w * 0.57f, h * 0.16f)
            close()
        }

        drawPath(path, color = Color.White)
        drawPath(leafPath, color = Color.White)
    }
}
