package fr.ign.cogit.mapping.datastructure.hexaTree;

public class TransmittedNode {
    
    /**
     * @param scaleId
     * @param hexaIndiceI
     * @param hexaIndiceJ
     * @param geoOrder
     * @author Dr Tsatcha D.
     */
    public TransmittedNode(int hexaIndiceI, int hexaIndiceJ,
            int geoOrder) {
        super();
        this.hexaIndiceI = hexaIndiceI;
        this.hexaIndiceJ = hexaIndiceJ;
        this.geoOrder = geoOrder;
        // a l'intialisation le type est inconnu...
        this.geoType=" ";
    }
    
    
    
    public TransmittedNode(int hexaIndiceI, int hexaIndiceJ,
            int geoOrder, String geoTypeParam) {
        super();
        this.hexaIndiceI = hexaIndiceI;
        this.hexaIndiceJ = hexaIndiceJ;
        this.geoOrder = geoOrder;
        // a l'intialisation le type est inconnu...
        this.geoType=geoTypeParam;
    }
    
    
    
    public TransmittedNode(int hexaIndiceI, int hexaIndiceJ,
            int geoOrder, String geoTypeParam, int cadreNumberParam, int hexNumberParam) {
        super();
        this.hexaIndiceI = hexaIndiceI;
        this.hexaIndiceJ = hexaIndiceJ;
        this.geoOrder = geoOrder;
        // a l'intialisation le type est inconnu...
        this.geoType=geoTypeParam;
        cadreNumber=cadreNumberParam;
        hexNumber=hexNumberParam;
    }

    /*
     * @param scaleId
     * designe l'id de l'echelle
     */
    protected  int scaleId;
    
    /*
     * @param hexaIndiceI
     * designe l'indice en I de l'hexagone
     */
    protected int hexaIndiceI;
    
    
    /*
     * @param hexaIndiceJ
     * designe l'indice en J de l'hexagone
     */
    protected int hexaIndiceJ;
    
    /*
     * @param geoOrder
     * designe la position de la geometrie
     * parmi les geometries associé à  l'hexagone
     */
     protected int geoOrder;
    
    
    /*
     * @param geoType
     * designe le type de geometries de l'objet
     * ceci est du au fait que la transmission 
     * peut cause la perte du type de l'objet
     * pour garder ses sous types realiser... on doit
     * garder le type de geometrie ainsi construite...
     */
     /*
      * @param cadreNumber
      * designe le numéro du cadre qui contient
      * la cellule hexagonale 0--8. egale à - à l'intialisation
      */
     protected int cadreNumber;
     
     /*
      * @param hexNumber
      * designe le numero de la cellule hexagone contenu dans le cadre
      * egale à -1 à l'intialisation.../
      * 
      */
     
     protected int hexNumber;
     
    
     public int getCadreNumber() {
        return cadreNumber;
    }



    public void setCadreNumber(int cadreNumber) {
        this.cadreNumber = cadreNumber;
    }



    public int getHexNumber() {
        return hexNumber;
    }



    public void setHexNumber(int hexNumber) {
        this.hexNumber = hexNumber;
    }


    protected String  geoType;

    public int getScaleId() {
        return scaleId;
    }

    public void setScaleId(int scaleId) {
        this.scaleId = scaleId;
    }

    public int getHexaIndiceI() {
        return hexaIndiceI;
    }

    public void setHexaIndiceI(int hexaIndiceI) {
        this.hexaIndiceI = hexaIndiceI;
    }

    public int getHexaIndiceJ() {
        return hexaIndiceJ;
    }

    public void setHexaIndiceJ(int hexaIndiceJ) {
        this.hexaIndiceJ = hexaIndiceJ;
    }

    public int getGeoOrder() {
        return geoOrder;
    }

    public String getGeoType() {
        return geoType;
    }



    public void setGeoType(String geoType) {
        this.geoType = geoType;
    }



    public void setGeoOrder(int geoOrder) {
        this.geoOrder = geoOrder;
    }

//    public String toString() {
//        return  "[hexaIndiceI" + ":"+ hexaIndiceI + "]" + " " +
//                "[hexaIndiceJ" + ":" + hexaIndiceJ + "]"+ " "+ 
//                "[geoOrder" + ":" + geoOrder + "]" +  " "+ 
//                "[geoType" + ":" + geoType + "]";
//                
//    }
    
    public String toString() {
        return  "[hexaIndiceI" + ":"+ hexaIndiceI + "]" + " " +
                "[hexaIndiceJ" + ":" + hexaIndiceJ + "]"+ " "+ 
                "[geoOrder" + ":" + geoOrder + "]" +  " "+ 
                "[geoType" + ":" + geoType + "]"+ " "+
                "[cadreNumber" + ":" + cadreNumber + "]"+ " "+
                "[hexNumber" + ":" + hexNumber + "]";
                
    }

    

}
