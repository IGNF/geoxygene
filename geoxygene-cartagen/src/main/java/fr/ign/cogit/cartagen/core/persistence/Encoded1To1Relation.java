/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.core.persistence;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encoded1To1Relation {
  /**
   * The actual class of the related entity (not necessarily the declared
   * class).
   * @return
   */
  Class<? extends IGeneObj> targetEntity();

  boolean inverse() default false;

  /**
   * The name of the relation in the other class.
   * @return
   */
  String invName() default "";

  /**
   * In a 1toN relation, gives the declared class of the inverse relation that
   * isn't necessarily the same as the class that is annotated. It is useful to
   * set the inverse of the relation while the relation is set.
   * @return
   */
  Class<? extends IGeneObj> invClass() default GeneObjDefault.class;

  /**
   * The name of the getters and setters (without "get" and "set" but beginning
   * in high case) of the real relation in order to be able to use it by
   * reflection.
   * @return
   */
  String methodName();
}
