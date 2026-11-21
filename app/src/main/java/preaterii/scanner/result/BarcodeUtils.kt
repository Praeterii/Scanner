package preaterii.scanner.result

import android.graphics.Bitmap
import android.graphics.Color
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.core.text.isDigitsOnly
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException

internal fun generateBarcodeBitmap(code: String): Bitmap? {
    val multiFormatWriter = MultiFormatWriter()
    return try {
        val type = if (code.isEanOrISBN()) {
            BarcodeFormat.EAN_13
        } else {
            BarcodeFormat.QR_CODE
        }
        val bitMatrix = multiFormatWriter.encode(code, type, 600, 300)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp[x, y] =
                    if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        bmp
    } catch (e: WriterException) {
        e.printStackTrace()
        null
    }
}

internal fun String.isEanOrISBN(): Boolean {
    return this.length == 13 && this.isDigitsOnly()
}

internal fun String.isUrl(): Boolean {
    return android.util.Patterns.WEB_URL.matcher(this).matches() ||
           this.startsWith("http://", true) ||
           this.startsWith("https://", true) ||
           this.startsWith("www.", true)
}
