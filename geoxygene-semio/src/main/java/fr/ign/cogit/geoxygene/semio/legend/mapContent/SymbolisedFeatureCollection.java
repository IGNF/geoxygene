package fr.ign.cogit.geoxygene.semio.legend.mapContent;

import java.awt.BasicStroke;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendComponent;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendLeaf;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.operation.AreaOp;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.Contrast;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Elodie Buard - IGN / Laboratoire COGIT
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Vianney Dugrain, IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 * Set of features with the same symbology choices (layer).
 */
public class SymbolisedFeatureCollection extends
		FT_FeatureCollection<SymbolisedFeature> {

  private static final Logger logger = Logger
      .getLogger(SymbolisedFeatureCollection.class);

  // ////////////////////////////////////////////////////////
  // /////////// ASSOCIATIONS, ATTRIBUTES
  // ////////////////////////////////////////////////////////

  /**
   * Name of the layer.
   */
  private String name;

  /**
   * Returns the name of the layer.
   * @return The name of the layer.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Specifies the name of the layer.
   * @param The name of the layer.
   */
  public void setName(String layerName) {
    this.name = layerName;
  }

  /**
   * Mean contrast of features of the layer.
   */
  private Contrast meanContrast = new Contrast();

  /**
   * Returns the mean contrast of features of the layer.
   * @return the mean contrast of features of the layer.
   */
  public Contrast getMeanContrast() {
    return meanContrast;
  }

  /**
   * Specifies the mean contrast of features of the layer.
   * @param The mean contrast of features of the layer.
   */
  public void setMeanContrast(Contrast contrast) {
    this.meanContrast = contrast;
  }

  /**
   * Area covered by all features of the layer.
   */
  private double totalArea = -1d;

  /**
   * Returns the total area covered by all features of the layer.
   * @return The total area covered by all features of the layer.
   */
  public double getTotalArea() {
    return this.totalArea;
  }

  /**
   * Specifies the total area covered by all features of the layer.
   * @param area The total area covered by all features of the layer.
   */
  public void setTotalArea(double area) {
    this.totalArea = area;
  }

  /**
   * The corresponding <code>LegendLeaf</code>.
   */
  private LegendLeaf legend;

  /**
   * Returns the corresponding <code>LegendLeaf</code>.
   * @return The corresponding <code>LegendLeaf</code>.
   */
  public LegendLeaf getLegend() {
    return this.legend;
  }

  /**
   * Specifies the corresponding <code>LegendLeaf</code>.
   * @param legendLeaf The corresponding <code>LegendLeaf</code>.
   */
  public void setLegend(LegendLeaf legendLeaf) {
    LegendLeaf old = this.legend;
    this.legend = legendLeaf;
    if (old != null) {
      old.getSymbolisedFeatureCollections().remove(this);
    }
    if (legendLeaf != null) {
      if (!legendLeaf.getSymbolisedFeatureCollections().contains(this))
        legendLeaf.getSymbolisedFeatureCollections().add(this);
    }
  }

  /**
   * Map to which this layer belongs to.
   * <p>
   * <strong>French:</strong><br />
   * Carte à laquelle appartient la famille carto.
   */
  private Map map;

  /**
   * Returns the map to which this layer belongs to.
   * @return The map to which this layer belongs to.
   */
  public Map getMap() {
    return this.map;
  }

  /**
   * Specifies the map to which this layer belongs to.
   * @param map The map to which this layer belongs to.
   */
  public void setMap(Map map) {
    Map old = this.map;
    this.map = map;
    if (old != null) {
      old.getSymbolisedFeatureCollections().remove(this);
    }
    if (map != null) {
      if (!map.getSymbolisedFeatureCollections().contains(this))
        map.getSymbolisedFeatureCollections().add(this);
    }
  }

  // ////////////////////////////////////////////////////////
  // ////////// METHODS
  // ////////////////////////////////////////////////////////

  /**
   * Returns the main color of the symbol of the layer.
   * @return The main color of the symbol of the layer.
   */
  public ColorimetricColor getColor() {
    return this.getLegend().getSymbol().getColor();
  }

  /**
   * Returns the geometric type of features of the layer.
   * @return The geometric type of features of the layer.
   */
  public int getGeometricType() {
    return this.getLegend().getSymbol().getTypeGeometrie();
  }

  /**
   * Computes the total area of features of the layer. If features geometry is
   * line, the method computeLinesArea is used. Features are aggregated and then
   * buffered.
   * <p>
   * <strong>French:</strong><br />
   * Si les objets sont des lignes : On fait appel à la méthode
   * computeLinesArea, dans laquelle les tronçons sont agrégés puis bufferisés
   * en polygones
   * <p>
   * Si les objets ne sont pas des lignes : Appel de la fonction computeArea()
   * sur les objets de la couche.
   * 
   */
  public void computesTotalArea() {
    double superficie = 0;

    if (this.get(0).getGeom().getClass().equals(GM_LineString.class)
        || this.get(0).getGeom().getClass().equals(GM_MultiCurve.class)) {
      superficie = computesLinesArea();
    } else {
      for (SymbolisedFeature objetCarto : this) {

        if (objetCarto.getArea() == -1) {
          AreaOp.computeArea(objetCarto);
        }
        superficie = superficie + objetCarto.getArea();
      }
    }
    this.setTotalArea(superficie);
  }

  /**
   * Compute the area of a linear SymbolisedFeatureCollection.
   * <p>
   * All the features of the SymbolisedFeatureCollection are aggregated. Then a
   * buffer is generated with the corresponding width. Finally, the area of this
   * buffer is computed with the JTS method.
   * <p>
   * <strong>French:</strong><br />
   * Calcule la surface des géométries de la feature collection (dont les
   * features ont une géométrie linéaire).
   * <p>
   * Méthode qui aggrège les géométries linéaires et effectue un buffer de la
   * taille du symbole.
   * 
   * @return The area of the linear <code>SymbolisedFeatureCollection</code>
   */
  public double computesLinesArea() {
    double superficie = 0;

    GraphicSymbol symbol = this.getLegend().getSymbol();
    // Getting the width of the line object
    double widthOnMap = symbol.getWidthOnMap();

    // computing the area of the line (agregation, buffer, area)
    if (this.get(0).getGeom().getClass().equals(GM_LineString.class)
        || this.get(0).getGeom().getClass().equals(GM_MultiCurve.class)) {
      SymbolisedFeature aggregLine = new DefaultSymbolisedFeature();

      if (symbol.getJoin() == BasicStroke.JOIN_MITER) {
        if (symbol.getCap() == BasicStroke.CAP_BUTT) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_FLAT, BufferParameters.JOIN_MITRE));
        } else if (symbol.getCap() == BasicStroke.CAP_ROUND) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_ROUND, BufferParameters.JOIN_MITRE));
        } else if (symbol.getCap() == BasicStroke.CAP_SQUARE) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_SQUARE, BufferParameters.JOIN_MITRE));
        }

      } else if (symbol.getJoin() == BasicStroke.JOIN_BEVEL) {
        if (symbol.getCap() == BasicStroke.CAP_BUTT) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_FLAT, BufferParameters.JOIN_BEVEL));
        } else if (symbol.getCap() == BasicStroke.CAP_ROUND) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_ROUND, BufferParameters.JOIN_BEVEL));
        } else if (symbol.getCap() == BasicStroke.CAP_SQUARE) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_SQUARE, BufferParameters.JOIN_BEVEL));
        }

      } else if (symbol.getJoin() == BasicStroke.JOIN_ROUND) {
        if (symbol.getCap() == BasicStroke.CAP_BUTT) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_FLAT, BufferParameters.JOIN_ROUND));
        } else if (symbol.getCap() == BasicStroke.CAP_ROUND) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_ROUND, BufferParameters.JOIN_ROUND));
        } else if (symbol.getCap() == BasicStroke.CAP_SQUARE) {
          aggregLine.setGeom(this.getGeomAggregate().buffer((widthOnMap / 2),
              8, BufferParameters.CAP_SQUARE, BufferParameters.JOIN_ROUND));
        }
      }

      superficie = aggregLine.getGeom().area();

    } else {
      logger
          .error("The Symbolised featureCollection does not contains linear objects.");
    }
    return superficie;
  }

  /**
   * Computes the mean of local contrasts of features of the layer.
   * 
   * <strong>French:</strong><br />
   * Calcule la moyenne des contrastes des objets d'une famille carto (moyenne
   * des contrastes bruts et des évaluations de la qualité des contrastes).
   */
  public void computesMeanContrast() {
    double sommeTeinte = 0;
    double sommeClarte = 0;
    double sommeQualiteTeinte = 0;
    double sommeQualiteClarte = 0;
    int nbObjet = 0;

    for (SymbolisedFeature objet : this) {
      if (objet.getContrast().getContrasteTeinte() == -1)
        continue; // cas où l'objet n'avait pas de relation
      nbObjet++;
      sommeTeinte = sommeTeinte + objet.getContrast().getContrasteTeinte();
      sommeClarte = sommeClarte + objet.getContrast().getContrasteClarte();
      sommeQualiteTeinte = sommeQualiteTeinte
          + objet.getContrast().getQualiteContrasteTeinte();
      sommeQualiteClarte = sommeQualiteClarte
          + objet.getContrast().getQualiteContrasteClarte();
    }
    if (nbObjet != 0) {
      this.getMeanContrast().setContrasteClarte(sommeClarte / nbObjet);
      this.getMeanContrast().setContrasteTeinte(sommeTeinte / nbObjet);
      this.getMeanContrast().setQualiteContrasteClarte(
          sommeQualiteClarte / nbObjet);
      this.getMeanContrast().setQualiteContrasteTeinte(
          sommeQualiteTeinte / nbObjet);
    } else {
      this.getMeanContrast().setContrasteClarte(-1);
      this.getMeanContrast().setContrasteTeinte(-1);
      this.getMeanContrast().setQualiteContrasteClarte(-1);
      this.getMeanContrast().setQualiteContrasteTeinte(-1);
    }
  }

  /**
   * TODO Revoir le commentaire Computes the weighted mean of local contrasts of
   * features of the layer. The ratio between the surface of each cartographic
   * object and the total surface of the symbolizedFeatureCollection is used to
   * weight the contrast mean.
   * 
   * <strong>French:</strong><br />
   * Calcule la moyenne des contrastes des objets d'une famille carto (moyenne
   * des contrastes bruts et des évaluations de la qualité des contrastes).
   */
  public void computesWeightedMeanContrast() {
    double sommeTeintePond = 0;
    double sommeClartePond = 0;
    double sommeQualiteTeintePond = 0;
    double sommeQualiteClartePond = 0;
    int nbObjet = 0;
    double sommeArea = 0;

    for (SymbolisedFeature objet : this) {
      if (objet.getContrast().getContrasteTeinte() == -1)
        continue; // cas où l'objet n'avait pas de relation
      nbObjet++;
      sommeTeintePond = sommeTeintePond
          + objet.getContrast().getContrasteTeinte() * objet.getArea();
      sommeClartePond = sommeClartePond
          + objet.getContrast().getContrasteClarte() * objet.getArea();
      sommeQualiteTeintePond = sommeQualiteTeintePond
          + objet.getContrast().getQualiteContrasteTeinte() * objet.getArea();
      sommeQualiteClartePond = sommeQualiteClartePond
          + objet.getContrast().getQualiteContrasteClarte() * objet.getArea();
      sommeArea = sommeArea + objet.getArea();
    }
    if (nbObjet != 0) {
      this.getMeanContrast().setContrasteClarte(sommeClartePond / sommeArea);
      this.getMeanContrast().setContrasteTeinte(sommeTeintePond / sommeArea);
      this.getMeanContrast().setQualiteContrasteClarte(
          sommeQualiteClartePond / sommeArea);
      this.getMeanContrast().setQualiteContrasteTeinte(
          sommeQualiteTeintePond / sommeArea);
    } else {
      this.getMeanContrast().setContrasteClarte(-1);
      this.getMeanContrast().setContrasteTeinte(-1);
      this.getMeanContrast().setQualiteContrasteClarte(-1);
      this.getMeanContrast().setQualiteContrasteTeinte(-1);
    }
    logger.info("Aire objets voisins : " + sommeArea);
    logger.info("moyenne contraste clarte : "
        + this.getMeanContrast().getContrasteClarte());
    logger.info("moyenne contraste teinte : "
        + this.getMeanContrast().getContrasteTeinte());
    logger.info("moyenne qualité contraste clarte : "
        + this.getMeanContrast().getQualiteContrasteClarte());
    logger.info("moyenne qualité contraste teinte : "
        + this.getMeanContrast().getQualiteContrasteTeinte());
    logger.info("");
  }

  /**
   * Returns one of the layers related to this layer, considered as the
   * reference one.
   * <p>
   * <strong>French:</strong><br />
   * Retourne la famille carto en relation "dominante" (i.e. celle qui sert de
   * référence, celle dont on va choisir la teinte pour toute la famille).
   * <p>
   * Pour l'instant la famille dominante est la premiere trouvée dans la liste
   * des familles en relation avec la famille courante (peut retourner la
   * famille courante)
   * 
   * TODO : Famile choisie au hasard, à améliorer
   * 
   * @return One of the layers related to this layer, considered as the
   *         reference one.
   */
  public SymbolisedFeatureCollection referenceRelatedFeatureCollection(
      final SemanticRelation relation) {
    return this.relatedFeatureCollections(relation).get(0);
  }

  /**
   * Returns all the layers related to this layer by the semantic relation.
   * <p>
   * <strong>French:</strong><br />
   * Renvoie la liste des familles en relation avec self par la relation passée
   * en paramêtre.
   * <p>
   * Code un peu tordu car il faut vérifier qu'on ne pointe pas vers un élément
   * d'une autre carte
   * 
   * TODO: code qui suppose que les relations ne sont instanciées qu'au niveau
   * des feuilles A rendre plus générique
   * 
   * @return All the layers related to this layer.
   */
  public List<SymbolisedFeatureCollection> relatedFeatureCollections(
      final SemanticRelation relation) {
    List<SymbolisedFeatureCollection> famillesEnRelation = new ArrayList<SymbolisedFeatureCollection>();
    for (LegendComponent element : relation.getRelatedComponents()) {
      if (element instanceof LegendLeaf) {
        for (SymbolisedFeatureCollection famille : ((LegendLeaf) element)
            .getSymbolisedFeatureCollections()) {
          if (famille.getMap() == this.getMap()) {
            famillesEnRelation.add(famille);
          }
        }
      }
    }
    return famillesEnRelation;
  }

  // //////// CODE VERSION LUCIL A REINTRODUIRE

  /**
   * Calcul des qualités (teinte et clarté) d'une famille carto TODO: code à
   * réintroduire (pondération des moyennes) : mais principe à creuser
   * @param familleCarto
   */
  public void calculerQualite() {
    // double sommeTeinte = 0;
    // double sommeClarte = 0;
    // double moyenneTeinte = 0;
    // double moyenneClarte = 0;
    // double qualiteTeintePonderee = 0;
    // double qualiteClartePonderee = 0;
    //
    // List<SymbolisedFeatureDecorator> objetsCartoLocaux = this.elements;
    //
    // for (SymbolisedFeatureDecorator symbolisedFeature : objetsCartoLocaux) {
    // sommeTeinte = sommeTeinte +
    // symbolisedFeature.getContrast().getQualiteContrasteTeinte();
    // sommeClarte = sommeClarte +
    // symbolisedFeature.getContrast().getQualiteContrasteClarte();
    // }
    //
    // moyenneTeinte = sommeTeinte / objetsCartoLocaux.size();
    // moyenneClarte = sommeClarte / objetsCartoLocaux.size();
    //
    // this.getMeanContrast().setQualiteContrasteClarte(moyenneClarte);
    // this.getMeanContrast().setQualiteContrasteTeinte(moyenneTeinte);
    //
    // this.computesTotalArea();
    //
    // for (SemanticRelation relation : this.getLegend().getRelations()) {
    // if (relation.isOrder()) {
    // //regarde la couleur des familles dans cet ordre
    // for (LegendComponent relatedComponent : relation.getRelatedComponents())
    // {
    // //FIXME : pour l'instant, le lien reliant LegendLeaf à
    // SymbolisedFeatureCollection est un lien 1-n
    // //FIXME : et si une relation concerne des thèmes ???
    // SymbolisedFeatureCollection relatedFamily =
    // ((LegendLeaf)relatedComponent).getSymbolisedFeatureCollections().get(0);
    // if (relatedFamily.getLegend().getSymbol().getColor().getHue()
    // != this.getLegend().getSymbol().getColor().getHue()){
    // //jamais utilisé en fait, pusique rechangé juste aprés
    // qualiteTeintePonderee = moyenneTeinte + 1;
    // }
    // qualiteClartePonderee =
    // this.getMeanContrast().getQualiteContrasteClarte();
    // }
    // }
    // else if (relation.isAssociation()) {
    // //regarde la couleur des familles dans l'association...
    // //idée : ne pas etre trop éloigné
    // for (LegendComponent relatedComponent : relation.getRelatedComponents())
    // {
    // //FIXME : pour l'instant, le lien reliant LegendLeaf à
    // SymbolisedFeatureCollection est un lien 1-n
    // //FIXME : et si une relation concerne des thèmes ???
    // SymbolisedFeatureCollection relatedFamily =
    // ((LegendLeaf)relatedComponent).getSymbolisedFeatureCollections().get(0);
    //
    // double hueContrast = ContrastCollection.getCOGITContrast(
    // relatedFamily.getLegend().getSymbol().getColor(),
    // this.getLegend().getSymbol().getColor()).getContrasteTeinte();
    // if (hueContrast > 2) {
    // qualiteTeintePonderee = moyenneTeinte + hueContrast / 5;
    // }
    // qualiteClartePonderee =
    // this.getMeanContrast().getQualiteContrasteClarte();
    // }
    // }
    // else if (relation.isDifference()) {
    // qualiteTeintePonderee =
    // this.getMeanContrast().getQualiteContrasteTeinte();
    // qualiteClartePonderee =
    // this.getMeanContrast().getQualiteContrasteClarte();
    // }
    //
    //
    // double superficieFC = this.getTotalArea();
    // // prendre en compte la superficie et la taille en nombre d'objets?
    // if (superficieFC != 0 && objetsCartoLocaux.size() < 3) {
    // qualiteTeintePonderee = qualiteTeintePonderee + 0.8;
    // //+1/objetscartos.size();
    // qualiteClartePonderee = qualiteClartePonderee + 0.8;
    // //1/objetscartos.size();
    // } else if (superficieFC == 0 && objetsCartoLocaux.size() < 3) {
    // qualiteTeintePonderee = qualiteTeintePonderee - 0.5;
    // qualiteClartePonderee = qualiteClartePonderee - 0.5;
    // } else if (superficieFC == 0 && objetsCartoLocaux.size() >= 3) {
    // qualiteTeintePonderee = qualiteTeintePonderee - 1 /
    // objetsCartoLocaux.size();
    // } else {
    // qualiteTeintePonderee = qualiteTeintePonderee + 1 /
    // objetsCartoLocaux.size();
    // qualiteClartePonderee=qualiteClartePonderee+1/objetsCartoLocaux.size();
    // }
    //
    // this.setQualitePondereeTeinte(qualiteTeintePonderee);
    // this.setQualitePondereeClarte(qualiteClartePonderee);
  }

}
