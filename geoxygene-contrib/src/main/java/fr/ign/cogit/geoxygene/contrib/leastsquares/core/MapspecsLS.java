/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.leastsquares.core;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.util.XMLUtil;

/**
 * @author G. Touya
 * 
 *         The specifications (i.e. parameters) of a least squares
 *         generalisation.
 */
public class MapspecsLS {

    public enum SelectionType {
        SELECTED_OBJECTS, WINDOW_OBJECTS, SELECTED_AREAS
    }

    private double scale;

    // les sets des contraintes activées par cette mapspec
    private Set<String> fixedConstraints = new HashSet<String>();
    private Set<String> rigidConstraints = new HashSet<String>();
    private Set<String> malleableConstraints = new HashSet<String>();

    /**
     * Map qui contient des tableaux de String contenant le nom de la contrainte
     * externe, le nom de la classe 1 et le nom de la classe 2 et en valeur le
     * seuil de séparation pour cette contrainte
     */
    private Map<String[], Double> externalConstraints = new HashMap<String[], Double>();

    private Set<String> fixedClasses = new HashSet<String>();
    private Set<String> rigidClasses = new HashSet<String>();
    private Set<String> malleableClasses = new HashSet<String>();

    private Map<String, Double> constraintWeights;

    // paramètres de sélection d'objets
    private Collection<IFeature> selectedObjects = new HashSet<IFeature>();

    /**
     * The densification step to apply on malleable features
     */
    private double densStep = 50.0;
    /**
     * True if features have to be filtered by Douglas & Peucker algorithm after
     * conflation to alter minor defects.
     */
    private boolean filter = false;
    private double filterThreshold = 1.0;

    /**
     * A map that gives the symbol width of every features in map mm.
     */
    private Map<IFeature, Double> mapSymbolWidth = new HashMap<IFeature, Double>();

    // constructeur
    public MapspecsLS() {
    }

    /**
     * Constructeur de mapspecs à partir d'un fichier XML de stockage des
     * mapspecs des Moindres Carrés et d'une sélection d'objets.
     * 
     * @param session
     *            la session Clarity en cours
     * @param fic
     *            le fichier XML contenant les mapspecs
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public MapspecsLS(File fic, Collection<IFeature> selectedObjects)
            throws SAXException, IOException, ParserConfigurationException {
        // on récupère la sélection d'objets
        this.selectedObjects = selectedObjects;

        // on commence par ouvrir le doucment XML pour le parser
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db;
        db = dbf.newDocumentBuilder();
        Document doc;
        doc = db.parse(fic);
        doc.getDocumentElement().normalize();

        // **************************************
        // ON PARSE LE FICHIER XML
        // **************************************
        // on commence par les mapspecs
        Element mapspecElem = (Element) doc.getElementsByTagName("mapspecs")
                .item(0);
        // puis on r�cup�re les classes fixes
        Element fixeElem = (Element) mapspecElem
                .getElementsByTagName("objets-fixes").item(0);
        if (fixeElem != null) {
            for (int i = 0; i < fixeElem.getElementsByTagName("classe")
                    .getLength(); i++) {
                Element classElem = (Element) fixeElem
                        .getElementsByTagName("classe").item(i);
                String classe = classElem.getChildNodes().item(0)
                        .getNodeValue();
                this.fixedClasses.add(classe);
            }
        } // if(fixeElem!=null)

        // puis on r�cup�re les classes rigides
        Element rigideElem = (Element) mapspecElem
                .getElementsByTagName("objets-rigides").item(0);
        if (rigideElem != null) {
            for (int i = 0; i < rigideElem.getElementsByTagName("classe")
                    .getLength(); i++) {
                Element classElem = (Element) rigideElem
                        .getElementsByTagName("classe").item(i);
                String classe = classElem.getChildNodes().item(0)
                        .getNodeValue();
                this.rigidClasses.add(classe);
            }
        } // if(rigideElem!=null)

        // puis on récupère les classes malléables
        Element mallElem = (Element) mapspecElem
                .getElementsByTagName("objets-malleables").item(0);
        if (mallElem != null) {
            for (int i = 0; i < mallElem.getElementsByTagName("classe")
                    .getLength(); i++) {
                Element classElem = (Element) mallElem
                        .getElementsByTagName("classe").item(i);
                String classe = classElem.getChildNodes().item(0)
                        .getNodeValue();
                this.malleableClasses.add(classe);
            }
        } // if(mallElem!=null)

        // on parse maintenant les contraintes externes
        Element contrExtElem = (Element) mapspecElem
                .getElementsByTagName("contraintes-externes").item(0);
        if (contrExtElem != null
                && contrExtElem.getElementsByTagName("contrainte") != null) {
            for (int i = 0; i < contrExtElem.getElementsByTagName("contrainte")
                    .getLength(); i++) {
                Element contrainteElem = (Element) contrExtElem
                        .getElementsByTagName("contrainte").item(i);
                // on r�cup�re le nom
                Element nomElem = (Element) contrainteElem
                        .getElementsByTagName("nom").item(0);
                String nom = nomElem.getChildNodes().item(0).getNodeValue();
                // on récupère la classe1
                Element classe1Elem = (Element) contrainteElem
                        .getElementsByTagName("classe1").item(0);
                String classe1 = classe1Elem.getChildNodes().item(0)
                        .getNodeValue();
                // on récupère la classe2
                Element classe2Elem = (Element) contrainteElem
                        .getElementsByTagName("classe2").item(0);
                String classe2 = classe2Elem.getChildNodes().item(0)
                        .getNodeValue();
                // on récupère le seuil
                Element seuilElem = (Element) contrainteElem
                        .getElementsByTagName("seuil").item(0);
                String seuil = seuilElem.getChildNodes().item(0).getNodeValue();
                String[] cleMap = { nom, classe1, classe2 };
                this.externalConstraints.put(cleMap, new Double(seuil));
            }
        }

        // on s'occupe maintenant des pond�rations
        this.constraintWeights = new HashMap<String, Double>();
        Element pondElem = (Element) doc.getElementsByTagName("ponderations")
                .item(0);
        for (int i = 0; i < pondElem.getElementsByTagName("contrainte")
                .getLength(); i++) {
            Element contrainteElem = (Element) pondElem
                    .getElementsByTagName("contrainte").item(i);
            Element nomElem = (Element) contrainteElem
                    .getElementsByTagName("classe").item(0);
            String nom = nomElem.getChildNodes().item(0).getNodeValue();
            Element poidsElem = (Element) contrainteElem
                    .getElementsByTagName("poids").item(0);
            String poidsS = poidsElem.getChildNodes().item(0).getNodeValue();
            Double poids = new Double(poidsS);
            this.constraintWeights.put(nom, poids);
        }

        if (this.constraintWeights.keySet()
                .contains(LSMovementConstraint.class.getName())) {
            this.fixedConstraints.add(LSMovementConstraint.class.getName());
            this.rigidConstraints.add(LSMovementConstraint.class.getName());
            this.malleableConstraints.add(LSMovementConstraint.class.getName());
        }
        if (this.constraintWeights.keySet()
                .contains(LSStiffnessConstraint.class.getName())) {
            this.rigidConstraints.add(LSStiffnessConstraint.class.getName());
        }
        if (this.constraintWeights.keySet()
                .contains(LSSideOrientConstraint.class.getName())) {
            this.rigidConstraints.add(LSSideOrientConstraint.class.getName());
        }
        if (this.constraintWeights.keySet()
                .contains(LSCurvatureConstraint.class.getName())) {
            this.malleableConstraints
                    .add(LSCurvatureConstraint.class.getName());
        }
        if (this.constraintWeights.keySet()
                .contains(LSMovementDirConstraint.class.getName())) {
            this.malleableConstraints
                    .add(LSMovementDirConstraint.class.getName());
        }

        // on parse enfin l'échelle
        Element echElem = (Element) doc.getElementsByTagName("echelle").item(0);

        if (echElem != null) {
            this.setEchelle(Double.parseDouble(
                    echElem.getChildNodes().item(0).getNodeValue()));
        }
    }

    public MapspecsLS(File fic, Collection<IFeature> selectedObjects,
            double scale)
            throws SAXException, IOException, ParserConfigurationException {
        this(fic, selectedObjects);
        this.setEchelle(scale);
    }

    public MapspecsLS(double echelle, Collection<IFeature> selectedObjects,
            Set<String> contraintesFixes, Set<String> contraintesRigides,
            Set<String> contraintesMalleables,
            Map<String[], Double> contraintesExternes, Set<String> classesFixes,
            Set<String> classesRigides, Set<String> classesMalleables,
            Map<String, Double> poidsContraintes) {
        super();
        this.setEchelle(echelle);
        this.fixedConstraints = contraintesFixes;
        this.rigidConstraints = contraintesRigides;
        this.malleableConstraints = contraintesMalleables;
        this.externalConstraints = contraintesExternes;
        this.fixedClasses = classesFixes;
        this.rigidClasses = classesRigides;
        this.malleableClasses = classesMalleables;
        this.constraintWeights = poidsContraintes;
        this.selectedObjects = selectedObjects;
    }

    public boolean isProxiTinActive() {
        for (String[] cle : this.externalConstraints.keySet()) {
            if (cle[0].equals(LSProximityConstraint.class.getName())) {
                return true;
            }
        }
        return false;
    }

    public void printContraintesExternes() {
        for (String[] cle : this.externalConstraints.keySet()) {
            System.out.println("contrainte externe : ");
            System.out.println(cle[0]);
            System.out.println(cle[1]);
            System.out.println(cle[2]);
            System.out.println(this.externalConstraints.get(cle));
        }
    }

    public void printPoidsContraintes() {
        for (String cle : this.constraintWeights.keySet()) {
            System.out.println("poids contrainte : ");
            System.out.println(cle);
            System.out.println(this.constraintWeights.get(cle));
        }
    }

    public Map<String, Double> getPoidsContraintes() {
        return this.constraintWeights;
    }

    public void setConstraintWeights(Map<String, Double> constraintWeights2) {
        this.constraintWeights = constraintWeights2;
    }

    public void setSelectedObjects(Collection<IFeature> selectedObjects) {
        this.selectedObjects = selectedObjects;
    }

    public Collection<IFeature> getSelectedObjects() {
        return this.selectedObjects;
    }

    public void addSelectedObjects(
            Collection<? extends IFeature> selectedObjects) {
        if (this.selectedObjects == null)
            this.selectedObjects = new HashSet<>();
        this.selectedObjects.addAll(selectedObjects);
    }

    /**
     * True if the parameter {@link Class} instance inherits from one of the
     * fixed classes.
     * 
     * @param classe
     * @return
     * @throws ClassNotFoundException
     */
    public boolean isFixedClass(Class<?> classe) throws ClassNotFoundException {
        for (String className : this.fixedClasses) {
            Class<?> fixClass = Class.forName(className);
            if (fixClass.isAssignableFrom(classe)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the parameter {@link Class} instance inherits from one of the
     * rigid classes.
     * 
     * @param classe
     * @return
     * @throws ClassNotFoundException
     */
    public boolean isRigidClass(Class<?> classe) throws ClassNotFoundException {
        for (String className : this.rigidClasses) {
            Class<?> fixClass = Class.forName(className);
            if (fixClass.isAssignableFrom(classe)) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if the parameter {@link Class} instance inherits from one of the
     * malleable classes.
     * 
     * @param classe
     * @return
     * @throws ClassNotFoundException
     */
    public boolean isMalleableClass(Class<?> classe)
            throws ClassNotFoundException {
        for (String className : this.malleableClasses) {
            Class<?> fixClass = Class.forName(className);
            if (fixClass.isAssignableFrom(classe)) {
                return true;
            }
        }
        return false;
    }

    public double getEchelle() {
        return scale;
    }

    public void setEchelle(double echelle) {
        this.scale = echelle;
    }

    public Set<String> getContraintesFixes() {
        return fixedConstraints;
    }

    public void setContraintesFixes(Set<String> contraintesFixes) {
        this.fixedConstraints = contraintesFixes;
    }

    public Set<String> getContraintesRigides() {
        return rigidConstraints;
    }

    public void setContraintesRigides(Set<String> contraintesRigides) {
        this.rigidConstraints = contraintesRigides;
    }

    public Set<String> getContraintesMalleables() {
        return malleableConstraints;
    }

    public void setContraintesMalleables(Set<String> contraintesMalleables) {
        this.malleableConstraints = contraintesMalleables;
    }

    public Map<String[], Double> getContraintesExternes() {
        return externalConstraints;
    }

    public void setContraintesExternes(
            Map<String[], Double> contraintesExternes) {
        this.externalConstraints = contraintesExternes;
    }

    public Set<String> getClassesFixes() {
        return fixedClasses;
    }

    public void setClassesFixes(Set<String> classesFixes) {
        this.fixedClasses = classesFixes;
    }

    public Set<String> getClassesRigides() {
        return rigidClasses;
    }

    public void setClassesRigides(Set<String> classesRigides) {
        this.rigidClasses = classesRigides;
    }

    public Set<String> getClassesMalleables() {
        return malleableClasses;
    }

    public void setClassesMalleables(Set<String> classesMalleables) {
        this.malleableClasses = classesMalleables;
    }

    public double getDensStep() {
        return densStep;
    }

    public void setDensStep(double densStep) {
        this.densStep = densStep;
    }

    public boolean isFilter() {
        return filter;
    }

    public void setFilter(boolean filter) {
        this.filter = filter;
    }

    public double getFilterThreshold() {
        return filterThreshold;
    }

    public void setFilterThreshold(double filterThreshold) {
        this.filterThreshold = filterThreshold;
    }

    public Map<IFeature, Double> getMapSymbolWidth() {
        return mapSymbolWidth;
    }

    public void setMapSymbolWidth(Map<IFeature, Double> mapSymbolWidth) {
        this.mapSymbolWidth = mapSymbolWidth;
    }

    /**
     * Save the mapspecs into a XML file.
     * 
     * @param file
     * @throws IOException
     * @throws TransformerException
     */
    public void saveToXml(File file) throws TransformerException, IOException {
        // build the DOM document
        Document xmlDoc = new DocumentImpl();
        Element root = xmlDoc.createElement("mapspecs");

        // puis on écrit les classes fixes
        if (getClassesFixes().size() > 0) {
            Element fixElem = xmlDoc.createElement("objets-fixes");
            for (String classe : getClassesFixes()) {
                Element classElem = xmlDoc.createElement("classe");
                Node n = xmlDoc.createTextNode(classe);
                classElem.appendChild(n);
                fixElem.appendChild(classElem);
            }
            root.appendChild(fixElem);
        }

        // puis on écrit les classes rigides
        if (getClassesRigides().size() > 0) {
            Element rigidElem = xmlDoc.createElement("objets-rigides");
            for (String classe : getClassesRigides()) {
                Element classElem = xmlDoc.createElement("classe");
                Node n = xmlDoc.createTextNode(classe);
                classElem.appendChild(n);
                rigidElem.appendChild(classElem);
            }
            root.appendChild(rigidElem);
        }

        // puis on écrit les classes malléables
        if (getClassesMalleables().size() > 0) {
            Element mallElem = xmlDoc.createElement("objets-malleables");
            for (String classe : getClassesMalleables()) {
                Element classElem = xmlDoc.createElement("classe");
                Node n = xmlDoc.createTextNode(classe);
                classElem.appendChild(n);
                mallElem.appendChild(classElem);
            }
            root.appendChild(mallElem);
        }

        // on écrit maintenant les contraintes externes
        Element contrExtElem = xmlDoc.createElement("contraintes-externes");
        for (String[] constrExt : getContraintesExternes().keySet()) {
            Element contrainteElem = xmlDoc.createElement("contrainte");
            // on écrit le nom
            Element nomElem = xmlDoc.createElement("nom");
            Node n = xmlDoc.createTextNode(constrExt[0]);
            nomElem.appendChild(n);
            contrainteElem.appendChild(nomElem);
            // on écrit la classe1
            Element classe1Elem = xmlDoc.createElement("classe1");
            n = xmlDoc.createTextNode(constrExt[1]);
            classe1Elem.appendChild(n);
            contrainteElem.appendChild(classe1Elem);
            // on écrit la classe2
            Element classe2Elem = xmlDoc.createElement("classe2");
            n = xmlDoc.createTextNode(constrExt[2]);
            classe2Elem.appendChild(n);
            contrainteElem.appendChild(classe2Elem);
            // on écrit le seuil
            Element seuilElem = xmlDoc.createElement("seuil");
            n = xmlDoc.createTextNode(
                    String.valueOf(getContraintesExternes().get(constrExt)));
            seuilElem.appendChild(n);
            contrainteElem.appendChild(seuilElem);
            contrExtElem.appendChild(contrainteElem);
        }
        root.appendChild(contrExtElem);

        // on s'occupe maintenant des pondérations
        Element pondElem = xmlDoc.createElement("ponderations");
        for (String constraintName : constraintWeights.keySet()) {
            Element contrainteElem = xmlDoc.createElement("contrainte");
            Element nomElem = xmlDoc.createElement("classe");
            Node n = xmlDoc.createTextNode(constraintName);
            nomElem.appendChild(n);
            contrainteElem.appendChild(nomElem);
            Element poidsElem = xmlDoc.createElement("poids");
            n = xmlDoc.createTextNode(
                    String.valueOf(constraintWeights.get(constraintName)));
            poidsElem.appendChild(n);
            contrainteElem.appendChild(poidsElem);
            pondElem.appendChild(contrainteElem);
        }
        root.appendChild(pondElem);

        xmlDoc.appendChild(root);
        // write to XML file
        XMLUtil.writeDocumentToXml(xmlDoc, file);
    }
}
