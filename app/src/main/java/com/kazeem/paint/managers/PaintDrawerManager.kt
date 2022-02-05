package com.kazeem.paint.managers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.mutableStateOf

data class BrushProperty(
    val path: Path,
    val paint: Paint
)

class PaintDrawerManager(context: Context) : View(context) {
    var color = mutableStateOf(Color.BLACK)
    private val brushpathList = mutableListOf<BrushProperty>()
    private var path: Path? = null
    private var paintToChange: Paint? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for((index, data) in brushpathList.withIndex()) {
            canvas.drawPath(data.path, data.paint)
        }
        if (path != null && paintToChange != null) canvas.drawPath(path!!, paintToChange!!)
    }

    fun changeColor(color : Int) {
        this.color.value = color
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (path == null) path = Path()
                path!!.moveTo(event.x, event.y)
                paintToChange = Paint()
                paintToChange!!.color = color.value
                paintToChange!!.style = Paint.Style.STROKE
                paintToChange!!.strokeJoin = Paint.Join.ROUND
                paintToChange!!.strokeCap = Paint.Cap.ROUND
                paintToChange!!.strokeWidth = 10f
            }
            MotionEvent.ACTION_MOVE -> {
                if (path == null) path = Path()
                path!!.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                brushpathList.add(BrushProperty(path!!, paintToChange!!))
                path = null
                paintToChange = null
            }
        }
        return true
    }
}