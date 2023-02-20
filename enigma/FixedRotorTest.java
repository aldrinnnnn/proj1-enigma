package enigma;

import org.junit.Test;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

public class FixedRotorTest {

    Alphabet mix = new Alphabet("ABCLMNXYZ123");
    Permutation perm = new Permutation("(ABC) (LXMYNZ)", mix);

    @Test
    public void testFixedRotor() {
        FixedRotor test = new FixedRotor("test", perm);
        assertEquals(0, test.setting());
        test.set(5);
        assertEquals(5, test.setting());
    }

    @Test
    public void testReflector() {
        Reflector test = new Reflector("test", perm);
        assertEquals(1, test.convertForward(0));
        assertEquals(test.convertBackward(0), test.convertForward(0));
    }
}
