package fr.ign.cogit.mapping.util;

import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;

public class ScaleInfoUtil {

    // Cette methode permet de generer l'echelle ..;

    /*
     * @param table designe le nom de la table
     */
    public static ScaleInfo generateScale(String table) {
        int scale = -1;
        ScaleInfo scaleinfo = null;
        boolean findScale = false;
        if (table != null) {

            String tab[] = table.split("_");
            // on vérifie que le champ contenant
            // l'echelle est renseigné
            if (tab != null) {
                // gestion du suffixe pour extraire des echelle
                
                System.out.println(tab[tab.length - 1] );
                if (tab[tab.length - 1] != null) {
                    // try {
                    // } catch (Exception e) {
                    //
                    // //*****traitement de l'exception l'echelle n'est pas
                    // renseignée ****/
                    // scaleinfo= new ScaleInfo(15, 25);
                    // throw new RuntimeException("'" + tab[tab.length - 1]
                    // + "'impossible de le convertir en entier");
                    //
                    // }
                    // on verifie si les caracteres du derniers sont des entiers
                    if ((tab[tab.length - 1].matches("^\\p{Digit}+$"))) {

                        scale = Integer.parseInt(tab[tab.length - 1]);
                       
                        switch (scale) {
                        // defintion des differentes echelles
                        // actuellement disponiles dans la base
                        // de données qui seront trés par le manager
                        case 15:
                            scaleinfo = new ScaleInfo(15, 25);
                            findScale = true;
                            break;
                        case 25:
                            scaleinfo = new ScaleInfo(25, 50);
                            findScale = true;
                            break;
                        case 50:
                            scaleinfo = new ScaleInfo(50, 75);
                            findScale = true;
                            break;
                        case 75:
                            scaleinfo = new ScaleInfo(75, 100);
                            findScale = true;
                            break;

                        case 100:
                            scaleinfo = new ScaleInfo(100, 250);
                            findScale = true;
                            break;
                        case 250:
                            scaleinfo = new ScaleInfo(250, 1000);
                            findScale = true;
                            break;
                        case 1000:
                            scaleinfo = new ScaleInfo(1000, 10000);
                            findScale = true;
                            break;

                        }
                    }

                } 

                    if (tab[0] != null && findScale==false ) {
                     
                        if (tab[0].contains("e")) {
                            // on extraire la valeur de l'echelle
                         
                            String scaleValue = tab[0].substring(1,
                                    tab[0].length());
                            // on verifie qu'on a bien à faire aux entiers
                            //System.out.println("scaleValue"+scaleValue);
                            
                            if ((scaleValue.matches("^\\p{Digit}+$"))) {
                                scale = Integer.parseInt(scaleValue);

                                switch (scale) {
                                // defintion des differentes echelles
                                // actuellement disponiles dans la base
                                // de données qui seront trés par le manager
                                case 15:
                                    scaleinfo = new ScaleInfo(15, 25);
                                    findScale = true;
                                    break;
                                case 25:
                                    scaleinfo = new ScaleInfo(25, 50);
                                    findScale = true;
                                    break;
                                case 50:
                                    scaleinfo = new ScaleInfo(50, 75);
                                    findScale = true;
                                    break;
                                case 75:
                                    scaleinfo = new ScaleInfo(75, 100);
                                    findScale = true;
                                    break;

                                case 100:
                                    scaleinfo = new ScaleInfo(100, 250);
                                    findScale = true;
                                    break;
                                case 250:
                                    scaleinfo = new ScaleInfo(250, 1000);
                                    findScale = true;
                                    break;
                                case 1000:
                                    scaleinfo = new ScaleInfo(1000, 10000);
                                    findScale = true;
                                    break;

                                }

                            }

                        }

                    }

                }

            }

        

        if (findScale == false) {
            scaleinfo = new ScaleInfo(15, 25);
            // si le champ de l'echelle n'est pas renseigné
            // que nous avons des echelle 15...
        }

        return scaleinfo;
    }

    /*
     * Cette methode permet de rechercher une echelle dans
     * dans le manager
     * @param manager
     * designe le gestion du Rtree multi-echelle
     * @param  scale
     * designe une echelle quelconque
     * @return scaleInfo  est null designe qu'aucune information
     * @ cette echelle est referencée dans le manager...
     */
    
    public static ScaleInfo existeScale(ScaleInfo scale, ManageRtreeMultiLevel manager) {
       
        ScaleInfo  scaleFinal=null;
        // permet de verifier que l'information sur l'echelle existe deja... dans
        // le manager...
        if(scale!=null && manager!=null){
        for (ScaleInfo s : manager.getRtreemultilevel()
                .getMultiLevelIndex().keySet()) {
            if(scale.getIdScale()==s.getIdScale()){
                scaleFinal=s;
                return scaleFinal;
            }
        }
         }else{
             throw new RuntimeException("l'echelle scale n'a pas été intialisée");
         }
        return scaleFinal;
    }
    
}
