import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class EncryptVIC {

    public static int ID_LEN = 5;   // # of chars in agent ID
    public static int DATE_LEN = 6;   // # of chars in date
    public static int PHRASE_LEN = 10;  // # letters of phrase to use
    public static int ANAGRAM_LEN = 10;  // # of chars in anagram
    public static int ANAGRAM_LETTERS = 8;   // # of letters in anagram

    public static char SPACE = (char) 32; // space is ASCII 32
    public static boolean DEBUG = false;    // toggle debug prints

    static class VICData {
        public String agentID;
        public String date;
        public String phrase;
        public String phraseOriginal;
        public String anagram;
        public String message;
        public String messageOriginal;
    } // class VICData

       /*---------------------------------------------------------------------
        |  Method readVICData (pathName)
        |
        |  Purpose:  VIC requires five pieces of information for an
        |            encoding to be performed:  (1) agent ID (form: #####),
        |            (2) date (form:YYMMDD), (3) a phrase containing at least
        |            10 letters, (4) an anagram of 8 commonly-used letters
        |            and 2 spaces, and (5) a message of at least one letter
        |            to be encoded.  This method reads these pieces from the
        |            given filename (or path + filename), one per line, and
        |            all pieces are sanity-checked.  When reasonable,
        |            illegal characters are dropped.  The program is halted
        |            if an unrecoverable problem with the data is discovered.
        |
        |  Pre-condition:  None.  (We even check to see if the file exists.)
        |
        |  Post-condition:  The returned VICData object's fields are populated
        |            with legal data.
        |
        |  Parameters:  pathName -- The filename or path/filename of the text
        |            file containing the file pieces of data.  If only a
        |            file name is provided, it is assumed that the file
        |            is located in the same directory as the executable.
        |
        |  Returns:  A reference to an object of class VICData that contains
        |            the five pieces of data.
        *-------------------------------------------------------------------*/

    public static VICData readVICData(String pathName) {
        VICData vic = new VICData(); // Object to hold the VIC input data
        Scanner inFile = null;       // Scanner file object reference

        try {
            inFile = new Scanner(new File(pathName));
        } catch (Exception e) {
            System.out.println("File does not exist: " + pathName + "!\n");
            System.exit(1);
        }

        // Read and sanity-check agent ID.  Needs to be ID_LEN long
        // and numeric.

        if (inFile.hasNext()) {
            vic.agentID = inFile.nextLine();
        } else {
            System.out.println("ERROR:  Agent ID not found!\n");
            System.exit(1);
        }

        if (vic.agentID.length() != ID_LEN) {
            System.out.printf("ERROR:  Agent ID length is %d, must be %d!\n",
                    vic.agentID.length(), ID_LEN);
            System.exit(1);
        }

        try {
            long idValue = Long.parseLong(vic.agentID);
        } catch (NumberFormatException e) {
            System.out.println("Agent ID `" + vic.agentID
                    + "contains non-numeric characters!\n");
            System.exit(1);
        }

        // Read and sanity-check date.  Needs to be DATE_LEN long
        // and numeric.

        if (inFile.hasNext()) {
            vic.date = inFile.nextLine();
        } else {
            System.out.println("ERROR:  Date not found!\n");
            System.exit(1);
        }

        if (vic.date.length() != DATE_LEN) {
            System.out.printf("ERROR:  Date length is %d, must be %d!\n",
                    vic.date.length(), DATE_LEN);
            System.exit(1);
        }

        try {
            long dateValue = Long.parseLong(vic.date);
        } catch (NumberFormatException e) {
            System.out.println("Date `" + vic.date
                    + "contains non-numeric characters!\n");
            System.exit(1);
        }

        // Read and sanity-check phrase.  After removing non-letters,
        // at least PHRASE_LEN letters must remain.

        if (inFile.hasNext()) {
            vic.phraseOriginal = inFile.nextLine();
            StringBuffer sb = new StringBuffer(vic.phraseOriginal);
            for (int i = 0; i < sb.length(); i++) {
                if (!Character.isLetter(sb.charAt(i))) {
                    sb.deleteCharAt(i);
                    i--;  // Don't advance to next index o.w. we miss a char
                }
            }
            vic.phrase = sb.toString().toUpperCase();
            if (vic.phrase.length() >= PHRASE_LEN) {
                vic.phrase = vic.phrase.substring(0, PHRASE_LEN);
            }
        } else {
            System.out.println("ERROR:  Phrase not found!\n");
            System.exit(1);
        }

        if (vic.phrase.length() != PHRASE_LEN) {
            System.out.printf("ERROR:  Phrase contains %d letter(s), "
                            + "must have at least %d!\n",
                    vic.phrase.length(), PHRASE_LEN);
            System.exit(1);
        }

        // Read and sanity-check anagram.  Must be ANAGRAM_LEN long,
        // and contain ANAGRAM_LETTERS letters and the rest spaces.

        if (inFile.hasNext()) {
            vic.anagram = inFile.nextLine().toUpperCase();
        } else {
            System.out.println("ERROR:  Anagram not found!\n");
            System.exit(1);
        }

        if (vic.anagram.length() != ANAGRAM_LEN) {
            System.out.printf("ERROR:  Anagram length is %d, must be %d!\n",
                    vic.anagram.length(), ANAGRAM_LEN);
            System.exit(1);
        }

        for (int i = 0; i < vic.anagram.length(); i++) {
            if (!Character.isLetter(vic.anagram.charAt(i))
                    && vic.anagram.charAt(i) != SPACE) {
                System.out.printf("ERROR:  Anagram contains character `%c'.\n",
                        vic.anagram.charAt(i));
                System.exit(1);
            }
        }

        int numLetters = 0;
        for (int i = 0; i < vic.anagram.length(); i++) {
            if (Character.isLetter(vic.anagram.charAt(i))) {
                numLetters++;
            }
        }
        if (numLetters != ANAGRAM_LETTERS) {
            System.out.printf("ERROR:  Anagram contains %d letters, "
                            + "should have %d plus %d spaces.\n",
                    numLetters, ANAGRAM_LETTERS,
                    ANAGRAM_LEN - ANAGRAM_LETTERS);
            System.exit(1);
        }

        // Read and sanity-check message.  After removing non-letters
        // and capitalizing, at least one letter must remain.

        if (inFile.hasNext()) {
            vic.messageOriginal = inFile.nextLine();
            StringBuffer sb = new StringBuffer(vic.messageOriginal);
            for (int i = 0; i < sb.length(); i++) {
                if (!Character.isLetter(sb.charAt(i))) {
                    sb.deleteCharAt(i);
                    i--;  // Don't advance to next index o.w. we miss a char
                }
            }
            vic.message = sb.toString().toUpperCase();
        } else {
            System.out.println("ERROR:  Message not found!\n");
            System.exit(1);
        }

        if (vic.message.length() == 0) {
            System.out.printf("ERROR:  Message contains no letters!\n");
            System.exit(1);
        }


        if (DEBUG) {
            System.out.printf("vic.agentID = %s\n", vic.agentID);
            System.out.printf("vic.date = %s\n", vic.date);
            System.out.printf("vic.phrase = %s\n", vic.phrase);
            System.out.printf("vic.anagram = %s\n", vic.anagram);
            System.out.printf("vic.message = %s\n", vic.message);
        }

        return vic;
    }

    /** Executes the first no carry for the algorithm.
     * @param data          The VICdata
     * @return              the no carry addition on the agent id and the first digits of the date.
     */
    private static String _noCarryIDAndDates(VICData data) {
        return VICOperations.noCarryAddition(data.agentID, data.date.substring(0, 5));
    }

    /** Chains the result of the first no carry addition
     * @param noCarryIDAndDates         The result of the first no carry
     * @return                          A chained addition.
     */
    private static String _chainNoCarryIDAndDates(String noCarryIDAndDates) {
        return VICOperations.chainAddition(noCarryIDAndDates, 10);
    }

    /** Digit permutation on the phrase
     * @param data          The VICData
     * @return              The digit permutation of the phrase
     */
    private static String _permutePhrase(VICData data) {
        return VICOperations.digitPermutation(data.phrase);
    }

    /** Adds the first chained no carry sum and the phrase digit permutation
     * @param chainedNoCarry            The chained no carry addition
     * @param digitPermutation          The digit permutation generated by the phrase
     * @return                          the no carry sum
     */
    private static String _addChainedNoCarryAndDigitPermutation(String chainedNoCarry, String digitPermutation) {
        return VICOperations.noCarryAddition(chainedNoCarry, digitPermutation);
    }

    /** Permutes the digit string
     * @param digits            The digit string to permute
     * @return                  a digit permutation
     */
    private static String _permuteDigitString(String digits) {
        return VICOperations.digitPermutation(digits);
    }

    /** Builds a straddling checkerboard from the permutation and the anagram
     * @param permutation           The digit permutation
     * @param anagram               The anagram
     * @return                      a straddling checkerboard
     */
    private static List<String> _buildStraddlingCheckerboard(String permutation, String anagram) {
        return VICOperations.straddlingCheckerboard(permutation, anagram);
    }

    /** Uses the straddling checkerboard to encode the message
     * @param checkerboard              The straddling checkerboard
     * @param message                   The message to encode
     * @return                          A String of digits
     */
    private static String _encodeMessage(List<String> checkerboard, String message) {
        List<Character> alphabet = new ArrayList<>();
        for (int ch = 'A'; ch <= 'Z'; ch++)
            alphabet.add((char) ch);
        StringBuilder sb = new StringBuilder();
        int index;
        for (char c : message.toCharArray()) {
            index = alphabet.indexOf(c);
            sb.append(checkerboard.get(index));
        }
        return sb.toString();
    }

    /** Inserts the agent ID into the ciphertext
     * @param data              The VICdata
     * @param cipherText        The encoded message
     * @return                  The final encoded message
     */
    private static String _insertID(VICData data, String cipherText) {
        String id = data.agentID;
        int lastNum = Integer.parseInt(data.date.substring(data.date.length() - 1));
        if (cipherText.length() < lastNum) {
            return cipherText + id;
        }
        return cipherText.substring(0, lastNum) + id + cipherText.substring(lastNum);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new IllegalArgumentException("This program needs an input file to encrypt!");
        } else if (args.length > 1) {
            throw new IllegalArgumentException("Two many arguments");
        }
        VICData data = readVICData(args[0]);
        String noCarryIDAndDate = _noCarryIDAndDates(data);
        String chainedNoCarry = _chainNoCarryIDAndDates(noCarryIDAndDate);
        String permutedPhrase = _permutePhrase(data);
        String digits = _addChainedNoCarryAndDigitPermutation(chainedNoCarry, permutedPhrase);
        String digitPermutation = _permuteDigitString(digits);
        List<String> checkerboard = _buildStraddlingCheckerboard(digitPermutation, data.anagram);
        String encoded = _encodeMessage(checkerboard, data.message);
        System.out.println(_insertID(data, encoded));
    }
}
