package fr.ign.cogit.mapping.datastructure.hexaTree;

/*
 * Cette classe permet de modeliser une famille dans la structure
 * hierarchique : 
 * Nous avons la cellule courante, ses soeurs, sa fille directe
 * racine, sa mère directe...
 * Cette classe permet de modeliser la cellule courante et les relations
 * entre ses soeurs, père et son fils 
 *    _______________
 *   / parent cell    \
 *  /     ________     \
 * |     /current \     |
 * |    /   ____   \    |
 * |   |   /    \   |   |
 * |   |  |Child |  |   |
 * |   |  |      |  |   |
 * |   |   \____/   |   |
 * |    \  cell    /    |
 * |     \________/     |
 * \                   /
 *  \_________________/                  
 * @author Dr Tsatcha
 */

public class HexaFamilly {
    /*
     * @param directParent Designe la mère directe de la cellule
     */
    protected HexaNode directParent;

    /*
     * @param directChild designe la fille directe de la cellule
     */

    protected HexaNode directChild;

    /*
     * @param cellSisters
     */

    protected HexaNeighbor cellSisters;

    /*
     * @return la fille directe
     */
    public HexaNode getDirectParent() {
        return directParent;
    }

    /*
     * @param directParent
     */
    public void setDirectParent(HexaNode directParent) {
        this.directParent = directParent;
    }

    /*
     * @return le fille directe
     */
    public HexaNode getDirectChild() {
        return directChild;
    }

    /*
     * @param directChild
     */
    public void setDirectChild(HexaNode directChild) {
        this.directChild = directChild;
    }

    /*
     * @return les cellules soeurs
     */
    public HexaNeighbor getCellSisters() {
        return cellSisters;
    }

    /*
     * @param cellSisters
     */
    public void setCellSisters(HexaNeighbor cellSisters) {
        this.cellSisters = cellSisters;
    }

    /**
     * @param directParentParam
     * @param directChildParam
     * @param cellSistersParam
     */
    public HexaFamilly(HexaNode directParentParam, HexaNode directChildParam,
            HexaNeighbor cellSistersParam) {
        super();
        this.directParent = directParentParam;
        this.directChild = directChildParam;
        this.cellSisters = cellSistersParam;
    }

}
