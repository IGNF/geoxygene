package fr.ign.cogit.mapping.datastructure.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import utils.ManyManyMap.ConvertInputCollection;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.mapping.clients.ControllerI;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.RtreeMultiLevelIndex;
import fr.ign.cogit.mapping.datastructure.hexaTree.HexaTreeIndex;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.util.ConverterUtil;
import fr.ign.cogit.mapping.util.HyperBoundingBoxUtil;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;
/*
 * @author Dr Tsatcha D.
 */

public class ManageHexaTreeMultiLevel {

    /*
     * @param indexInfo designe les informations sur l'index
     */
    protected String indexInfo = "indexInfo";
    /*
     * @param hexaTreemultilevel designe les informations sur l'hexatree
     */
    protected HexaTreeMultiLevel hexaTreemultilevel;

    /*
     * Designe le repertoire de stockage des contenus...
     */
    protected String Directory;

    /*
     * @param cadrageArea designe le cadrage courant... dont les elements sont
     * indexés dans l'hexaTree
     */

    protected CadrageArea cadrageArea;

    /*
     * @param hexaTreeIndex
     * 
     * designe l'hexaTree
     */

    protected SpatialIndex hexaTreeIndex;

    /*
     * @param convertisseur designe le convertisseur qui est chargé de rapporter
     * les paramètres ecran du client...
     */

    protected Converter convertisseur;

    /*
     * param manager designe le manager rtree qui rapporte certaines methode et
     * variables à l'hexaTree
     */
    protected ManageRtreeMultiLevel manager;

    /*
     * /**
     * 
     * @param hexaTreemultilevel
     * 
     * @param directory
     */
    public ManageHexaTreeMultiLevel(HexaTreeMultiLevel hexaTreemultilevelParam,
            String directoryParam, Converter convertisseurParam,
            ManageRtreeMultiLevel managerParam) {
        super();
        this.hexaTreemultilevel = hexaTreemultilevelParam;
        Directory = directoryParam;
        convertisseur = convertisseurParam;
        manager = managerParam;
        ChargeLevels();

    }

    // permet de charger dans la struture de données @see RtreeMultiLevelIndex
    // les indexes qui ont été recemment créér...
    // le fichier à charger se compose des informations definies dans ScaleInfo
    // @param nameScale (espace) @param idScale (espace) @param minScaleValue
    // (espace)
    // @param maxScaleValue

    public HexaTreeMultiLevel getHexaTreemultilevel() {
        return hexaTreemultilevel;
    }

    public void setHexaTreemultilevel(HexaTreeMultiLevel hexaTreemultilevel) {
        this.hexaTreemultilevel = hexaTreemultilevel;
    }

    public Converter getConvertisseur() {
        return convertisseur;
    }

    public void setConvertisseur(Converter convertisseur) {
        this.convertisseur = convertisseur;
    }

    public void ChargeLevels() {

        BufferedReader ficTexte = null;
        String filePath = Directory + File.separatorChar + indexInfo;
        String theLine;
        String[] containByLine;
        ScaleInfo scaleInfo;
        HexaTreeIndex hexaTreeIndex;

        try {
            ficTexte = new BufferedReader(new FileReader(new File(filePath)));
            do {
                theLine = ficTexte.readLine();
                if (theLine != null) {

                    // on segemente les elements de la ligne en fonction des
                    // espaces
                    containByLine = theLine.split("\\s+");
                    if (containByLine != null) {
                        // on n a pas besoin de renseigner
                        // le nameScale car la classe sert le contruire
                        scaleInfo = new ScaleInfo(
                                Integer.valueOf(containByLine[2]),
                                Integer.valueOf(containByLine[3]));
                        // on verifie si la 4 ième
                        // composante existe... c'est ce dernier qui
                        // garde les informations sur la couvertutr
                        
                         

                        // on extraire le boundingbox dans le fichier de
                        // sauvegarde
                        if (containByLine.length > 5) {
                        String converterString=theLine.substring(containByLine[0].length()+ 
                                containByLine[1].length()+containByLine[2].length()+
                                containByLine[3].length()+3, theLine.length());
                       // System.out.println("------"+converterString);
//                      scaleInfo.setConvertisseurInfo(ConverterUtil
//                        if (containByLine.length > 4) {
//                            String converterString = null;
//                            for (int i = 4; i < containByLine.length; i++) {
//
//                                if (i == 4) {
//                                    converterString = containByLine[4];
//                                } else {
//                                    converterString = converterString
//                                            + containByLine[i];
//                                }
//                            }
//                            System.out.println("------"+converterString);
//                            scaleInfo.setConvertisseurInfo(ConverterUtil
//                                    .obtainConverterFromtext(converterString));
//
//                            // System.out.println("la 4 valeur "+ boundingBox);
//                        }
                        // la valeur courante de l'echelle à partir des
                        // coordonnées
                        // de l'echelle enregistré
                        // scaleInfo.setMaxRectangle(HyperBoundingBoxUtil.obtainHuperFromText(containByLine[4]));
                        // if (existAnLevel(Integer.valueOf(containByLine[2]))){
                        ConverterUtil conver = new  ConverterUtil();
                        conver.obtainConverterFromtext(converterString);
                        convertisseur.setInterestPoint(conver.getInterestPoint());
                        convertisseur.setWeight(conver.getWeight());
                        convertisseur.setHeight(conver.getHeight());
                        scaleInfo.setConvertisseurInfo(convertisseur);
                        scaleInfo.setCadreName( containByLine[4]);
//                        scaleInfo.setConvertisseurInfo(ConverterUtil
//                             .obtainConverterFromtext(converterString));
                        }
                        hexaTreeIndex = new HexaTreeIndex(Directory,
                                scaleInfo.getNameScale(), scaleInfo.getCadreName());
                        hexaTreeIndex.setConvertisseur(convertisseur);
                        hexaTreeIndex.setManager(manager);
                        hexaTreemultilevel.getMultiLevelIndex().put(scaleInfo,
                                hexaTreeIndex);
                        // }
                    }

                }
            } while (theLine != null);
            // System.out.println(ligne);
            ficTexte.close();
            System.out.println("\n");
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    /*
     * Cette methode permet de creer un index inextant dans le manager..
     */

    public void createOneLevel(int minScale, int maxScale, int cadre) {
        // ChargeLevels();
        ScaleInfo scaleInfo;
        HexaTreeIndex hexaTreeIndex;
        String cadreName = "";
        if (hexaTreemultilevel != null) {
            try {

                // int id = rtreemultilevel.getMultiLevelIndex().size();
                // aucun index a deja été crée
                // if (id == 0) {
                int id = minScale;
                scaleInfo = new ScaleInfo(minScale, maxScale);
                cadreName =scaleInfo.getNameScale() +"_cadre"+
                String.valueOf(String.valueOf(cadre));
                scaleInfo.setCadreName(cadreName);
                // ancien
                // rtreeIndex = new RTreeIndex(Directory, scaleName);
                // nouveau
                hexaTreeIndex = new HexaTreeIndex(Directory, scaleInfo.getNameScale(), cadreName);
                scaleInfo.setConvertisseurInfo(convertisseur);
                hexaTreeIndex.setConvertisseur(convertisseur);
                hexaTreeIndex.setManager(manager);
                // scaleInfo.setMaxRectangle(rtreeIndex.getMaxRectangle());
                upDateScale(scaleInfo, hexaTreeIndex);
                // rtreemultilevel.getMultiLevelIndex().put(scaleInfo,
                // rtreeIndex);

            } catch (Exception e) {
                throw new HexaTreeMultiLevelException(this,
                        "exception de creation de l'index", e);
            }
        }
    }

    //
    // /*
    // * Cette methode permet de creer un index
    // * connaissant les informations sur de l'echelle
    // * @param scale
    // * designe une echelle recemment construite par la classe
    // * @see ScaleInfoUtil
    // * voir la classe @see ScaleInfo
    // * Le nom de l'index est le nom renseigné dans l'echelle @param scale
    // */
    //
    // public void createOneLevelFromScale(ScaleInfo scale) {
    // ChargeLevels();
    // ScaleInfo scaleInfo;
    // RTreeIndex rtreeIndex;
    // String scaleName = "";
    // if (rtreemultilevel != null) {
    // try {
    //
    // // int id = rtreemultilevel.getMultiLevelIndex().size();
    // // aucun index a deja été crée
    // // if (id == 0) {
    // int id = scale.getMinScaleValue();
    // scaleName = scale.getNameScale();
    // rtreeIndex = new RTreeIndex(Directory, scaleName);
    // rtreemultilevel.getMultiLevelIndex().put(scale, rtreeIndex);
    //
    // } catch (Exception e) {
    // throw new RtreeMultiLevelException(this,
    // "exception de creation de l'index", e);
    // }
    // }
    // saveLevelsInFile();
    // }

    /*
     * permet de mettre à jour certaines informations sur le ScaleInfo et
     * l'indiquer au manager
     */

    public void upDateScale(ScaleInfo scale, HexaTreeIndex index) {

        if (existAnLevel(scale.getIdScale())) {

            for (ScaleInfo s : hexaTreemultilevel.getMultiLevelIndex().keySet()) {

                if (s.getIdScale() == scale.getIdScale()
                        && s.getCadreName().equals(scale.getCadreName())) {
                    hexaTreemultilevel.getMultiLevelIndex().remove(s);
                }
            }

            hexaTreemultilevel.getMultiLevelIndex().put(scale, index);

        } else {

            // on cree un nouveau
            hexaTreemultilevel.getMultiLevelIndex().put(scale, index);

        }

    }

    // verifie qu'un index a deja été representé

    public boolean existAnLevel(int valueP) {
        if (hexaTreemultilevel != null) {
            for (ScaleInfo scale : hexaTreemultilevel.getMultiLevelIndex()
                    .keySet()) {

                if (scale.getIdScale() == valueP) {
                    return true;
                }
            }
        }
        return false;

    }
    
    /*
     * existenc de l'index et le cadre...
     */
    
    public boolean existAnLevel(int valueP, int cadre) {
        String cadreName = "scale" + String.valueOf(valueP)+"_cadre"+
                String.valueOf(String.valueOf(cadre));         
        if (hexaTreemultilevel != null) {
            for (ScaleInfo scale : hexaTreemultilevel.getMultiLevelIndex()
                    .keySet()) {
                 if (scale.getIdScale() == valueP && 
                         cadreName.equals(scale.getCadreName())) {
                    return true;
                }
            }
        }
        return false;

    }

    // suppression des indexes et mise à jour du fichier de stockage

    public void deleteOneLevel(int valueP, int cadre) {
        try {
            if (existAnLevel(valueP, cadre)) {
                if (hexaTreemultilevel != null) {
                    for (ScaleInfo scale : hexaTreemultilevel
                            .getMultiLevelIndex().keySet()) {
                        String cadreName = "scale" + String.valueOf(valueP)
                                + "_cadre"
                                + String.valueOf(String.valueOf(cadre));
                        if (scale.getIdScale() == valueP
                                && cadreName.equals(scale.getCadreName())) {
                            HexaTreeIndex hexaTreeIndex = hexaTreemultilevel
                                    .getMultiLevelIndex().get(scale);

                            hexaTreeIndex.delete();
                            hexaTreemultilevel.getMultiLevelIndex().remove(
                                    scale);
                        }
                    }
                }

            }

        } catch (Exception e) {
            throw new HexaTreeMultiLevelException(this,
                    "suppression d'une echelle", e);
        }

    }
    


    
    public HexaTreeIndex useAnLevel(double valueP, int cadre) {
        int minV = 1000000;
        // si la valeur de l'echelle est tres petite
        // on reste à la plus petite valeur de l'echelle
        // existant dans le manager
        HexaTreeIndex hexaTreeIndexMin = null;
        String cadreName="";
        String scaleName = "scale" + String.valueOf(valueP) + "_cadre"
                + String.valueOf(String.valueOf(cadre));

        // si la valeur de l'echelle est très grande
        // on reste à la plus grande echelle contenu
        // dans le manager
        HexaTreeIndex hexatreeIndexMax = null;
        int maxV = -1;

        if (hexaTreemultilevel != null) {
            for (ScaleInfo scale : hexaTreemultilevel.getMultiLevelIndex()
                    .keySet()) {
                scale.setConvertisseurInfo(convertisseur);
                // on garde à chaque la plus petite echelle correspondant à
                // chaque iteration
                if (scale.getMinScaleValue() < minV) {

                    minV = scale.getMinScaleValue();
                    cadreName = "scale" + String.valueOf(minV) + "_cadre"
                            + String.valueOf(String.valueOf(cadre));
                    if (cadreName.equals(scale.getCadreName())) {
                        hexaTreeIndexMin = new HexaTreeIndex(Directory,scale.getNameScale(),
                                cadreName);
                        hexaTreeIndexMin.setConvertisseur(convertisseur);
                        hexaTreeIndexMin.setManager(manager);
                    }

                }

                // on garde à chaque la plus grande echelle correspondant à
                // chaque iteration

                if (scale.getMaxScaleValue() > maxV) {
                    maxV = scale.getMaxScaleValue();
                    scaleName = "scale" + String.valueOf(maxV) + "_cadre"
                            + String.valueOf(String.valueOf(cadre));
                    if (scaleName.equals(scale.getNameScale())) {
                        hexatreeIndexMax = new HexaTreeIndex(Directory,
                                scaleName);

                        hexatreeIndexMax.setConvertisseur(convertisseur);
                        hexatreeIndexMax.setManager(manager);
                    }
                }

                if (scale.getMinScaleValue() <= valueP
                        && scale.getMaxScaleValue() > valueP) {

                    scaleName = "scale" + String.valueOf(scale.getNameScale())
                            + "_cadre" + String.valueOf(String.valueOf(cadre));

                    if (scaleName.equals(scale.getNameScale())) {
                        HexaTreeIndex HexatreeIndex = new HexaTreeIndex(
                                Directory, scale.getNameScale());

                        HexatreeIndex.setConvertisseur(convertisseur);
                        HexatreeIndex.setManager(manager);
                        return HexatreeIndex;
                    } else{

                        HexaTreeIndex HexatreeIndex = new HexaTreeIndex(
                                Directory, scaleName);
                        HexatreeIndex.setConvertisseur(convertisseur);
                        HexatreeIndex.setManager(manager);
                        return HexatreeIndex;
                    }
                }
            }

            if (valueP < minV) {
                return hexaTreeIndexMin;
            } else {
                if (valueP > maxV) {
                    return hexatreeIndexMax;
                }
            }
        }
        return null;

    }
    
    
    /*
     * Cette methode permet d'utiliser le cadre d'une echelle deja
     * constituée dans le systeme de fichier
     * @param scaleId
     * designe l'id de l'echelle en question
     * @param
     * designe le numero du cadre associé à cette echelle
     * qu'on souhaite acceder.
     */

    public HexaTreeIndex useScaleCadre(ScaleInfo scaleInfo, int cadre) {
        int minV = 1000000;
        // si la valeur de l'echelle est tres petite
        // on reste à la plus petite valeur de l'echelle
        // existant dans le manager
        HexaTreeIndex hexaTreeIndexMin = null;
      
        String cadreName = scaleInfo.getNameScale() + "_cadre"
                + String.valueOf(String.valueOf(cadre));

        // si la valeur de l'echelle est très grande
        // on reste à la plus grande echelle contenu
        // dans le manager
        HexaTreeIndex hexatreeIndex = null;
        int maxV = -1;

        if (hexaTreemultilevel != null) {
            for (ScaleInfo scale : hexaTreemultilevel.getMultiLevelIndex()
                    .keySet()) {
                scale.setConvertisseurInfo(convertisseur);
                // on garde à chaque la plus petite echelle correspondant à
                // chaque iteration
                if (scale.getCadreName().equals(cadreName)) {
                    scale.setCadreName(cadreName);
                    hexatreeIndex = new HexaTreeIndex(Directory,
                            scale.getNameScale(), cadreName);
                    hexatreeIndex.setConvertisseur(convertisseur);
                    hexatreeIndex.setManager(manager);
                    
                    return hexatreeIndex;
                }

            }
        }

        return null;

    }

    // Cette methode permet de creer si besoin le fichier de stockage
    // et charger les informations sur des indexes...
    public void saveLevelsInFile() {

        // on verifie que le contenu des adresses en mémoire est non null

        try {

            String texte = "";

            try {

                PrintWriter sortie = null;

                File fichier = new File(Directory + File.separatorChar
                        + indexInfo);

                fichier.setWritable(true);
                if (!fichier.exists()) {
                    fichier.createNewFile();

                } else {
                    // on supprime le fichier et on le recrèe
                    fichier.delete();
                    fichier.createNewFile();

                }

                try {
                    sortie = new PrintWriter(new FileWriter(fichier, true));
                    // FileWriter monFileWriter = new
                    // FileWriter(fichier,true);
                } catch (IOException e) {
                    System.err.print("impossible de creer le fichier");
                }
                // monFileWriter.write(texte, 0 , texte.length());

                // on ecrire dans le fichier
                texte = null;
                if (hexaTreemultilevel != null) {
                    for (ScaleInfo scale : hexaTreemultilevel
                            .getMultiLevelIndex().keySet()) {
                        if (scale.getConvertisseurInfo() != null) {
                            texte = scale.getNameScale() + " "
                                    + scale.getIdScale() + " "
                                    + scale.getMinScaleValue() + " "
                                    + scale.getMaxScaleValue() + " "
                                    + scale.getCadreName() + " "
                                    + scale.getConvertisseurInfo().toString();
                        } else {

                            texte = scale.getNameScale() + " "
                                    + scale.getIdScale() + " "
                                    + scale.getMinScaleValue() + " "
                                    + scale.getMaxScaleValue()+ " "
                                    + scale.getCadreName() + " ";
                        }
                        sortie.println(texte);
                        texte = null;
                    }

                } // sortie.println(texte);
                if (sortie.checkError()) {
                    System.err.print("le fichier n'a pas �t� enregistrer");
                }

                System.out.println("fichier crꥢ+Rep + nomFic");

            } catch (IOException ioe) {
                System.out.print("Erreur : ");
                ioe.printStackTrace();
            }

        } catch (Exception e) {
            throw new HexaTreeMultiLevelException(this,
                    "exception de creation de l'index", e);
        }

    }
    /*
     * Cette methode permet de sauvegarder le contenu du manager
     * en prenant en compte le nouveau point d'interet du convertisseur
     * les entrées à changer... ...
     * on doit le mettre avant d'aller indexer d'autres contenus.
     */
    public void saveLevelsInFile(Converter convertisseur,
            int [] colapsePoint) {

        // on verifie que le contenu des adresses en mémoire est non null

        try {

            String texte = "";

            try {

                PrintWriter sortie = null;

                File fichier = new File(Directory + File.separatorChar
                        + indexInfo);

                fichier.setWritable(true);
                if (!fichier.exists()) {
                    fichier.createNewFile();

                } else {
                    // on supprime le fichier et on le recrèe
                    fichier.delete();
                    fichier.createNewFile();

                }

                try {
                    sortie = new PrintWriter(new FileWriter(fichier, true));
                    // FileWriter monFileWriter = new
                    // FileWriter(fichier,true);
                } catch (IOException e) {
                    System.err.print("impossible de creer le fichier");
                }
                // monFileWriter.write(texte, 0 , texte.length());

                // on ecrire dans le fichier
                texte = null;
                if (hexaTreemultilevel != null) {
                    for (ScaleInfo scale : hexaTreemultilevel
                            .getMultiLevelIndex().keySet()) {
                        scale.setConvertisseurInfo(convertisseur);
                        if(belongToColapse(scale.getIdScale(),colapsePoint)){
                        String cadreName = scale.getCadreName().substring
                                (scale.getCadreName().length()-1);
                        
                        }
                        
                        if (scale.getConvertisseurInfo() != null) {
                            texte = scale.getNameScale() + " "
                                    + scale.getIdScale() + " "
                                    + scale.getMinScaleValue() + " "
                                    + scale.getMaxScaleValue() + " "
                                    + scale.getCadreName() + " "
                                    + scale.getConvertisseurInfo().toString();
                        } else {

                            texte = scale.getNameScale() + " "
                                    + scale.getIdScale() + " "
                                    + scale.getMinScaleValue() + " "
                                    + scale.getMaxScaleValue()+ " "
                                    + scale.getCadreName() + " ";
                        }
                        sortie.println(texte);
                        texte = null;
                    }

                } // sortie.println(texte);
                if (sortie.checkError()) {
                    System.err.print("le fichier n'a pas �t� enregistrer");
                }

                System.out.println("fichier crꥢ+Rep + nomFic");

            } catch (IOException ioe) {
                System.out.print("Erreur : ");
                ioe.printStackTrace();
            }

        } catch (Exception e) {
            throw new HexaTreeMultiLevelException(this,
                    "exception de creation de l'index", e);
        }

    }
    
    
    /*
     * cette methode permet de verifier qu'un le numero donné
     * a été modifié..;
     * @param valeur 
     * designe la valeur qu'on souhaite verifier qu'il appartient
     * dans la liste
     * @param  colapse designe la liste des cadres qui se rencontrent ou pas...
     */
    
    public boolean belongToColapse(int valeur, int[] colapse) {

        for (int i = 0; i < colapse.length; i++) {

            if (colapse[i] == valeur)
                return true;
        }
        return false;

    }

    /*
     * permet de retourner les informations sur 
     * l'echelle quand il est deja present dans le 
     * manager car ces informations sont plus enrichies
     * @param valueP 
     * designe l'identifiant de l'index qu'on souhaite indexer
     * @return renvoie les informations pour la meme echelle contenue
     * dans le manager.
     */
    public ScaleInfo getScaleFromManager(int valueP) {
        if (hexaTreemultilevel != null) {
            for (ScaleInfo scale : hexaTreemultilevel.getMultiLevelIndex()
                    .keySet()) {

                if (scale.getIdScale() == valueP) {
                    return scale;
                }
            }
        }
        return null;

    }
    
    /*
     * 
     */
    
    
}
