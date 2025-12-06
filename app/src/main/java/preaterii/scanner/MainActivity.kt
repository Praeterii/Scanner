package preaterii.scanner

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity() {

    override fun onResume() {
        super.onResume()
        IntentIntegrator(this)
            .setOrientationLocked(true)
            .setCaptureActivity(CustomScannerActivity::class.java)
            .setPrompt("")
            .setBeepEnabled(false)
            .initiateScan()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                finish()
            } else {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra(ResultActivity.EXTRA_SCAN_DATA, result.contents)
                this.startActivity(intent)
                finish()
            }
        }
    }
}
