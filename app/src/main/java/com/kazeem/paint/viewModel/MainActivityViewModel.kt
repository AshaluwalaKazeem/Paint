package com.kazeem.paint.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.kazeem.paint.R
import com.kazeem.paint.Utils
import com.kazeem.paint.models.DrawingToolData
import com.kazeem.paint.models.Tools

class MainActivityViewModel(savedStateHandle: SavedStateHandle): ViewModel() {
    val drawingToolList = listOf<DrawingToolData>(
        DrawingToolData(Tools.Pencil, R.drawable.ic_pencil),
        DrawingToolData(Tools.Arrow, R.drawable.ic_baseline_arrow),
        DrawingToolData(Tools.Rectangle, R.drawable.ic_rectangle),
        DrawingToolData(Tools.Ellipse, R.drawable.ic_ellipse),
        DrawingToolData(Tools.ColorPalette, R.drawable.ic_color_palette),
    )
    val selectedDrawingTool = mutableStateOf(savedStateHandle.get(Utils.selectedDrawingToolKey) ?: Tools.Pencil)
    val toolColor = mutableStateOf(savedStateHandle.get(Utils.selectedColor) ?: Color.Black)

    fun setToolColor(color: Color) {
        toolColor.value = color
    }
}