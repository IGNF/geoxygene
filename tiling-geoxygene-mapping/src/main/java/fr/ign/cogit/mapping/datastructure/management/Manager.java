package fr.ign.cogit.mapping.datastructure.management;

import java.util.concurrent.ConcurrentHashMap;
import fr.ign.cogit.mapping.generalindex.Index;

/*
 * @author Dr Tsatcha D.
 */

public interface Manager{

    // permet de charger le contenu associé à un manager
    // depuis un fichier...
    // Cette classe n'a pas encore été utilisé...
    /*
     * @param fichier designe le fichier de stockage des informations associées
     * au manager
     */
    public void ChargeLevels(String fichier, ConcurrentHashMap<ScaleInfo, Index>
        container) throws ManagerException;

    /*
     * permet de creer les differentes echelles associées à la transformation
     */

    public void createOneLevel(int minScale, int maxScale,ConcurrentHashMap<ScaleInfo, Index>
    container)
            throws ManagerException;

    /*
     * permet de sauvegarder le context du manager dans un fichier
     */
    public void saveLevelsInFile(String fichier, ConcurrentHashMap<ScaleInfo, Index>
    container) throws ManagerException;

    /*
     * cette methode permet de supprimer toutes les echelles créees...
     */

    public void deleteAllLevel(String fichier,ConcurrentHashMap<ScaleInfo, Index>
    container) throws ManagerException;

    /*
     * permet d'afficher les contenus du manager à l'ecran...
     */

    public String toString() throws ManagerException;

    /*
     * @param valueP designe une valeur entier d'une echelle qu'on souhaite
     * exploité
     * 
     * @return l'objet associé à cette valeur
     */

    public Index useAnLevel(double valueP) throws ManagerException;

    /*
     * @param valueP designe une valeur entier d'une echelle qu'on souhaite
     * exploité retourne vrai lorsque l'echelle associé à cette valeur existe
     * sinon retourne faux
     */

    public boolean existAnLevel(int valueP) throws ManagerException;

    /*
     * @param valueP designe une valeur entier d'une echelle qu'on souhaite
     * exploité supprime l'echelle associé à la valeur valueP...
     */

    public void deleteOneLevel(int valueP) throws ManagerException;

    /*
     * Cette methode met à jour les informations de l'echelle scale associé à
     * l'objet index.
     */
    public void upDateScale(ScaleInfo scale, Index index)
            throws ManagerException;

}
