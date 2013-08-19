package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 
 * 
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "forceAppariementSimple",
    "redecoupageArcsNonApparies",
    "chercheRondsPoints",
    "filtrageImpassesParasites",
    "redecoupageNoeudsNonApparies",
    "distanceNoeudArc",
    "distanceProjectionNoeud"
})
@XmlRootElement(name = "ParamVarianteGeneralProcess")
public class ParamVarianteGeneralProcess {
  
    /**
     * Niveau de complexité: recherche ou non de liens 1-n aux noeuds. Si true: un
     * noeud du réseau 1 est toujours apparié avec au plus un noeud du réseau 2
     * (1-1). Si false (défaut): on recherche liens 1-n aux noeuds .
     * 
     * NB: dans le cas simple, le processus est énormément simplifié !!!!!!!! Ceci
     * peut être pertinent si les données ont le même niveau de détail par
     * exemple.
     */
    @XmlElement(name = "forceAppariementSimple")
    private boolean forceAppariementSimple = false;
  
    /**
     * Appariement en deux passes qui tente un surdécoupage du réseau pour les
     * arcs non appariés en première passe. Si true: les arcs du réseau 1 non
     * appariés dans une première passe sont redécoupés de manière à introduire un
     * noeud dans le reséau 1 aux endroits où il s'éloigne trop du réseau 2. Le
     * "trop" est égal à projeteNoeud2surReseau1_DistanceProjectionNoeud. Si false
     * (défaut): processus en une seule passe.
     * 
     * NB: pour l'instant, après ce re-découpage l'appariement est entièrement
     * refait, ce qui est long et très loin d'être optimisé: code à revoir !!!
     */
    @XmlElement(name = "SeuilFusionNoeuds")
    private boolean redecoupageArcsNonApparies = false;
    
    /**
     * Recherche des ronds-points (faces circulaires) dans le réseau 2, pour
     * éviter d'apparier un noeud du réseau 1 à une partie seulement d'un
     * rond-point (si une partie seulement est appariée, tout le rond point
     * devient apparié). NB: Paramètre utile uniquement pour les réseaux routiers
     * a priori.
     */
    @XmlElement(name = "ChercheRondsPoints")
    private boolean chercheRondsPoints = false;
    
    /**
     * Quand un arc est apparié à un ensemble d'arcs, élimine de cet ensemble les
     * petites impasses qui créent des aller-retour parasites (de longueur
     * inférieure à distanceNoeuds). NB: paramètre spécifique aux réseaux simples,
     * qui permet d'améliorer le recalage.
     */
    @XmlElement(name = "FiltrageImpassesParasites")
    private boolean filtrageImpassesParasites = false;
  
    
    /**
     * Appariement en deux passes qui tente un surdécoupage du réseau pour les
     * noeuds non appariés en première passe. Si true: les noeuds du réseau 1 non
     * appariés dans une première passe sont projetés au plus proche sur le réseau
     * 2 pour le découper Si false (défaut): processus en une seule passe.
     * 
     * Il s'agit en fait de la même opération que celle qui est faite quand
     * 'projeteNoeuds1SurReseau2'='true', mais uniquement pour les noeuds non
     * appariés en première passe, et avec des seuils éventuellement différents et
     * définis par les paramètres suivants : -
     * redecoupageNoeudsNonAppariesDistanceNoeudArc. -
     * redecoupageNoeudsNonAppariesDistanceProjectionNoeud
     * 
     * NB: pour l'instant, après ce re-découpage l'appariement est entièrement
     * refait, ce qui est long et très loin d'être optimal: à revoir à l'occasion,
     */
    @XmlElement(name = "RedecoupageNoeudsNonApparies")
    private boolean redecoupageNoeudsNonApparies = false;
    
    /**
     * Distance max de la projection des noeuds du réseau 1 sur le réseau 2.
     * Utilisé uniquement si varianteRedecoupageNoeudsNonApparies = true.
     */
    @XmlElement(name = "DistanceNoeudArc")
    private double distanceNoeudArc = 100;
    
    /**
     * Distance min entre la projection d'un noeud sur un arc et les extrémités de
     * cet arc pour créer un nouveau noeud sur le réseau 2 ? Utilisé uniquement si
     * varianteRedecoupageNoeudsNonApparies = true.
     */
    @XmlElement(name = "DistanceProjectionNoeud")
    private double distanceProjectionNoeud = 50;
    
  
    /**
     * Constructor.
     */
    public ParamVarianteGeneralProcess() {
        forceAppariementSimple = false;
        redecoupageArcsNonApparies = false;
        chercheRondsPoints = false;
        filtrageImpassesParasites = false;
        
        redecoupageNoeudsNonApparies = false;
        distanceNoeudArc = 100;
        distanceProjectionNoeud = 50;
    }
    
    
    
    public void setForceAppariementSimple(boolean forceAppariementSimple) {
        this.forceAppariementSimple = forceAppariementSimple;
    }
    
    public boolean getForceAppariementSimple() {
        return this.forceAppariementSimple;
    }
    
    public void setRedecoupageArcsNonApparies(boolean redecoupageArcsNonApparies) {
        this.redecoupageArcsNonApparies = redecoupageArcsNonApparies;
    }
    
    public boolean getRedecoupageArcsNonApparies() {
        return this.redecoupageArcsNonApparies;
    }
    
    public void setChercheRondsPoints(boolean chercheRondsPoints) {
        this.chercheRondsPoints = chercheRondsPoints;
    }
    
    public boolean getChercheRondsPoints() {
        return chercheRondsPoints;
    }
    
    public void setFiltrageImpassesParasites(boolean b) {
        filtrageImpassesParasites = b;
    }
    
    public boolean getFiltrageImpassesParasites() {
        return filtrageImpassesParasites;
    }
    
    
    
    public void setRedecoupageNoeudsNonApparies(boolean b) {
        redecoupageNoeudsNonApparies = b;
    }
    
    public boolean getRedecoupageNoeudsNonApparies() {
        return redecoupageNoeudsNonApparies;
    }
    
    public void setDistanceNoeudArc(double d) {
        distanceNoeudArc = d;
    }
    
    public double getDistanceNoeudArc() {
        return distanceNoeudArc;
    }
    
    public void setDistanceProjectionNoeud(double d) {
        distanceProjectionNoeud = d;
    }
    
    public double getDistanceProjectionNoeud() {
        return distanceProjectionNoeud;
    }

}
