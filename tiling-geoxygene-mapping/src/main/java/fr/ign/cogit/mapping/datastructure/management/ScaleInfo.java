package fr.ign.cogit.mapping.datastructure.management;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;

import fr.ign.cogit.mapping.clients.Converter;

/*
 * Cette classe permet de recuperer les informations
 * necessaires à la transmission des echelles
 * @author Dtsatcha
 * @see RtreeMultiLevelIndex
 * @see ManageRtreeMultiLevel
 * Une echelle est definie par l'intervalle [@minScaleValue, @maxScaleValue[
 */
public class ScaleInfo {
    /*
     * @param minScaleValue designe la valeur minimale en dessous de laquelle on
     * charge les elements de l'echelle inferieure
     */

    protected int minScaleValue;

    /*
     * @param maxScaleValue designe la valeur maximale au dessus de laquelle on
     * charge les elements de l'echelle supérieure
     */

    protected int maxScaleValue;

    /*
     * @param idScale designe l'identifiant de l'echelle
     */

    protected int idScale;

    /*
     * @param nameScale designe le nom de l'echelle qui doit est etre une
     * compisition entre le terme "scale"+ "id"
     */

    protected String nameScale;

    /*
     * les coordonnées réelles de l'echelle
     */
    /*
     * @param le coin inférieur droite
     */
    protected HyperPoint bornInfScale = new HyperPoint(new double[] { 0, 0 });
    /*
     * @param bornSupScale designe le coin supérieure gauche
     */

    protected HyperPoint bornSupScale = new HyperPoint(new double[] { 0, 0 });

    /*
     * Designe le plus grand echelle utilisée pour les elements couramment
     * MaxRectangle intialisée actuellement comme un point.
     */

    protected HyperBoundingBox MaxRectangle = null;

    // new HyperBoundingBox(bornInfScale,bornSupScale);;
    
    
    protected Converter convertisseurInfo = null;


    /*
     * Designe l'echelle de transmission associé à cette echelle vers le
     * disposisitif de visualisation est proportionnelle à l'echelle de
     * visualisation
     * 
     * @param transmittedScaleX
     */
    protected double transmittedScaleX=1;

    /*
     * idem sur l'axe des ordonnées
     * 
     * @param transmittedScaleY
     */

    protected double transmittedScaleY=1;
    
    
    
    /*
     * @param cadreName
     * designe le nom du cadre... utilisée pour le tuilage
     * au niveau du client...
     */
    
    protected String CadreName;

    public String getCadreName() {
        return CadreName;
    }

    public void setCadreName(String cadreName) {
        CadreName = cadreName;
    }

    public double getTransmittedScaleX() {
        return transmittedScaleX;
    }

    public void setTransmittedScaleX(double transmittedScaleX) {
        this.transmittedScaleX = transmittedScaleX;
    }

    public double getTransmittedScaleY() {
        return transmittedScaleY;
    }

    public void setTransmittedScaleY(double transmittedScaleY) {
        this.transmittedScaleY = transmittedScaleY;
    }

    /*
     * @return le rectangle maximal dans lequel sont insérés tous les elements
     * de l'echelle
     */
    public HyperBoundingBox getMaxRectangle() {
        return MaxRectangle;
    }

    public void setMaxRectangle(HyperBoundingBox maxRectangleParam) {
        MaxRectangle = maxRectangleParam;
    }

    /**
     * @param minScaleValue
     * @param maxScaleValue
     * @param idScale
     * @param nameScale
     * @param maxRectangle
     */
    public ScaleInfo(int minScaleValue, int maxScaleValue, int idScale,
            String nameScale, HyperBoundingBox maxRectangle) {
        super();
        this.minScaleValue = minScaleValue;
        this.maxScaleValue = maxScaleValue;
        this.idScale = idScale;
        this.nameScale = nameScale;
        MaxRectangle = maxRectangle;

    }

    /**
     * @param minScaleValue
     * @param maxScaleValue
     * @param idScale
     */
    public ScaleInfo(int idScaleParam, int minScaleValueParam,
            int maxScaleValueParam) {
        super();
        this.minScaleValue = minScaleValueParam;
        this.maxScaleValue = maxScaleValueParam;
        this.idScale = idScaleParam;
        this.nameScale = "scale" + String.valueOf(idScale);
        // MaxRectangle=new HyperBoundingBox(bornInfScale,bornSupScale);

    }

    /**
     * Ce contructeur permet de contruire un objet echelle en considerant
     * simplement les valeurs [@minScaleValue, @maxScaleValue[ dans ce
     * identifiant est égal minScaleValue
     * 
     * @param minScaleValue
     * @param maxScaleValue
     */

    public ScaleInfo(int minScaleValueParam, int maxScaleValueParam) {
        super();
        this.minScaleValue = minScaleValueParam;
        this.maxScaleValue = maxScaleValueParam;
        this.idScale = minScaleValueParam;
        this.nameScale = "scale" + String.valueOf(idScale);
        // MaxRectangle=new HyperBoundingBox(bornInfScale,bornSupScale);

    }

    /*
     * Un constructeur de l'echelle en considerant que la table de la base de
     * données à indexer est renseigné
     */

    // retourne la valeur minScaleValue de l'echelle
    public int getMinScaleValue() {
        return minScaleValue;
    }

    // charge la valeur minimale de l'ehelle
    public void setMinScaleValue(int minScaleValue) {
        this.minScaleValue = minScaleValue;
    }

    // retourne la valeur maximale de l'echelle

    public int getMaxScaleValue() {
        return maxScaleValue;
    }

    // charge la valeur maximale de l'echelle
    public void setMaxScaleValue(int maxScaleValue) {
        this.maxScaleValue = maxScaleValue;
    }

    // retourne l'identifiant de l'echelle qui sera determiner automatique par
    // @see ManageMultiLevel

    public int getIdScale() {
        return idScale;
    }

    // charge l'identifiant de l'echelle
    public void setIdScale(int idScale) {
        this.idScale = idScale;
    }

    // retourne le nom de l'echelle
    public String getNameScale() {
        return nameScale;
    }

    // charge le nom de l'echelle
    public void setNameScale(String nameScale) {
        this.nameScale = nameScale;
    }

    // permet de vérifier que deux echelles sont egales
    // dans ce cas ils auront les memes identifiants
    // par ailleurs ils difficiles de verifier les bonnes
    // inférieures et superieurs des echelles car ces sont des
    // doubles...
    public boolean equals(Object obj) {
        if (obj != null && (obj.getClass().equals(this.getClass()))) {
            if (obj instanceof ScaleInfo) {
                ScaleInfo scale = (ScaleInfo) obj;
                // comparer deux valeurs de double est très compliqué
                // il est important de prendre des intervalles relatifs
                return (scale.getIdScale() == idScale);
            }
            return false;

        }

        return false;

    }

    public String toString() {
        return "[name" + ":" + nameScale + "]" + " " + 
               "[id" + ":" + idScale + "]" + " " +
               "[minValue" + ":" + String.valueOf(minScaleValue)+ "]" + " " + 
               "[maxValue" + ":" + String.valueOf(maxScaleValue)+ "]" + " ";
    }

    public String printScaleForAll() {

        if (MaxRectangle != null && convertisseurInfo != null) {
            return "[name" + ":" + nameScale + "]" + " " + "[id" + ":"
                    + idScale + "]" + " " + "[minValue" + ":"
                    + String.valueOf(minScaleValue) + "]" + " " + "[maxValue"
                    + ":" + String.valueOf(maxScaleValue) + "]" + " "
                    + "[HyperBoundingBox" + ":" + MaxRectangle.toString() + "]"
                    + " " + "[convertisseurInfo" + ":"
                    + convertisseurInfo.toString() + "]";
        }

        else if (MaxRectangle != null) {

            return "[name" + ":" + nameScale + "]" + " " + "[id" + ":"
                    + idScale + "]" + " " + "[minValue" + ":"
                    + String.valueOf(minScaleValue) + "]" + " " + "[maxValue"
                    + ":" + String.valueOf(maxScaleValue) + "]" + " "
                    + "[HyperBoundingBox" + ":" + MaxRectangle.toString() + "]";

        } else if (convertisseurInfo != null) {

            return "[name" + ":" + nameScale + "]" + " " + "[id" + ":"
                    + idScale + "]" + " " + "[minValue" + ":"
                    + String.valueOf(minScaleValue) + "]" + " " + "[maxValue"
                    + ":" + String.valueOf(maxScaleValue) + "]" + " "
                    + "[convertisseurInfo" + ":" + convertisseurInfo.toString()
                    + "]";

        } else {

            return "[name" + ":" + nameScale + "]" + " " + "[id" + ":"
                    + idScale + "]" + " " + "[minValue" + ":"
                    + String.valueOf(minScaleValue) + "]" + " " + "[maxValue"
                    + ":" + String.valueOf(maxScaleValue) + "]" + " ";
        }
    }
    
    
 public String printScale() {
        
        if( MaxRectangle!=null){
        return "[name" + ":" + nameScale + "]" + " " + "[id" + ":" + idScale
                + "]" + " " + "[minValue" + ":" + String.valueOf(minScaleValue)
                + "]" + " " + "[maxValue" + ":" + String.valueOf(maxScaleValue)
                + "]" + " " + "[HyperBoundingBox" + ":"
                + MaxRectangle.toString() + "]" + " ";
        }else{
            
            return "[name" + ":" + nameScale + "]" + " " + "[id" + ":" + idScale
                    + "]" + " " + "[minValue" + ":" + String.valueOf(minScaleValue)
                    + "]" + " " + "[maxValue" + ":" + String.valueOf(maxScaleValue)
                    + "]" + " ";
        }
    }
 
 
 public String printScale1() {
     
     if( convertisseurInfo!=null){
     return "[name" + ":" + nameScale + "]" + " " + "[id" + ":" + idScale
             + "]" + " " + "[minValue" + ":" + String.valueOf(minScaleValue)
             + "]" + " " + "[maxValue" + ":" + String.valueOf(maxScaleValue)
             + "]" + " " 
             + "[convertisseurInfo" + ":" + convertisseurInfo.toString()
             + "]";

     }else{
         
         return "[name" + ":" + nameScale + "]" + " " + "[id" + ":" + idScale
                 + "]" + " " + "[minValue" + ":" + String.valueOf(minScaleValue)
                 + "]" + " " + "[maxValue" + ":" + String.valueOf(maxScaleValue)
                 + "]" + " ";
     }
 }



    public Converter getConvertisseurInfo() {
        return convertisseurInfo;
    }

    public void setConvertisseurInfo(Converter convertisseurInfo) {
        this.convertisseurInfo = convertisseurInfo;
    }

}
