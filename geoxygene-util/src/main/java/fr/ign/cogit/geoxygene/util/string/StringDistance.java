package fr.ign.cogit.geoxygene.util.string;

import org.apache.commons.codec.language.DoubleMetaphone;
import org.apache.commons.lang.StringUtils;

public class StringDistance {
  String a;
  String b;
  public StringDistance(String a, String b) {
    DoubleMetaphone m = new DoubleMetaphone();
    this.a = m.doubleMetaphone(a);
    this.b = m.doubleMetaphone(b);
  }
  public int getDistance() {
    return StringUtils.getLevenshteinDistance(this.a, this.b);
  }
}
