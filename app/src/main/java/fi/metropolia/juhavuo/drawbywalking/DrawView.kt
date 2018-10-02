package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class DrawView(context: Context, attributeSet: AttributeSet): View(context,attributeSet){

    var bm: Bitmap? = null
    val paint = Paint()
    var dx: Float = 0F
    var dy: Float = 0F
    val ratio: Double= 2*PI/360
    var circleX: Float = 0F
    var circleY: Float = 0F
    var timeToSetBitmap: Boolean = false
    val pointList: MutableList<DrawingPoint> = java.util.ArrayList()


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(timeToSetBitmap){
            canvas.drawBitmap(bm!!,0F,0F,null)
        }
        for(circle in pointList){
            circle.drawPoint(canvas)
        }
    }

    fun saveToBitmap(): Bitmap{
        val bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            background?.draw(this) ?: this.drawColor(Color.WHITE)
            draw(this)
        }

        return bitmap
    }

    fun getParameters(dir: Float, totAcc: Float){

        dx = totAcc * cos(dir*ratio.toFloat())
        dy = totAcc * sin(dir*ratio.toFloat())
        paint.color = Color.BLACK
        circleX+=dx*10
        circleY+=dy*10
        pointList.add(DrawingPoint(circleX,circleY,paint))
    }

    fun setLocation(xNew: Float, yNew: Float){
        circleX = xNew
        circleY = yNew
    }

    fun setBitmap(bitmap: Bitmap){
        bm = bitmap
        timeToSetBitmap = true
        //this.setBitmap(bitmap)
    }

}