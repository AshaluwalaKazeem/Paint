package com.kazeem.paint.managers

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.compose.runtime.mutableStateOf
import com.kazeem.paint.models.Tools
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class BrushProperty(
    val path: Path? = null,
    var x2: Float = 0.0f,
    var y2: Float = 0.0f,
    var x1: Float = 0.0f,
    var y1: Float = 0.0f,
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
    private val phi = 0.718
    private val arrowLength = 50

    private var x2: Float = 0.0f
    private var y2: Float = 0.0f
    private var x1: Float = 0.0f
    private var y1: Float = 0.0f

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for((index, data) in brushPathList.withIndex()) {
            when(data.tool) {
                Tools.Pencil -> {
                    canvas.drawPath(data.path!!, data.paint)
                }
                Tools.Rectangle -> {
                    canvas.drawRect(data.x1, data.y1, data.x2, data.y2, data.paint)
                }
                Tools.Ellipse -> {
                    canvas.drawOval(data.x1, data.y1, data.x2, data.y2, data.paint)
                }
                Tools.Arrow -> {
                    canvas.drawLine(data.x1, data.y1, data.x2, data.y2, data.paint)
                    drawArrowHead(canvas, Point(data.x2.toInt(), data.y2.toInt()), Point(data.x1.toInt(), data.y1.toInt()), data.paint)
                }
            }
        }
        if(selectedTool != null) {
            when(selectedTool) {
                Tools.Pencil -> {
                    if (path != null && paintToChange != null) canvas.drawPath(path!!, paintToChange!!)
                }
                Tools.Rectangle -> {
                    if (paintToChange != null) canvas.drawRect(x1, y1, x2, y2, paintToChange!!)
                }
                Tools.Ellipse -> {
                    if (paintToChange != null) canvas.drawOval(x1, y1, x2, y2, paintToChange!!)
                }
                Tools.Arrow -> {
                    canvas.drawLine(x1, y1, x2, y2, paintToChange!!)
                    drawArrowHead(canvas, Point(x2.toInt(), y2.toInt()), Point(x1.toInt(), y1.toInt()), paintToChange!!)
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
        x2 = event.x
        y2 = event.y
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
                x1 = x2
                y1 = y2
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
                    Tools.Rectangle, Tools.Ellipse, Tools.Arrow -> {
                        brushPathList.add(BrushProperty(x2 = x2, y2 = y2, x1 = x1, y1 = y1, paint = paintToChange!!, tool = selectedTool!!))
                    }
                }
                x2 = 0f
                y2 = 0f
                x1 = 0f
                y1 = 0f
                path = null
                paintToChange = null
                selectedTool = null
            }
        }
        return true
    }



    private fun drawArrowHead(canvas: Canvas, tip: Point, tail: Point, paint: Paint) {
        val dy: Double = (tip.y - tail.y).toDouble()
        val dx: Double = (tip.x - tail.x).toDouble()
        val theta = atan2(dy, dx)
        var tempX: Int = tip.x
        var tempY: Int = tip.y
        //make arrow touch the circle
        if (tip.x > tail.x && tip.y == tail.y) {
            tempX = tip.x - 10
        } else if (tip.x < tail.x && tip.y == tail.y) {
            tempX = tip.x + 10
        } else if (tip.y > tail.y && tip.x == tail.x) {
            tempY = tip.y - 10
        } else if (tip.y < tail.y && tip.x == tail.x) {
            tempY = tip.y + 10
        } else if (tip.x > tail.x || tip.x < tail.x) {
            val rCosTheta = (10 * cos(theta)).toInt()
            val xx: Int = tip.x - rCosTheta
            val yy = ((xx - tip.x) * (dy / dx) + tip.y).toInt()
            tempX = xx
            tempY = yy
        }
        var x: Double
        var y: Double
        var rho: Double = theta + phi
        for (j in 0..1) {
            x = tempX - arrowLength * cos(rho)
            y = tempY - arrowLength * sin(rho)
            canvas.drawLine(
                tempX.toFloat(),
                tempY.toFloat(),
                x.toFloat(),
                y.toFloat(),
                paint
            )
            rho = theta - phi
        }
    }
}