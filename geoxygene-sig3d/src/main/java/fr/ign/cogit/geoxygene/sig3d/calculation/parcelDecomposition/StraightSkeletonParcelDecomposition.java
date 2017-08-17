package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import org.apache.commons.math3.random.GaussianRandomGenerator;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.analysis.FindObjectInDirection;
import fr.ign.cogit.geoxygene.sig3d.calculation.CampSkeleton;
import fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition.geom.Strip;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * Re-implementation of block decomposition into parcels from :
 * 
 * Vanegas, C. A., Kelly, T., Weber, B., Halatsch, J., Aliaga, D. G., Müller,
 * P., May 2012. Procedural generation of parcels in urban modeling. Comp.
 * Graph. Forum 31 (2pt3).
 * 
 * Decomposition method by using straight skeleton
 * 
 * @author Mickael Brasebin
 *
 */
public class StraightSkeletonParcelDecomposition {

	//////////////////////////////////////////////////////
	// Input data parameters
	// Must be a double attribute
	public static String NAME_ATT_IMPORTANCE = "LARGEUR";
	public final static String NAME_ATT_ROAD = "NOM_VOIE_G";

	//////////////////////////////////////////////////////

	// Indicate if a boundary of partial skeleton is at the border of input
	// block
	public final static String ATT_IS_INSIDE = "INSIDE";
	public final static int ARC_VALUE_INSIDE = 1;
	public final static int ARC_VALUE_OUTSIDE = 2;

	public final static String ATT_ROAD = "NOM_VOIE";

	public static String ATT_FACE_ID_STRIP = "ID_STRIP";

	// Indicate the importance of the neighbour road
	public static String ATT_IMPORTANCE = "IMPORTANCE";

	public static boolean DEBUG = false;
	public static String FOLDER_OUT_DEBUG = "/home/mickael/Bureau/Parcel_div/test/test1/debug/";

	private static Logger logger = Logger.getLogger(StraightSkeletonParcelDecomposition.class);

	/**
	 * Main algorithm to process the algorithm
	 * 
	 * @param pol
	 *            : polygon block that will be decomposed
	 * @param roads
	 *            : roads around the block polygon, may be empty or null. It
	 *            determines some priority according to road importance.
	 * @param maxDepth
	 *            : maximal depth of a parcel
	 * @param maxDistanceForNearestRoad
	 *            : parameters that determine how far a road is considered from
	 *            block exterior
	 * @param minimalArea
	 *            : minimal area of a parcel
	 * @param minWidth
	 *            : minimum width of a parcel
	 * @param maxWidth
	 *            : maximal width of a parcel
	 * @param noiseParameter
	 *            : standard deviation of width distribution beteween minWidth
	 *            and mawWidthdetermineInteriorLineString
	 * @param rng
	 *            : Random generator
	 * @return
	 */
	public static IFeatureCollection<IFeature> runStraightSkeleton2(IPolygon pol, IFeatureCollection<IFeature> roads,
			double maxDepth, double maxDistanceForNearestRoad, double minimalArea, double minWidth, double maxWidth,
			double noiseParameter, RandomGenerator rng) {

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		logger.info("------Begin decomposition with runStraightSkeleton method-----");
		logger.info("------Partial skelton application-----");
		// Partial skeleton
		CampSkeleton cs = new CampSkeleton(pol, maxDepth);

		// Information is stored in getPoids method
		detectAndAnnotateRoadEdges(pol, cs.getCarteTopo().getListeArcs());
		detectNeighbourdRoad(pol, cs.getCarteTopo().getListeArcs(), roads, maxDistanceForNearestRoad);

		if (DEBUG) {
			logger.info("------Saving for debug  ...-----");
			for (Arc a : cs.getCarteTopo().getListeArcs()) {

				Face f1 = a.getFaceDroite();

				String str = "";
				if (f1 != null) {
					str = f1.toString();
				}

				Face f2 = a.getFaceGauche();

				String str2 = "";
				if (f2 != null) {
					str2 = f2.toString();
				}

				AttributeManager.addAttribute(a, "FD", str, "String");
				AttributeManager.addAttribute(a, "FG", str2, "String");
			}

			debugExport(cs.getCarteTopo().getListeFaces(), "initialFaces");

			debugExport(cs.getCarteTopo().getListeArcs(), "exteriorArcs");
		}

		logger.info("------Annotation of external edges-----");
		// Information is stored in getPoids method
		HashMap<String, List<Face>> llFace = detectStrip(cs.getCarteTopo().getListeFaces(), pol, roads,
				maxDistanceForNearestRoad);

		if (DEBUG) {
			logger.info("------Saving for debug  ...-----");
			debugExport(cs.getCarteTopo().getListeFaces(), "striproad");
		}

		List<List<Face>> stripFace = splittingInAdjacentStrip(llFace);
		if (DEBUG) {
			logger.info("------Saving for debug  ...-----");
			List<Face> lf = new ArrayList<>();
			int count = 0;
			for (List<Face> lfTemp : stripFace) {
				for (Face fTemp : lfTemp) {
					lf.add(fTemp);

				}
				count++;
			}

			System.out.println(count);
			debugExport(lf, "striproadCorrected");
		}

		logger.info("------Fast strip cleaning...-----");
		stripFace = fastStripCleaning(stripFace, minimalArea);

		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			debugExport(cs.getCarteTopo().getPopFaces(), "fastStripCleaning");
		}

		List<ILineString> interiorEdgesByStrip = detectInteriorEdges(stripFace);

		if (DEBUG) {
			logger.info("------Saving for debug  ...-----");
			List<IFeature> lf = new ArrayList<>();
			int count = 0;
			for (ILineString line : interiorEdgesByStrip) {

				IFeature fTemp = new DefaultFeature(line);
				AttributeManager.addAttribute(fTemp, ATT_FACE_ID_STRIP, count, "Integer");
				lf.add(fTemp);

				count++;
			}

			System.out.println(count);
			debugExport(lf, "interiorEdgesByStrip");
		}
		
		
		HashMap<String,IDirectPosition> limitsPoints = interiorLimitPointsBetweenStrip(stripFace, pol);
		
		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<>();
			
			for(String str: limitsPoints.keySet()){
				IFeature feat = new DefaultFeature(new GM_Point(limitsPoints.get(str)));
				
				AttributeManager.addAttribute(feat, "inter", str, "String");
				
				
				featColl.add(feat);
			}
			debugExport(featColl, "interPoints");
		}
		

		logger.info("------Fixing diagonal edges...-----");
		// 3 group of edges : interior/exterior/side
		List<List<ILineString>> listOfLists = fixingDiagonal2(stripFace, limitsPoints);
		listOfLists.add(0,interiorEdgesByStrip);
		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			IFeatureCollection<IFeature> featDebug = new FT_FeatureCollection<>();
			for (List<ILineString> ls : listOfLists) {
				for (ILineString lls : ls) {
					featDebug.add(new DefaultFeature(lls));
				}
			}
			debugExport(featDebug, "stepanotation5");
		}
		

		featCollOut = generateParcel(listOfLists, minWidth, maxWidth, noiseParameter, rng);

		return featCollOut;
	}

	////////////////////////////////////////
	///////// V2 above
	///////////////////////////////////////

	private static List<ILineString> detectInteriorEdges(List<List<Face>> stripFace) {
		List<List<Arc>> lLLArc = new ArrayList<>();

		for (List<Face> lFtemp : stripFace) {

			List<Arc> currentList = new ArrayList<>();
			lLLArc.add(currentList);

			for (Face f : lFtemp) {

				List<Arc> lArcTemp = f.arcs();

				for (Arc a : lArcTemp) {
					if (Integer.parseInt(a.getAttribute(ATT_IS_INSIDE).toString()) == ARC_VALUE_OUTSIDE) {
						continue;
					}

					if (lFtemp.contains(a.getFaceDroite()) && lFtemp.contains(a.getFaceGauche())) {
						continue;
					}

					if (currentList.contains(a)) {
						continue;
					}

					if (a.getFaceGauche() != null && a.getFaceDroite() != null) {
						Object o = a.getFaceGauche().getAttribute(ATT_FACE_ID_STRIP);
						Object o2 = a.getFaceDroite().getAttribute(ATT_FACE_ID_STRIP);

						// We do not keep limits between two adjacent strip
						if (o != null && o2 != null) {

							int id1 = Integer.parseInt(o.toString());
							int id2 = Integer.parseInt(o2.toString());

							
							if (Math.abs(id1 - id2) == 1) {
							
								continue;
							}

							if (Math.abs(id1 - id2) == (stripFace.size() - 1)) {
							
								continue;
							}


						}
					}

					currentList.add(a);

				}

			}

		}

		List<ILineString> lsListOut = new ArrayList<>();

		// Merge intoLineString
		for (List<Arc> arcs : lLLArc) {



			List<ILineString> lsList = new ArrayList<>();

			for (Arc a : arcs) {

				lsList.add(a.getGeometrie());
			
			}

			ILineString ls = Operateurs.union(lsList, 0.1);
			lsListOut.add(ls);

	
		}

		return lsListOut;
	}
	
	
	
	private static HashMap<String,IDirectPosition> interiorLimitPointsBetweenStrip(List<List<Face>> stripFace, IPolygon pol) {
		
		ILineString exterior = new GM_LineString(pol.exteriorCoord());
		HashMap<String, List<Arc>> mapLimitArcs = new HashMap<>();
		HashMap<String, IDirectPosition> mapInterPoint = new HashMap<>();

		for (List<Face> lFtemp : stripFace) {

		

			for (Face f : lFtemp) {

				List<Arc> lArcTemp = f.arcs();

				for (Arc a : lArcTemp) {
					if (Integer.parseInt(a.getAttribute(ATT_IS_INSIDE).toString()) == ARC_VALUE_OUTSIDE) {
						continue;
					}

					if (lFtemp.contains(a.getFaceDroite()) && lFtemp.contains(a.getFaceGauche())) {
						continue;
					}

				

					if (a.getFaceGauche() != null && a.getFaceDroite() != null) {
						Object o = a.getFaceGauche().getAttribute(ATT_FACE_ID_STRIP);
						Object o2 = a.getFaceDroite().getAttribute(ATT_FACE_ID_STRIP);

						// We do not keep limits between two adjacent strip
						if (o != null && o2 != null) {

							int id1 = Integer.parseInt(o.toString());
							int id2 = Integer.parseInt(o2.toString());

							if ((Math.abs(id1 - id2) == 1) || (Math.abs(id1 - id2) == (stripFace.size() - 1)) ) {
								
								int idMin = Math.min(id1, id2);
							    int idMax = Math.max(id1, id2);
							    
							    String id = idMin+"-"+idMax;
							    
							    List<Arc> lA = mapLimitArcs.get(id);
							    
							    if(lA == null){
							    	lA = new ArrayList<>();
							    	mapLimitArcs.put(id, lA);
							    }
								if(!lA.contains(a)){
									AttributeManager.addAttribute(a, "TEMP_KEY", id, "String");
									lA.add(a);
								}
								
								continue;
							}

						
					
						}
					}


				}

			}

		}

		List<ILineString> lsListOut = new ArrayList<>();

		// Merge intoLineString
		for (String str : mapLimitArcs.keySet()) {

			List<ILineString> lsList = new ArrayList<>();
			
			List<Arc> lA = mapLimitArcs.get(str);

			for (Arc a : lA) {

				lsList.add(a.getGeometrie());
			}
			
			String key = lA.get(0).getAttribute("TEMP_KEY").toString();


			ILineString ls = Operateurs.union(lsList, 0.1);
			lsListOut.add(ls);
			
			IDirectPosition dp1 = ls.coord().get(0);
			IDirectPosition dpLast = ls.coord().get(ls.coord().size()-1);
			IPoint p1 = new GM_Point(dp1);
			IPoint pLast = new GM_Point(dpLast);
			
			if(p1.distance(exterior) > pLast.distance(exterior)){
				mapInterPoint.put(key, dp1);
			}else{
				mapInterPoint.put(key, dpLast);
			}
			
		}
		
		

		return mapInterPoint;
	}
	

	private static HashMap<String, List<Face>> detectStrip(List<Face> listFace, IPolygon pol,
			IFeatureCollection<IFeature> roads, double thresholdRoad) {

		HashMap<String, List<Face>> hashFaces = new HashMap<>();

		// For edge face
		for (Face f : listFace) {

			List<Arc> arcs = f.arcs();

			String bestRoadName = "";
			double importance = 0;
			double maxLength = Double.NEGATIVE_INFINITY;

			// For each arc we determine the nearest road
			for (Arc a : arcs) {

				IFeature feat = FindObjectInDirection.find(a.getGeom(), pol, roads, thresholdRoad);

				if (feat == null) {
					continue;
				}

				Object o = feat.getAttribute(NAME_ATT_ROAD);

				String roadName = "";

				if (o != null) {
					roadName = o.toString();
				}

				double lengthTemp = a.getGeometrie().length();

				if (lengthTemp > maxLength) {

					maxLength = lengthTemp;
					bestRoadName = roadName;

					Object otemp = a.getAttribute(ATT_IMPORTANCE);

					if (otemp != null) {
						importance = Double.parseDouble(otemp.toString());
					}

				}

			}

			AttributeManager.addAttribute(f, ATT_IMPORTANCE, importance, "Double");

			AttributeManager.addAttribute(f, ATT_FACE_ID_STRIP, bestRoadName, "String");

			List<Face> lFaces = hashFaces.get(bestRoadName);

			if (lFaces == null) {
				lFaces = new ArrayList<>();
				hashFaces.put(bestRoadName, lFaces);

			}

			lFaces.add(f);

		}
		// ... and group them according to roads name

		return hashFaces;

	}

	private static List<List<Face>> splittingInAdjacentStrip(HashMap<String, List<Face>> llFace) {

		List<List<Face>> lFOut = new ArrayList<>();

		for (List<Face> lF : llFace.values()) {

			List<List<Face>> lFOutTemp = createGroup(lF, 0);
			System.out.println("NB Group : " + lFOutTemp.size());

			for (List<Face> lf : lFOutTemp) {
				lFOut.add(lf);
			}
		}

		// Adding id strip attribute
		int count = 0;
		for (List<Face> lfTemp : lFOut) {
			for (Face fTemp : lfTemp) {

				AttributeManager.addAttribute(fTemp, ATT_FACE_ID_STRIP, count + "", "String");

			}
			count++;
		}

		return lFOut;
	}

	/**
	 * Fixing diagonals as named in the article : removing parts of a following
	 * strip if it has less importance than the current one
	 * 
	 * @param stripFace
	 * @param externalSkeltonArcs
	 * @return
	 */
	private static List<List<ILineString>> fixingDiagonal2(List<List<Face>> stripFace,
			HashMap<String,IDirectPosition> interPoints) {

		List<List<ILineString>> outArcs = new ArrayList<List<ILineString>>();

		List<ILineString> lExteriorLineString = new ArrayList<>();

		List<ILineString> lSide = new ArrayList<>();

		// Determining interior and exterior linestring for each limit
		int nbGroup = stripFace.size();
		for (int i = 0; i < nbGroup; i++) {

			ILineString exteriorGroupLineString = determineExteriorLineString(stripFace.get(i));
			lExteriorLineString.add(exteriorGroupLineString);

		}

		// For each group (it is not necessary to treat the last one as we //
		// consider the previous and next strip

		for (int i = 0; i < nbGroup ; i++) {
			
		
			List<Face> currentGroup = stripFace.get(i);

			int nextIndex = (i == (nbGroup - 1)) ? (0) : (i + 1);
			
			String idPointInter = Math.min(i, nextIndex)+"-"+Math.max(i, nextIndex);

			double nextImportance = Double
					.parseDouble(stripFace.get(nextIndex).get(0).getAttribute(ATT_IMPORTANCE).toString());
			double currentImportance = Double.parseDouble(currentGroup.get(0).getAttribute(ATT_IMPORTANCE).toString());

			ILineString currentExterior = lExteriorLineString.get(i);
			
			if(currentExterior.length() < 0.5){
				continue;
			}

			ILineString nextExterior = lExteriorLineString.get(nextIndex);


			// Determination of limit point on interior between two strips
			IDirectPosition pointToCast =  interPoints.get(idPointInter);

		


			ILineString lineStringToSplit;
			// Line to split is determined
			// according to the importance b
			boolean splitCurrentLine = (nextImportance > currentImportance);

			if (splitCurrentLine) { // We split the current line
				lineStringToSplit = currentExterior;

			} else { // we split the next line
				lineStringToSplit = nextExterior;

			}

			// Line is splitting by projecting the limit points and each part is
			// re-affected to relevant group
			List<IDirectPosition> dpl = new ArrayList<>();
			dpl.addAll(lineStringToSplit.coord().getList());

			int pointIndex = Operateurs.projectAndInsertWithPosition(pointToCast, dpl);

			DirectPositionList sidePoint = new DirectPositionList();
			sidePoint.add(pointToCast);
			sidePoint.add(dpl.get(pointIndex));
			ILineString ls = new GM_LineString(sidePoint);
			lSide.add(ls);
			
	
		

			if (pointIndex == -1) {
				logger.error("This case is not supposed to happen for point projection : "
						+ StraightSkeletonParcelDecomposition.class);
			}

			IDirectPositionList dpl1 = new DirectPositionList();
			IDirectPositionList dpl2 = new DirectPositionList();

			for (int j = 0; j <= pointIndex; j++) {
				dpl1.add(dpl.get(j));
			}
			for (int j = pointIndex; j < dpl.size(); j++) {
				dpl2.add(dpl.get(j));
			}

			ILineString ls1 = new GM_LineString(dpl1);
			ILineString ls2 = new GM_LineString(dpl2);

			

			List<ILineString> lsList = new ArrayList<>();

			if (splitCurrentLine) { // We merge the next line with part of
									// current line
				// Switching variable
				if (ls1.distance(nextExterior) > 0.01) {
					ILineString lsTemp = ls1;
					ls1 = ls2;
					ls2 = lsTemp;

				}

				
				lsList.add(ls1);
				lsList.add(nextExterior);
				
				
		//	nextExterior = Operateurs.union(lsList);
				
				
				lExteriorLineString.set(i, ls2);
				lExteriorLineString.set(nextIndex, nextExterior);

			} else {

				// We merge the next line with part of next line
				// Switching variable
				if (ls1.distance(currentExterior) > 0.01) {
					ILineString lsTemp = ls1;
					ls1 = ls2;
					ls2 = lsTemp;

				}
				lsList.add(ls1);
				lsList.add(currentExterior);
				
				
			//	currentExterior = Operateurs.union(lsList);

				lExteriorLineString.set(i, currentExterior);
				lExteriorLineString.set(nextIndex, ls2);

			}

		}


		outArcs.add(lExteriorLineString);
		outArcs.add(lSide);

		return outArcs;

	}

	private static List<List<Face>> createGroup(List<? extends Face> facesIn, double connexionDistance) {

		List<List<Face>> listGroup = new ArrayList<>();

		while (!facesIn.isEmpty()) {

			Face face = facesIn.remove(0);

			List<Face> currentGroup = new ArrayList<>();
			currentGroup.add(face);

			int nbElem = facesIn.size();

			bouclei: for (int i = 0; i < nbElem; i++) {

				for (Face faceTemp : currentGroup) {

					if (facesIn.get(i).getGeom().distance(faceTemp.getGeom()) <= connexionDistance) {

						currentGroup.add(facesIn.remove(i));

						i = -1;
						nbElem--;
						continue bouclei;

					}
				}

			}

			listGroup.add(currentGroup);
		}

		return listGroup;

	}

	/**
	 * Main algorithm to process the algorithm
	 * 
	 * @param pol
	 *            : polygon block that will be decomposed
	 * @param roads
	 *            : roads around the block polygon, may be empty or null. It
	 *            determines some priority according to road importance.
	 * @param maxDepth
	 *            : maximal depth of a parcel
	 * @param maxDistanceForNearestRoad
	 *            : parameters that determine how far a road is considered from
	 *            block exterior
	 * @param minimalArea
	 *            : minimal area of a parcel
	 * @param minWidth
	 *            : minimum width of a parcel
	 * @param maxWidth
	 *            : maximal width of a parcel
	 * @param noiseParameter
	 *            : standard deviation of width distribution beteween minWidth
	 *            and mawWidth
	 * @param rng
	 *            : Random generator
	 * @return
	 */
	public static IFeatureCollection<IFeature> runStraightSkeleton(IPolygon pol, IFeatureCollection<IFeature> roads,
			double maxDepth, double maxDistanceForNearestRoad, double minimalArea, double minWidth, double maxWidth,
			double noiseParameter, RandomGenerator rng) {

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		logger.info("------Begin decomposition with runStraightSkeleton method-----");
		logger.info("------Partial skelton application-----");
		// Partial skeleton
		CampSkeleton cs = new CampSkeleton(pol, maxDepth);

		if (DEBUG) {
			logger.info("------Saving for debug  ...-----");
			debugExport(cs.getCarteTopo().getListeFaces(), "initialFaces");
			debugExport(cs.getExteriorArcs(), "extArcs");
			debugExport(cs.getInteriorArcs(), "intArcs");
			debugExport(cs.getIncludedArcs(), "incArcs");

		}

		logger.info("------Annotation of external edges-----");
		// Information is stored in getPoids method
		detectAndAnnotateRoadEdges(pol, cs.getExteriorArcs());

		if (DEBUG) {
			logger.info("------Saving for debug  ...-----");
			debugExport(cs.getExteriorArcs(), "stepanotation");
		}

		logger.info("------Detecting neighbour roads and affecting importance ...-----");
		detectNeighbourdRoad(pol, cs.getExteriorArcs(), roads, maxDistanceForNearestRoad);

		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			debugExport(cs.getExteriorArcs(), "stepanotation2");
		}

		logger.info("------Grouping faces in strips...-----");
		List<List<Face>> stripFace = generateStrip(cs.getCarteTopo().getPopFaces(), cs.getExteriorArcs());

		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			debugExport(cs.getCarteTopo().getPopFaces(), "stepanotation3");
		}

		logger.info("------Fast strip cleaning...-----");
		stripFace = fastStripCleaning(stripFace, minimalArea);

		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			debugExport(cs.getCarteTopo().getPopFaces(), "stepanotation4");
		}

		logger.info("------Fixing diagonal edges...-----");
		// 3 group of edges : interior/exterior/side
		List<Strip> listOfLists = fixingDiagonal(stripFace, cs.getExteriorArcs());

		featCollOut.add(new DefaultFeature());
		/*
		if (DEBUG) {
			logger.info("------Saving for debug ...-----");
			IFeatureCollection<IFeature> featDebug = new FT_FeatureCollection<>();
			for (List<ILineString> ls : listOfLists) {
				for (ILineString lls : ls) {
					featDebug.add(new DefaultFeature(lls));
				}
			}
			debugExport(featDebug, "stepanotation5");
		}

		featCollOut = generateParcel(listOfLists, minWidth, maxWidth, noiseParameter, rng);*/

		return featCollOut;
	}

	/**
	 * Detect edges from polygon block that lays at the exterior and annotate
	 * edges as outside or inside the polygon
	 * 
	 * @param pol
	 * @param arcToAnnotate
	 */
	private static void detectAndAnnotateRoadEdges(IPolygon pol, List<Arc> arcToAnnotate) {

		ILineString ls = new GM_LineString(pol.getExterior().coord());

		IGeometry buffer = ls.buffer(0.5);

		for (Arc a : arcToAnnotate) {

			if (buffer.contains(a.getGeometrie())) {
				int value = ARC_VALUE_OUTSIDE;
				AttributeManager.addAttribute(a, ATT_IS_INSIDE, value, "Integer");
			} else {
				int value = ARC_VALUE_INSIDE;

				AttributeManager.addAttribute(a, ATT_IS_INSIDE, value, "Integer");
			}

		}

	}

	/**
	 * Detect nearest road from exterior edges and annotates according to the
	 * importance.
	 * 
	 * @param pol
	 * @param arcToAnnotate
	 * @param roads
	 * @param thresholdRoad
	 */
	private static void detectNeighbourdRoad(IPolygon pol, List<Arc> arcToAnnotate, IFeatureCollection<IFeature> roads,
			double thresholdRoad) {

		for (Arc a : arcToAnnotate) {

			// If it is not an external arc, 0 is set by default
			if (Integer.parseInt(a.getAttribute(ATT_IS_INSIDE).toString()) == ARC_VALUE_INSIDE) {
				AttributeManager.addAttribute(a, ATT_IMPORTANCE, 0.0, "Double");
				AttributeManager.addAttribute(a, ATT_ROAD, "", "String");
				continue;
			}

			IFeature feat = FindObjectInDirection.find(a.getGeom(), pol, roads, thresholdRoad); // NearestRoadFinder.findNearest(roads,
																								// a.getGeom(),
																								// thresholdRoad);

			if (feat == null) {
				AttributeManager.addAttribute(a, ATT_IMPORTANCE, 0.0, "Double");
				AttributeManager.addAttribute(a, ATT_ROAD, "", "String");
				continue;
			}

			Object o = feat.getAttribute(NAME_ATT_IMPORTANCE);
			double value = 0;

			if (o == null || !(o instanceof Double)) {
				logger.warn("Attribute : " + NAME_ATT_IMPORTANCE + "  not found or null ");
			} else {
				value = Double.parseDouble(o.toString());
			}
			AttributeManager.addAttribute(a, ATT_IMPORTANCE, value, "Double");

			o = feat.getAttribute(NAME_ATT_ROAD);
			String valuestr = "";
			if (o == null || !(o instanceof String)) {
				logger.warn("Attribute : " + NAME_ATT_ROAD + "  not found or null ");
			} else {
				valuestr = o.toString();
			}
			AttributeManager.addAttribute(a, ATT_ROAD, valuestr, "String");

		}

	}

	/**
	 * Generates the different strip according to exterior edges importance
	 * 
	 * @param popFaces
	 * @param exteriorArc
	 * @return
	 */
	private static List<List<Face>> generateStrip(IPopulation<Face> popFaces, List<Arc> exteriorArc) {

		String currentStripName = null;
		int count = -1;

		List<List<Face>> correspondanceMapID = new ArrayList<>();

		// For edge face
		for (Face f : popFaces) {
			// Determining whose that belongs to the same roads and group them
			// according to roads name
			for (Arc a : f.arcs()) {

				if (!exteriorArc.contains(a)) {
					continue;
				}

				if (Integer.parseInt(a.getAttribute(ATT_IS_INSIDE).toString()) == ARC_VALUE_INSIDE) {
					continue;
				}

				String newRoadName = a.getAttribute(ATT_ROAD).toString();

				if (currentStripName == null ||  ! (currentStripName.equals(newRoadName))) {
					currentStripName = newRoadName;
					count++;
					correspondanceMapID.add(new ArrayList<>());
				}

				correspondanceMapID.get(count).add(f);

				AttributeManager.addAttribute(f, ATT_FACE_ID_STRIP, count, "Integer");
				AttributeManager.addAttribute(f, ATT_IMPORTANCE, a.getAttribute(ATT_IMPORTANCE), "Double");

				break;

			}

		}
		// ... and group them according to roads name

		correspondanceMapID = ordonnateGroupe(correspondanceMapID);
		for (int i = 0; i < correspondanceMapID.size(); i++) {

			List<Face> currentGroup = correspondanceMapID.get(i);
			for (Face f : currentGroup) {
				AttributeManager.addAttribute(f, ATT_FACE_ID_STRIP, i, "Integer");

			}
		}

		return correspondanceMapID;

	}

	private static List<List<Face>> ordonnateGroupe(List<List<Face>> lGroupes) {

		List<ILineString> lExteriorLineString = new ArrayList<>();
		List<List<Face>> orderedGroup = new ArrayList<>();

		for (List<Face> groupe : lGroupes) {

			ILineString exteriorLineString = determineExteriorLineString(groupe);
			lExteriorLineString.add(exteriorLineString);
		}

		// FirstGroup
		IGeometry currentLineString = lExteriorLineString.remove(0).buffer(0.1);
		orderedGroup.add(lGroupes.remove(0));

		for (int i = 0; i < lGroupes.size(); i++) {

			List<Face> currentGroup = lGroupes.get(i);

			for (Face f : currentGroup) {

				if (!f.getGeom().intersects(currentLineString)) {
					continue;
				}

				orderedGroup.add(lGroupes.remove(i));
				currentLineString = lExteriorLineString.remove(i).buffer(0.1);
				i = -1;
				break;
			}

		}

		if (!lGroupes.isEmpty()) {
			logger.warn("All groups are not used during ordering. Left groups : " + lGroupes.size());
		}

		return orderedGroup;

	}

	/**
	 * Make a fast clean by removing small strips and affect them according to
	 * most important adjacent road
	 * @TODO
	 * @param initStripping
	 * @param minimalArea
	 * @return
	 */
	private static List<List<Face>> fastStripCleaning(List<List<Face>> initStripping, double minimalArea) {

		int nbGroup = initStripping.size();

		for (int i = 0; i < nbGroup; i++) {

			List<Face> currentGroup = initStripping.get(i);
			double totalArea = 0;

			for (Face f : currentGroup) {
				totalArea = totalArea + f.getGeometrie().area();
			}

			if (totalArea < minimalArea) {

				int previousIndex = (i == 0) ? (nbGroup - 1) : (i - 1);
				int nextIndex = (i == (nbGroup - 1)) ? (0) : (i + 1);

				double previousImportance = Double
						.parseDouble(initStripping.get(previousIndex).get(0).getAttribute(ATT_IMPORTANCE).toString());
				double nextImportance = Double
						.parseDouble(initStripping.get(nextIndex).get(0).getAttribute(ATT_IMPORTANCE).toString());

				if (previousImportance > nextImportance) {
					// The group is merged with previous one

					initStripping.get(previousIndex).addAll(initStripping.remove(i));

					i--;
					nbGroup--;

				} else if (previousImportance < nextImportance) {

					System.out.println("Je merge");
					// The group is merged with the next one
					initStripping.get(nextIndex--).addAll(initStripping.remove(i));
					i--;
					nbGroup--;
				}

				for (Face f : currentGroup) {
					AttributeManager.addAttribute(f, ATT_IMPORTANCE, Math.max(previousIndex, nextImportance), "Double");

				}

			}
		}

		initStripping = ordonnateGroupe(initStripping);
		int count = 0;
		for (int i = 0; i < nbGroup; i++) {
			// Groups are recounted
			List<Face> currentGroup = initStripping.get(i);
			for (Face f : currentGroup) {
				AttributeManager.addAttribute(f, ATT_FACE_ID_STRIP, count, "Integer");

			}
			count++;
		}

		return initStripping;
	}

	/**
	 * Fixing diagonals as named in the article : removing parts of a following
	 * strip if it has less importance than the current one
	 * 
	 * @param stripFace
	 * @param externalSkeltonArcs
	 * @return
	 */
	private static List<Strip> fixingDiagonal(List<List<Face>> stripFace, List<Arc> externalSkeltonArcs) {

		List<List<ILineString>> outArcs = new ArrayList<List<ILineString>>();

		List<ILineString> lExteriorLineString = new ArrayList<>();
		List<ILineString> lInteriorLineString = new ArrayList<>();

		List<ILineString> lSideIni = new ArrayList<>();
		
		List<Strip> lStrip = new  ArrayList<>();
		lStrip.add(null);


		// Determining interior and exterior linestring for each limit
		for (List<Face> groupe : stripFace) {
			ILineString interiorLineString = determineInteriorLineString(groupe);
			ILineString exteriorLineString = determineExteriorLineString(groupe);

			lExteriorLineString.add(exteriorLineString);
			lInteriorLineString.add(interiorLineString);

		}

		// For each group (it is not necessary to treat the last one as we
		// consider the previous and next strip
		int nbGroup = stripFace.size();

		for (int i = 0; i < nbGroup; i++) {
			System.out.println(lInteriorLineString.get(i));
		}

		for (int i = 0; i < nbGroup - 1; i++) {

			List<Face> currentGroup = stripFace.get(i);

			int nextIndex = (i == (nbGroup - 1)) ? (0) : (i + 1);

			double nextImportance = Double
					.parseDouble(stripFace.get(nextIndex).get(0).getAttribute(ATT_IMPORTANCE).toString());
			double currentImportance = Double.parseDouble(currentGroup.get(0).getAttribute(ATT_IMPORTANCE).toString());

			ILineString currentExterior = lExteriorLineString.get(i);
			ILineString currentInterior = lInteriorLineString.get(i);
			ILineString nextExterior = lExteriorLineString.get(nextIndex);
			ILineString nextInterior = lInteriorLineString.get(nextIndex);

			// Determination of limit point on interior between two strips
			IDirectPosition pointToCast = currentInterior.coord().get(0);

			IPoint ptCas = new GM_Point(pointToCast);

			if (nextInterior.distance(ptCas) > 0.5) {
				pointToCast = currentInterior.coord().get(currentInterior.coord().size() - 1);
				ptCas = new GM_Point(pointToCast);
				if (nextInterior.distance(ptCas) > 0.5) {

					logger.error("This case is not supposed to happen, the test is not robust enough : "
							+ StraightSkeletonParcelDecomposition.class);
					logger.error(new GM_Point(pointToCast));
					logger.error(nextInterior);
				}

			}

			ILineString lineStringToSplit;
			// Line to split is determined according to the importance
			boolean splitCurrentLine = (nextImportance > currentImportance);

			if (splitCurrentLine) {
				// We split the current line
				lineStringToSplit = currentExterior;

			} else {
				// we split the next line
				lineStringToSplit = nextExterior;

			}

			// Line is splitting by projecting the limit points and each part is
			// re-affected to relevant group
			List<IDirectPosition> dpl = lineStringToSplit.coord().getList();

			int pointIndex = Operateurs.projectAndInsertWithPosition(pointToCast, dpl);

			DirectPositionList sidePoint = new DirectPositionList();
			sidePoint.add(pointToCast);
			sidePoint.add(dpl.get(pointIndex));
			lSideIni.add(new GM_LineString(sidePoint));

			if (pointIndex == -1) {
				logger.error("This case is not supposed to happen for point projection : "
						+ StraightSkeletonParcelDecomposition.class);
			}

			IDirectPositionList dpl1 = new DirectPositionList();
			IDirectPositionList dpl2 = new DirectPositionList();

			for (int j = 0; j <= pointIndex; j++) {
				dpl1.add(dpl.get(j));
			}
			for (int j = pointIndex; j < dpl.size(); j++) {
				dpl2.add(dpl.get(j));
			}

			ILineString ls1 = new GM_LineString(dpl1);
			ILineString ls2 = new GM_LineString(dpl2);

			// Splitted line too small
			if (ls1.length() < 0.5 || ls2.length() < 0.5) {
		
				continue;
			}

			List<ILineString> ls = new ArrayList<>();

			if (splitCurrentLine) {
				// We merge the next line with part of current line
				// Switching variable
				if (ls1.distance(nextExterior) > 0.01) {
					ILineString lsTemp = ls1;
					ls1 = ls2;
					ls2 = lsTemp;

				}

				ls.add(ls1);
				ls.add(nextExterior);
				//nextExterior = Operateurs.union(ls);
				lExteriorLineString.set(i, ls2);
				lExteriorLineString.set(nextIndex, nextExterior);

			} else {

				// We merge the next line with part of next line
				// Switching variable
				if (ls1.distance(currentExterior) > 0.01) {
					ILineString lsTemp = ls1;
					ls1 = ls2;
					ls2 = lsTemp;

				}
				ls.add(ls1);
				ls.add(currentExterior);
				//currentExterior = Operateurs.union(ls);

				lExteriorLineString.set(i, currentExterior);
				lExteriorLineString.set(nextIndex, ls2);

			}

		}

		outArcs.add(lInteriorLineString);
		outArcs.add(lExteriorLineString);
		outArcs.add(lSideIni);

		
		return lStrip;

	}

	/**
	 * Generate parcels from the list of strips
	 * 
	 * @param listOfLists
	 * @param minWidth
	 * @param maxWidth
	 * @param noiseParameter
	 * @param rng
	 * @return
	 */
	private static IFeatureCollection<IFeature> generateParcel(List<List<ILineString>> listOfLists, double minWidth,
			double maxWidth, double noiseParameter, RandomGenerator rng) {
		// Exterior and interior linstering for each groups
		List<ILineString> lInteriorLineString = listOfLists.get(0);
		List<ILineString> lExteriorLineString = listOfLists.get(1);
		List<ILineString> lSideLineString = listOfLists.get(2);
		
		int nbStrip = lInteriorLineString.size();

		// Random parameters
		GaussianRandomGenerator rawGenerator = new GaussianRandomGenerator(rng);
		double gaussianCenter = (minWidth + maxWidth) / 2;

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();

		int count = 0;
		
		//Nettoyage des strip
		for (int i = 0; i < nbStrip; i++) {
		ILineString currentExterior = lExteriorLineString.get(i);
			
			if(currentExterior == null ||currentExterior.length() < 0.01){
				lInteriorLineString.remove(i);
				lExteriorLineString.remove(i);
				i--;
				nbStrip--;
			}
		}

		// For each strip

		for (int i = 2; i < 3; i++) {
			ILineString currentExterior = lExteriorLineString.get(i);
			
			if(currentExterior == null ||currentExterior.length() < 0.01){
				lSideLineString.add(i,null);
				continue;
			}
			
			
			ILineString currentInterior = lInteriorLineString.get(i);
			ILineString previousLimitSide = lSideLineString.get(i);
			if(previousLimitSide==null){
				previousLimitSide = (i==0) ? lSideLineString.get(lSideLineString.size()-1):lSideLineString.get(i);
			}
			
			ILineString nextLimitSide =  (i==nbStrip-1) ? lSideLineString.get(0):lSideLineString.get(i+1);
			if(nextLimitSide == null)
			{
				nextLimitSide = lSideLineString.get(1);//Only case that is suppose to happens
			}
			
	
			List<ILineString> lsProjected = new ArrayList<>();
			
			if(currentInterior != null && !(currentInterior.length() < 0.01)){
				lsProjected.add(currentInterior);
			}
			
			
			if(nbStrip > 1){

				if(! previousLimitSide.intersects(currentExterior.buffer(0.05))){
					lsProjected.add(previousLimitSide);
				}
			
				
				if(! nextLimitSide.intersects(currentExterior.buffer(0.05))){
					lsProjected.add(nextLimitSide);
				}
			
					
			}
			
			currentInterior = Operateurs.union(lsProjected);
			
			
		
			
			
	
			
			// Strip polygon is deomposed part by part
			boolean endFlag = false;
			
			boolean firstLimit = true;
			
			while (!endFlag) {
				// Take a random abscissa
				double s = rawGenerator.nextNormalizedDouble() * noiseParameter * 3 + gaussianCenter;
				// Clamping limits
				s = Math.min(maxWidth, s);
				s = Math.max(s, minWidth);

				System.out.println(s);
				// This value is the par of exterior lines from which the parcel
				// is produced
				ILineString lsTokeep = null;
				// the value is longer than the length of remining line, a
				// parcel is produced with the rest of the line
				if (s > currentExterior.length()) {

					lsTokeep = currentExterior;
					endFlag = true;
				} else {
					// Line is splitting
					ILineString[] ls = splitLine(currentExterior, s);
					ILineString ls1 = ls[0];
					ILineString ls2 = ls[1];

					// Determining what part of the splitted lines is the
					// interesting cut
					// If the remaining length for the rest of the line is too
					// small
					// it is also merged
					if (Math.abs(s - ls1.length()) < 0.01) {

						if (ls2.length() < minWidth) {

							lsTokeep = currentExterior;
							endFlag = true;
						} else {
							lsTokeep = ls1;
							currentExterior = ls2;
						}

					} else {

						if (ls1.length() < minWidth) {

							lsTokeep = currentExterior;
							endFlag = true;
						} else {
							currentExterior = ls1;
							lsTokeep = ls2;
						}

					}
				}

				// Lateral lines are generated by projected extremities of
				// exterior parts of the block on interior line
				IDirectPosition dpExt1 = lsTokeep.coord().get(0);
				IDirectPosition dpExt2 = lsTokeep.coord().get(lsTokeep.coord().size() - 1);

				IDirectPosition dpCast1 = Operateurs.projection(dpExt1, currentInterior);
				IDirectPosition dpCast2 = Operateurs.projection(dpExt2, currentInterior);

				IDirectPositionList dplLat = new DirectPositionList();
				dplLat.add(dpCast1);
				dplLat.add(dpExt1);

				IDirectPositionList dplLat2 = new DirectPositionList();
				dplLat2.add(dpCast2);
				dplLat2.add(dpExt2);

				ILineString lsLat = (firstLimit) ? previousLimitSide : new GM_LineString(dplLat);
				ILineString lsLat2 = (endFlag) ? nextLimitSide: new GM_LineString(dplLat2);
				
				

				List<ILineString> lsOut = new ArrayList<>();
				lsOut.add(lsTokeep);
				lsOut.add(new GM_LineString(dplLat));

				// If lateral lines are interesecting the parcel is not composed
				// by a part of interior line
				if (!lsLat.intersects(lsLat2.buffer(0.05))) {

					// Determining part of interior line between lateral lines
					ILineString ls = cutBetweentLats(lsLat, lsLat2, currentInterior);

					lsOut.add(ls);

				} else {

					lsOut.add(null);

				}
				lsOut.add(new GM_LineString(dplLat2));
				IFeature featOut = new DefaultFeature(calculateSurface(lsOut));
				AttributeManager.addAttribute(featOut, "ID", count++, "Integer");

				featCollOut.add(featOut);

				firstLimit = false;
				
			}

		}

		return featCollOut;
	}
	
	
	 

	/**
	 * Helper to determine exterior linestring from a face list
	 * 
	 * @param currentGroup
	 * @return
	 */
	private static ILineString determineExteriorLineString(List<Face> currentGroup) {
		List<ILineString> lineStringToMerge = new ArrayList<>();
		List<Arc> encounterdEdges = new ArrayList<>();

		for (Face f : currentGroup) {
			for (Arc a : f.arcs()) {

				Object o = a.getAttribute(ATT_IS_INSIDE);
				if (o == null) {
					continue;
				}

				if (Integer.parseInt(o.toString()) == ARC_VALUE_OUTSIDE) {
					
					if(!encounterdEdges.contains(a)){
						lineStringToMerge.add(a.getGeometrie());
						encounterdEdges.add(a);
					}
					
				}

			}

		}

		ILineString union = Operateurs.union(lineStringToMerge);

		return union;
	}

	/**
	 * Helper to determine interior linestring from a face list
	 * 
	 * @param currentGroup
	 * @return
	 */
	private static ILineString determineInteriorLineString(List<Face> currentGroup) {
		List<ILineString> lineStringToMerge = new ArrayList<>();

		for (Face f : currentGroup) {
			for (Arc a : f.arcs()) {
				Object o = a.getAttribute(ATT_IS_INSIDE);
				if (o == null) {
					continue;
				}

				if (Integer.parseInt(o.toString()) == ARC_VALUE_INSIDE) {
					lineStringToMerge.add(a.getGeometrie());
				}

			}

		}

		ILineString union = Operateurs.union(lineStringToMerge);

		return union;
	}

	/**
	 * Cuts the part of a linestring (interiorCurve) between the intersection of
	 * two other linestring (lsLat1, lsLat2) WARNING : works only in this
	 * specific context
	 * 
	 * @param lsLat1
	 * @param lsLat2
	 * @param interiorCurve
	 * @return
	 */
	private static ILineString cutBetweentLats(ILineString lsLat1, ILineString lsLat2, ILineString interiorCurve) {

		IDirectPosition dp1 = lsLat1.coord().get(0);

		IDirectPosition dp2 = lsLat1.coord().get(lsLat1.coord().size() - 1);

		IDirectPosition idtemp = null;

		if (interiorCurve.intersects((new GM_Point(dp1)).buffer(0.05))) {

			idtemp = dp1;

		} else {
			idtemp = dp2;
		}

		IDirectPosition dp3 = lsLat2.coord().get(0);

		IDirectPosition dp4 = lsLat2.coord().get(lsLat2.coord().size() - 1);

		IDirectPosition idtemp2 = null;

		if (lsLat2.intersects((new GM_Point(dp3)).buffer(0.05))) {

			idtemp2 = dp3;

		} else {
			idtemp2 = dp4;
		}

		ILineString[] lsSpli = splitLine((ILineString) interiorCurve.clone(), idtemp);

		ILineString goodLineString = null;

		if (lsSpli.length == 1) {
			goodLineString = lsSpli[0];
		}

		if (lsSpli[0].length() < 0.05) {
			goodLineString = lsSpli[1];
		}

		if (goodLineString == null) {

			if (lsSpli[0].intersects((new GM_Point(idtemp2)).buffer(0.05))) {
				goodLineString = lsSpli[0];
			} else {
				goodLineString = lsSpli[1];
			}

		}

		lsSpli = splitLine(goodLineString, idtemp2);

		if (lsSpli[0].intersects((new GM_Point(idtemp)).buffer(0.05))) {
			if (lsSpli[1].intersects((new GM_Point(idtemp)).buffer(0.05))) {
				// This case corresponds to a strip that makes a loop
				// We consider to return the shortest
				if (lsSpli[0].length() > lsSpli[1].length()) {
					return lsSpli[1];
				}
				return lsSpli[0];
			}
			return lsSpli[0];
		}
		return lsSpli[1];

	}

	/**
	 * Simple code to determine surface from a list of line string WARNING :
	 * works only in this specific context
	 * 
	 * @param ls
	 * @return
	 */
	private static IOrientableSurface calculateSurface(List<ILineString> ls) {

		IDirectPositionList dpl = new DirectPositionList();

		dpl.addAll(ls.get(1).reverse().coord());
		if (ls.get(2) != null) {
			dpl.addAll(ls.get(2).reverse().coord());
		}

		dpl.addAll(ls.get(3).coord());
		dpl.addAll(ls.get(0).reverse().coord());

		return new GM_Polygon(new GM_LineString(dpl));
	}

	private static ILineString[] splitLine(ILineString line, double s) {

		IDirectPosition dpInter = Operateurs.pointEnAbscisseCurviligne(line, s);

		return splitLine(line, dpInter);
	}

	private static ILineString[] splitLine(ILineString line, IDirectPosition dpInter) {

		List<IDirectPosition> dplTemp = line.coord().getList();
		int pointIndex = Operateurs.projectAndInsertWithPosition(dpInter, dplTemp);

		IDirectPositionList dpl1 = new DirectPositionList();

		for (int j = 0; j <= pointIndex; j++) {
			dpl1.add(dplTemp.get(j));
		}

		ILineString ls1 = new GM_LineString(dpl1);

		IDirectPositionList dpl2 = new DirectPositionList();
		for (int j = pointIndex; j < dplTemp.size(); j++) {
			dpl2.add(dplTemp.get(j));
		}

		ILineString ls2 = new GM_LineString(dpl2);

		ILineString tab[] = new ILineString[2];
		tab[0] = ls1;
		tab[1] = ls2;

		return tab;

	}

	////////////////////////
	////// DEBUG METHODS
	///////////////////////

	private static void debugExport(IFeatureCollection<? extends IFeature> feats, String name) {

		if (feats != null) {
			ShapefileWriter.write(feats, FOLDER_OUT_DEBUG + name);
		}
	}

	private static void debugExport(List<? extends IFeature> feats, String name) {

		IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<>();
		featCollOut.addAll(feats);
		ShapefileWriter.write(featCollOut, FOLDER_OUT_DEBUG + name);
	}
}
