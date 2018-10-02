package fi.metropolia.juhavuo.drawbywalking

import android.graphics.Canvas
import android.graphics.Paint

class DrawingPoint(x: Float, y: Float, p: Paint, batch: Int){

    var xPoint: Float = x
    var yPoint: Float = y
    var paint: Paint = p
    val radius: Float = 10F
    val drawing_batch: Int = batch

    fun drawPoint(canvas: Canvas){
        canvas.drawCircle(xPoint,yPoint,radius,paint)
    }
}