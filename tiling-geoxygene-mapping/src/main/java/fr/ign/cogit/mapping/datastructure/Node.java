package fr.ign.cogit.mapping.datastructure;

import org.geotools.coverage.processing.operation.Scale;

import fr.ign.cogit.mapping.datastructure.hexaTree.HexaNode;
import fr.ign.cogit.mapping.datastructure.hexaTree.TransmittedNode;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;

/*
 * @author Dr Dieudonne Tsatcha
 * Cette classe permet de stocker les informations
 * necessaires en dehors de la geometrie de l'objet
 * extraite d'une base de données postgis.
 * Le noeud joue un role important dans la defintion
 * de l'elements. Il doit permettre de construire un noeud unique
 * dans la table des indexes.
 * Plustard nous pourrons introduire des notions de temps
 * afin retirer regulièrementd dans la base de données des contenus
 * qui n'ont pas été recemment mis à jour.
 * @see NodeUtil.java
 * @see ScaleInfo
 */
public class Node {

    /*
     * @param schemaName designe le schema de la base de données
     */

    /*
     * @param tablename designe le nom de la table
     */
    public String tableName;

    /*
     * @param type designe le type de la données
     */
    public String signature; // geometrie ou empreinte

    /*
     * @param srid dessigne son srid
     */
    public String srid;
    /*
     * @param scale designe les informations sur l'echelle
     * 
     * @see Scale
     */
    public ScaleInfo scale;
    /**
     * @param tableName
     * @param type
     * @param srid
     * @param scale
     */

    public String geoType;

    /*
     * @param tableId designe l'indentifiant du tuple de la table qui permet de
     * fabriquer un noeud unique
     */
    public int tableId;

    // informations necessaire actuellement
    // pour stocker un element dans la base
    // de données
    /*
     * @param transmittedInfo
     * designe les informations utilisées pour la transmission
     * entre l'index et le client
     */
    public TransmittedNode transmittedInfo;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSrid() {
        return srid;
    }

    public void setSrid(String srid) {
        this.srid = srid;
    }

    public ScaleInfo getScale() {
        return scale;
    }

    public void setScale(ScaleInfo scale) {
        this.scale = scale;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public Node(String sridParam, ScaleInfo scaleParam) {
        super();
        this.srid = sridParam;
        this.scale = scaleParam;
    }

    /**
     * @param signature
     * @param srid
     * @param scale
     */
    public Node(String sridParam, ScaleInfo scaleParam, String signatureParam) {
        super();
        this.signature = signatureParam;
        this.srid = sridParam;
        this.scale = scaleParam;
    }

    /**
     * @param tableName
     *            Designe le nom de la table à indexer dans la base de données,
     *            ce nom est different du nom de l'echelle car l'echelle permet
     *            d'indexer plusieurs table
     * @param signature
     * @param srid
     * @param scale
     */
    public Node(String sridParam, ScaleInfo scaleParam, String signatureParam,
            String tableNameParam) {
        super();
        this.signature = signatureParam;
        this.srid = sridParam;
        this.scale = scaleParam;
        this.tableName = tableNameParam;
    }

    /**
     * @param tableName
     *            Designe le nom de la table à indexer dans la base de données,
     *            ce nom est different du nom de l'echelle car l'echelle permet
     *            d'indexer plusieurs table
     * @param signature
     * @param srid
     * @param scale
     */
    public Node(String sridParam, ScaleInfo scaleParam, String signatureParam,
            String tableNameParam, int tableIdParam, TransmittedNode transmittedInfoParam) {
        super();
        this.signature = signatureParam;
        this.srid = sridParam;
        this.scale = scaleParam;
        this.tableName = tableNameParam;
        this.tableId = tableIdParam;
        transmittedInfo= transmittedInfoParam;
    }

    /**
     * @param tableName
     *            Designe le nom de la table à indexer dans la base de données,
     *            ce nom est different du nom de l'echelle car l'echelle permet
     *            d'indexer plusieurs table
     * @param signature
     * @param srid
     * @param scale
     *            Ce construcuteur sera appelé une fois qu'on trouvera la facon
     *            la plus optimale d'introduire le type de l'entité
     */
    public Node(String sridParam, ScaleInfo scaleParam, String signatureParam,
            String tableNameParam, int tableIdParam, String geoTypeParam) {
        super();
        this.signature = signatureParam;
        this.srid = sridParam;
        this.scale = scaleParam;
        this.tableName = tableNameParam;
        this.tableId = tableIdParam;
        this.geoType = geoTypeParam;
    }

    public String toString() {
        return scale.toString() 
                + "[srid" + ":" + srid + "]" + " "
                + "[signature" + ":" + signature + "]" + " " 
                + "[tableName"  + ":" + tableName + "]" + " "
                + "[tableID" + ":" + String.valueOf(tableId) + "]"+ " "
                + transmittedInfo.toString();
    }
    
    
    public Node(String sridParam, ScaleInfo scaleParam, String signatureParam,
            String tableNameParam, int tableIdParam, String geoTypeParam,
            TransmittedNode transmittedInfoParam) {
        super();
        this.signature = signatureParam;
        this.srid = sridParam;
        this.scale = scaleParam;
        this.tableName = tableNameParam;
        this.tableId = tableIdParam;
        this.geoType = geoTypeParam;
        transmittedInfo= transmittedInfoParam;
    }
    
    

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object) permet de verifier
     * l'galité entre deux noeuds
     */
//    public boolean equals(Object obj) {
//        if (!(obj instanceof HexaNode))
//            return false;
//        Node node = (Node) obj;
//        if (scale.getIdScale() == node.getScale().getIdScale()
//                && signature == node.getSignature()
//                && tableName == node.getTableName()
//                && tableId == node.getTableId()) {
//            return true;
//        }
//
//        return false;
//    }
    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     * cette egalité permet de recuperer les informations
     * sur des cellules réalisées après transmission..;
     */
    
    public boolean equals(Object obj) {
        if (!(obj instanceof HexaNode))
            return false;
        Node node = (Node) obj;
        if (scale.getIdScale() == node.getScale().getIdScale()
                && signature == node.getSignature()
                && tableName == node.getTableName()
                && tableId == node.getTableId() && transmittedInfo.getGeoOrder()==
                node.getTransmittedInfo().getGeoOrder() && transmittedInfo.getHexaIndiceI()==
                node.getTransmittedInfo().getHexaIndiceI() && 
                transmittedInfo.getHexaIndiceJ()==node.getTransmittedInfo().getHexaIndiceJ() ) {
            return true;
        }

        return false;
    }

    public TransmittedNode getTransmittedInfo() {
        return transmittedInfo;
    }

    public void setTransmittedInfo(TransmittedNode transmittedInfo) {
        this.transmittedInfo = transmittedInfo;
    }
    
    
    
    

}
