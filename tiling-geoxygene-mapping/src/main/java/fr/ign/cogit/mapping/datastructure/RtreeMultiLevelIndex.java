package fr.ign.cogit.mapping.datastructure;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;


public class RtreeMultiLevelIndex {
    
    /**
     * Cette classe permet de construire une base de données necessaire
     * pour la résoudre le probleme de la transmission multi-echelle
     * Dans ce cas, elle doit fabriquer  plusieurs rtree pour chaque
     * echelle. Les données necessaires pour un rtree sont indiquées
     * par une index et par un repertoire..;
     * @uthor Dtsatcha
     * @see ManageRtreeMultiLevel
     * @see ScaleInfo
     * @see RTreeIndex
     */
    /*
     * designe le nombre d'echelle
     */
  // protected int taille;
    /*
     * Defintion de la Rtree muulti-echelle afin
     * de gerer les problemes muuti echelles
     * Nous avons choisi une structure de données à acces
     * concurent
     */
    //protected List<RTreeIndex> multiLevelIndex ;
    /*
     * La cle de cette structure de données designe chaque 
     * echelle par exemple nous aurons .. echelle 1 scale0, scale1..etc...
     */
    ConcurrentHashMap<ScaleInfo,RTreeIndex> multiLevelIndex = new ConcurrentHashMap<ScaleInfo, RTreeIndex>();
    //ConcurrentHashMap<PointGeo,VoisinsNode> test1 = new ConcurrentHashMap<PointGeo, VoisinsNode>();

    public ConcurrentHashMap<ScaleInfo, RTreeIndex> getMultiLevelIndex() {
        return multiLevelIndex;
    }

    public void setMultiLevelIndex(
            ConcurrentHashMap<ScaleInfo, RTreeIndex> multiLevelIndex) {
        this.multiLevelIndex = multiLevelIndex;
    }

    public RtreeMultiLevelIndex(
            ConcurrentHashMap<ScaleInfo, RTreeIndex> multiLevelIndexParam) {
        super();
        this.multiLevelIndex = multiLevelIndexParam;
    }
}
