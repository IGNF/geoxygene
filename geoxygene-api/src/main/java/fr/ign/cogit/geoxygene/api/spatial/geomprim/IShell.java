package fr.ign.cogit.geoxygene.api.spatial.geomprim;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomcomp.ICompositeSurface;

public interface IShell extends ICompositeSurface {
  /**
   * TODO A implémenter
   */
  @Override
  public abstract boolean isSimple();

  /**
   * Renvoie la liste des facettes composant la surface
   * 
   * @return la liste des facettes composant la surface
   */
  public abstract List<IOrientableSurface> getlisteFaces();
}
