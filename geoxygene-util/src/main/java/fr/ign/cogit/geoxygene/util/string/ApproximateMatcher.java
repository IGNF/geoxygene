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
  
  private boolean compareBackwards=false;
  private boolean ignoreTiret=false;
  
  
  /** For avoiding allocations.  This can only be used by one thread at a
   *  time.  INVARIANT: buffer!=null => buffer is a bufSize by bufSize array.
   */
  private volatile int[][] buffer;
  private volatile int bufSize;
  

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
  
  /** Méthode identique à process mais enlève les accents dans le cas
   *  où ignoreCase est activé
   *  Ajout Eric
   */
  public String processAccent(String s) {
      //Optimize for special case.
      if (! (ignoreCase || compareBackwards || ignoreWhitespace))
          return s;

      StringBuffer buf=new StringBuffer(s.length());
      if (compareBackwards) {
          for (int i=0; i<s.length(); i++) {
              char c=s.charAt(s.length()-i-1);
              if (ignoreCase)
                  c=Character.toLowerCase(c);
                  if (Character.getNumericValue(c)==-1)
                          c = this.oteAccent(c);
              if (ignoreWhitespace) 
                  if (c==' ' || c=='_')
                      continue;
              if (ignoreTiret)
                  if (c=='-')
                      continue;
              buf.append(c);
          }
      } else {                  //Exactly like above, but forward.
          for (int i=0; i<s.length(); i++) {
              char c=s.charAt(i);
              if (ignoreCase)
                  c=Character.toLowerCase(c);
              if (Character.getNumericValue(c)==-1)
                          c = this.oteAccent(c);
              if (ignoreWhitespace) 
                  if (c==' ' || c=='_')
                      continue;
                  if (ignoreTiret)
                      if (c=='-')
                          continue;
              buf.append(c);
          }
      }
      return buf.toString();
  }
  
  /** Méthode qui ôte les accents d'un char
   * Ajout Eric
   */
  public char oteAccent(char c) {
      
      if (c=='é') return 'e';
      if (c=='è') return 'e';
      if (c=='ê') return 'e';
      if (c=='ç') return 'c';
      if (c=='â') return 'a';
      if (c=='î') return 'i';
      if (c=='ô') return 'o';
      if (c=='û') return 'u';
      if (c=='ä') return 'a';
      if (c=='ë') return 'e';
      if (c=='ï') return 'i';
      if (c=='ö') return 'o';
      if (c=='ü') return 'u';
      if (c=='à') return 'a';
      if (c=='ù') return 'u';
      
      return c;
  }
  
  /*
   * Returns the edit distance between s1 and s2.  That is, returns the number
   * of insertions, deletions, or replacements necessary to transform s1 into
   * s2.  A value of 0 means the strings match exactly.<p>
   *
   * If you want to ignore case or whitespace, or compare backwards, s1 and s2
   * should be the return values of a call to process(..).
   */
  public final int match(String s1, String s2) {
      //Let m=s1.length(), n=s2.length(), and k be the edit difference between
      //s1 and s2.  It's possible to reduce the time from O(mn) time to O(kn)
      //time by repeated iterations of the the k-difference algorithm.  But
      //this is a bit complicated.
      return matchInternal(s1, s2, Integer.MAX_VALUE);
  }
  
  /**
   * If the edit distance between s1 and s2 is less than or equal to maxOps,
   * returns the edit distance.  Otherwise returns some number greater than
   * maxOps.
   */    
  private int matchInternal(String s1, String s2, int maxOps) {
      //Swap if necessary to ensure |s1|<=|s2|.
      if (s1.length()<=s2.length()) 
          return matchInternalProcessed(s1, s2, maxOps);
      else 
          return matchInternalProcessed(s2, s1, maxOps);
  }
  
  /**
   * Same as matchInternal, but with weaker precondition.
   *     @requires |s1|<=|s2|
   */
  private int matchInternalProcessed(
          String s1, String s2, final int maxOps) {
      //A classic implementation using dynamic programming.  d[i,j] is the
      //edit distance between s1[0..i-1] and s2[0..j-1] and is defined
      //recursively.  Note that there are "margins" of 1 on the left and
      //top of this matrix.  See Chapter 11 of _Algorithms on Strings, Trees,
      //and Sequences_ by Dan Gusfield for a complete discussion.
      //
      //A key optimization is that we only fill in part of the row.  This is
      //based on the observation that any maxOps-difference global alignment
      //must not contain any cell (i, i+l) or (i,i-l), where l>maxOps.
      //
      //There are two additional twists to the usual algorithm.  First, we fill in
      //the matrix anti-diagonally instead of one row at a time.  Secondly, we
      //stop if the minimum value of the last two diagonals is greater than
      //maxOps.
      final int s1n=s1.length();
      final int s2n=s2.length();
//SEB        Assert.that(s1n<=s2n);
      
      if (maxOps<=0)
          return (s1.equals(s2)) ? 0 : 1;
      //Strings of vastly differing lengths don't match.  This is necessary to
      //prevent the last return statement below from incorrectly returning
      //zero.
      else if (Math.abs(s1n-s2n) > maxOps) {
          return maxOps+1;
      }
      //If one of the strings is empty, the distance is trivial to calculate.
      else if (s1n==0) { //s2n==0 ==> s1n==0           
          return s2n;
      }
      
      //Optimization: recycle buffer for matrix if possible. 
      int[][] d;
      if (buffer!=null
              && (bufSize >= Math.max(s1n+1, s2n+1)))
          d=buffer; 
      else 
          d=new int[s1n+1][s2n+1];               //Note d[0][0]==0
      int diagonals=2*Math.min(s1n+1, s2n+1)-1
                       +Math.min(s2n-s1n, maxOps);
      int minThisDiag;              //The min value of this diagonal
      int minLastDiag=0;            //The min value of last diagonal
      
      //For each k'th anti-diagonal except first (measured from the origin)...
      for (int k=1; k<diagonals; k++) {            
          //1. Calculate indices of left corner of diagonal (i1, j1) and upper
          //right corner (i2, j2).  This is black magic.  You really need to
          //look at a diagram to see why it works.
          int i1=k/2+maxOps/2;
          int j1=k/2-maxOps/2;
          int i2=k/2-maxOps/2;
          int j2=k/2+maxOps/2;            
          if ((k%2)!=0) {              //odd k?
              if ((maxOps%2)==0) {     //even maxOps?
                  //out and away from last endpoint
                  j1++;
                  i2++;
              } else {
                  //in towards the diagonal
                  i1++;
                  j2++;
              }
          }           
          //If endpoints don't fall on board, adjust accordingly
          if (j1<0 || i1>s1n) {
              i1=Math.min(k, s1n);
              j1=k-i1;
          }
          if (i2<0 || j2>s2n) {
              j2=Math.min(k, s2n);
              i2=k-j2;
          }
          
          //2. Calculate matrix values for corners. This is just like the loop
          //below except (1) we need to be careful of array index problems 
          //and (2) we don't bother looking to the left of (i1, j1) or above 
          //(i2, j2) if it's on the outer diagonal.
//SEB             Assert.that(i1>0, "Zero i1");  //j1 may be zero
//SEB             Assert.that(j2>0, "Zero j2");  //i2 may be zero
          //   a) Look in towards diagonal
          d[i1][j1]=d[i1-1][j1]+1;
          d[i2][j2]=d[i2][j2-1]+1;                            
          //   b) Look along the diagonal, unless on edge of matrix
          if (j1>0) 
              d[i1][j1]=Math.min(d[i1][j1],
                            d[i1-1][j1-1] + diff(s1.charAt(i1-1),
                                                 s2.charAt(j1-1)));
          if (i2>0)
              d[i2][j2]=Math.min(d[i2][j2],
                            d[i2-1][j2-1] + diff(s1.charAt(i2-1),
                                                 s2.charAt(j2-1)));
          //   c) Look out away from the diagonal if "inner diagonal" or on
          //   bottom row, unless on edge of matrix.
          boolean innerDiag=(k%2)!=(maxOps%2);
          if ((innerDiag || i1==s1n) && j1>0)
              d[i1][j1]=Math.min(d[i1][j1],
                                 d[i1][j1-1]+1);            
          if (innerDiag && i2>0) 
              d[i2][j2]=Math.min(d[i2][j2],
                                 d[i2-1][j2]+1);
          minThisDiag=Math.min(d[i1][j1], d[i2][j2]);

          //3. Calculate matrix value for each element of the diagonal except
          //the endpoints...
          int i=i1-1;
          int j=j1+1;
          while (i>i2 && j<j2) {
              d[i][j]=1;
              //Fill in d[i][j] using previous calculated values
              int dij=min3(d[i-1][j-1] + diff(s1.charAt(i-1), s2.charAt(j-1)),
                           d[i-1][j]   + 1,
                           d[i][j-1]   + 1); 
              d[i][j]=dij;
              minThisDiag=Math.min(minThisDiag, dij);
              //Move up and to the right in the matrix.
              i--;
              j++;
          }
          
          //If min value on last two diags is too big, quit.
          if (minThisDiag>maxOps && minLastDiag>maxOps) {
              return minThisDiag;
          }
          minLastDiag=minThisDiag;
      }     

      return d[s1n][s2n];
  }
  
  /** Returns 0 if a==b, or 1 otherwise. */
  /*private static int diff(char a, char b) {
      if (a==b) 
          return 0;
      else 
          return 1;
  }*/

  private static int min3(int n1, int n2, int n3) {
      return( Math.min( n1, Math.min( n2, n3 ) ) );
  }

}
