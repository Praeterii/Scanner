package preaterii.scanner

import android.text.TextUtils

fun String.isEanOrISBN(): Boolean {
    return this.length == 13 && TextUtils.isDigitsOnly(this)
}