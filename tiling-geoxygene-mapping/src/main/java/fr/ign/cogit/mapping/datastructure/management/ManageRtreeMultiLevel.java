package fr.ign.cogit.mapping.datastructure.management;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import org.deegree.io.rtree.HyperBoundingBox;

import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil.ToStringAdapter;

import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.RtreeMultiLevelIndex;
import fr.ign.cogit.mapping.util.ConverterUtil;
import fr.ign.cogit.mapping.util.HyperBoundingBoxUtil;

public class ManageRtreeMultiLevel {
    /*
     * Cette classe permet de gerer les differents index Rtree du système...
     * Quand elle est activée , elle recherche intialement les indexes qui sont
     * sauvegardés dans le fichier (indexInfo) qui sert au stockage des indexes
     * Elle peut aussi etre utilisé pour creer un nouveau index s'il n'existe
     * pas encore
     * 
     * @author Dtsatcha
     * 
     * @see RtreeMultiLevelIndex
     * 
     * @see ScaleInfo
     * 
     * @see RTreeIndex
     */
    protected String indexInfo = "indexInfo";
    /*
     * @param rtreemultilevel Elle classe chargé en mémoire qui contient tous
     * index des rtee recemment utilisés
     */
    protected RtreeMultiLevelIndex rtreemultilevel;

    /*
     * @param Directory Désigne le chemin du repertoire de stockage du fichier
     * (indexInfo) Ce chemin sera utilisé pour aussi stocké toutes les
     * informations necessaire à l'indexation Rtree..;
     */

    protected String Directory;

    /**
     * @param rtreemultilevelParam
     * @param directoryParam
     */
    public ManageRtreeMultiLevel(RtreeMultiLevelIndex rtreemultilevelParam,
            String directoryParam) {
        super();
        this.rtreemultilevel = rtreemultilevelParam;
        Directory = directoryParam;
        ChargeLevels();
    }

    // permet de charger dans la struture de données @see RtreeMultiLevelIndex
    // les indexes qui ont été recemment créér...
    // le fichier à charger se compose des informations definies dans ScaleInfo
    // @param nameScale (espace) @param idScale (espace) @param minScaleValue
    // (espace)
    // @param maxScaleValue

    public void ChargeLevels() {

        BufferedReader ficTexte = null;
        String filePath = Directory + File.separatorChar + indexInfo;
        String theLine;
        String[] containByLine;
        ScaleInfo scaleInfo;
        RTreeIndex rtreeIndex;
       
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
                        
                        // on extraire le boundingbox dans le fichier de sauvegarde
                        if(containByLine.length>4){
                            String boundingBox=null;
                            for  (int i=4; i< containByLine.length; i++){
                                
                                  if(i==4){
                                      boundingBox =containByLine[4];
                                  }else{
                                      boundingBox =boundingBox+containByLine[i];
                                  }
                            }
                            
                        scaleInfo.setMaxRectangle(HyperBoundingBoxUtil
                                        .obtainHuperFromText(boundingBox));
                        
                    //    System.out.println("la 4 valeur "+ boundingBox);
                        }
                        // la valeur courante de l'echelle à partir des
                        // coordonnées
                        // de l'echelle enregistré
                        // scaleInfo.setMaxRectangle(HyperBoundingBoxUtil.obtainHuperFromText(containByLine[4]));
                        // if (existAnLevel(Integer.valueOf(containByLine[2]))){
                        rtreeIndex = new RTreeIndex(Directory,
                                containByLine[0],scaleInfo.getMaxRectangle());
                                   
                        rtreemultilevel.getMultiLevelIndex().put(scaleInfo,
                                rtreeIndex);
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

    public void createOneLevel(int minScale, int maxScale) {
      //  ChargeLevels();
        ScaleInfo scaleInfo;
        RTreeIndex rtreeIndex;
        String scaleName = "";
        if (rtreemultilevel != null) {
            try {

                // int id = rtreemultilevel.getMultiLevelIndex().size();
                // aucun index a deja été crée
                // if (id == 0) {
                int id = minScale;
                scaleInfo = new ScaleInfo(minScale, maxScale);
                scaleName = "scale" + String.valueOf(id);
                // ancien
                // rtreeIndex = new RTreeIndex(Directory, scaleName);
                // nouveau
                rtreeIndex = new RTreeIndex(Directory, scaleName,
                        scaleInfo.getMaxRectangle());
                // scaleInfo.setMaxRectangle(rtreeIndex.getMaxRectangle());
                upDateScale(scaleInfo, rtreeIndex);
                // rtreemultilevel.getMultiLevelIndex().put(scaleInfo,
                // rtreeIndex);

            } catch (Exception e) {
                throw new RtreeMultiLevelException(this,
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

    public RtreeMultiLevelIndex getRtreemultilevel() {
        return rtreemultilevel;
    }

    public void setRtreemultilevel(RtreeMultiLevelIndex rtreemultilevel) {
        this.rtreemultilevel = rtreemultilevel;
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
                if (rtreemultilevel != null) {
                    for (ScaleInfo scale : rtreemultilevel.getMultiLevelIndex()
                            .keySet()) {
                        if (scale.getMaxRectangle() != null) {
                            texte = scale.getNameScale() + " "
                                    + scale.getIdScale() + " "
                                    + scale.getMinScaleValue() + " "
                                    + scale.getMaxScaleValue() + " "
                                    + scale.getMaxRectangle().toString();
                        } else {

                            texte = scale.getNameScale() + " "
                                    + scale.getIdScale() + " "
                                    + scale.getMinScaleValue() + " "
                                    + scale.getMaxScaleValue();
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
            throw new RtreeMultiLevelException(this,
                    "exception de creation de l'index", e);
        }

    }

    // suppression des indexes et mise à jour du fichier de stockage

    public void deleteAllLevel() {

        if (rtreemultilevel != null) {
            for (ScaleInfo scale : rtreemultilevel.getMultiLevelIndex()
                    .keySet()) {
                RTreeIndex rtreeIndex = rtreemultilevel.getMultiLevelIndex()
                        .get(scale);
                rtreeIndex.delete();
                rtreemultilevel.getMultiLevelIndex().remove(scale);

            }

            rtreemultilevel.getMultiLevelIndex().clear();
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String texte = " ";
        int i = 0;
        if (rtreemultilevel != null) {
            for (ScaleInfo scale : rtreemultilevel.getMultiLevelIndex()
                    .keySet()) {
                if (i == 0) {
                    texte = scale.printScale();
                    i++;
                } else {
                    texte = texte + scale.printScale();
                }

            }
        }
        return texte;
    }

    // on peut souhaiter utiliser un index connaissance l'echelle
    // l'echelle peut etre indiquée en renseignant les deux composantes
    // minValue and maxMalue;
    /*
     * @valueP Designe la valeur de l'echelle qu'on souhaite recuperer son index
     */

    public RTreeIndex useAnLevel(double valueP) {
        int minV = 1000000;
        // si la valeur de l'echelle est tres petite
        // on reste à la plus petite valeur de l'echelle
        // existant dans le manager
        RTreeIndex rtreeIndexMin = null;

        // si la valeur de l'echelle est très grande
        // on reste à la plus grande echelle contenu
        // dans le manager
        RTreeIndex rtreeIndexMax = null;
        int maxV = -1;

        if (rtreemultilevel != null) {
            for (ScaleInfo scale : rtreemultilevel.getMultiLevelIndex()
                    .keySet()) {
                // on garde à chaque la plus petite echelle correspondant à
                // chaque iteration
                if (scale.getMinScaleValue() < minV) {
                    minV = scale.getMinScaleValue();
                    rtreeIndexMin = new RTreeIndex(Directory,
                            scale.getNameScale(), scale.getMaxRectangle());
                     
                }

                // on garde à chaque la plus grande echelle correspondant à
                // chaque iteration

                if (scale.getMaxScaleValue() > maxV) {
                    maxV = scale.getMaxScaleValue();
                    rtreeIndexMax = new RTreeIndex(Directory,
                            scale.getNameScale(), scale.getMaxRectangle());
                }

                if (scale.getMinScaleValue() <= valueP
                        && scale.getMaxScaleValue() > valueP) {
                    RTreeIndex rtreeIndex = new RTreeIndex(Directory,
                            scale.getNameScale(), scale.getMaxRectangle());
                    return rtreeIndex;
                }
            }

            if (valueP < minV) {
                return rtreeIndexMin;
            } else {
                if (valueP > maxV) {
                    return rtreeIndexMax;
                }
            }
        }
        return null;

    }

    // verifie qu'un index a deja été representé

    public boolean existAnLevel(int valueP) {
        if (rtreemultilevel != null) {
            for (ScaleInfo scale : rtreemultilevel.getMultiLevelIndex()
                    .keySet()) {

                if (scale.getIdScale() == valueP) {
                    return true;
                }
            }
        }
        return false;

    }

    // suppression des indexes et mise à jour du fichier de stockage

    public void deleteOneLevel(int valueP) {
        try {
            if (existAnLevel(valueP)) {
                if (rtreemultilevel != null) {
                    for (ScaleInfo scale : rtreemultilevel.getMultiLevelIndex()
                            .keySet()) {

                        if (scale.getIdScale() == valueP) {
                            RTreeIndex rtreeIndex = rtreemultilevel
                                    .getMultiLevelIndex().get(scale);

                            rtreeIndex.delete();
                            rtreemultilevel.getMultiLevelIndex().remove(scale);
                        }
                    }
                }

            }

        } catch (Exception e) {
            throw new RtreeMultiLevelException(this,
                    "suppression d'une echelle", e);
        }

    }

    /*
     * permet de mettre à jour certaines informations sur le ScaleInfo et
     * l'indiquer au manager
     */

    public void upDateScale(ScaleInfo scale, RTreeIndex index) {

        if (existAnLevel(scale.getIdScale())) {

            for (ScaleInfo s : rtreemultilevel.getMultiLevelIndex().keySet()) {

                if (s.getIdScale() == scale.getIdScale()) {
                    rtreemultilevel.getMultiLevelIndex().remove(s);
                }
            }

            rtreemultilevel.getMultiLevelIndex().put(scale, index);

        } else {

            // on cree un nouveau
            rtreemultilevel.getMultiLevelIndex().put(scale, index);

        }

    }
    
   
}
