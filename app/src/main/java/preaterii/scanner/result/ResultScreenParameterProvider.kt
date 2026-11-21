package preaterii.scanner.result

import androidx.compose.ui.tooling.preview.PreviewParameterProvider

class ResultScreenParameterProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf(
        "5901588016443", // Barcode (EAN-13)
        "https://developer.android.com/" // QR Code
    )
}