package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DrawView(context: Context, attributeSet: AttributeSet): View(context,attributeSet){

    var bm: Bitmap? = null
    val paint = Paint()
    var direction: Float = 0F
    var totalAcceleration: Float = 0F
    //var size: Float = 10F
    var circleX: Float = 0F
    var circleY: Float = 0F
    var firstDraw: Boolean = true
    val pointList: MutableList<DrawingPoint> = ArrayList()





    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(firstDraw){
            circleX = width.toFloat()/2
            circleY = height.toFloat()/2
            firstDraw = false
        }
        for(circle in pointList){
            circle.drawPoint(canvas)
        }
        //canvas.drawCircle(circleX,circleY,size,paint)
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
        direction = dir
        totalAcceleration = totAcc
    }

    fun changeCoords(){
        circleX += 2F
        circleY += 2F
        paint.color = Color.BLACK
        pointList.add(DrawingPoint(circleX,circleY,paint))
    }

}