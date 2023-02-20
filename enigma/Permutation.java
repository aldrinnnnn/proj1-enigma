package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author AldrinSembrana
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        while (_cycles.contains(")(")) {
            int err = _cycles.indexOf(")(");
            _cycles = _cycles.substring(0, err + 1) + " "
                    + _cycles.substring(err + 1);
        }
        _perms = new char[_alphabet.size()];
        _cycleGroups = _cycles.split("\\s");
        for (int i = 0; i < _cycleGroups.length; i++) {
            int x = _cycleGroups[i].length();
            if (x > 0 && _cycleGroups[i].charAt(x - 1) != ')') {
                System.out.println(_cycleGroups[i]);
                throw error("no ending parenthesis");
            }
        }
        int k;
        for (int i = 0; i < _perms.length; i++) {
            for (int j = 0; j < _cycleGroups.length; j++) {
                if (_cycleGroups[j].contains("" + _alphabet.toChar(i))) {
                    k = _cycleGroups[j].indexOf("" + _alphabet.toChar(i));
                    if (_cycleGroups[j].charAt(k + 1) == ')') {
                        k = 0;
                    }
                    _perms[i] = _cycleGroups[j].charAt(k + 1);
                    break;
                }
            }
            if (_perms[i] == '\0') {
                _perms[i] = _alphabet.toChar(i);
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        _cycles = _cycles.concat(" " + cycle);
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char ch = _alphabet.toChar(wrap(p));
        return _alphabet.toInt(permute(ch));
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char ch = _alphabet.toChar(wrap(c));
        return _alphabet.toInt(invert(ch));
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _perms[_alphabet.toInt(p)];
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int counter = 0;
        while (counter < _perms.length) {
            if (_perms[counter] == c) {
                break;
            }
            counter++;
        }
        return _alphabet.toChar(counter);
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            if (_alphabet.toChar(i) == _perms[i]) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String _cycles;

    /** Char array of permutations. */
    private char[] _perms;

    /** String array of the cycles. */
    private String[] _cycleGroups;
}
