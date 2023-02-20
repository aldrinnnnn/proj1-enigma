package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

/** The suite of all JUnit tests for the Alphabet class.
 *  @AldrinSembrana
 */
public class AlphabetTest {
    /* ***** TESTS ***** */

    @Test
    public void testSize() {
        Alphabet all = new Alphabet();
        Alphabet abc = new Alphabet("ABC");
        Alphabet half = new Alphabet("ABCDEFGHIJKLM");
        assertEquals(26, all.size());
        assertEquals(3, abc.size());
        assertEquals(13, half.size());
    }

    @Test
    public void testContains() {
        Alphabet all = new Alphabet();
        Alphabet xyz = new Alphabet("XYZ");
        assertTrue(all.contains('Q'));
        assertTrue(xyz.contains('Z'));
        assertFalse(xyz.contains('A'));
    }

    @Test
    public void testToChar() {
        Alphabet normal = new Alphabet();
        Alphabet reverse = new Alphabet("ZYXWVUTSRQPONMLKJIHGFEDCBA");
        assertEquals('A', normal.toChar(0));
        assertEquals('Z', normal.toChar(25));
        assertEquals('Z', reverse.toChar(0));
        assertNotEquals('Z', reverse.toChar(25));
    }

    @Test
    public void testToInt() {
        Alphabet normal = new Alphabet();
        Alphabet reverse = new Alphabet("ZYXWVUTSRQPONMLKJIHGFEDCBA");
        assertEquals(0, normal.toInt('A'));
        assertEquals(25, normal.toInt('Z'));
        assertEquals(0, reverse.toInt('Z'));
        assertNotEquals(25, reverse.toInt('Z'));
    }
}
