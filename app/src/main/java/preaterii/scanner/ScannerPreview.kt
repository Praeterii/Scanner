package preaterii.scanner

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode",
    locale = "EN"
)
@Preview(showBackground = true, device = Devices.TABLET, name = "Tablet", locale = "PL")
@Preview(showBackground = true, device = Devices.FOLDABLE, name = "Foldable", locale = "PL")
annotation class ScannerPreview
