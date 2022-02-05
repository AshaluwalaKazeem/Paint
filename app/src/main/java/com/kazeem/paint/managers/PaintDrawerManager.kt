package com.kazeem.paint.managers

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.mutableStateOf
import com.kazeem.paint.models.Tools

data class BrushProperty(
    val path: Path? = null,
    var pointX: Float = 0.0f,
    var pointY: Float = 0.0f,
    var startX: Float = 0.0f,
    var startY: Float = 0.0f,
    val tool: Tools,
    val paint: Paint
)

class PaintDrawerManager(context: Context) : View(context) {
    var color = mutableStateOf(Color.BLACK)
    var tool = mutableStateOf(Tools.Pencil)
    private val brushPathList = mutableListOf<BrushProperty>()
    private var path: Path? = null
    private var paintToChange: Paint? = null
    private var selectedTool: Tools? = null

    private var pointX: Float = 0.0f
    private var pointY: Float = 0.0f
    private var startX: Float = 0.0f
    private var startY: Float = 0.0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for((index, data) in brushPathList.withIndex()) {
            when(data.tool) {
                Tools.Pencil -> {
                    canvas.drawPath(data.path!!, data.paint)
                }
                Tools.Rectangle -> {
                    canvas.drawRect(data.startX, data.startY, data.pointX, data.pointY, data.paint)
                }
            }
        }
        if(selectedTool != null) {
            when(selectedTool) {
                Tools.Pencil -> {
                    if (path != null && paintToChange != null) canvas.drawPath(path!!, paintToChange!!)
                }
                Tools.Rectangle -> {
                    if (paintToChange != null) canvas.drawRect(startX, startY, pointX, pointY, paintToChange!!)
                }
            }
        }

    }

    fun changeColor(color : Int) {
        this.color.value = color
    }

    fun changeTool(tool: Tools) {
        this.tool.value = tool
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        pointX = event.x
        pointY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (path == null) path = Path()
                selectedTool = tool.value
                path!!.moveTo(event.x, event.y)
                paintToChange = Paint()
                paintToChange!!.color = color.value
                paintToChange!!.style = Paint.Style.STROKE
                paintToChange!!.strokeJoin = Paint.Join.ROUND
                paintToChange!!.strokeCap = Paint.Cap.ROUND
                paintToChange!!.strokeWidth = 10f
                startX = pointX
                startY = pointY
            }
            MotionEvent.ACTION_MOVE -> {
                if (path == null) path = Path()
                path!!.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                when(tool.value) {
                    Tools.Pencil -> {
                        brushPathList.add(BrushProperty(path = path!!, paint = paintToChange!!, tool = selectedTool!!))
                    }
                    Tools.Rectangle -> {
                        brushPathList.add(BrushProperty(pointX = pointX, pointY = pointY, startX = startX, startY = startY, paint = paintToChange!!, tool = selectedTool!!))
                    }
                }
                pointX = 0f
                pointY = 0f
                startX = 0f
                startY = 0f
                path = null
                paintToChange = null
                selectedTool = null
            }
        }
        return true
    }
}