package fr.ign.cogit.cartagen.core.genericschema;

import java.lang.reflect.InvocationTargetException;

import fr.ign.cogit.cartagen.core.persistence.EncodedRelation;
import fr.ign.cogit.geoxygene.api.feature.IFeature;

public interface IPersistentObject extends IFeature {

  // *********************************************//
  // OBJECT PERSISTENCE METHODS //
  // *********************************************//
  /**
   * This method is only useful for the persistence of IGeneObj relations: as it
   * is not possible to persist interface relations (that are everywhere in the
   * GeneObj), the ids are stored in a private field for each relation; this
   * method fills the id field with the ids of the objects that are actually
   * related. The method uses the {@link EncodedRelation} annotation to find the
   * relations to update in {@code this}.
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws IllegalAccessException
   * @throws InvocationTargetException
   * @throws NoSuchFieldException
   */
  void updateRelationIds() throws SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException,
      InvocationTargetException, NoSuchFieldException;

  /**
   * This method is the opposite of updateRelationIds() in the persistence
   * management. It fills the relations of {@code this} with other
   * {@link IGeneObj} objects using the id collection fields annotated by
   * {@link EncodedRelation}. The setters of the relations are used by
   * reflection and the objects are queried using their ids and their class,
   * encoded in the annotation fields.
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws NoSuchFieldException
   * @throws SecurityException
   * @throws NoSuchMethodException
   */
  void fillRelationsFromIds() throws IllegalArgumentException,
      IllegalAccessException, InvocationTargetException, SecurityException,
      NoSuchFieldException, NoSuchMethodException;

  /**
   * This method is only useful for the persistence of IGeneObj. To avoid using
   * EJB with GeOxygene objects, the GeOxygene object related to a IGeneObj
   * object is transient, so it has to be restored. It is restored using
   * {@code this} geometry and attributes. The GeOxygene object relations are
   * not restored by this method but by restoreGeoxRelations().
   */
  void restoreGeoxObjects();

  /**
   * This method is only useful for the persistence of IGeneObj.To avoid using
   * EJB with GeOxygene objects, the GeOxygene object related to a IGeneObj
   * object is transient, so it has to be restored. The standard restoration is
   * carried out by restoreGeoxObjects() but the relations are restored
   * afterwards by this method, once all the GeOxygene objects have been
   * restored.
   */
  void restoreGeoxRelations();

}
