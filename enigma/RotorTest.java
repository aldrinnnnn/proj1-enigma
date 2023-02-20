package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

public class RotorTest {

    Alphabet mix = new Alphabet("ABCLMNXYZ123");
    Permutation perm1 = new Permutation("(ABC) (LXMYNZ)", mix);
    Permutation perm2 = new Permutation("(A2B1C3) (MZYLXN)", mix);
    Rotor testRotor = new Rotor("test", perm1);
    Rotor testRotor2 = new Rotor("test2", perm2);

    @Test
    public void testSettings() {
        assertEquals("test", testRotor.name());
        assertEquals(mix, testRotor.alphabet());
        assertEquals(perm1, testRotor.permutation());
        assertEquals(12, testRotor.size());
        assertEquals(0, testRotor.setting());
        testRotor.set(5);
        assertEquals(5, testRotor.setting());
        testRotor.set('3');
        assertEquals(11, testRotor.setting());
    }

    @Test
    public void testConverts() {
        assertEquals(1, testRotor.convertForward(0));
        assertEquals(11, testRotor.convertForward(11));
        assertEquals(0, testRotor.convertBackward(1));
        assertEquals(11, testRotor.convertBackward(11));
        assertEquals(11, testRotor2.convertForward(2));
        assertEquals(5, testRotor2.convertForward(6));
        assertEquals(2, testRotor2.convertBackward(11));
        assertEquals(6, testRotor2.convertBackward(5));
        testRotor.set(5);
        testRotor2.set(5);
        assertEquals(3, testRotor.convertForward(0));
        assertEquals(2, testRotor.convertForward(11));
        assertEquals(5, testRotor.convertForward(5));
        assertEquals(0, testRotor.convertBackward(3));
        assertEquals(11, testRotor.convertBackward(2));
        assertEquals(5, testRotor.convertBackward(5));
        assertEquals(10, testRotor2.convertForward(2));
        assertEquals(7, testRotor2.convertForward(6));
        assertEquals(2, testRotor2.convertBackward(10));
        assertEquals(6, testRotor2.convertBackward(7));
        testRotor.set(11);
        testRotor2.set(11);
        assertEquals(0, testRotor.convertForward(0));
        assertEquals(11, testRotor.convertForward(11));
        assertEquals(0, testRotor.convertBackward(0));
        assertEquals(11, testRotor.convertBackward(11));
        assertEquals(10, testRotor2.convertForward(2));
        assertEquals(5, testRotor2.convertForward(6));
        assertEquals(2, testRotor2.convertBackward(10));
        assertEquals(6, testRotor2.convertBackward(5));
    }
}
