package fi.metropolia.juhavuo.drawbywalking

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class DrawView(context: Context, attributeSet: AttributeSet, bitmap: Bitmap): View(context,attributeSet){

    val bm: Bitmap = bitmap

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(bm,0f,0f,null)
    }

}