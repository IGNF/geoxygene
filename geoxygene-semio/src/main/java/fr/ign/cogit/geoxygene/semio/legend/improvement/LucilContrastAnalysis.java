package fr.ign.cogit.geoxygene.semio.legend.improvement;

import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendComponent;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendLeaf;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.Map;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeatureCollection;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelation;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.HueQualityContrastComparator;
import fr.ign.cogit.geoxygene.semio.legend.symbol.color.LightnessQualityContrastComparator;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorReferenceSystem;
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
 * @author Elodie Buard
 * @author Sebastien Mustiere
 * @author Charlotte Hoarau
 * 
 * This class contains the methods of improvement and modification of a map.
 *
 */
public class LucilContrastAnalysis implements ContrastAnalysis {
  private static final Logger logger = Logger.getLogger(LucilContrastAnalysis.class);
  
  private Map map;
  public Map getMap() {
    return map;
  }

  private StopCriteria stop;
  private boolean surfaceWeights;
  
  double pireNoteTeinte;
  double pireNoteClarte;
  SymbolisedFeatureCollection familleCartoTeinteAChanger;
  SymbolisedFeatureCollection familleCartoClarteAChanger;
  @Override
  
  public void improvementStep() {
    this.amelioreContrastes();
  }

  @Override
  public void initialize(Map map, StopCriteria stop){
   this.map = map;
   this.stop = stop;
   this.stop.initialize();
   this.pireNoteTeinte = -1;
   this.pireNoteClarte = -1;
   this.familleCartoTeinteAChanger = null;
   this.familleCartoClarteAChanger = null;
  }

  public boolean isSurfaceWeights() {
    return surfaceWeights;
  }

  public void setSurfaceWeights(boolean surfaceWeights) {
    this.surfaceWeights = surfaceWeights;
  }

  /* (non-Javadoc)
   * @see legend.improvement.ContrastAnalysis#run(double, double)
   */
  @Override
  public void run(double neighborsDistance){
    logger.trace("LucilContrastAnalysis runing");
    this.map.searchForNeighbors(neighborsDistance);
    this.homogenizeOrderedSymbolisedFeatureCollection2();
    logger.info("Neighbors relations build");
    while (!this.stop.isChecked()) {
      this.improvementStep();
    }
  }
  
  /** 
   * Effectue une itération pour améliorer les contrastes de la carte.
   * Methode d'entrée du processus d'amélioration de légende 'LUCIL'
   *  
   * NB: on suppose que la famille n'est que dans une seule relation 
   *     (mais avec plusieurs familles eventuellement).
   */
  public Map amelioreContrastes() {
  
      // Calcul des contrastes (sur la carte, qui les calcule sur les familles, 
      // qui les calculent sur les objets, qui les calculent sur les relations. 
      logger.info("1/ Calcul des contrastes dans la carte");
      if (this.surfaceWeights) {
        logger.info("Pondération par les surfaces");
        this.map.instantiateAllWeightedContrasts();
      } else {
        logger.info("Pas de pondération");
        this.map.instantiateAllContrasts();
      }
      
      
      logger.info("2/ Bilan des contrastes");
      this.sortContrasts();
      
      // Choix de la famille dont il faut changer la couleur est en priorié, et comment la changer:
      // 1/ SI la famille avec la pire note de teinte a une note de teinte supérieure à 2
      //    ALORS on la change alors avec la methode "changerTeinte"
      // 2/ SI la famille avec la pire note de teinte a une note de teinte supérieure à 1
      //    ALORS 2a/ SI la note de contraste de teinte de la carte globale est supérieure é
      //                 la note de contraste de clarté de la carte globale 
      //              ALORS on la change alors avec la methode "changerTeinte"
      //          2b/ SINON on change la famille avec la pire note de clarté 
      //                    avec la methode "changerClarté"
      // 3/ SINON on ne fait rien
      logger.info("3/ Modification des couleurs"); 
      @SuppressWarnings("unused")
      ColorimetricColor couleurFinale = this.familleCartoTeinteAChanger.getColor();
  
      if (this.pireNoteTeinte > 2) {
        logger.info("La famille '" + this.familleCartoTeinteAChanger.getName() 
                      + "' a un très mauvais contraste de teinte, elle doit changer sa teinte ("
                      + this.familleCartoTeinteAChanger.getColor().getUsualName().trim()+")");
          changerTeinteLUCIL(this.familleCartoTeinteAChanger);
          couleurFinale = this.familleCartoTeinteAChanger.getColor();
      }
      else if (this.pireNoteTeinte > 1 ) {
          if (this.map.getMeanContrast().getQualiteContrasteTeinte() > this.map.getMeanContrast().getQualiteContrasteClarte()) {
            logger.info("La famille '"+ this.familleCartoTeinteAChanger.getName()
                      + "' a un mauvais contraste de teinte, elle doit changer sa teinte ("
                      + this.familleCartoTeinteAChanger.getColor().getUsualName().trim().toLowerCase()+")");
              changerTeinteLUCIL(this.familleCartoTeinteAChanger);
              couleurFinale = this.familleCartoTeinteAChanger.getColor();
          }
  
          else {
            logger.info("La famille '" + this.familleCartoClarteAChanger.getName()
                      + "' a un mauvais contraste de clarté, elle doit changer sa clarté ("
                      + this.familleCartoClarteAChanger.getColor().getUsualName().trim().toLowerCase()+")");
            couleurFinale = changerClarteLUCIL(this.familleCartoClarteAChanger);
          }
      }
      else {
        logger.info("La carte est bien contrastée -> on ne change rien");
      }
      return this.map;
  }
  
  
  /** Changement de teinte
   * 
   * NB: Dans la version actuelle du processus, on suppose que la famille 
   *     n'est que dans une seule relation (mais avec plusieurs familles eventuellement).
   *     Les cas plus complexes ne sont pas gérés pour l'instant.
   *     
   * TODO: Voir si on peut traiter des cas un peu plus complexes?????? 
   */
  public static void changerTeinteLUCIL(SymbolisedFeatureCollection familleCarto){
      ColorReferenceSystem crs = ColorReferenceSystem.unmarshall(
              ColorReferenceSystem.class.getClassLoader().getResourceAsStream("color/ColorReferenceSystem.xml"));
      // On parcours l'ensemble des relations faisant intervenir la famille
      List<SemanticRelation> relations = familleCarto.getLegend().getRelations();
      
      // si la famille n'est pas en relation: on s'arrête là
      if (relations.size() == 0) return ;
      
      // FIXME On suppose qu'il n'y a de toutes facons qu'une relation au plus
      SemanticRelation relation = familleCarto.getLegend().getRelations().get(0);
      
      // familles concernees par la relation
      List<SymbolisedFeatureCollection> famillesEnRelation = 
        familleCarto.relatedFeatureCollections(relation); 

      // si la relation ne contient qu'une famille (cas de relations mal saisies), on s'arrête là
      if (famillesEnRelation.size() <2 ) return;
      
      famillesEnRelation.remove(familleCarto);
      
      ColorimetricColor couleurInitiale = familleCarto.getColor();
      ColorimetricColor couleurFinale;
      String teinte;
      int intensite ;

      // Si la famille est en relation d'ordre avec d'autres familles.
      //
      // On propose comme nouvelle couleur de la famille, une couleur qui a:
      // 1/ la même clarté que la couleur initiale de la famille
      // 2/ la teinte de la premiére famille concernée par la relation
      //    (i.e. met toutes les familles en relation dans la même teinte, 
      //          celle de la premiere famille de la liste)
      if (relation.getType() == SemanticRelation.ORDER) {
          logger.info("La famille participe à une relation d'ordre");
          SymbolisedFeatureCollection familleCartoAssociee = famillesEnRelation.get(0);
          ColorimetricColor couleurOrdre = familleCartoAssociee.getColor();
          teinte = couleurOrdre.getHue();
          logger.debug("teinte : " + teinte);
          intensite = couleurInitiale.getLightness();
          logger.debug("intensite : " + intensite);
          couleurFinale = new ColorimetricColor(teinte, intensite);
          familleCarto.getLegend().getSymbol().setColor(couleurFinale);
          logger.info("On lui affecte la teinte de la famille '"+famillesEnRelation.get(0).getName()+"' qui participe à cette relation");
          logger.info("> couleur choisie :  "+couleurFinale.getUsualName().trim().toLowerCase());
          return;
      }

      // Si la famille est en relation d'association avec d'autres familles.
      //
      // On propose comme nouvelle couleur de la famille, une couleur qui a:  
      // 1/ la même clarté que la couleur initiale de la famille
      // 2/ mais une teinte voisine (la premiére de la liste des teintes voisines!) 
      //    de la premiére famille concernée par la relation
      if (relation.getType() == SemanticRelation.ASSOCIATION) {
          logger.info("La famille participe à une relation d'association");
          SymbolisedFeatureCollection familleCartoAssociee = famillesEnRelation.get(0);
          
          List<ColorimetricColor> couleursVoisines = crs.getHueNeighborColors(familleCartoAssociee.getColor());
          couleurFinale = couleursVoisines.get(0);
          // FIXME NB Elodie: semble buggé????? me change la clarté aussi
          
          familleCarto.getLegend().getSymbol().setColor(couleurFinale);
          logger.info("On lui affecte une teinte voisine de la famille '"+famillesEnRelation.get(0).getName()+"' qui participe à cette relation");
          logger.info("> couleur choisie :  "+couleurFinale.getUsualName());
      }

      // TODO NB: Cas jamais rencontré dans les exemples (pas de dissociation). Code non testé donc.
      //
      // Si la famille est en relation de dissociaton avec d'autres familles.
      //
      // On propose comme nouvelle couleur de la famille, une couleur qui a 
      // 1/ la même clarté que la couleur initiale de la famille si celle-ci est entre 1 et 7, 
      //    1 si celle-ci est églae à 0
      //    7 si celle-ci est égale à 8
      // 2/ mais une teinte voisine (la deuxiéme de la liste!) de la premiére famille 
      //    concernée par la relation
      else if (relation.getType() == SemanticRelation.DIFFERENCE) {
          logger.info("La famille participe à une relation de dissociation");
          //changement lent, par les teintes voisines
          List<ColorimetricColor> couleursVoisines = crs.getHueNeighborColors(couleurInitiale);
          couleurFinale = couleursVoisines.get(1);
          if (couleurInitiale.getLightness() == 0){
              couleurFinale.setLightness(1);
          }else if (couleurInitiale.getLightness() ==8){
              couleurFinale.setLightness(7);      
          }
          familleCarto.getLegend().getSymbol().setColor(couleurFinale);
          logger.info("On lui affecte une teinte voisine, mais pas trop, de la famille '"+famillesEnRelation.get(0).getName()+"' qui participe à cette relation (à revoir)");
          logger.info("> couleur choisie :  "+couleurFinale.getUsualName().trim().toLowerCase());
      }

  }

  /** Changement de clarte
   * 
   * NB: Dans la version actuelle du processus, on suppose que la famille 
   *     n'est que dans une seule relation (mais avec plusieurs familles eventuellement).
   *     Les cas plus complexes ne sont pas gérés pour l'instant.
   *     
   * TODO: Voir si on peut  traiter des cas un peu plus complexes?????? 
   */
  public static ColorimetricColor changerClarteLUCIL(SymbolisedFeatureCollection familleCarto) {
      // On parcours l'ensemble des relations faisant intervenir la famille
      // On suppose qu'il n'y a de toutes facons qu'une relation au plus
      List<SemanticRelation> relations = familleCarto.getLegend().getRelations();
      
      // si la famille n'est pas en relation: on s'arrête là
      if (relations.size() == 0) return null;
      SemanticRelation relation = familleCarto.getLegend().getRelations().get(0);
      
      // familles concernees par la relation
      List<SymbolisedFeatureCollection> famillesEnRelation = familleCarto.relatedFeatureCollections(relation); 
      
      // TODO enelver la famille elle même? pas sér: famillesEnRelation.remove(this);
      // si la relation ne contient qu'une famille (cas de relations mal saisies), on s'arrête là
      if (famillesEnRelation.size() == 1 ) return null;
      
      ColorimetricColor couleurInitiale = familleCarto.getColor();
      ColorimetricColor couleurFinale=null;
      int intensite=0 ;

      // Si la famille est en relation d'ordre avec d'autres familles.
      //
      // 1/ SI la clarté initale de la famille courante est égale à 0:
      //    ALORS on propose la teinte grise et l'intensité égale à 1 pour la famille,  
      // 2/ SI la clarté initale de la famille est égale à 8:
      //    ALORS on propose la la teinte grise et l'intensité égale à 7 pour la famille,  
      // 3/ SINON, on parcourt toutes les familles en relation et pour chaque famille f 
      //       3a/ SI cette famille f n'a pas la même teinte de la famille courante,
      //           ALORS on lui applique la methode "changerTeinte"
      //       3b/ SI cette famille f est plus foncée que la famille courante, 
      //           ET si la différence d'intensité entre les familles est inférieure à 2
      //           ALORS on diminue l'intensité de la couleur de la fammile courante de 1
      //       3c/ SI cette famille f est moins foncée que la famille courante (on augmente le contraste de clarté), 
      //           ET si la différence d'intensité entre les familles est inférieure à 2
      //           ALORS on augmente l'intensité de la couleur de la fammile courante de 1 (on augmente le contraste de clarté)
      // 
      if (relation.getType() == SemanticRelation.ORDER) {
          if(couleurInitiale.getLightness() == 0) {
              intensite = 1;
              couleurFinale = new ColorimetricColor("gris", intensite);
          }
          else if(couleurInitiale.getLightness()== 8) {
              intensite = 7;
              couleurFinale = new ColorimetricColor("gris", intensite);
          }
          else {
              for (SymbolisedFeatureCollection familleCartoEnRelation : famillesEnRelation) {
                  ColorimetricColor couleur = familleCartoEnRelation.getColor();
                  //si false
                  if (!couleur.getHue().equals(couleurInitiale.getHue()))
                      changerTeinteLUCIL(familleCarto);

                  if (couleur.getLightness() > couleurInitiale.getLightness()
                          &&(couleur.getLightness() - couleurInitiale.getLightness()) < 2)
                      intensite = couleurInitiale.getLightness() - 1;
                  else if (couleur.getLightness() < couleurInitiale.getLightness()
                          && (couleurInitiale.getLightness() - couleur.getLightness()) < 2)
                      intensite = couleurInitiale.getLightness() + 1;
                  if (intensite == 8) intensite = 7;
                  else if (intensite == 0) intensite = 1;
              }
              couleurFinale = new ColorimetricColor(couleurInitiale.getHue().trim().toLowerCase(), intensite);
          }
      }

      // Si la famille est en relation d'association OU de dissociation avec d'autres familles.
      //
      // 1/ SI la clarté initale de la famille courante est égale à 0:
      //    ALORS on propose la la teinte grise et l'intensité égale à 1 pour la famille,  
      // 2/ SI la clarté initale de la famille est égale à 8:
      //    ALORS on propose la la teinte grise et l'intensité égale à 7 pour la famille,  
      // 3/ SINON, 
      //    3a/ SI la famille contient des surfaces, 
      //        ALORS on diminue son intensité de 1
      //    3b/ SI la famille contient des lignes, 
      //        ALORS on augmente son intensité de 1
      //    3b/ SI la famille contient des points, 
      //        ALORS on augmente son intensité de 2
      //
      else if (relation.getType() == SemanticRelation.ASSOCIATION || relation.getType() == SemanticRelation.DIFFERENCE) {
          if(couleurInitiale.getLightness() == 0) {
              intensite = 1;
              couleurFinale = new ColorimetricColor("gris", intensite);
          }
          else if(couleurInitiale.getLightness() == 8) {
              intensite = 7;
              couleurFinale = new ColorimetricColor("gris", intensite);
          }
          else {
              if (familleCarto.getGeometricType() == GraphicSymbol.UNKNOWN) {
                  familleCarto.getLegend().getSymbol().setTypeGeometrie(familleCarto.get(0));
              }
              // Eclaircir un polygone, mettre du foncé dans un point ou une ligne
              //FIXME ça ne marche pas quand on a des couleurs grisées ou des gris colorées...
              if (familleCarto.getGeometricType() == GraphicSymbol.SURFACES)
                  intensite = familleCarto.getColor().getLightness() - 1;
              else if(familleCarto.getGeometricType() == GraphicSymbol.LINES)
                  intensite = familleCarto.getColor().getLightness() + 1;
              else if(familleCarto.getGeometricType() == GraphicSymbol.POINTS)
                      intensite = familleCarto.getColor().getLightness() + 2;
              // Seb: avant passait de 8 à 1 et de 0 à 7 : bug je suppose
              if (intensite == 8) intensite = 7;
              else if (intensite == 0) intensite = 1;
              couleurFinale = new ColorimetricColor(couleurInitiale.getHue().trim().toLowerCase(), intensite);
          }
      }

      return couleurFinale;
  }
  
  /**
   * Pour toutes les familles de la carte en relation d'ordre,
   * on homogénéize les teintes en affectant la couleur de la famille "dominante"
   * à toutes les familles concernées par la relation d'ordre.
   * 
   * FIXME: et si une famille appartient à plusieurs relations d'ordre ??
   * TODO: on pourrait vérifier aussi que plusieurs relations d'ordre 
   * ne sont pas symbolisées par la même couleur dominante 
   */
  public void homogenizeOrderedSymbolisedFeatureCollection(){
    for (SymbolisedFeatureCollection famille : this.map.getSymbolisedFeatureCollections()) {
        // On parcours l'ensemble des relations faisant intervenir la famille
        // On suppose qu'il n'y a de toutes facons qu'une relation au plus
        List<SemanticRelation> relations = famille.getLegend().getRelations();
        
        // si la famille n'est pas en relation: on s'arrête là
        if (relations.size() == 0) continue;
        SemanticRelation relation = famille.getLegend().getRelations().get(0);

        // SI la famille participe à une relation d'ordre
        // ET SI la famille n'est pas la famille dominante de cette relation
        
        // Traitement systématique des familles en relation d'ordre
        // -> toutes les couleurs de familles en relation d'ordre prennent systématiquement
        //    la teinte de la couleur "dominante" de la famille
        // FIXME ça ne dépend pas des notes de contraste, la couleur dominante pourrait être 
        // celle de la famille la mieux contrastée parmi celles concernées par la relation d'ordre
        
        if (relation.getType() == SemanticRelation.ORDER) {
          ColorimetricColor referenceColor = famille.referenceRelatedFeatureCollection(relation).getColor();
          
          if (relation.getRelatedComponents().size()>4 && 
              (referenceColor.getWheel(ColorReferenceSystem.defaultColorRS()).getidSaturation()==1
                  ||referenceColor.getWheel(ColorReferenceSystem.defaultColorRS()).getidSaturation()==2)) {
            logger.info("Pas assez de couleur dispo dans ce quartier pour cette relation d'ordre");
            //TODO changer toutes les couleur vers le quartier correspondant.
          } else {
            if (famille != famille.referenceRelatedFeatureCollection(relation)) {
                // on récupére la couleur de la famille dominante
                ColorimetricColor newColor = new ColorimetricColor(
                        famille.referenceRelatedFeatureCollection(relation).getColor().getHue(),
                        famille.getColor().getLightness());
                famille.getLegend().getSymbol().setColor(newColor);
                logger.info("Prétraitement systématique des familles dans une relation d'ordre:");
                logger.info("  La famille " + famille.getName() + " doit changer sa teinte en " +
                        newColor.getHue()+ " pour respecter ses relations d'ordre");
                logger.info("  Couleur de la famille : " + famille.getColor().getUsualName()+ "\n"); 
            }
          }
        }
    }
    logger.info("");
  }
  
  /**
   * 
   */
  public void homogenizeOrderedSymbolisedFeatureCollection2(){
    for (SemanticRelation relation : this.map.getMapLegend().getSRD().getRelations()) {
      if (relation.getType() == SemanticRelation.ORDER) {
        ColorimetricColor colorRef = 
          ((LegendLeaf)relation.getRelatedComponents().get(0)).getSymbol().getColor();
        
        for (LegendComponent component : relation.getRelatedComponents()) {
          ColorimetricColor newColor = new ColorimetricColor(
              colorRef.getHue(),
              ((LegendLeaf)component).getSymbol().getColor().getLightness());
          ((LegendLeaf)component).getSymbol().setColor(newColor);
        }
        changerClarteRelationOrdre(relation);
      }
    }
  }
  
  public void changerClarteRelationOrdre(SemanticRelation relation){
    List<LegendComponent> components = relation.getRelatedComponents();
    int saturation = 
      ((LegendLeaf)components.get(0)).getSymbol()
      .getColor().getWheel(ColorReferenceSystem.defaultColorRS()).getidSaturation();
    int nbFamille = relation.getRelatedComponents().size();
    ColorimetricColor hueColor = ((LegendLeaf)components.get(0)).getSymbol().getColor();
    if (saturation == 1 || saturation == 2){
      switch (nbFamille) {
        case 2:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          break;
        case 3:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          break;
        case 4:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          ((LegendLeaf)components.get(3)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),1));
          break;
      }
    } else {
      switch (nbFamille) {
        case 2:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          break;
        case 3:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          break;
        case 4:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          ((LegendLeaf)components.get(3)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),1));
          break;
        case 5:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),4));
          ((LegendLeaf)components.get(3)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          ((LegendLeaf)components.get(4)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),1));
          break;
        case 6:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),6));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),4));
          ((LegendLeaf)components.get(3)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(4)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          ((LegendLeaf)components.get(4)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),2));
          break;
        case 7:
          ((LegendLeaf)components.get(0)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),7));
          ((LegendLeaf)components.get(1)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),6));
          ((LegendLeaf)components.get(2)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),5));
          ((LegendLeaf)components.get(3)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),4));
          ((LegendLeaf)components.get(4)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),3));
          ((LegendLeaf)components.get(4)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),2));
          ((LegendLeaf)components.get(4)).getSymbol().setColor(new ColorimetricColor(hueColor.getHue(),1));
          break;
      }
      
    }
  }
  
  public void sortContrasts(){
    // Recherche des familles qui ont les plus mauvaises notes de contraste et de teinte
    // ATTENTION: cela veut dire note la plus forte (note = 0 -> pas de problème)!!!!

    logger.info("2/ Bilan des contrastes"); 
    for (SymbolisedFeatureCollection familleCarto : this.map.getSymbolisedFeatureCollections()) {
      logger.trace("- "+familleCarto.getName()
                            + " -> teinte: "+familleCarto.getMeanContrast().getContrasteTeinte() 
                            + ", clarté: "+familleCarto.getMeanContrast().getContrasteClarte()
                            + ", qualité teinte: "+familleCarto.getMeanContrast().getQualiteContrasteTeinte()
                            + ", qualité clarté: "+familleCarto.getMeanContrast().getQualiteContrasteClarte());
        if (familleCarto.getMeanContrast().getQualiteContrasteTeinte() > pireNoteTeinte) {
          this.pireNoteTeinte = familleCarto.getMeanContrast().getQualiteContrasteTeinte();
          this.familleCartoTeinteAChanger = familleCarto;
        }
        if (familleCarto.getMeanContrast().getQualiteContrasteClarte() > pireNoteClarte){
          this.pireNoteClarte = familleCarto.getMeanContrast().getQualiteContrasteClarte();
          this.familleCartoClarteAChanger = familleCarto;
        }
    }
    logger.info("> Contraste global sur la carte"
            + " -> teinte: "+this.map.getMeanContrast().getContrasteTeinte() 
            + ", clarté: "+this.map.getMeanContrast().getContrasteClarte()
            + ", qualité teinte: "+this.map.getMeanContrast().getQualiteContrasteTeinte()
            + ", qualité clarté: "+this.map.getMeanContrast().getQualiteContrasteClarte());
    logger.info("> pireNoteTeinte : " + this.pireNoteTeinte + ", pireNoteClarte : " + this.pireNoteClarte);
    logger.info("");
    
    Collections.sort(this.map.getSymbolisedFeatureCollections(), new HueQualityContrastComparator());
//    for (int i = 0; i < this.map.getSymbolisedFeatureCollections().size(); i++) {
//      logger.info(i + "note famille " + this.map.getSymbolisedFeatureCollections().get(i).getName()
//          + " : " + this.map.getSymbolisedFeatureCollections().get(i)
//          .getMeanContrast().getQualiteContrasteTeinte());
//    }
    Collections.sort(this.map.getSymbolisedFeatureCollections(), new LightnessQualityContrastComparator());
//    for (int i = 0; i < this.map.getSymbolisedFeatureCollections().size(); i++) {
//      logger.info(i + "note famille " + this.map.getSymbolisedFeatureCollections().get(i).getName()
//          + " : " + this.map.getSymbolisedFeatureCollections().get(i)
//          .getMeanContrast().getQualiteContrasteClarte());
//    }
  }
}
