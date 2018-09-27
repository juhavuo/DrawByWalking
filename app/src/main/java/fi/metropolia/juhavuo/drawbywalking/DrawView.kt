package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View

class DrawView(context: Context, attributeSet: AttributeSet): View(context,attributeSet){

    var bm: Bitmap? = null
    val rect = Rect(0,0,200,200)
    val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.BLACK
        canvas.drawRect(rect,paint)
    }




    fun saveToBitmap(): Bitmap{
        val bitmap = Bitmap.createBitmap(this.width,this.height,Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            background?.draw(this) ?: this.drawColor(Color.WHITE)
            draw(this)
        }

        return bitmap
    }

}