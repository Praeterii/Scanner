package preaterii.scanner

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import preaterii.scanner.result.isEanOrISBN

@Config(manifest=Config.NONE)
@RunWith(AndroidJUnit4::class)
class StringExtensionsTest {
    @Test
    fun ean(){
        Assert.assertTrue("1234567890123".isEanOrISBN())
    }

    @Test
    fun notEan() {
        Assert.assertFalse("12345678901".isEanOrISBN())
    }
}