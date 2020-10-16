package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.process;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.contrib.I18N;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.ParametresApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.GroupeApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * 
 * 
 *
 */
public class ExportAttribut {
    
    /** LOGGER. */
    protected static Logger LOGGER = LogManager.getLogger(ExportAttribut.class.getName());

    /**
     * Méthode qui renvoie en sortie des liens 1 ARC - 1 ARC.
     * Cette méthode crée une géométrie aux liens au passage.
     * 
     * @param liensReseaux
     * @param param
     * @return a set of links between the two networks given as arguments
     */
    public static EnsembleDeLiens exportAttributs(EnsembleDeLiens liensReseaux, ParametresApp param, SchemaDefaultFeature schema) {

        EnsembleDeLiens liensGeneriques = new EnsembleDeLiens();
        liensGeneriques.setNom(liensReseaux.getNom());

        // On compile toutes les populations du reseau 1 [resp. 2] dans une liste
        List<IFeatureCollection<? extends IFeature>> pops1 = new ArrayList<IFeatureCollection<? extends IFeature>>(
                param.populationsArcs1);
        pops1.addAll(param.populationsNoeuds1);
        List<IFeatureCollection<? extends IFeature>> pops2 = new ArrayList<IFeatureCollection<? extends IFeature>>(
                param.populationsArcs2);
        pops2.addAll(param.populationsNoeuds2);

        // Boucle sur les liens entre cartes topo
        Iterator<Lien> itLiensReseaux = liensReseaux.iterator();
        while (itLiensReseaux.hasNext()) {

            LienReseaux lienReseau = (LienReseaux) itLiensReseaux.next();

            // On récupère tous les objets des cartes topo concernés
            Set<IFeature> objetsCT1PourUnLien = new HashSet<IFeature>(lienReseau.getArcs1());
            objetsCT1PourUnLien.addAll(lienReseau.getNoeuds1());
            Iterator<Groupe> itGroupes1 = lienReseau.getGroupes1().iterator();
            while (itGroupes1.hasNext()) {
                GroupeApp groupe1 = (GroupeApp) itGroupes1.next();
                objetsCT1PourUnLien.addAll(groupe1.getListeArcs());
                objetsCT1PourUnLien.addAll(groupe1.getListeNoeuds());
            }

            Set<IFeature> objetsCT2PourUnLien = new HashSet<IFeature>(lienReseau.getArcs2());
            objetsCT2PourUnLien.addAll(lienReseau.getNoeuds2());
            Iterator<Groupe> itGroupes2 = lienReseau.getGroupes2().iterator();
            while (itGroupes2.hasNext()) {
                GroupeApp groupe2 = (GroupeApp) itGroupes2.next();
                objetsCT2PourUnLien.addAll(groupe2.getListeArcs());
                objetsCT2PourUnLien.addAll(groupe2.getListeNoeuds());
            }

            // On parcours chaque couple d'objets de cartes topos appariés
            Iterator<IFeature> itObjetsCT1PourUnLien = objetsCT1PourUnLien.iterator();
            while (itObjetsCT1PourUnLien.hasNext()) {
                
                IFeature objetCT1 = itObjetsCT1PourUnLien.next();
                Collection<IFeature> objets1 = LienReseaux.getCorrespondants(objetCT1, pops1);
                
                Iterator<IFeature> itObjetsCT2PourUnLien = objetsCT2PourUnLien.iterator();
                while (itObjetsCT2PourUnLien.hasNext()) {

                    IFeature objetCT2 = itObjetsCT2PourUnLien.next();
                    Collection<IFeature> objets2 = LienReseaux.getCorrespondants(objetCT2, pops2);
                    
                    if (objetCT1.getGeom() instanceof GM_LineString && objetCT2.getGeom() instanceof GM_LineString) {
                    
                        if (objets1.isEmpty() && objets2.isEmpty()) {
                            // Cas où il n'y a pas de correspondant dans les données de départ des 2 côtés
                            Lien lienG = liensGeneriques.nouvelElement();
                            lienG.setEvaluation(lienReseau.getEvaluation());
                            lienG.setCommentaire(I18N.getString("LienReseaux.NoCorrespondentInBothDatabases")); //$NON-NLS-1$
                            if (param.exportGeometrieLiens2vers1) {
                                lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT1, objetCT2));
                            } else {
                                lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT2, objetCT1));
                            }
                            
                            // On affecte le schema au lien 
                            lienG.setSchema(schema);
                            // Attributs a transferer
                            int cpt = 0;
                            Object[] valAttribute = new Object[schema.getFeatureType().getFeatureAttributes().size()];
                                
                            // On recupere la liste des attributs a affecter
                            GF_FeatureType bzz = schema.getFeatureType();
                            List<GF_AttributeType> listAttribut = bzz.getFeatureAttributes();
                            for (int j = 0; j < listAttribut.size(); j++) {
                                    
                                String attribut = listAttribut.get(j).getMemberName();
                                
                                // TODO : bug à corriger si on a le même attribut dans les 2 reseaux, mais est-ce possible ?? 
                                    
                                // On le cherche dans l'arc 1
                                for (int k = 0; k < objetCT1.getFeatureType().getFeatureAttributes().size(); k++) {
                                    GF_AttributeType attributeType = objetCT1.getFeatureType().getFeatureAttributes().get(k);
                                    String attributArc1 = attributeType.getMemberName();
                                    if (attribut.equals("OBJECTID1") && attributArc1.equals("OBJECTID")) {
                                        valAttribute[cpt] = objetCT1.getAttribute("OBJECTID");
                                        cpt++;
                                        LOGGER.debug("Attribut " + attributArc1 + " transféré.");
                                    } else if (attributArc1.equals(attribut)) {
                                        valAttribute[cpt] = objetCT1.getAttribute(attributArc1);
                                        cpt++;
                                        LOGGER.debug("Attribut " + attributArc1 + " transféré.");
                                    }
                                }
                                
                                // On le cherche dans l'arc 2
                                for (int k = 0; k < objetCT2.getFeatureType().getFeatureAttributes().size(); k++) {
                                    GF_AttributeType attributeType = objetCT2.getFeatureType().getFeatureAttributes().get(k);
                                    String attributArc2 = attributeType.getMemberName();
                                    //System.out.println(attributArc2);
                                    if (attribut.equals("OBJECTID2") && attributArc2.equals("OBJECTID")) {
                                        valAttribute[cpt] = objetCT2.getAttribute("OBJECTID");
                                        cpt++;
                                        LOGGER.debug("Attribut " + attributArc2 + " transféré.");
                                    } else if (attributArc2.equals(attribut)) {
                                        valAttribute[cpt] = objetCT2.getAttribute(attributArc2);
                                        cpt++;
                                        LOGGER.debug("Attribut " + attributArc2 + " transféré.");
                                    }
                                }
                                
                            }
                            
                            // On ajoute les attributs
                            lienG.setAttributes(valAttribute);
                            
                            // On passe au suivant
                            continue;
                        }

                    
                        if (objets1.isEmpty()) {
                            
                            System.out.println("**");
                            
                            // Cas où il n'y a pas de correspondant dans les données de BD1
                            /*Iterator<? extends IFeature> itObjets2 = objets2.iterator();
                            while (itObjets2.hasNext()) {
                                IFeature objet2 = itObjets2.next();
                                Lien lienG = liensGeneriques.nouvelElement();
                                lienG.setEvaluation(lienReseau.getEvaluation());
                                lienG.setCommentaire(I18N.getString("LienReseaux.NoCorrespondentInDB1")); //$NON-NLS-1$
                                if (param.exportGeometrieLiens2vers1) {
                                    lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT1, objet2));
                                } else {
                                    lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet2, objetCT1));
                                }
                                lienG.addObjetComp(objet2);
                            }
                            continue;*/
                        }

                        if (objets2.isEmpty()) {
                            
                            System.out.println("++");
                            
                            // Cas où il n'y a pas de correspondant dans les données de BD2
                            /*Iterator<? extends IFeature> itObjets1 = objets1.iterator();
                            while (itObjets1.hasNext()) {
                                IFeature objet1 = itObjets1.next();
                                Lien lienG = liensGeneriques.nouvelElement();
                                lienG.setEvaluation(lienReseau.getEvaluation());
                                lienG.setCommentaire(I18N.getString("LienReseaux.NoCorrespondentInDB1")); //$NON-NLS-1$
                                if (param.exportGeometrieLiens2vers1) {
                                    lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet1, objetCT2));
                                } else {
                                    lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objetCT2, objet1));
                                }
                                lienG.addObjetRef(objet1);
                            }
                            continue;*/
                        }

                        // cas où il y a des correspondants dans les deux BD
                        /*Iterator<? extends IFeature> itObjets1 = objets1.iterator();
                        while (itObjets1.hasNext()) {
                            IFeature objet1 = itObjets1.next();
                            Iterator<? extends IFeature> itObjets2 = objets2.iterator();
                            while (itObjets2.hasNext()) {
                                IFeature objet2 = itObjets2.next();
                                Lien lienG = liensGeneriques.nouvelElement();
                                lienG.setEvaluation(lienReseau.getEvaluation());
                                lienG.setCommentaire(""); //$NON-NLS-1$
                                lienG.setReference(Integer.toString(objet1.getId()));
                                lienG.setComparaison(Integer.toString(objet2.getId()));
                                if (param.exportGeometrieLiens2vers1) {
                                    lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet1, objet2));
                                } else {
                                    lienG.setGeom(LienReseaux.creeGeometrieLienSimple(objet2, objet1));
                                }
                                lienG.addObjetRef(objet1);
                                lienG.addObjetComp(objet2);
                            }
                        }*/
                    
                    } // fin ARC - ARC

                }
            }
        }

        // Retour
        return liensGeneriques;
    }

}
