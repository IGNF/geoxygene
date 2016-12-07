/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.urban;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.spatialanalysis.measures.Compactness;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.XMLUtil;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import smile.classification.SVM;
import smile.data.Dataset;
import smile.data.NominalAttribute;
import smile.math.kernel.GaussianKernel;

/**
 * Implementation of the urban building classification process by Stefan
 * Steiniger (Steiniger et al 2008, Tansactions in GIS). Buildings are
 * classified in one of the five classes: inner city, urban, suburban, rural,
 * industry & commercial.
 * @author GTouya
 *
 */
public class BuildingClassifierSVM {

  private IFeatureCollection<IFeature> buildings;

  public enum BuildingClass {
    INNER_CITY, URBAN, SUBURBAN, RURAL, INDUSTRY;

    public Color getColor() {
      if (this.equals(INNER_CITY))
        return Color.GREEN;
      if (this.equals(URBAN))
        return Color.BLUE;
      if (this.equals(SUBURBAN))
        return Color.MAGENTA;
      if (this.equals(INDUSTRY))
        return Color.ORANGE;
      return Color.GRAY;
    }
  }

  public enum BuildingDescriptor {
    BAr, BCo, BSh, BSq, BEl, BCy, NoBdg, BAHull, BABuff;

    public String getDescription() {
      if (this.equals(BAr))
        return "Building area";
      if (this.equals(BCo))
        return "number of vertices";
      if (this.equals(BSh))
        return "Building shape, i.e. Schumm's index";
      if (this.equals(BSq))
        return "Building squareness, i.e. mean deviation from square angles";
      if (this.equals(BEl))
        return "Building elongation";
      if (this.equals(BCy))
        return "Number of inner rings in the building";
      if (this.equals(NoBdg))
        return "Number of buildings in the neighbourhood";
      if (this.equals(BAHull))
        return "Area of buildings in the neighbourhood divided by area of convex hull";
      if (this.equals(BABuff))
        return "Area of buildings in the neighbourhood divided by area of the buffer used for neighbourhood";
      return "";
    }
  }

  private SVM<double[]> svm;
  private Dataset<double[]> trainingDataset;
  private List<BuildingDescriptor> descriptorsUsed;
  private double bufferSize = 50.0;

  public BuildingClassifierSVM(IFeatureCollection<IFeature> buildings) {
    super();
    this.buildings = buildings;
    this.svm = new SVM<double[]>(new GaussianKernel(8.0), 5.0, 5,
        SVM.Multiclass.ONE_VS_ONE);
    this.descriptorsUsed = new ArrayList<>();
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BAr"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BCo"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BSh"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BSq"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BEl"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BCy"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("NoBdg"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BAHull"));
    this.descriptorsUsed.add(BuildingDescriptor.valueOf("BABuff"));
  }

  public IFeatureCollection<IFeature> getBuildings() {
    return buildings;
  }

  public void setBuildings(IFeatureCollection<IFeature> buildings) {
    this.buildings = buildings;
  }

  public Dataset<double[]> getTrainingDataset() {
    return trainingDataset;
  }

  public void setTrainingDataset(Dataset<double[]> trainingDataset) {
    this.trainingDataset = trainingDataset;
  }

  public List<BuildingDescriptor> getDescriptorNames() {
    return descriptorsUsed;
  }

  public void setDescriptorNames(List<BuildingDescriptor> descriptorNames) {
    this.descriptorsUsed = descriptorNames;
  }

  public double getBufferSize() {
    return bufferSize;
  }

  public void setBufferSize(double bufferSize) {
    this.bufferSize = bufferSize;
  }

  /**
   * Add a descriptor among the available ones to the list of the used
   * descriptors.
   * @param descriptor
   */
  public void addDescriptor(BuildingDescriptor descriptor) {
    if (!this.descriptorsUsed.contains(descriptor))
      this.descriptorsUsed.add(descriptor);
  }

  /**
   * Remove a descriptor from the list of the used descriptors.
   * @param descriptor
   */
  public void removeDescriptor(BuildingDescriptor descriptor) {
    if (this.descriptorsUsed.contains(descriptor))
      this.descriptorsUsed.remove(descriptor);
  }

  /**
   * Train the SVM with the given dataset.
   * @param descriptors
   * @param response
   */
  public void train(TrainingData trainingData) {
    this.trainingDataset = new Dataset<double[]>("training",
        new NominalAttribute("class"));
    for (Integer building : trainingData.getDescriptors().keySet()) {
      trainingDataset.add(trainingData.getDescriptorsArray(building),
          trainingData.getResponse().get(building).ordinal());
    }
    svm.learn(trainingDataset.toArray(new double[trainingDataset.size()][]),
        trainingDataset.toArray(new int[trainingDataset.size()]));
    svm.finish();
  }

  public class TrainingData {
    private Map<Integer, Map<BuildingDescriptor, Double>> descriptors;
    private Map<Integer, BuildingClass> response;

    public TrainingData(
        Map<Integer, Map<BuildingDescriptor, Double>> descriptors,
        Map<Integer, BuildingClass> response) {
      super();
      this.descriptors = descriptors;
      this.response = response;
    }

    public TrainingData() {
      super();
      this.descriptors = new HashMap<>();
      this.response = new HashMap<>();
    }

    public TrainingData(File xmlFile, List<BuildingDescriptor> usedDescriptors)
        throws ParserConfigurationException, SAXException, IOException {
      super();
      this.descriptors = new HashMap<>();
      this.response = new HashMap<>();

      // load the file
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db;
      db = dbf.newDocumentBuilder();
      Document doc;
      doc = db.parse(xmlFile);
      doc.getDocumentElement().normalize();

      // parse the file
      Element root = (Element) doc.getElementsByTagName("training-dataset")
          .item(0);
      for (int i = 0; i < root.getElementsByTagName("example")
          .getLength(); i++) {
        Element exElem = (Element) root.getElementsByTagName("example").item(i);
        Element buildingElem = (Element) exElem.getElementsByTagName("building")
            .item(0);
        int id = Integer
            .valueOf(buildingElem.getChildNodes().item(0).getNodeValue());
        Element classElem = (Element) exElem.getElementsByTagName("class")
            .item(0);
        String buildingClass = classElem.getChildNodes().item(0).getNodeValue();
        Map<BuildingDescriptor, Double> map = new HashMap<>();
        Element descrsElem = (Element) exElem
            .getElementsByTagName("descriptors").item(0);
        for (int j = 0; j < descrsElem.getElementsByTagName("descriptor")
            .getLength(); j++) {
          Element descrElem = (Element) descrsElem
              .getElementsByTagName("descriptor").item(j);
          Element nameElem = (Element) descrElem.getElementsByTagName("name")
              .item(0);
          String name = nameElem.getChildNodes().item(0).getNodeValue();
          Element valueElem = (Element) descrElem.getElementsByTagName("value")
              .item(0);
          Double value = Double
              .valueOf(valueElem.getChildNodes().item(0).getNodeValue());
          if (usedDescriptors.contains(BuildingDescriptor.valueOf(name)))
            map.put(BuildingDescriptor.valueOf(name), value);
        }
        this.descriptors.put(id, map);
        this.response.put(id, BuildingClass.valueOf(buildingClass));
      }
    }

    public Map<Integer, Map<BuildingDescriptor, Double>> getDescriptors() {
      return descriptors;
    }

    public void setDescriptors(
        Map<Integer, Map<BuildingDescriptor, Double>> descriptors) {
      this.descriptors = descriptors;
    }

    public Map<Integer, BuildingClass> getResponse() {
      return response;
    }

    public void setResponse(Map<Integer, BuildingClass> response) {
      this.response = response;
    }

    public void addExample(Integer buildingId, BuildingClass buildingClass)
        throws Exception {
      Map<BuildingDescriptor, Double> map = new HashMap<>();
      for (BuildingDescriptor descriptor : descriptorsUsed) {
        double value = getDescriptorValue(buildingId, descriptor);
        map.put(descriptor, value);
      }
      this.descriptors.put(buildingId, map);
      this.response.put(buildingId, buildingClass);
    }

    /**
     * Get the descriptors for a given building in a double array.
     * @param building
     * @return
     */
    public double[] getDescriptorsArray(Integer building) {
      Map<BuildingDescriptor, Double> buildingDescr = descriptors.get(building);
      double[] array = new double[buildingDescr.size()];

      for (int i = 0; i < descriptorsUsed.size(); i++) {
        BuildingDescriptor descrName = descriptorsUsed.get(i);
        array[i] = buildingDescr.get(descrName);
      }

      return array;
    }

    public void writeToXml(File file) throws TransformerException, IOException {
      Node n = null;
      // ********************************************
      // CREATION DU DOCUMENT XML
      // Document (Xerces implementation only).
      DocumentImpl xmlDoc = new DocumentImpl();
      // Root element.
      Element root = xmlDoc.createElement("training-dataset");

      for (Integer id : descriptors.keySet()) {
        Element exElem = xmlDoc.createElement("example");
        root.appendChild(exElem);

        Element buildingElem = xmlDoc.createElement("building");
        n = xmlDoc.createTextNode(id.toString());
        buildingElem.appendChild(n);
        exElem.appendChild(buildingElem);
        Element classElem = xmlDoc.createElement("class");
        n = xmlDoc.createTextNode(response.get(id).name());
        classElem.appendChild(n);
        exElem.appendChild(classElem);
        Element descrsElem = xmlDoc.createElement("descriptors");
        exElem.appendChild(descrsElem);
        Map<BuildingDescriptor, Double> buildDescrs = descriptors.get(id);
        for (BuildingDescriptor name : buildDescrs.keySet()) {
          Element descrElem = xmlDoc.createElement("descriptor");
          descrsElem.appendChild(descrElem);
          Element nameElem = xmlDoc.createElement("name");
          n = xmlDoc.createTextNode(name.name());
          nameElem.appendChild(n);
          descrElem.appendChild(nameElem);
          Element valueElem = xmlDoc.createElement("value");
          n = xmlDoc.createTextNode(buildDescrs.get(name).toString());
          valueElem.appendChild(n);
          descrElem.appendChild(valueElem);
        }
      }

      // File writing
      xmlDoc.appendChild(root);
      XMLUtil.writeDocumentToXml(xmlDoc, file);
    }
  }

  public double getDescriptorValue(Integer buildingId,
      BuildingDescriptor descriptor) throws Exception {
    IFeature building = getBuildingFromId(buildingId);
    if (BuildingDescriptor.BAr.equals(descriptor))
      return this.getBAr(building);
    if (BuildingDescriptor.BCo.equals(descriptor))
      return this.getBCo(building);
    if (BuildingDescriptor.BSh.equals(descriptor))
      return this.getBSh(building);
    if (BuildingDescriptor.BSq.equals(descriptor))
      return this.getBSq(building);
    if (BuildingDescriptor.BEl.equals(descriptor))
      return this.getBEl(building);
    if (BuildingDescriptor.BCy.equals(descriptor))
      return this.getBCy(building);
    if (BuildingDescriptor.NoBdg.equals(descriptor))
      return this.getNoBdg(building);
    if (BuildingDescriptor.BAHull.equals(descriptor))
      return this.getBAHull(building);
    if (BuildingDescriptor.BABuff.equals(descriptor))
      return this.getBABuff(building);

    return 0.0;
  }

  private IFeature getBuildingFromId(Integer buildingId) {
    for (IFeature building : this.buildings)
      if (building.getId() == buildingId)
        return building;
    return null;
  }

  public void addInnerCityExamples(TrainingData trainingData,
      Set<IFeature> examples) throws Exception {
    for (IFeature example : examples)
      trainingData.addExample(example.getId(), BuildingClass.INNER_CITY);
  }

  public void addUrbanExamples(TrainingData trainingData,
      Set<IFeature> examples) throws Exception {
    for (IFeature example : examples)
      trainingData.addExample(example.getId(), BuildingClass.URBAN);
  }

  public void addSuburbanExamples(TrainingData trainingData,
      Set<IFeature> examples) throws Exception {
    for (IFeature example : examples)
      trainingData.addExample(example.getId(), BuildingClass.SUBURBAN);
  }

  public void addRuralExamples(TrainingData trainingData,
      Set<IFeature> examples) throws Exception {
    for (IFeature example : examples)
      trainingData.addExample(example.getId(), BuildingClass.RURAL);
  }

  public void addIndustrialExamples(TrainingData trainingData,
      Set<IFeature> examples) throws Exception {
    for (IFeature example : examples)
      trainingData.addExample(example.getId(), BuildingClass.INDUSTRY);
  }

  /**
   * Compute the BAr descriptor, i.e. the area of the building.
   * @param building
   * @return
   */
  private double getBAr(IFeature building) {
    return building.getGeom().area();
  }

  /**
   * Compute the BCo descriptor, i.e. the number of corners in the building,
   * i.e. the number of vertices in the outer ring.
   * @param building
   * @return
   */
  private double getBCo(IFeature building) {
    if (building.getGeom() instanceof IPolygon)
      return ((IPolygon) building.getGeom()).exteriorLineString().coord().size()
          - 1;
    return 0;
  }

  /**
   * Compute the BSq descriptor, i.e. the mean deviation of all building corners
   * from a perpendicular angle.
   * @param building
   * @return
   * @throws Exception
   */
  private double getBSq(IFeature building) throws Exception {
    Squareness squareness = new Squareness(building.getGeom(), 15.0, 0.5);
    List<Double> deviations = squareness.getDeviations();
    DescriptiveStatistics stats = new DescriptiveStatistics();
    for (Double dev : deviations)
      stats.addValue(dev);
    return stats.getMean();
  }

  /**
   * Compute the BSh descriptor, i.e. the Schumm's compactness index (McEachren
   * 1985).
   * @param building
   * @return
   */
  private double getBSh(IFeature building) {
    Compactness compactness = new Compactness((IPolygon) building.getGeom());
    return compactness.getSchummIndex();
  }

  /**
   * Compute the BEl descriptor, i.e. the elongation of the building
   * @param building
   * @return
   */
  private double getBEl(IFeature building) {
    return CommonAlgorithms.elongation(building.getGeom());
  }

  /**
   * Compute the BCy descriptor, i.e. the number of courtyards in the building
   * (number of holes of the polygon).
   * @param building
   * @return
   */
  private double getBCy(IFeature building) {
    return ((IPolygon) building.getGeom()).getInterior().size();
  }

  /**
   * Compute the NoBdg descriptor, i.e. the number of buildings intersecting the
   * buffer of the building.
   * @param building
   * @return
   */
  private double getNoBdg(IFeature building) {
    Collection<IFeature> inter = this.buildings
        .select(building.getGeom().buffer(bufferSize));
    return inter.size();
  }

  /**
   * Compute the BAHull descriptor, i.e. the area of the convex hull of all the
   * buildings intersecting the buffer of the building.
   * @param building
   * @return
   */
  private double getBAHull(IFeature building) {
    Collection<IFeature> inter = this.buildings
        .select(building.getGeom().buffer(bufferSize));
    IMultiSurface<IPolygon> multi = GeometryEngine.getFactory()
        .createMultiPolygon();
    for (IFeature interB : inter) {
      multi.add((IPolygon) interB.getGeom());
    }
    return multi.area() / multi.convexHull().area();
  }

  /**
   * Compute the BABuff descriptor, i.e. the total area of the intersections
   * between the buffer and the neighbouring buildings that intersect the
   * buffer.
   * @param building
   * @return
   */
  private double getBABuff(IFeature building) {
    IGeometry buffer = building.getGeom().buffer(bufferSize);
    Collection<IFeature> inter = this.buildings.select(buffer);
    double totalArea = 0.0;
    for (IFeature interB : inter) {
      totalArea += buffer.intersection(interB.getGeom()).area();
    }
    return totalArea / buffer.area();
  }

  /**
   * Predict the building class for a given building. The system has to be
   * trained before.
   * @param building
   * @return
   * @throws Exception
   */
  public BuildingClass predict(IFeature building) throws Exception {
    double[] values = new double[this.descriptorsUsed.size()];
    for (int i = 0; i < values.length; i++)
      values[i] = this.getDescriptorValue(building,
          this.descriptorsUsed.get(i));
    int result = svm.predict(values);
    return BuildingClass.values()[result];
  }

  private double getDescriptorValue(IFeature building,
      BuildingDescriptor descriptor) throws Exception {
    if (BuildingDescriptor.BAr.equals(descriptor))
      return this.getBAr(building);
    if (BuildingDescriptor.BCo.equals(descriptor))
      return this.getBCo(building);
    if (BuildingDescriptor.BSh.equals(descriptor))
      return this.getBSh(building);
    if (BuildingDescriptor.BSq.equals(descriptor))
      return this.getBSq(building);
    if (BuildingDescriptor.BEl.equals(descriptor))
      return this.getBEl(building);
    if (BuildingDescriptor.BCy.equals(descriptor))
      return this.getBCy(building);
    if (BuildingDescriptor.NoBdg.equals(descriptor))
      return this.getNoBdg(building);
    if (BuildingDescriptor.BAHull.equals(descriptor))
      return this.getBAHull(building);
    if (BuildingDescriptor.BABuff.equals(descriptor))
      return this.getBABuff(building);

    return 0.0;
  }

  /**
   * As proposed by Steiniger et al (2008), this method standardizes a building
   * classification by looking at the class of the neighbours. If more than half
   * of the buildings in the radius share the same class, the class of the
   * building is changed to the majority one.
   * @param classification
   * @param radius the radius of research around a building to standardize the
   *          class
   * @return
   */
  public Map<IFeature, BuildingClass> standardizeClassification(
      Map<IFeature, BuildingClass> classification, double radius) {
    Map<IFeature, BuildingClass> newClassif = new HashMap<>();

    for (IFeature building : classification.keySet()) {
      // search the neighbours
      Collection<IFeature> neighbours = this.buildings
          .select(building.getGeom().centroid(), radius);
      int nbInner = 0, nbUrban = 0, nbSub = 0, nbIndus = 0, nbRural = 0;
      for (IFeature neighbour : neighbours) {
        if (classification.get(neighbour).equals(BuildingClass.INDUSTRY))
          nbIndus++;
        else if (classification.get(neighbour).equals(BuildingClass.INNER_CITY))
          nbInner++;
        else if (classification.get(neighbour).equals(BuildingClass.URBAN))
          nbUrban++;
        else if (classification.get(neighbour).equals(BuildingClass.SUBURBAN))
          nbSub++;
        else
          nbRural++;
      }

      // check if one class has the majority
      if (nbInner > neighbours.size() / 2)
        newClassif.put(building, BuildingClass.INNER_CITY);
      else if (nbUrban > neighbours.size() / 2)
        newClassif.put(building, BuildingClass.URBAN);
      else if (nbSub > neighbours.size() / 2)
        newClassif.put(building, BuildingClass.SUBURBAN);
      else if (nbIndus > neighbours.size() / 2)
        newClassif.put(building, BuildingClass.INDUSTRY);
      else if (nbRural > neighbours.size() / 2)
        newClassif.put(building, BuildingClass.RURAL);
      else
        newClassif.put(building, classification.get(building));
    }

    return newClassif;
  }
}
