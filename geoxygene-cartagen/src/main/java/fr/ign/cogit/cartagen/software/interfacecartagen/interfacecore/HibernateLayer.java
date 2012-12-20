/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 28 janv. 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.postgis.GeodatabaseHibernatePostgis;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * une couche d'objets destine a être symbolise et affiché dans un panelvisu
 * gestion du cache d'objets a afficher
 * @author julien Gaffuri 28 janv. 2009
 * 
 */
public class HibernateLayer extends Layer {
  // private final static Logger
  // logger=Logger.getLogger(Couche.class.getName());

  /**
	 */
  private Class<IFeature> persistantClass;

  public HibernateLayer(Class<IFeature> classePersistante,
      boolean selectionnable) {
    this.persistantClass = classePersistante;
    this.setSelectable(selectionnable);
    this.symbolisedLayers = new ArrayList<SymbolisedLayer>();
  }

  public HibernateLayer(Class<IFeature> classePersistante) {
    // par defaut, selectionnable
    this(classePersistante, true);
  }

  @Override
  protected void cacheUpdate(VisuPanel pv) throws InterruptedException {
    // vide la cache precedent
    this.displayCache.clear();

    // recupere l'enveloppe de la fenetre d'affichage
    IGeometry env = pv.getDisplayEnvelope().getGeom();

    // recupere la BD
    GeodatabaseHibernatePostgis gcb = (GeodatabaseHibernatePostgis) DataSet.db;

    // recupere les objets a afficher
    gcb.begin();
    this.displayCache = gcb.loadAllFeatures(this.persistantClass, env);
    gcb.commit();

    // ca y est, le cache est a jour...
    this.setCacheUpdate(false);
  }

}
