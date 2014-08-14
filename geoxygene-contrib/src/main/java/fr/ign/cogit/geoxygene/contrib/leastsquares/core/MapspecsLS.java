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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * @author G. Touya
 * 
 *         Les objets Java de cette classe contiennent les différentes
 *         définitions d'une Mapspec de Moindres Carrés.
 */
public class MapspecsLS {

  public enum SelectionType {
    SELECTED_OBJECTS, WINDOW_OBJECTS, SELECTED_AREAS
  }

  private double echelle;

  // les sets des contraintes activées par cette mapspec
  private Set<String> contraintesFixes = new HashSet<String>();
  private Set<String> contraintesRigides = new HashSet<String>();
  private Set<String> contraintesMalleables = new HashSet<String>();

  /**
   * Map qui contient des tableaux de String contenant le nom de la contrainte
   * externe, le nom de la classe 1 et le nom de la classe 2 et en valeur le
   * seuil de séparation pour cette contrainte
   */
  private Map<String[], Double> contraintesExternes = new HashMap<String[], Double>();

  // les sets de classes gothic choisies
  private Set<String> classesFixes = new HashSet<String>();
  private Set<String> classesRigides = new HashSet<String>();
  private Set<String> classesMalleables = new HashSet<String>();

  private Map<String, Double> poidsContraintes;

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
   * Constructeur de mapspecs à partir d'un fichier XML de stockage des mapspecs
   * des Moindres Carrés et une session Clarity.
   * 
   * @param session la session Clarity en cours
   * @param fic le fichier XML contenant les mapspecs
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
    Element fixeElem = (Element) mapspecElem.getElementsByTagName(
        "objets-fixes").item(0);
    if (fixeElem != null) {
      for (int i = 0; i < fixeElem.getElementsByTagName("classe").getLength(); i++) {
        Element classElem = (Element) fixeElem.getElementsByTagName("classe")
            .item(i);
        String classe = classElem.getChildNodes().item(0).getNodeValue();
        this.classesFixes.add(classe);
      }
    }// if(fixeElem!=null)

    // puis on r�cup�re les classes rigides
    Element rigideElem = (Element) mapspecElem.getElementsByTagName(
        "objets-rigides").item(0);
    if (rigideElem != null) {
      for (int i = 0; i < rigideElem.getElementsByTagName("classe").getLength(); i++) {
        Element classElem = (Element) rigideElem.getElementsByTagName("classe")
            .item(i);
        String classe = classElem.getChildNodes().item(0).getNodeValue();
        this.classesRigides.add(classe);
      }
    }// if(rigideElem!=null)

    // puis on récupère les classes malléables
    Element mallElem = (Element) mapspecElem.getElementsByTagName(
        "objets-malleables").item(0);
    if (mallElem != null) {
      for (int i = 0; i < mallElem.getElementsByTagName("classe").getLength(); i++) {
        Element classElem = (Element) mallElem.getElementsByTagName("classe")
            .item(i);
        String classe = classElem.getChildNodes().item(0).getNodeValue();
        this.classesMalleables.add(classe);
      }
    }// if(mallElem!=null)

    // on parse maintenant les contraintes externes
    Element contrExtElem = (Element) mapspecElem.getElementsByTagName(
        "contraintes-externes").item(0);
    for (int i = 0; i < contrExtElem.getElementsByTagName("contrainte")
        .getLength(); i++) {
      Element contrainteElem = (Element) contrExtElem.getElementsByTagName(
          "contrainte").item(i);
      // on r�cup�re le nom
      Element nomElem = (Element) contrainteElem.getElementsByTagName("nom")
          .item(0);
      String nom = nomElem.getChildNodes().item(0).getNodeValue();
      // on récupère la classe1
      Element classe1Elem = (Element) contrainteElem.getElementsByTagName(
          "classe1").item(0);
      String classe1 = classe1Elem.getChildNodes().item(0).getNodeValue();
      // on récupère la classe2
      Element classe2Elem = (Element) contrainteElem.getElementsByTagName(
          "classe2").item(0);
      String classe2 = classe2Elem.getChildNodes().item(0).getNodeValue();
      // on récupère le seuil
      Element seuilElem = (Element) contrainteElem
          .getElementsByTagName("seuil").item(0);
      String seuil = seuilElem.getChildNodes().item(0).getNodeValue();
      String[] cleMap = { nom, classe1, classe2 };
      this.contraintesExternes.put(cleMap, new Double(seuil));
    }

    // on s'occupe maintenant des pond�rations
    this.poidsContraintes = new HashMap<String, Double>();
    Element pondElem = (Element) doc.getElementsByTagName("ponderations").item(
        0);
    for (int i = 0; i < pondElem.getElementsByTagName("contrainte").getLength(); i++) {
      Element contrainteElem = (Element) pondElem.getElementsByTagName(
          "contrainte").item(i);
      Element nomElem = (Element) contrainteElem.getElementsByTagName("classe")
          .item(0);
      String nom = nomElem.getChildNodes().item(0).getNodeValue();
      Element poidsElem = (Element) contrainteElem
          .getElementsByTagName("poids").item(0);
      String poidsS = poidsElem.getChildNodes().item(0).getNodeValue();
      Double poids = new Double(poidsS);
      this.poidsContraintes.put(nom, poids);
    }

    if (this.poidsContraintes.keySet().contains(
        LSMovementConstraint.class.getName())) {
      this.contraintesFixes.add(LSMovementConstraint.class.getName());
      this.contraintesRigides.add(LSMovementConstraint.class.getName());
      this.contraintesMalleables.add(LSMovementConstraint.class.getName());
    }
    if (this.poidsContraintes.keySet().contains(
        LSStiffnessConstraint.class.getName())) {
      this.contraintesRigides.add(LSStiffnessConstraint.class.getName());
    }
    if (this.poidsContraintes.keySet().contains(
        LSSideOrientConstraint.class.getName())) {
      this.contraintesRigides.add(LSSideOrientConstraint.class.getName());
    }
    if (this.poidsContraintes.keySet().contains(
        LSCurvatureConstraint.class.getName())) {
      this.contraintesMalleables.add(LSCurvatureConstraint.class.getName());
    }
    if (this.poidsContraintes.keySet().contains(
        LSMovementDirConstraint.class.getName())) {
      this.contraintesMalleables.add(LSMovementDirConstraint.class.getName());
    }

    // on parse enfin l'échelle
    Element echElem = (Element) doc.getElementsByTagName("echelle").item(0);
    this.setEchelle(Double.parseDouble(echElem.getChildNodes().item(0)
        .getNodeValue()));
  }

  public MapspecsLS(double echelle, Collection<IFeature> selectedObjects,
      Set<String> contraintesFixes, Set<String> contraintesRigides,
      Set<String> contraintesMalleables,
      Map<String[], Double> contraintesExternes, Set<String> classesFixes,
      Set<String> classesRigides, Set<String> classesMalleables,
      Map<String, Double> poidsContraintes) {
    super();
    this.setEchelle(echelle);
    this.contraintesFixes = contraintesFixes;
    this.contraintesRigides = contraintesRigides;
    this.contraintesMalleables = contraintesMalleables;
    this.contraintesExternes = contraintesExternes;
    this.classesFixes = classesFixes;
    this.classesRigides = classesRigides;
    this.classesMalleables = classesMalleables;
    this.poidsContraintes = poidsContraintes;
    this.selectedObjects = selectedObjects;
  }

  public boolean isProxiTinActive() {
    for (String[] cle : this.contraintesExternes.keySet()) {
      if (cle[0].equals(LSProximityConstraint.class.getName())) {
        return true;
      }
    }
    return false;
  }

  public void printContraintesExternes() {
    for (String[] cle : this.contraintesExternes.keySet()) {
      System.out.println("contrainte externe : ");
      System.out.println(cle[0]);
      System.out.println(cle[1]);
      System.out.println(cle[2]);
      System.out.println(this.contraintesExternes.get(cle));
    }
  }

  public void printPoidsContraintes() {
    for (String cle : this.poidsContraintes.keySet()) {
      System.out.println("poids contrainte : ");
      System.out.println(cle);
      System.out.println(this.poidsContraintes.get(cle));
    }
  }

  public Map<String, Double> getPoidsContraintes() {
    return this.poidsContraintes;
  }

  public void setPoidsContraintes(HashMap<String, Double> poidsContraintes) {
    this.poidsContraintes = poidsContraintes;
  }

  public void setSelectedObjects(Collection<IFeature> selectedObjects) {
    this.selectedObjects = selectedObjects;
  }

  public Collection<IFeature> getSelectedObjects() {
    return this.selectedObjects;
  }

  /**
   * True if the parameter {@link Class} instance inherits from one of the fixed
   * classes.
   * @param classe
   * @return
   * @throws ClassNotFoundException
   */
  public boolean isFixedClass(Class<?> classe) throws ClassNotFoundException {
    for (String className : this.classesFixes) {
      Class<?> fixClass = Class.forName(className);
      if (fixClass.isAssignableFrom(classe)) {
        return true;
      }
    }
    return false;
  }

  /**
   * True if the parameter {@link Class} instance inherits from one of the rigid
   * classes.
   * @param classe
   * @return
   * @throws ClassNotFoundException
   */
  public boolean isRigidClass(Class<?> classe) throws ClassNotFoundException {
    for (String className : this.classesRigides) {
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
   * @param classe
   * @return
   * @throws ClassNotFoundException
   */
  public boolean isMalleableClass(Class<?> classe)
      throws ClassNotFoundException {
    for (String className : this.classesMalleables) {
      Class<?> fixClass = Class.forName(className);
      if (fixClass.isAssignableFrom(classe)) {
        return true;
      }
    }
    return false;
  }

  public double getEchelle() {
    return echelle;
  }

  public void setEchelle(double echelle) {
    this.echelle = echelle;
  }

  public Set<String> getContraintesFixes() {
    return contraintesFixes;
  }

  public void setContraintesFixes(Set<String> contraintesFixes) {
    this.contraintesFixes = contraintesFixes;
  }

  public Set<String> getContraintesRigides() {
    return contraintesRigides;
  }

  public void setContraintesRigides(Set<String> contraintesRigides) {
    this.contraintesRigides = contraintesRigides;
  }

  public Set<String> getContraintesMalleables() {
    return contraintesMalleables;
  }

  public void setContraintesMalleables(Set<String> contraintesMalleables) {
    this.contraintesMalleables = contraintesMalleables;
  }

  public Map<String[], Double> getContraintesExternes() {
    return contraintesExternes;
  }

  public void setContraintesExternes(Map<String[], Double> contraintesExternes) {
    this.contraintesExternes = contraintesExternes;
  }

  public Set<String> getClassesFixes() {
    return classesFixes;
  }

  public void setClassesFixes(Set<String> classesFixes) {
    this.classesFixes = classesFixes;
  }

  public Set<String> getClassesRigides() {
    return classesRigides;
  }

  public void setClassesRigides(Set<String> classesRigides) {
    this.classesRigides = classesRigides;
  }

  public Set<String> getClassesMalleables() {
    return classesMalleables;
  }

  public void setClassesMalleables(Set<String> classesMalleables) {
    this.classesMalleables = classesMalleables;
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
}
