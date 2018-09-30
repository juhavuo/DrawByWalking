package fi.metropolia.juhavuo.drawbywalking

import android.graphics.Canvas
import android.graphics.Paint

class DrawingPoint(x: Float, y: Float, p: Paint){

    var xPoint: Float = x
    var yPoint: Float = y
    var paint: Paint = p
    val radius: Float = 10F

    fun drawPoint(canvas: Canvas){
        canvas.drawCircle(xPoint,yPoint,radius,paint)
    }
}