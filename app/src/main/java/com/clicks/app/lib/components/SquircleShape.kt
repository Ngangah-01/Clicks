package com.clicks.app.lib.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp


class SquircleShape(
    private val radius: Dp = 24.dp,
    private val cornerSmoothing: Float = 0.6f
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { radius.toPx() }
            .coerceIn(0f, minOf(size.width, size.height) / 2f)
        return Outline.Generic(squirclePath(size, r, cornerSmoothing.coerceIn(0f, 1f)))
    }
}

private fun squirclePath(size: Size, radius: Float, smoothing: Float): Path {
    val w = size.width
    val h = size.height
    val curve = (radius * (1f + smoothing))
        .coerceAtMost(minOf(w, h) / 2f)
    val handle = radius * (1f - smoothing * 0.6f)

    return Path().apply {
        moveTo(curve, 0f)
        lineTo(w - curve, 0f)
        cubicTo(w - curve + handle, 0f, w, curve - handle, w, curve)
        lineTo(w, h - curve)
        cubicTo(w, h - curve + handle, w - curve + handle, h, w - curve, h)
        lineTo(curve, h)
        cubicTo(curve - handle, h, 0f, h - curve + handle, 0f, h - curve)
        lineTo(0f, curve)
        cubicTo(0f, curve - handle, curve - handle, 0f, curve, 0f)
        close()
    }
}