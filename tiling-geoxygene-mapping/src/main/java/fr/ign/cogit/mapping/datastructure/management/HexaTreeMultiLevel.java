package fr.ign.cogit.mapping.datastructure.management;

import java.util.concurrent.ConcurrentHashMap;

import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTreeIndex;
/*
 * @author Dr Tsatcha D.
 */

public class HexaTreeMultiLevel {

    
    ConcurrentHashMap<ScaleInfo,HexaTreeIndex> multiLevelIndex = new ConcurrentHashMap<ScaleInfo, HexaTreeIndex>();
    //ConcurrentHashMap<PointGeo,VoisinsNode> test1 = new ConcurrentHashMap<PointGeo, VoisinsNode>();

    public ConcurrentHashMap<ScaleInfo, HexaTreeIndex> getMultiLevelIndex() {
        return multiLevelIndex;
    }

    public void setMultiLevelIndex(
            ConcurrentHashMap<ScaleInfo, HexaTreeIndex> multiLevelIndex) {
        this.multiLevelIndex = multiLevelIndex;
    }

    public HexaTreeMultiLevel(
            ConcurrentHashMap<ScaleInfo, HexaTreeIndex> multiLevelIndexParam) {
        super();
        this.multiLevelIndex = multiLevelIndexParam;
    }
}

