package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @AldrinSembrana
 */
public class PermutationTest {

    public Permutation getNewPermutation(String cycles, Alphabet alphabet) {
        Permutation result = new Permutation(cycles, alphabet);
        return result;
    }

    public Alphabet getNewAlphabet(String chars) {
        Alphabet result = new Alphabet(chars);
        return result;
    }

    public Alphabet getNewAlphabet() {
        return new Alphabet();
    }
    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testPermutationAll() {
        Alphabet mix = getNewAlphabet("ABCLMNXYZ123");
        Permutation perm1 = getNewPermutation("(ABC) (LXMYNZ)", mix);
        Permutation perm2 = getNewPermutation("(ABC) (LXMYNZ) (321)", mix);
        Permutation perm3 = new Permutation("(A2B1C3) (MZYLXN)", mix);
        assertEquals(12, perm1.size());
        assertEquals(mix, perm1.alphabet());
        assertTrue(perm2.derangement());
        assertFalse(perm1.derangement());
        assertEquals(1, perm1.permute(0));
        assertEquals(7, perm1.permute(4));
        assertEquals(9, perm1.permute(9));
        assertEquals(6, perm1.permute(15));
        assertEquals(0, perm1.permute(2));
        assertEquals(3, perm3.permute(7));
        assertEquals(0, perm1.invert(1));
        assertEquals(4, perm1.invert(7));
        assertEquals(9, perm1.invert(9));
        assertEquals(0, perm1.invert(13));
        assertEquals(2, perm1.invert(0));
        assertEquals('B', perm1.permute('A'));
        assertEquals('Y', perm1.permute('M'));
        assertEquals('1', perm1.permute('1'));
        assertEquals('A', perm1.permute('C'));
        assertEquals('A', perm1.invert('B'));
        assertEquals('M', perm1.invert('Y'));
        assertEquals('1', perm1.invert('1'));
        assertEquals('C', perm1.invert('A'));
    }
}
