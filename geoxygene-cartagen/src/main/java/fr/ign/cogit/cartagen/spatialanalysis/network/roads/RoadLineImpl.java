/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.network.roads;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import fr.ign.cogit.cartagen.core.carto.CartoDescribedLin;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.Reseau;
import fr.ign.cogit.geoxygene.schemageo.impl.routier.TronconDeRouteImpl;

public class RoadLineImpl extends TronconDeRouteImpl implements
    CartoDescribedLin {

  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // All static fields //
  private static Logger logger = Logger.getLogger(RoadLineImpl.class.getName());
  // Public fields //

  // Protected fields //

  // Package visible fields //

  // Private fields //
  private String nature;
  private int importance;
  private double symbolIntWidth, symbolExtWidth;

  // //////////////////////////////////////////
  // Static methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public RoadLineImpl(Reseau res, boolean estFictif, ICurve geom) {
    super(res, estFictif, geom);
    this.symbolIntWidth = 0.0;
    this.symbolExtWidth = 0.0;
  }

  public RoadLineImpl(Reseau res, String nature, ICurve geom) {
    super(res, false, geom);
    this.setNature(nature);
    this.symbolIntWidth = 0.0;
    this.symbolExtWidth = 0.0;
  }

  public RoadLineImpl(Reseau res, String nature, ICurve geom, int importance,
      double symbolIntWidth, double symbolExtWidth) {
    super(res, false, geom);
    this.setNature(nature);
    this.setImportance(importance);
    this.setSymbolIntWidth(symbolIntWidth);
    this.setSymbolExtWidth(symbolExtWidth);
  }

  // Getters and setters //
  public void setNature(String nature) {
    this.nature = nature;
  }

  public String getNature() {
    return this.nature;
  }

  public void setImportance(int importance) {
    this.importance = importance;
  }

  public int getImportance() {
    return this.importance;
  }

  public void setSymbolIntWidth(double symbolIntWidth) {
    this.symbolIntWidth = symbolIntWidth;
  }

  public void setSymbolExtWidth(double symbolExtWidth) {
    this.symbolExtWidth = symbolExtWidth;
  }

  // Other public methods //
  @Override
  public Object getAttribute(String nom) {
    GF_FeatureType ft = this.getFeatureType();
    AttributeType attribute = null;
    if (ft == null) {
      attribute = new AttributeType();
      attribute.setNomField(nom);
      attribute.setMemberName(nom);
    } else {
      attribute = ((FeatureType) ft).getFeatureAttributeByName(nom);
    }

    if (attribute.getMemberName().equals("geom")) {
      RoadLineImpl.logger
          .warning("WARNING : Pour récupérer la primitive géométrique par défaut, veuillez utiliser "
              + "la méthode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)");
      return this.getGeom();
    }
    if (attribute.getMemberName().equals("topo")) {
      RoadLineImpl.logger
          .warning("WARNING : Pour récupérer la primitive topologique par défaut, veuillez utiliser "
              + "la méthode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)");
      return this.getTopo();
    }
    Object valeur = null;
    String nomFieldMaj = null;
    if (attribute.getNomField().length() == 0) {
      nomFieldMaj = attribute.getNomField();
    } else {
      nomFieldMaj = Character.toUpperCase(attribute.getNomField().charAt(0))
          + attribute.getNomField().substring(1);
    }
    String nomGetFieldMethod = "get" + nomFieldMaj;
    Class<?> classe = this.getClass();
    while (!classe.equals(Object.class)) {
      try {
        Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod,
            (Class[]) null);
        valeur = methodGetter.invoke(this, (Object[]) null);
        return valeur;
      } catch (SecurityException e) {
        RoadLineImpl.logger
            .config("SecurityException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      } catch (IllegalArgumentException e) {
        RoadLineImpl.logger
            .config("IllegalArgumentException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      } catch (NoSuchMethodException e) {
        // if (logger.isTraceEnabled())
        // logger.trace("La mï¿½thode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
      } catch (IllegalAccessException e) {
        RoadLineImpl.logger
            .config("IllegalAccessException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      } catch (InvocationTargetException e) {
        RoadLineImpl.logger
            .config("InvocationTargetException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      }
      classe = classe.getSuperclass();
    }
    // rï¿½essayer si le getter est du genre isAttribute, ie pour un
    // boolï¿½en
    nomGetFieldMethod = "is" + nomFieldMaj;
    classe = this.getClass();
    while (!classe.equals(Object.class)) {
      try {
        Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod,
            (Class[]) null);
        valeur = methodGetter.invoke(this, (Object[]) null);
        return valeur;
      } catch (SecurityException e) {
        RoadLineImpl.logger
            .config("SecurityException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      } catch (IllegalArgumentException e) {
        RoadLineImpl.logger
            .config("IllegalArgumentException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      } catch (NoSuchMethodException e) {
        // if (logger.isTraceEnabled())
        // logger.trace("La mï¿½thode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
      } catch (IllegalAccessException e) {
        RoadLineImpl.logger
            .config("IllegalAccessException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      } catch (InvocationTargetException e) {
        RoadLineImpl.logger
            .config("InvocationTargetException pendant l'appel de la méthode "
                + nomGetFieldMethod + " sur la classe " + classe);
      }
      classe = classe.getSuperclass();
    }
    RoadLineImpl.logger.info("Echec de l'appel à la méthode "
        + nomGetFieldMethod + " sur la classe " + this.getClass());
    return null;
  }

  @Override
  public void setAttribute(String nom, Object value) {
    // first get (or create) the attribute object
    GF_FeatureType ft = this.getFeatureType();
    AttributeType attribute = null;
    if (ft == null) {
      attribute = new AttributeType();
      attribute.setNomField(nom);
      attribute.setMemberName(nom);
    } else {
      attribute = ((FeatureType) ft).getFeatureAttributeByName(nom);
    }

    if (attribute.getMemberName().equals("geom")) {
      RoadLineImpl.logger
          .warning("WARNING : Pour affecter la primitive géométrique par défaut, veuillez utiliser "
              + "la méthode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)");
      this.setGeom((IGeometry) value);
    } else if (attribute.getMemberName().equals("topo")) {
      RoadLineImpl.logger
          .warning("WARNING : Pour affecter la primitive topologique par défaut, veuillez utiliser "
              + "la méthode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)");
      this.setTopo((ITopology) value);
    } else {
      try {
        String nomFieldMaj2;
        if (attribute.getNomField().length() == 0) {
          nomFieldMaj2 = attribute.getNomField();
        } else {
          nomFieldMaj2 = Character.toUpperCase(attribute.getNomField()
              .charAt(0))
              + attribute.getNomField().substring(1);
        }
        String nomSetFieldMethod = "set" + nomFieldMaj2;
        Method methodSetter = this.getClass().getDeclaredMethod(
            nomSetFieldMethod, value.getClass());
        methodSetter.invoke(this, value);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public double getSymbolExtWidth() {
    return this.symbolExtWidth;
  }

  @Override
  public double getSymbolIntWidth() {
    return this.symbolIntWidth;
  }

  @Override
  public IPolygon getSymbolBulk() {
    return (IPolygon) this.geom.buffer(this.symbolExtWidth);
  }

  @Override
  public String getSymbolName() {
    return "road symbol";
  }

  @Override
  public String toString() {
    String text = "RoadLineImpl number " + this.id;
    if (this.id == 0) {
      text = text + " " + this.getGeom().coord();
    }
    return text;
  }

  // //////////////////////////////////////////
  // Protected methods //
  // //////////////////////////////////////////

  // //////////////////////////////////////////
  // Package visible methods //
  // //////////////////////////////////////////

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////

}
