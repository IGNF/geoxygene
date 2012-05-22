package fr.ign.cogit.geoxygene.util.string;

/**
 * Levenshtein distance, also known as Edit distance.
 * <p>
 * Distance de Levenshtein ou distance d'édition. Compte le nombre minimal de
 * transformations (elimination, remplacement, insertion de lettres) pour passer
 * d'une chaîne à l'autre. Cette distance est très utilisée pour comparer des
 * chaînes caractères.
 * @author Julien Perret
 * @author Eric Grosso
 */
final public class ApproximateMatcher {
  private boolean ignoreAccent = false;
  private boolean ignoreCase = false;
  private boolean ignoreDash = false;
  private boolean ignoreWhitespace = false;

  public ApproximateMatcher() {
  }

  public ApproximateMatcher(boolean ignoreAccent, boolean ignoreCase,
      boolean ignoreDash, boolean ignoreWhitespace) {
    this.ignoreAccent = ignoreAccent;
    this.ignoreCase = ignoreCase;
    this.ignoreDash = ignoreDash;
    this.ignoreWhitespace = ignoreWhitespace;
  }

  public void setIgnoreCase(boolean ignoreCase) {
    this.ignoreCase = ignoreCase;
  }

  public void setIgnoreWhitespace(boolean ignoreWhitespace) {
    this.ignoreWhitespace = ignoreWhitespace;
  }

  public void setIgnoreDash(boolean ignoreDash) {
    this.ignoreDash = ignoreDash;
  }

  public void setIgnoreAccent(boolean ignoreAccent) {
    this.ignoreAccent = ignoreAccent;
  }

  public String process(String s) {
    // if no processing required, return the string
    if (!(this.ignoreAccent || this.ignoreCase || this.ignoreDash || this.ignoreWhitespace)) {
      return s;
    }
    StringBuffer stringBuffer = new StringBuffer(s.length());
    // build a new string
    for (int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      if (this.ignoreCase) {
        c = Character.toLowerCase(c);
      }
      if (this.ignoreAccent && Character.getNumericValue(c) == -1) {
        c = this.removeAccent(c);
      }
      if (this.ignoreWhitespace && (c == ' ' || c == '_')) {
        continue;
      }
      if (this.ignoreDash && c == '-') {
        continue;
      }
      stringBuffer.append(c);
    }
    return stringBuffer.toString();
  }

  /**
   * Méthode qui enlève les accents d'un char
   */
  public char removeAccent(char c) {
    if (c == 'é' || c == 'è' || c == 'ê' || c == 'ë') {
      return 'e';
    }
    if (c == 'ç') {
      return 'c';
    }
    if (c == 'â' || c == 'à' || c == 'ä') {
      return 'a';
    }
    if (c == 'î' || c == 'ï') {
      return 'i';
    }
    if (c == 'ô' || c == 'ö') {
      return 'o';
    }
    if (c == 'û' || c == 'ü' || c == 'ù') {
      return 'u';
    }
    return c;
  }

  /**
   * Compares two chars.
   * @param a first char
   * @param b second char
   * @return 0 if a == b, 1 otherwise
   */
  private static int diff(char a, char b) {
    return (a == b) ? 0 : 1;
  }

  /**
   * Get the minimum of three ints.
   * @param n1 first value
   * @param n2 second value
   * @param n3 third value
   * @return the minimum of three values
   */
  private static int min(int n1, int n2, int n3) {
    return Math.min(n1, Math.min(n2, n3));
  }

  /**
   * Compute the Levenshtein Distance between s1 and s2.
   * @param s1 first string
   * @param s2 second string
   * @return the Levenshtein Distance between s1 and s2
   */
  public int distance(String s1, String s2) {
    // Step 1: initialize
    int s1Length = s1.length();
    int s2Length = s2.length();
    if (s1Length == 0) {
      return s2Length;
    }
    if (s2Length == 0) {
      return s1Length;
    }
    int d[][] = new int[s1Length + 1][s2Length + 1];
    // Step 2: initialize first row and column
    for (int indexS1 = 0; indexS1 <= s1Length; indexS1++) {
      d[indexS1][0] = indexS1;
    }
    for (int indexS2 = 0; indexS2 <= s2Length; indexS2++) {
      d[0][indexS2] = indexS2;
    }
    // Step 3: examine characters from s1
    for (int indexS1 = 1; indexS1 <= s1Length; indexS1++) {
      char charS1 = s1.charAt(indexS1 - 1);
      // Step 4: examine characters from s2
      for (int indexS2 = 1; indexS2 <= s2Length; indexS2++) {
        char charS2 = s2.charAt(indexS2 - 1);
        // Step 5: cost is 0 is characters are the same, 1 otherwise
        int cost = ApproximateMatcher.diff(charS1, charS2); // cost
        // Step 6: set the cell cost to the min of its neighbours
        d[indexS1][indexS2] = ApproximateMatcher.min(
            d[indexS1 - 1][indexS2] + 1, d[indexS1][indexS2 - 1] + 1,
            d[indexS1 - 1][indexS2 - 1] + cost);
      }
    }
    // Step 7
    return d[s1Length][s2Length];
  }

  /**
   * Compute the Levenshtein Distance between s1 and s2.
   * @param s1 first string
   * @param s2 second string
   * @param ignoreAccent true if the accents should be ignored
   * @param ignoreCase true if the case should be ignored
   * @param ignoreDash true if dashes should be ignored
   * @param ignoreWhitespace true if whitespaces and underscores should be
   *          ignored
   * @return the Levenshtein Distance between s1 and s2
   */
  public static int distance(String s1, String s2, boolean ignoreAccent,
      boolean ignoreCase, boolean ignoreDash, boolean ignoreWhitespace) {
    ApproximateMatcher matcher = new ApproximateMatcher(ignoreAccent,
        ignoreCase, ignoreDash, ignoreWhitespace);
    return matcher.distance(matcher.process(s1), matcher.process(s2));
  }
}
