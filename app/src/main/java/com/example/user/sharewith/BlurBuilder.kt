package com.example.user.sharewith

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.*

/**
 * Created by User on 08-02-2021.
 */
class BlurBuilder {
    companion object {
        val BITMAP_SCALE: Float = 0.4f
        val BLUR_RADIUS: Float = 7.5f


        fun blur(context: Context, image: Bitmap): Bitmap {

            var width: Int = Math.round(image.getWidth() * BlurBuilder.BITMAP_SCALE);
            var height: Int = Math.round(image.getHeight() * BlurBuilder.BITMAP_SCALE);


            var inputBitmap: Bitmap = Bitmap.createScaledBitmap(image, width, height, false);
            var outputBitmap: Bitmap = Bitmap.createBitmap(inputBitmap);


            var rs: RenderScript = RenderScript.create(context);
            var theIntrinsic: ScriptIntrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            var tmpIn: Allocation = Allocation.createFromBitmap(rs, inputBitmap);
            var tmpOut: Allocation = Allocation.createFromBitmap(rs, outputBitmap);


            theIntrinsic.setRadius(BlurBuilder.BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap
        }
    }
}