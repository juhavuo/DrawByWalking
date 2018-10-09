package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/*
    Custom view to show drawing. One can set bitmap to view so that one can load drawing from file or
    use photo as background. Drawing uses circles as shape and information of this is stored in
    DrawingPoint mutable list. From the list elements can be removed one batch at a time
 */
class DrawView(context: Context, attributeSet: AttributeSet): View(context,attributeSet){

    var bm: Bitmap? = null
    //val paint = Paint()
    var color_rgb = Color.BLACK
    private var dx: Float = 0F
    private var dy: Float = 0F
    val ratio: Double= 2*PI/360
    private var circleX: Float = 0F
    private var circleY: Float = 0F
    private var size: Float = 10F
    var timeToSetBitmap: Boolean = false
    val pointList: MutableList<DrawingPoint> = java.util.ArrayList()

    /*
        Responsible for graphics of view, draws all pointList elements and when necessery, the bitmap
        so that the loaded content can be shown
     */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if(timeToSetBitmap){
            canvas.drawBitmap(bm!!,0F,0F,null)
        }
        for(circle in pointList){
            circle.drawPoint(canvas)
        }
    }

    /*
        Needed in saving the contents of drawing view
     */
    fun saveToBitmap(): Bitmap{
        val bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            background?.draw(this) ?: this.drawColor(Color.WHITE)
            draw(this)
        }
        return bitmap
    }

    /*
        Add points to draw and obtain parameter for drawing
     */
    fun getParameters(dir: Float, totAcc: Float, batch: Int){

        dx = totAcc * cos(dir*ratio.toFloat())
        dy = totAcc * sin(dir*ratio.toFloat())
        val paint = Paint()
        paint.color = color_rgb
        circleX+=dx*10
        circleY+=dy*10
        pointList.add(DrawingPoint(circleX,circleY,size,paint,batch))
    }

    /*
        Sets locations, needed to use, when one presses view to start drawing
     */
    fun setLocation(xNew: Float, yNew: Float){
        circleX = xNew
        circleY = yNew
    }

    fun setBitmap(bitmap: Bitmap){
        bm = bitmap
        timeToSetBitmap = true
        //this.setBitmap(bitmap)
    }

    /*
        Removes the latest batch
     */
    fun removeValuesFromPointList(){
        if(pointList.size>0){
            val removed_batch = pointList[pointList.size-1].drawing_batch
            var i = pointList.size-1
            while(i>=0){
                if(pointList[i].drawing_batch == removed_batch){
                    pointList.remove(pointList[i])
                }
                i--
            }
            invalidate()
        }
    }

    fun changeColor(new_rgb: Int){
        color_rgb = new_rgb
    }

    fun changeSize(new_size: Float){
        size = new_size
    }

}