package fi.metropolia.juhavuo.drawbywalking

import android.graphics.Canvas
import android.graphics.Paint

/*
    Drawing point is used to store information about point in path drawn by movement
    Parameters needed are coordinates x and y, size of pen, drawing color information an Paint object
    and batch number to keep track, what points are removed, when undo button is pressed
 */
class DrawingPoint(x: Float, y: Float,size: Float, p: Paint, batch: Int){

    private var xPoint: Float = x
    private var yPoint: Float = y
    private var paint: Paint = p
    private val radius: Float = size
    val drawing_batch: Int = batch

    fun drawPoint(canvas: Canvas){
        canvas.drawCircle(xPoint,yPoint,radius,paint)
    }
}