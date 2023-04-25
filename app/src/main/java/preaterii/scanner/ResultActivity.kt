package preaterii.scanner

import android.content.Intent
import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder

class ResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SCAN_DATA = "scanned"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contents = intent?.extras?.getString(EXTRA_SCAN_DATA)
        if (contents != null) {
            setContentView(R.layout.activity_result)
            addBarcodeText(findViewById(R.id.barcodeTV), contents)
            addBarcodeImage(findViewById(R.id.codeIV), contents)
            addCeneoLink(findViewById(R.id.ceneoTV), contents)
            findViewById<View>(R.id.fab).setOnClickListener {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, contents)
                    type = "text/plain"
                }

                val shareIntent = Intent.createChooser(sendIntent, null)
                startActivity(shareIntent)
            }
        } else {
            finish()
        }
    }

    private fun addBarcodeImage(imageView: ImageView, code: String) {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val type = if (code.isEanOrISBN()) {
                BarcodeFormat.EAN_13
            } else {
                BarcodeFormat.QR_CODE
            }
            val bitMatrix = multiFormatWriter.encode(code, type, 200, 200)  // TODO 200 dp
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            imageView.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    private fun addBarcodeText(textView: TextView, contents: String) {
        textView.text = contents
        Linkify.addLinks(textView, Linkify.WEB_URLS)
    }

    private fun addCeneoLink(textView: TextView, contents: String) {
        if (contents.isEanOrISBN()) {
            textView.append("\n")
            textView.append("https://www.ceneo.pl/szukaj-$contents")
            Linkify.addLinks(textView, Linkify.WEB_URLS)
        } else {
            textView.visibility = View.GONE
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finish()
    }
}