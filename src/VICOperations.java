import java.util.*;

/**
 *
 */
public class VICOperations {

    /**
     * Implements No carry addition
     * Example 7 + 18 = 15
     *
     * @param a first number to add
     * @param b second number to add
     * @return a string of the result
     */
    public static String noCarryAddition(String a, String b) {
        if (a.length() == 0 && b.length() == 0) {
            return "";
        } else if (a.length() < b.length()) {
            return b.substring(0, 1) + noCarryAddition(a, b.substring(1));
        } else if (b.length() < a.length()) {
            return a.substring(0, 1) + noCarryAddition(a.substring(1), b);
        } else {
            int aNum = Integer.parseInt(a.substring(0, 1));
            int bNum = Integer.parseInt(b.substring(0, 1));
            String sum = String.valueOf((aNum + bNum) % 10);
            return sum + noCarryAddition(a.substring(1), b.substring(1));
        }
    }

    /**
     * Recursive routine for chainAddition.
     *
     * @param num    original number to add
     * @param retNum accumulator for the addition
     * @param len    the length of the chain addition
     * @return the result of the chain addition
     */
    private static String _chainAddition(String num, String retNum, int len) {
        if (retNum.length() == len) {
            return retNum;
        } else if (retNum.length() == 1) {
            return _chainAddition(num + num, num + num, len);
        } else if (retNum.length() > len) {
            return retNum.substring(0, len);
        } else {
            int first = Integer.parseInt(num.substring(0, 1));
            int second = Integer.parseInt(num.substring(1, 2));
            String sum = String.valueOf((first + second) % 10);
            return _chainAddition(num.substring(1) + sum, retNum + sum, len);
        }
    }

    /**
     * Performs chain addition of length len on num
     *
     * @param num the number to be added.
     * @param len the length of the chain addition
     * @return the result of the chain addition
     */
    public static String chainAddition(String num, int len) {
        return _chainAddition(num, num, len);
    }

    /** Main routing to build the permutation
     * @param sortedWord        a sorted word
     * @param num               The number to associate
     * @param map               the map storing the associations
     * @return
     */
    private static HashMap<Character, List<String>> _digitPermutation(String sortedWord, String num, HashMap<Character, List<String>> map) {
        if (sortedWord.isEmpty()) {
            return map;
        } else {
            Character first = sortedWord.charAt(0);
            map.putIfAbsent(first, new ArrayList<>());
            String next = String.valueOf(Integer.valueOf(num) + 1);
            map.get(first).add(num);
            return _digitPermutation(sortedWord.substring(1), next, map);
        }
    }

    /**
     * Performs a digit permutation on the given word
     *
     * @param word the word to permute
     * @return A string representing the digit permutation of word
     */
    public static String digitPermutation(String word) {
        if (word.length() < 10) {
            return null;
        } else {
            char[] temp = word.toUpperCase().toCharArray();
            Arrays.sort(temp);
            HashMap<Character, List<String>> map = _digitPermutation(new String(temp), "0", new HashMap<Character, List<String>>());
            StringBuilder ret = new StringBuilder();
            for(char ch : word.toCharArray()) {
               List<String> lst = map.get(ch);
               String first = lst.remove(0);
               map.put(ch, lst);
               ret.append(first);
            }
            return ret.toString();
        }
    }

    /** Converts a string into a set of chars
     * @param anagram       The string to convert
     * @return              a set of characters
     */
    private static Set<Character> _convertStringToCharacterSet(String anagram) {
       Set<Character> anaChars = new HashSet<>();
       for (char ch : anagram.toCharArray()) {
           anaChars.add(ch);
       }
       return anaChars;
    }

    /** Generates the complement of the anagram
     * @param anagram       The anagram to take the complement of
     * @return              A list of characters representing the complement
     */
    private static List<Character> _generateRestOfAlphabet(String anagram) {
        List<Character> lst = new ArrayList<>();
        Set<Character> anaChars = _convertStringToCharacterSet(anagram);
        for (int ch = 'A'; ch <= 'Z'; ch++) {
            if (!anaChars.contains((char) ch)) {
                lst.add((char) ch);
            }
        }
        return lst;
    }

    /** Splits the remaining letters into two list of appropriate size
     * @param rest      The rest of the alphabet
     * @return          A list of lists of characters.
     */
    private static List<List<Character>> _splitRestOfAlphabet(List<Character> rest) {
        if (rest.size() < 10) {
            throw new IllegalArgumentException("Rest of alphabet must be greater that 10");
        }
        List<List<Character>> lst = new ArrayList<List<Character>>();
        lst.add(rest.subList(0, 10));
        lst.add(rest.subList(10, rest.size()));
        return lst;
    }

    /** Maps the characters of the anagram to the permutation.
     * @param permutation       the permutation for keys
     * @param anagram           the anagram to associate
     * @param alphaMap          the main map for the straddling checkerboard
     */
    private static void _mapAnagramToPermutation(String permutation, String anagram, Map<Character, String> alphaMap) {
        for (int i = 0; i < anagram.length(); i++) {
            if (anagram.charAt(i) != ' ') {
                alphaMap.put(anagram.charAt(i), Character.toString(permutation.charAt(i)));
            }
        }
    }

    /** Associates the whitespace of the anagram with the rest of the alphabet
     * @param permutation       the permutation for keys
     * @param anagram           the anagram to associate
     * @return                  a map with the associations
     */
    private static Map<Character, List<Character>> _mapWhitespaceToRestOfAlphabet(String permutation, String anagram) {
        HashMap<Character, List<Character>> map = new HashMap<>();
        char firstSpace = permutation.charAt(anagram.indexOf(" "));
        char lastSpace = permutation.charAt(anagram.lastIndexOf(" "));
        List<List<Character>> lst = _splitRestOfAlphabet(_generateRestOfAlphabet(anagram));
        map.put(firstSpace, lst.get(0));
        map.put(lastSpace, lst.get(1));
        return map;
    }

    /** Associates the rest of the alphabet with their respective strings.
     * @param permutation
     * @param whiteSpaceMap
     * @param alphaMap
     */
    private static void _assocAlphaMapWithRestOfAlphabet(String permutation, Map<Character, List<Character>> whiteSpaceMap, Map<Character, String> alphaMap) {
        for (char ch : whiteSpaceMap.keySet()) {
            for (int i = 0; i < whiteSpaceMap.get(ch).size(); i++) {
                alphaMap.put(whiteSpaceMap.get(ch).get(i), Character.toString(ch) + Character.toString(permutation.charAt(i)));
            }
        }
    }

    /** Generates the final List for the straddling checkerboard
     * @param alphaMap      The map of the alphabet to strings
     * @return              The final list of the straddling checkerboard
     */
    private static List<String> _generateStraddlingCheckerboard(TreeMap<Character, String> alphaMap) {
        List<String> checkerBoard = new ArrayList<>();
        for (char ch: alphaMap.keySet()) {
            checkerBoard.add(alphaMap.get(ch));
        }
        return checkerBoard;
    }

    /** Associates letters to numbers.
     * @param permutation       The given permutation
     * @param anagram           the anagram to associate
     * @return                  A list of Strings
     */
    public static List<String> straddlingCheckerboard(String permutation, String anagram) {
        TreeMap<Character, String> alphaMap = new TreeMap<>();
        String nAnagram = anagram.toUpperCase();
        _mapAnagramToPermutation(permutation, nAnagram, alphaMap);
        _assocAlphaMapWithRestOfAlphabet(permutation, _mapWhitespaceToRestOfAlphabet(permutation, anagram), alphaMap);
        return _generateStraddlingCheckerboard(alphaMap);
    }
}
