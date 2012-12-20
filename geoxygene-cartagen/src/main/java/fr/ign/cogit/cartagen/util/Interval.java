/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

/**
 * Class to model number intervals, useful to define scale ranges.
 * @author GTouya
 * 
 * @param <T>
 */
public class Interval<T extends Number> implements Cloneable {

  /**
   * Bound rules values.
   */
  public enum Rule {

    /**
     * The bound is inclusive.
     */
    INCLUSIVE,
    /**
     * The bound is exclusive.
     */
    EXCLUSIVE;
  }

  /**
   * Min bound rule. <br>
   * Default value is <code>Rule.INCLUSIVE</code> to stay compatible with
   * version 1.0
   * @since 1.1
   */
  private Rule minimumRule = Rule.INCLUSIVE;
  /**
   * Max bound rule. <br>
   * Default value is <code>Rule.INCLUSIVE</code> to stay compatible with
   * version 1.0
   * @since 1.1
   */
  private Rule maximumRule = Rule.INCLUSIVE;
  /**
   * Holds the minimum value.
   */
  private T minimum;
  /**
   * Holds the maximum value.
   */
  private T maximum;

  /**
   * Creates a new instance.
   * @param minimum The minimum value.
   * @param maximum The maximum value.
   * @throws IllegalArgumentException If either <code>minimum</code> or
   *           <code>maximum</code> is <code>null</code>.
   */
  public Interval(T minimum, T maximum) throws IllegalArgumentException {
    this(minimum, maximum, Rule.INCLUSIVE, Rule.INCLUSIVE);
  }

  /**
   * Creates a new instance.
   * @param minimum The minimum value.
   * @param maximum The maximum value.
   * @param minimumRule The rule for the minimum bound; can be <code>null</code>
   *          . <br>
   *          If <code>null</code> will be set to <code>Rule.INCLUSIVE</code>.
   * @param maximumRule The rule for the maximum bound; can be <code>null</code>
   *          . <br>
   *          If <code>null</code> will be set to <code>Rule.INCLUSIVE</code>.
   * @throws IllegalArgumentException If either <code>minimum</code> or
   *           <code>maximum</code> is <code>null</code> or if the specified
   *           interval is invalid. <br>
   *           Typically, an invalid interval may be ]x, x[, [x, x[ or ]x, x].
   * @since 1.1
   */
  public Interval(T minimum, T maximum, Rule minimumRule, Rule maximumRule)
      throws IllegalArgumentException {
    if (minimum == null || maximum == null) {
      throw new IllegalArgumentException("Value cannot be null.");
    }
    Rule minRule = (minimumRule == null) ? Rule.INCLUSIVE : minimumRule;
    Rule maxRule = (maximumRule == null) ? Rule.INCLUSIVE : maximumRule;
    if ((minimum.doubleValue() == maximum.doubleValue())
        && ((minRule == Rule.EXCLUSIVE) || (maxRule == Rule.EXCLUSIVE))) {
      throw new IllegalArgumentException("The specified interval is invalid");
    } else if (minimum.doubleValue() <= maximum.doubleValue()) {
      this.minimum = minimum;
      this.maximum = maximum;
    } else {
      this.minimum = maximum;
      this.maximum = minimum;
    }
  }

  // ///////////////////////////////
  /**
   * Creates a copy of this interval.
   * @return A copy of this interval.
   */
  @Override
  @SuppressWarnings("unchecked")
  public Interval<T> clone() {
    Interval<T> copy = this;
    try {
      copy = (Interval<T>) super.clone();
    } catch (CloneNotSupportedException cnse) {
      // Silently consume exception.
    }
    return copy;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + (this.minimum != null ? this.minimum.hashCode() : 0);
    return hash;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("rawtypes")
  @Override
  public boolean equals(Object obj) {
    return ((obj instanceof Interval) && this.equals((Interval) obj));
  }

  /**
   * @param interval The interval to be tested.
   * @return <code>True</code> if the test is verified; <code>false</code>
   *         otherwise.
   */
  public boolean equals(Interval<?> interval) {
    return ((interval != null)
        && (this.minimum.doubleValue() == interval.minimum())
        && (this.maximum.doubleValue() == interval.maximum())
        && (this.minimumRule == interval.minimumRule) && (this.maximumRule == interval.maximumRule));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return (this.minimumRule == Rule.INCLUSIVE ? "[" : "]") + this.minimum
        + ", " + this.maximum
        + (this.maximumRule == Rule.INCLUSIVE ? "]" : "[");
  }

  // ///////////////////////////////
  /**
   * Gets the minimum value.
   * @return A <code>T</code> instance.
   */
  public T getMinimum() {
    return this.minimum;
  }

  /**
   * Gets the maximum value.
   * @return A <code>T</code> instance.
   */
  public T getMaximum() {
    return this.maximum;
  }

  /**
   * Gets the minimum value in <code>double</code> precision.
   * @return A <code>double</code>.
   */
  public double minimum() {
    return this.minimum.doubleValue();
  }

  /**
   * Gets the maximum value in <code>double</code> precision.
   * @return A <code>double</code>.
   */
  public double maximum() {
    return this.maximum.doubleValue();
  }

  /**
   * Gets the <code>maximum - minimum</code> value in <code>double</code>
   * precision.
   * @return A <code>double</code>.
   */
  public double range() {
    return this.maximum.doubleValue() - this.minimum.doubleValue();
  }

  /**
   * Gets the rule of the minimum bound.
   * @return A <code>Rule<code> instance; never <code>null</code>.
   * @since 1.1
   */
  public Rule getMinimumRule() {
    return this.minimumRule;
  }

  /**
   * Gets the rule of the maximum bound.
   * @return A <code>Rule<code> instance; never <code>null</code>.
   * @since 1.1
   */
  public Rule getMaximumRule() {
    return this.maximumRule;
  }

  /**
   * Indicates whether the provided value belongs to this interval.
   * @param number The value to be tested.
   * @return <code>True</code> if the test is verified; <code>false</code>
   *         otherwise.
   * @throws java.lang.IllegalArgumentException If <code>number</code> is
   *           <code>null</code> or this interval is invalid.
   */
  public boolean contains(Number number) throws IllegalArgumentException {
    if (number == null) {
      throw new IllegalArgumentException("Value cannot be null.");
    }
    boolean containsMin = (this.minimumRule == Rule.INCLUSIVE) ? (this.minimum
        .doubleValue() <= number.doubleValue())
        : (this.minimum.doubleValue() < number.doubleValue());
    boolean containsMax = (this.maximumRule == Rule.INCLUSIVE) ? (number
        .doubleValue() <= this.maximum.doubleValue())
        : (number.doubleValue() < this.maximum.doubleValue());
    return containsMin && containsMax;
  }

  /**
   * Indicates whether the provided interval belongs to this interval.
   * @param interval The interval to be tested.
   * @return <code>True</code> if the test is verified; <code>false</code>
   *         otherwise.
   * @throws java.lang.IllegalArgumentException If <code>interval</code> is
   *           <code>null</code>.
   */
  public boolean contains(Interval<?> interval) throws IllegalArgumentException {
    if (interval == null) {
      throw new IllegalArgumentException("Value cannot be null.");
    }
    boolean containsMin = (this.minimumRule == Rule.INCLUSIVE) ? (this.minimum
        .doubleValue() <= interval.minimum())
        : (this.minimum.doubleValue() < interval.minimum());
    boolean containsMax = (this.maximumRule == Rule.INCLUSIVE) ? (interval
        .maximum() <= this.maximum.doubleValue())
        : (interval.maximum() < this.maximum.doubleValue());
    return containsMin && containsMax;
  }

  /**
   * Indicates whether the provided interval intersects to this interval.
   * @param interval The interval to be tested.
   * @return <code>True</code> if the test is verified; <code>false</code>
   *         otherwise.
   * @throws java.lang.IllegalArgumentException If <code>interval</code> is
   *           <code>null</code>.
   * @since 1.1
   */
  public boolean intersects(Interval<?> interval)
      throws IllegalArgumentException {
    if (interval == null) {
      throw new IllegalArgumentException("Value cannot be null.");
    }
    boolean containsMin = (this.minimumRule == Rule.INCLUSIVE) ? (this.minimum
        .doubleValue() <= interval.minimum())
        : (this.minimum.doubleValue() < interval.minimum());
    boolean containsMax = (this.maximumRule == Rule.INCLUSIVE) ? (interval
        .maximum() <= this.maximum.doubleValue())
        : (interval.maximum() < this.maximum.doubleValue());
    return containsMin || containsMax;
  }

  /**
   * Rescale the provided number to fit into this interval.
   * @param number The number to rescale.
   * @return A <code>double</code>.
   * @throws java.lang.IllegalArgumentException If <code>number</code> is
   *           <code>null</code>.
   */
  public double rescale(Number number) throws IllegalArgumentException {
    double result = Interval.rescale(number, this);
    return result;
  }

  /**
   * Rescale the provided number to fit into the provided interval.
   * @param number The number to rescale.
   * @param interval The source interval.
   * @return A <code>double</code>.
   * @throws java.lang.IllegalArgumentException If <code>number</code> or
   *           <code>intervall</code>. is <code>null</code>.
   * @since 1.1
   */
  public static double rescale(Number number,
      Interval<? extends Number> interval) throws IllegalArgumentException {
    if (number == null) {
      throw new IllegalArgumentException("Value cannot be null.");
    } else if (interval == null) {
      throw new IllegalArgumentException("Source interval cannot be null.");
    }
    double result = Interval.rescale(number.doubleValue(), interval.minimum(),
        interval.maximum());
    if (interval.getMinimumRule() == Rule.EXCLUSIVE
        && result == interval.minimum()) {
      result = Math.min(result + Double.MIN_VALUE, interval.maximum());
    } else if (interval.getMaximumRule() == Rule.EXCLUSIVE
        && result == interval.maximum()) {
      result = Math.max(result - Double.MIN_VALUE, interval.minimum());
    }
    return result;
  }

  /**
   * Rescale the provided number to fit into the <code>[minimum, maximum]</code>
   * interval.
   * @param value The value to rescale.
   * @param minimum The minimum value.
   * @param maximum The maximum value.
   * @return A <code>double</code>.
   */
  public static double rescale(double value, double minimum, double maximum) {
    double min = Math.min(minimum, maximum);
    double max = Math.max(minimum, maximum);
    double result = Math.min(value, max);
    result = Math.max(result, min);
    return result;
  }

  /**
   * Project interval <code>[source]</code> on interval
   * <code>[destination]</code> ands return the image of the given value.
   * @param number The value to be projected.
   * @param source The source interval (value must be contained within the
   *          interval).
   * @param destination The destination interval.
   * @return The image of the given value after the projection.
   * @throws IllegalArgumentException If <code>value</code>, <code>source</code>
   *           or <code>destination</code> is <code>null</code> or if
   *           <code>source</code> has a range of 0.
   */
  public static double project(Number number,
      Interval<? extends Number> source, Interval<? extends Number> destination)
      throws IllegalArgumentException {
    if (number == null) {
      throw new IllegalArgumentException("Value cannot be null.");
    } else if (source == null) {
      throw new IllegalArgumentException("Source interval cannot be null.");
    } else if (source.range() == 0) {
      throw new IllegalArgumentException(
          "Range of source interval must not be 0.");
    } else if (destination == null) {
      throw new IllegalArgumentException("Destination interval cannot be null.");
    }
    return Interval.project(number.doubleValue(), source.minimum(), source
        .maximum(), destination.minimum(), destination.maximum());
  }

  /**
   * Project interval <code>[inMin, inMax]</code> on interval
   * <code>[outMin, outMax]</code> and return the image of the given value. <BR>
   * This method does not create new <code>Interval</code> objects.
   * @param value The value to be projected.
   * @param inMin The source minimum.
   * @param inMax The source maximum.
   * @param outMin The destination minimum.
   * @param outMax the destination maximum.
   * @return The image of the given value after the projection.
   * @throws IllegalArgumentException If the source interval has a range of 0.
   */
  public static double project(double value, double inMin, double inMax,
      double outMin, double outMax) throws IllegalArgumentException {
    double min1 = Math.min(inMin, inMax);
    double max1 = Math.max(inMin, inMax);
    double range1 = max1 - min1;
    if (range1 == 0) {
      throw new IllegalArgumentException(
          "Range of source interval must not be 0.");
    }
    double value1 = value;
    value1 = Math.max(min1, value1);
    value1 = Math.min(max1, value1);
    double min2 = Math.min(outMin, outMax);
    double max2 = Math.max(outMin, outMax);
    double range2 = max2 - min2;
    double value2 = min2 + (range2 * (value1 - min1)) / range1;
    return value2;
  }

}
