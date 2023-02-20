package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author AldrinSembrana
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                    new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                        + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
     *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        boolean set = false;
        Machine mach = readConfig();
        while (_input.hasNextLine()) {
            String lineTracker = _input.nextLine();
            if (lineTracker.startsWith("*")) {
                setUp(mach, lineTracker);
                set = true;
            } else {
                if (!set) {
                    throw new EnigmaException("NO SETTING");
                }
                String lineNoSpaces = lineTracker.replaceAll("\\s", "");
                printMessageLine(mach.convert(lineNoSpaces));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            _alphabet = new Alphabet(_config.next());
            int rotors = _config.nextInt();
            int pawls = _config.nextInt();
            ArrayList<Rotor> allRotors = new ArrayList<>();
            _storeInfo = _config.next();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, rotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            _rotorName = _storeInfo;
            String permCycles = "";
            String rotorInfo = _config.next();
            _rotorType = rotorInfo.substring(0, 1);
            if (_rotorType.equals("M")) {
                _rotorNotch = rotorInfo.substring(1);
            }
            _storeInfo = _config.next();
            while (_storeInfo.charAt(0) == '(' && _config.hasNext()) {
                permCycles += _storeInfo + " ";
                _storeInfo = _config.next();
            }
            if (!_config.hasNext()) {
                permCycles += _storeInfo;
            }
            Permutation permResult = new Permutation(permCycles, _alphabet);
            if (_rotorType.equals("M")) {
                return new MovingRotor(_rotorName, permResult, _rotorNotch);
            } else if (_rotorType.equals("R")) {
                return new Reflector(_rotorName, permResult);
            } else {
                return new FixedRotor(_rotorName, permResult);
            }
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] settingsArray = settings.split("\\s");
        String[] rotorArray = new String[M.numRotors()];
        String permString = "";
        for (int i = 1; i <= M.numRotors(); i++) {
            rotorArray[i - 1] = settingsArray[i];
        }
        M.insertRotors(rotorArray);
        M.setRotors(settingsArray[M.numRotors() + 1]);
        if (M.numRotors() + 2 == settingsArray.length) {
            Permutation plug = new Permutation("", M.alphabet());
            M.setPlugboard(plug);
        } else {
            for (int i = M.numRotors() + 2; i < settingsArray.length; i++) {
                permString += settingsArray[i] + " ";
            }
            Permutation plug = new Permutation(permString.substring
                    (0, permString.length() - 1), M.alphabet());
            M.setPlugboard(plug);
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String result = "";
        String msgCopy = msg;
        for (int i = 0; i < (msg.length() / 5); i++) {
            result += msgCopy.substring(0, 5) + " ";
            msgCopy = msgCopy.substring(5);
        }
        if (msgCopy.length() > 0) {
            result += msgCopy;
            _output.println(result);
        } else {
            _output.println(result);
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;

    /** names of rotors. */
    private String _rotorName;

    /** type of rotor. */
    private String _rotorType;

    /** a rotor's notches. */
    private String _rotorNotch;

    /** means of accessing a variable across methods. */
    private String _storeInfo;
}
