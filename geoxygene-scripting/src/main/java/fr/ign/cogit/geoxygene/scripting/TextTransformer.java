package fr.ign.cogit.geoxygene.scripting;

/**
 * Do a simple transformation from string to string
 * @author JeT
 *
 */
public interface TextTransformer {

  /**
   * Do a simple transformation from string to string
   * @param text text to transform
   * @return transformed text
   */
  String transform(String text) throws TextTransformException;
}
