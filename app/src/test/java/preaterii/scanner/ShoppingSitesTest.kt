package preaterii.scanner

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import java.util.Locale
import preaterii.scanner.result.getShoppingSites

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class ShoppingSitesTest {

    @Test
    fun testPolishLocale() {
        val sites = getShoppingSites("1234567890123", Locale.forLanguageTag("pl-PL"))
        assertEquals(3, sites.size)
        assertEquals("Ceneo", sites[0].name)
        assertEquals("Amazon.pl", sites[1].name)
        assertEquals("Allegro", sites[2].name)
    }

    @Test
    fun testGermanLocale() {
        val sites = getShoppingSites("1234567890123", Locale.forLanguageTag("de-DE"))
        assertEquals(1, sites.size)
        assertEquals("Amazon.de", sites[0].name)
        assertEquals("https://www.amazon.de/s?k=1234567890123", sites[0].url)
    }

    @Test
    fun testUkLocale() {
        val sites = getShoppingSites("1234567890123", Locale.forLanguageTag("en-GB"))
        assertEquals(1, sites.size)
        assertEquals("Amazon.co.uk", sites[0].name)
        assertEquals("https://www.amazon.co.uk/s?k=1234567890123", sites[0].url)
    }

    @Test
    fun testFrenchLocale() {
        val sites = getShoppingSites("1234567890123", Locale.forLanguageTag("fr-FR"))
        assertEquals(1, sites.size)
        assertEquals("Amazon.fr", sites[0].name)
    }

    @Test
    fun testUsLocaleFallback() {
        val sites = getShoppingSites("1234567890123", Locale.forLanguageTag("en-US"))
        assertEquals(1, sites.size)
        assertEquals("Amazon.com", sites[0].name)
        assertEquals("https://www.amazon.com/s?k=1234567890123", sites[0].url)
    }
}
