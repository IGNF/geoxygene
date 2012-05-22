package fr.ign.cogit.geoxygene.api.spatial.geomcomp;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISurfaceBoundary;

public interface ICompositeSurface extends IComposite, IOrientableSurface {

  /** Renvoie la liste des GM_OrientableSurface */
  List<IOrientableSurface> getGenerator();

  /** Renvoie la GM_OrientableSurface de rang i */
  IOrientableSurface getGenerator(int i);

  /**
   * Affecte une GM_OrientableSurface au rang i. Attention : aucun contrôle de
   * continuité n'est effectué.
   */
  void setGenerator(int i, IOrientableSurface value);

  /**
   * Ajoute une GM_OrientableSurface en fin de liste. Attention : aucun contrôle
   * de continuité n'est effectué.
   */
  void addGenerator(IOrientableSurface value);

  /**
   * A FAIRE. Ajoute une GM_OrientableSurface en fin de liste avec un contrôle
   * de continuité avec la tolérance passée en paramètre. Envoie une exception
   * en cas de problème.
   * 
   * @param value
   * @param tolerance
   * @throws Exception
   */
  void addGenerator(IOrientableSurface value, double tolerance)
      throws Exception;

  /**
   * A FAIRE. Ajoute une GM_OrientableSurface en fin de liste avec un contrÃ´le
   * de continuitÃ© avec la tolÃ©rance passÃ©e en paramÃ¨tre. Eventuellement
   * change le sens d'orientation de la surface pour assurer la continuite.
   * Envoie une exception en cas de problÃ¨me.
   * 
   * @param value
   * @param tolerance
   * @throws Exception
   */
  void addGeneratorTry(IOrientableSurface value, double tolerance)
      throws Exception;

  /**
   * Ajoute une GM_OrientableSurface au rang i. Attention : aucun contrÃ´le de
   * continuitÃ© n'est effectuÃ©.
   */
  void addGenerator(int i, IOrientableSurface value);

  /**
   * Efface la (ou les) GM_OrientableSurface passÃ© en paramÃ¨tre. Attention :
   * aucun contrÃ´le de continuitÃ© n'est effectuÃ©.
   */
  void removeGenerator(IOrientableSurface value) throws Exception;

  /**
   * Efface la GM_OrientableSurface de rang i. Attention : aucun contrÃ´le de
   * continuitÃ© n'est effectuÃ©.
   */
  void removeGenerator(int i) throws Exception;

  /** Nombre de GM_OrientableSurface constituant self */
  int sizeGenerator();

  /** Renvoie la primitive de self. */
  @Override
  // le calcul est fait en dynamique dans la methode privee simplifyPrimitve.
  ISurface getPrimitive();

  /** Renvoie la primitive orientÃ©e positivement. */
  @Override
  IOrientableSurface getPositive();

  /** Renvoie la primitive orientÃ©e nÃ©gativement. */
  @Override
  IOrientableSurface getNegative();

  /**
   * Redéfinition de l'opérateur "boundary" sur GM_OrientableSurface. Renvoie
   * une GM_SurfaceBoundary.
   */
  @Override
  ISurfaceBoundary boundary();

  // //////////////////////////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  // MÃ©thodes "validate" /////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  /**
   * A FAIRE - renvoie toujours true pour le moment. VÃ©rifie la continuitÃ© des
   * composants. Renvoie TRUE s'ils sont contigus, FALSE sinon. Cette mÃ©thode
   * n'est pas dans la norme.
   * 
   * @param tolerance
   * @return
   */
  boolean validate(double tolerance);
}
