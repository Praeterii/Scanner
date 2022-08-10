package preaterii.scanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {
    private val requestCode = 42
    private var initiateScan = true
    private var contents = ""

    override fun onStart() {
        super.onStart()
        if (initiateScan) {
            IntentIntegrator(this)
                .setOrientationLocked(true)
                .setPrompt("")
                .initiateScan()
            initiateScan = false
        } else {
            initiateScan = true
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra(ResultActivity.EXTRA_SCAN_DATA, contents)
            this.startActivity(intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == this.requestCode) {
            initiateScan = true
        } else {
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (result != null) {
                if (result.contents == null) {
                    finish()
                } else {
                    initiateScan = false
                    contents = result.contents
                }
            }
        }
    }
}
