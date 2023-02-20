package enigma;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.LinkedHashMap;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author AldrinSembrana
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 < PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = new HashMap<>();
        _currRotors = new LinkedHashMap<>();
        for (Rotor rotor : allRotors) {
            _allRotors.put(rotor.name(), rotor);
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        ArrayList<Rotor> currRotorArr = new
                ArrayList<Rotor>(_currRotors.values());
        return currRotorArr.get(k);
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _currRotors.clear();
        int numRotating = 0;
        int numFixed = 0;
        for (int i = 0; i < rotors.length; i++) {
            if (_allRotors.get(rotors[i]) != null) {
                _currRotors.put(rotors[i], _allRotors.get(rotors[i]));
                if (_allRotors.get(rotors[i]).rotates()) {
                    numRotating++;
                } else {
                    numFixed++;
                }
            } else {
                throw error("invalid rotor given available rotors");
            }
        }
        if ((numRotating != numPawls())
                || (numFixed != numRotors() - numPawls())) {
            throw new EnigmaException("Wrong rotor types");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            getRotor(i + 1).set(alphabet().toInt(setting.charAt(i)));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        for (int i = 0; i < _currRotors.size(); i++) {
            if (getRotor(i).rotates()) {
                if (getRotor(i) == getRotor(_currRotors.size() - 1)) {
                    getRotor(i).advance();
                } else if (getRotor(i + 1).atNotch()) {
                    getRotor(i).advance();
                } else if (getRotor(i - 1).rotates() && getRotor(i).atNotch()) {
                    getRotor(i).advance();
                }
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int currChar = c;
        for (int i = _currRotors.size() - 1; i >= 0; i--) {
            currChar = getRotor(i).convertForward(currChar);
        }
        for (int j = 1; j < _currRotors.size(); j++) {
            currChar = getRotor(j).convertBackward(currChar);
        }
        return currChar;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String encryption = "";
        for (int i = 0; i < msg.length(); i++) {
            encryption += alphabet().toChar(convert
                    (alphabet().toInt(msg.charAt(i))));
        }
        return encryption;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Number of rotors. */
    private int _numRotors;

    /** Number of pawls. */
    private int _pawls;

    /** plugboard. */
    private Permutation _plugboard;

    /** HashMap containing all rotors. */
    private HashMap<String, Rotor> _allRotors;

    /** Sorted HashMap of current rotors. */
    private LinkedHashMap<String, Rotor> _currRotors;
}
