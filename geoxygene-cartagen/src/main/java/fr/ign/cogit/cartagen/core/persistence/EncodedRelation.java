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
public @interface EncodedRelation {
  Class<? extends IGeneObj> targetEntity();

  /**
   * In a 1toN relation, gives the declared class of the inverse relation that
   * isn't necessarily the same as the class that is annotated. It is useful to
   * set the inverse of the relation while the relation is set.
   * @return
   */
  Class<? extends IGeneObj> invClass() default GeneObjDefault.class;

  String methodName();

  String invMethodName() default "";

  boolean nToM() default false;

  boolean inverse() default true;

  CollectionType collectionType() default CollectionType.COLLECTION;
}
