package fr.ign.cogit.geoxygene.osm.importexport.metrics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.geometrie.IndicesForme;
import fr.ign.cogit.geoxygene.osm.importexport.OSMObject;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.importexport.OSMWay;
import fr.ign.cogit.geoxygene.osm.importexport.OsmRelationMember;
import fr.ign.cogit.geoxygene.osm.importexport.postgis.LoadFromPostGIS;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeometryConversion;
import fr.ign.cogit.geoxygene.osm.schema.OsmSource;
import fr.ign.cogit.geoxygene.util.algo.CommonAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

public class BuildingAssessment {
    LoadFromPostGIS loader;
    public Map<Long, OSMResource> buildings;
    public Map<Long, OSMResource> invisibleBuildings;
    Double[] borders;
    String timestamp;

    // Delimiter used in CSV file
    private static final String COMMA_DELIMITER = ",";
    private static final String NEW_LINE_SEPARATOR = "\n";

    public BuildingAssessment(String host, String port, String dbName,
            String dbUser, String dbPwd) {
        this.loader = new LoadFromPostGIS(host, port, dbName, dbUser, dbPwd);
        this.buildings = new HashMap<Long, OSMResource>();
        this.invisibleBuildings = new HashMap<Long, OSMResource>();
    }

    public void loadBuildings(String city, String timestamp) throws Exception {
        this.borders = loader.getCityBoundary(city, timestamp);
        this.timestamp = timestamp;
        this.buildings = loader.getSnapshotBuilding(borders, timestamp);

    }

    public void loadInvisibleBuildings(String city, String timestamp)
            throws Exception {
        this.borders = loader.getCityBoundary(city, timestamp);
        this.timestamp = timestamp;
        Set<OSMResource> invisible = loader.getInvisibleBuilding(borders,
                timestamp);
        for (OSMResource r : invisible)
            invisibleBuildings.put(r.getId(), r);

    }

    public Map<Long, IFeature> buildGeometry(String epsg) throws Exception {
        Map<Long, IFeature> buildingGeom = new HashMap<Long, IFeature>();

        OsmGeometryConversion converter = new OsmGeometryConversion(epsg);
        for (OSMResource b : buildings.values()) {
            if (!b.isVisible()) {
                OSMDefaultFeature feature = new OSMDefaultFeature(
                        b.getContributeur(), null, b.getId(), b.getChangeSet(),
                        b.getVersion(), b.getUid(), b.getDate(), b.getTags());
                feature.setSource(OsmSource.valueOfTag(b.getSource()));
                feature.setCaptureTool(b.getCaptureTool());
                buildingGeom.put(b.getId(), feature);
                continue;
            }

            List<OSMResource> nodes = new ArrayList<OSMResource>();
            if (b.getGeom().getClass().getSimpleName()
                    .equalsIgnoreCase("OSMWay")) {
                // Récupérer la liste des nodes qui composent le way
                if (((OSMWay) b.getGeom()).isPolygon()) {
                    System.out.println("OSMResource ID : " + b.getId());
                    List<Long> vertices = ((OSMWay) b.getGeom()).getVertices();
                    System.out
                            .println("Number of vertices :" + vertices.size());
                    nodes = loader.getNodes(vertices, timestamp);
                    if (nodes == null)
                        continue;
                    System.out.println("Number of nodes :" + nodes.size());
                    for (OSMResource n : nodes)
                        System.out.println(n.getId());
                    System.out.println(((OSMWay) b.getGeom()).isPolygon());
                    // if
                    // (OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay)
                    // b.getGeom(), nodes).dimension() < 2)

                    // throw new NumberPointsException(
                    // "Way #" + b.getId() + " est composé de " + nodes.size() +
                    // "nodes");

                    // OSMDefaultFeature feature = new
                    // OSMDefaultFeature(b.getContributeur(),
                    // OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay)
                    // b.getGeom(), nodes), b.getId(),
                    // b.getChangeSet(), b.getVersion(), b.getUid(),
                    // b.getDate(), b.getTags());

                    OSMDefaultFeature feature = new OSMDefaultFeature(
                            b.getContributeur(),
                            converter.convertOSMPolygon((OSMWay) b.getGeom(),
                                    nodes, true),
                            b.getId(), b.getChangeSet(), b.getVersion(),
                            b.getUid(), b.getDate(), b.getTags());
                    feature.setSource(OsmSource.valueOfTag(b.getSource()));
                    feature.setCaptureTool(b.getCaptureTool());
                    buildingGeom.put(b.getId(), feature);
                    nodes.clear();
                }

            }
            if (b.getGeom().getClass().getSimpleName()
                    .equalsIgnoreCase("OSMRelation")) {
                // Récupérer les membres de la relation
                OSMRelation primitive = (OSMRelation) b.getGeom();
                OsmRelationMember outer = primitive.getOuterMembers().get(0);
                OSMResource outerWay = loader.getWay(outer.getRef(), timestamp);

                nodes = loader.getNodes(
                        ((OSMWay) outerWay.getGeom()).getVertices(), timestamp);
                if (nodes == null)
                    continue;
                IPolygon polygon = null;
                if (epsg.equals("2154"))
                    polygon = OsmGeometryConversion
                            .convertOSMPolygonToLambert93(
                                    (OSMWay) outerWay.getGeom(), nodes);
                else
                    polygon = converter.convertOSMPolygon(
                            (OSMWay) outerWay.getGeom(), nodes, true);
                // if (polygon.dimension() < 2)
                // throw new NumberPointsException("Way #" + b.getId() + " est
                // composé de " + nodes.size() + "nodes");

                List<OsmRelationMember> inners = primitive.getInnerMembers();
                // add inner rings to polygon
                for (OsmRelationMember inner : inners) {
                    OSMResource resource = loader.getWay(inner.getRef(),
                            timestamp);
                    nodes.clear();
                    if (resource == null)
                        continue;
                    nodes = loader.getNodes(
                            ((OSMWay) resource.getGeom()).getVertices(),
                            timestamp);
                    if (nodes == null)
                        continue;
                    IRing ring = null;
                    if (epsg.equals("2154"))
                        ring = OsmGeometryConversion
                                .convertOSMPolygonToLambert93(
                                        (OSMWay) resource.getGeom(), nodes)
                                .getExterior();
                    else
                        ring = converter
                                .convertOSMPolygon((OSMWay) resource.getGeom(),
                                        nodes, true)
                                .getExterior();
                    if (ring.coord().size() < 4)
                        continue;
                    polygon.addInterior(ring);
                }
                OSMDefaultFeature feature = new OSMDefaultFeature(
                        b.getContributeur(), polygon, b.getId(),
                        b.getChangeSet(), b.getVersion(), b.getUid(),
                        b.getDate(), b.getTags());
                feature.setSource(OsmSource.valueOfTag(b.getSource()));
                feature.setCaptureTool(b.getCaptureTool());

                buildingGeom.put(b.getId(), feature);
                nodes.clear();
            }
        }
        return buildingGeom;
    }

    public OSMObject getHistorySinceDate(OSMResource r, String timestamp)
            throws Exception {
        OSMObject history = new OSMObject(r.getId());
        if (r.getVersion() == 1) {
            history.addcontribution(r);
            return history;
        }
        if (Date.from(Instant.parse(timestamp)).after(r.getDate())) {
            history.addcontribution(r);
            return history;
        }

        if (r.getGeom().getClass().getSimpleName().equals("OSMWay"))
            history = getWayHistory(r, timestamp);
        if (r.getGeom().getClass().getSimpleName().equals("OSMRelation"))
            history = getRelationHistory(r, timestamp);
        history.addcontribution(r);
        return history;

    }

    public OSMObject getWayHistory(OSMResource r, String timestamp)
            throws Exception {
        OSMObject history = new OSMObject(r.getId());

        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);

            String query = "SELECT way.id,way.uid,way.vway, way.changeset, way.username, way.datemodif, hstore_to_json(way.tags), way.visible,way.composedof "
                    + "FROM way WHERE id = " + r.getId() + " AND vway <"
                    + r.getVersion() + " AND datemodif >= '"
                    + Date.from(Instant.parse(timestamp))
                    + "' ORDER BY vway ASC";
            System.out.println(query);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result = s.executeQuery(query);
            while (result.next()) {
                OSMResource way = loader.writeWay(result);
                history.addcontribution(way);
            }
            s.close();
            conn.close();
            return history;
        } catch (Exception e) {
            throw e;
        }

    }

    public OSMObject getRelationHistory(OSMResource r, String timestamp)
            throws Exception {
        OSMObject history = new OSMObject(r.getId());

        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            String query = "SELECT min(vrel) AS min FROM relation WHERE id = "
                    + r.getId() + " AND vrel <" + r.getVersion()
                    + " AND datemodif >= '"
                    + Date.from(Instant.parse(timestamp)) + "'";

            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet result = s.executeQuery(query);
            int min = 0;
            while (result.next())
                min = result.getInt("min");
            if (min > 0)
                // Query OSMRelationMember first
                for (int i = min; i < r.getVersion(); i++) {
                    String idrel = String.valueOf(r.getId())
                            + String.valueOf(i);
                    query = "SELECT * FROM relationmember WHERE idrel=" + idrel;
                    Statement state = conn.createStatement(
                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                    ResultSet resultMb = state.executeQuery(query);
                    List<OsmRelationMember> members = new ArrayList<OsmRelationMember>();
                    while (resultMb.next()) {
                        members.add(loader.writeRelationMember(resultMb));
                    }
                    state.close();
                    if (members.size() > 0) {
                        query = "SELECT r.id,r.uid,r.vrel, r.changeset, r.username, r.datemodif, hstore_to_json(r.tags), r.visible "
                                + "FROM relation r WHERE idrel = " + idrel;
                        result = s.executeQuery(query);
                        while (result.next()) {
                            OSMResource rel = loader.writeRelation(result,
                                    members);
                            history.addcontribution(rel);
                        }
                    }

                }
            s.close();
            conn.close();
            return history;
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * 
     * @param feat
     * @return the following metadata : an "anonymised" user ID, number of tags,
     *         the building-tag value, the source-tag value
     */
    public Object[] getMetada(IFeature feat) {
        int nbTags = this.buildings.get(Long.valueOf(feat.getId())).getTags()
                .size();
        String buildingValue = this.buildings.get(Long.valueOf(feat.getId()))
                .getTags().get("building");
        boolean isYesBuilding = buildingValue.equalsIgnoreCase("yes");
        String source = this.buildings.get(Long.valueOf(feat.getId()))
                .getSource();
        boolean isFromCadaster = (source.contains("cadas")
                || source.contains("Cadas"));
        int uid = this.buildings.get(Long.valueOf(feat.getId())).getUid() + 111; // Anonymisé
        Object[] metadata = { uid, nbTags, isYesBuilding, isFromCadaster };
        return metadata;

    }

    /**
     * 
     * @param feat
     * @return the following metrics : perimeter, area, shortest edge length,
     *         median edge length, elongation value, convexity, compacity,
     *         smallest surrounding rectangle area
     */
    public Object[] getGeomMetrics(IFeature feat) {
        Double perimeter = feat.getGeom().length();
        Double area = feat.getGeom().area();

        Double shortestEdge = CommonAlgorithmsFromCartAGen
                .getShortestEdgeLength(feat.getGeom());
        Double medianEdge = CommonAlgorithmsFromCartAGen
                .getEdgeLengthMedian(feat.getGeom());

        Double elongation = CommonAlgorithms.elongation(feat.getGeom());
        Double convexity = CommonAlgorithms.convexity(feat.getGeom());
        Double compacite = IndicesForme
                .indiceCompacite(((IPolygon) feat.getGeom()));
        Double areaSSR = SmallestSurroundingRectangleComputation
                .getSSR(feat.getGeom()).area();
        Object[] metrics = { perimeter, area, shortestEdge, medianEdge,
                elongation, convexity, compacite, areaSSR };
        return metrics;
    }

    /**
     * 
     * @param features
     * @param f
     * @return a table of metric containing : the number of intersected objects,
     *         the ratio intersected area/area, the number of touched objects,
     *         the ratio length touched/length
     */
    public Object[] getTopologyMetric(IFeature f,
            Collection<IFeature> features) {
        int nbIntersects = 0;
        int nbTouches = 0;
        Double ratioIntersect = 0.0;
        Double ratioTouchLength = 0.0;
        System.out.println("Feature ID : " + Long.valueOf(f.getId()));
        for (IFeature bati : features) {
            if (bati.getId() == f.getId())
                continue;
            if (f.getGeom().intersectsStrictement(bati.getGeom())) {
                nbIntersects++;
                ratioIntersect += f.getGeom().difference(bati.getGeom()).area()
                        / f.getGeom().area();
            }
            if (f.getGeom().touches(bati.getGeom())) {
                nbTouches++;
                ratioTouchLength += f.getGeom().intersection(bati.getGeom())
                        .length() / f.getGeom().length();
            }
        }
        Object[] metrics = { nbIntersects, ratioIntersect, nbTouches,
                ratioTouchLength };
        return metrics;

    }

    /**
     * Retrieves the ways of the loaders that are tagged with the following key
     * tags : amenity, leisure, landuse, natural
     * 
     * @return
     * @throws Exception
     */
    public Map<Long, IFeature> getLULC(String epsg) throws Exception {
        String[] lulcKeyTags = { "natural" };
        Set<OSMResource> lulc = this.loader.filterByTags(lulcKeyTags);
        Map<Long, IFeature> lulcGeom = new HashMap<Long, IFeature>();
        for (OSMResource b : lulc) {
            // On retire les batiments qui pourraient se trouver dans la
            // sélection
            if (b.getTags().containsKey("building")
                    || b.getTags().containsKey("building:part")
                    || b.getTags().containsKey("indoor"))
                continue;
            if (((OSMWay) b.getGeom()).isPolygon()) {
                System.out.println("OSMResource ID : " + b.getId());
                List<Long> vertices = ((OSMWay) b.getGeom()).getVertices();
                System.out.println("Number of vertices :" + vertices.size());
                List<OSMResource> nodes = loader.getNodes(vertices, timestamp);
                if (nodes == null)
                    continue;
                System.out.println("Number of nodes :" + nodes.size());
                for (OSMResource n : nodes)
                    System.out.println(n.getId());
                System.out.println(((OSMWay) b.getGeom()).isPolygon());

                OsmGeometryConversion converter = new OsmGeometryConversion(
                        epsg);

                // OSMDefaultFeature feature = new
                // OSMDefaultFeature(b.getContributeur(),
                // OsmGeometryConversion.convertOSMPolygonToLambert93((OSMWay)
                // b.getGeom(), nodes), b.getId(),
                // b.getChangeSet(), b.getVersion(), b.getUid(),
                // b.getDate(), b.getTags());

                OSMDefaultFeature feature = new OSMDefaultFeature(
                        b.getContributeur(),
                        converter.convertOSMPolygon((OSMWay) b.getGeom(), nodes,
                                true),
                        b.getId(), b.getChangeSet(), b.getVersion(), b.getUid(),
                        b.getDate(), b.getTags());

                feature.setSource(OsmSource.valueOfTag(b.getSource()));
                feature.setCaptureTool(b.getCaptureTool());
                lulcGeom.put(b.getId(), feature);
                nodes.clear();
            }
        }

        return lulcGeom;
    }

    public Map<Long, IFeature> getRoads(String epsg) throws Exception {
        String[] keytag = { "highway" };
        Set<OSMResource> roads = this.loader.filterByTags(keytag);
        Map<Long, IFeature> roadsGeom = new HashMap<Long, IFeature>();
        for (OSMResource r : roads) {
            System.out.println("OSMResource ID : " + r.getId());
            // On ne prend pas les routes qui portent le tag "highway"="service"
            // ou "highway"="footway"
            if (r.getTags().get("highway").equals("footway")
                    || r.getTags().get("highway").equals("service"))
                continue;
            List<Long> vertices = ((OSMWay) r.getGeom()).getVertices();
            System.out.println("Number of vertices :" + vertices.size());
            List<OSMResource> nodes = loader.getNodes(vertices, timestamp);
            if (nodes == null)
                continue;
            System.out.println("Number of nodes :" + nodes.size());
            for (OSMResource n : nodes)
                System.out.println(n.getId());

            OsmGeometryConversion converter = new OsmGeometryConversion(epsg);
            OSMDefaultFeature feature = new OSMDefaultFeature(
                    r.getContributeur(),
                    converter.convertOSMLine((OSMWay) r.getGeom(), nodes, true),
                    r.getId(), r.getChangeSet(), r.getVersion(), r.getUid(),
                    r.getDate(), r.getTags());

            feature.setSource(OsmSource.valueOfTag(r.getSource()));
            feature.setCaptureTool(r.getCaptureTool());
            roadsGeom.put(r.getId(), feature);
            nodes.clear();

        }
        return roadsGeom;

    }

    public static void toCSV(String FILE_HEADER,
            Map<Long, Object[]> indicatorList, File file) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.append(FILE_HEADER.toString());
        fileWriter.append(NEW_LINE_SEPARATOR);
        try {
            for (Long id : indicatorList.keySet()) {
                fileWriter.append(String.valueOf(id));
                for (int i = 0; i < indicatorList.get(id).length; i++) {
                    fileWriter.append(COMMA_DELIMITER);
                    fileWriter.append(String.valueOf(indicatorList.get(id)[i]));
                }
                fileWriter.append(NEW_LINE_SEPARATOR);
            }
        } catch (Exception e) {
            System.out.println("Erreur d'écriture");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out
                        .println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

    public void addID2Postgres(String city, Set<Long> IDs) throws Exception {

        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            StringBuffer query = new StringBuffer();
            query.append("INSERT INTO indicators." + city + "(id) VALUES ");
            Iterator<Long> it = IDs.iterator();
            while (it.hasNext())
                query.append("(" + it.next() + "),");
            query.replace(query.length() - 1, query.length(),
                    " ON CONFLICT DO NOTHING;");
            System.out.println(query);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            s.execute(query.toString());

            s.close();
            conn.close();

        } catch (Exception e) {
            throw e;
        }

    }

    public void updatePostgresIndicators(String query) throws Exception {

        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            s.execute(query);

            s.close();
            conn.close();

        } catch (Exception e) {
            throw e;
        }
    }

    public double minSurfaceDistanceFromMatching(IFeature geom)
            throws Exception {
        Double dist = 1.0; // Max distance by default
        if (nbIntersectionWithBatiBDTOPO(geom) == 0)
            return dist;
        // Make a WKT polygon
        String wkt = WktGeOxygene.makeWkt(geom.getGeom());
        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT min(1-(ST_AREA(ST_INTERSECTION(bati.geometrie, ST_GeomFromText('"
                    + wkt + "',2154)))" + "/"
                    + "ST_AREA(ST_UNION(bati.geometrie, ST_GeomFromText('" + wkt
                    + "',2154)))))" + " AS min "
                    + "FROM ign.batiment93_non_detruit bati WHERE "
                    + "ST_INTERSECTS(bati.geometrie, ST_GeomFromText('" + wkt
                    + "',2154))";
            ResultSet r = s.executeQuery(query);
            while (r.next())
                dist = r.getDouble("min");

            s.close();
            conn.close();
            return dist;
        } catch (Exception e) {
            throw e;
        }
    }

    public Integer nbIntersectionWithBatiBDTOPO(IFeature geom)
            throws Exception {
        Integer nb = 0;

        // Make a WKT polygon
        String wkt = WktGeOxygene.makeWkt(geom.getGeom());

        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT COUNT(*) AS count FROM ign.batiment93_non_detruit bati "
                    + "WHERE ST_INTERSECTS(bati.geometrie,"
                    + "ST_GeomFromText('" + wkt + "',2154));";
            ResultSet r = s.executeQuery(query);
            while (r.next())
                nb = r.getInt("count");

            s.close();
            conn.close();
            return nb;
        } catch (Exception e) {
            throw e;
        }
    }

    public int countFromDB(String query) throws Exception {
        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            ResultSet r = s.executeQuery(query);
            r.next();
            int result = r.getInt(1);
            s.close();
            conn.close();
            return result;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * 
     * @param uid
     * @return true if the contributor's infos are already stored in database,
     *         false if not
     * @throws SQLException
     * @throws Exception
     */
    public boolean contributorInfosExist(Integer uid)
            throws SQLException, Exception {

        java.sql.Connection conn;
        try {
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT n_total_contrib IS NOT NULL FROM contributor WHERE uid = "
                    + uid;
            System.out.println(query);
            ResultSet r = s.executeQuery(query);
            r.next();
            boolean exist = r.getBoolean(1);
            s.close();
            conn.close();
            return exist;
        } catch (Exception e) {
            throw e;
        }

    }

    public void updateContributorInfos(Integer uid)
            throws SQLException, Exception {
        // Number of creations
        String query = "SELECT COUNT(*) AS created_nodes FROM node WHERE uid = "
                + uid + " AND vnode = 1;";
        int nbCreatedNodes = this.countFromDB(query);
        query = "SELECT COUNT(*) AS created_way FROM way WHERE uid = " + uid
                + " AND vway = 1;";
        int nbCreatedWay = this.countFromDB(query);
        query = "SELECT COUNT(*) AS created_rel FROM relation WHERE uid = "
                + uid + " AND vrel = 1;";
        int nbCreatedRel = this.countFromDB(query);
        int totalCreated = nbCreatedNodes + nbCreatedWay + nbCreatedRel;
        System.out.println("Nombre de nodes créés : " + nbCreatedNodes);
        System.out.println("Nombre de ways créés : " + nbCreatedWay);
        System.out.println("Nombre de relations créés : " + nbCreatedRel);
        System.out.println("Total créés : " + totalCreated);

        // Number of deletes
        query = "SELECT COUNT(*) AS deleted_nodes FROM node WHERE uid = " + uid
                + " AND visible IS FALSE;";
        int nbDeletedNodes = this.countFromDB(query);
        query = "SELECT COUNT(*) AS deleted_way FROM way WHERE uid = " + uid
                + " AND visible IS FALSE;";
        int nbDeletedWay = this.countFromDB(query);
        query = "SELECT COUNT(*) AS deleted_rel FROM relation WHERE uid = "
                + uid + " AND  visible IS FALSE;";
        int nbDeletedRel = this.countFromDB(query);
        int totalDeleted = nbDeletedNodes + nbDeletedWay + nbDeletedRel;
        System.out.println("\nNombre de nodes del : " + nbDeletedNodes);
        System.out.println("Nombre de ways del : " + nbDeletedWay);
        System.out.println("Nombre de relations del : " + nbDeletedRel);
        System.out.println("Total del : " + totalDeleted);

        // Number of modifications
        query = "SELECT COUNT(*) AS modif_nodes FROM node WHERE uid = " + uid
                + " AND vnode >1 AND visible IS TRUE;";
        int nbModifNodes = this.countFromDB(query);
        query = "SELECT COUNT(*) AS modif_way FROM way WHERE uid = " + uid
                + " AND vway >1 AND visible IS TRUE;";
        int nbModifWay = this.countFromDB(query);
        query = "SELECT COUNT(*) AS modif_rel FROM relation WHERE uid = " + uid
                + " AND vrel >1 AND visible IS TRUE;";
        int nbModifRel = this.countFromDB(query);
        int totalModif = nbModifNodes + nbModifWay + nbModifRel;
        System.out.println("\nNombre de nodes modif : " + nbModifNodes);
        System.out.println("Nombre de ways modif : " + nbModifWay);
        System.out.println("Nombre de relations modif : " + nbModifRel);
        System.out.println("Total modif : " + totalModif);

        int totalContrib = totalCreated + totalDeleted + totalModif;
        System.out.println("\nTotal Contributions : " + totalContrib);

        // Number of night contributions
        query = "SELECT COUNT(*) FROM node WHERE uid = " + uid
                + " AND (date_part('hour', datemodif) < 6 OR date_part('hour', datemodif) > 18);";
        int nbNightNodes = this.countFromDB(query);
        query = "SELECT COUNT(*) FROM way WHERE uid = " + uid
                + " AND (date_part('hour', datemodif) < 6 OR date_part('hour', datemodif) > 18);";
        int nbNightWay = this.countFromDB(query);
        query = "SELECT COUNT(*) FROM relation WHERE uid = " + uid
                + " AND (date_part('hour', datemodif) < 6 OR date_part('hour', datemodif) > 18);";
        int nbNightRel = this.countFromDB(query);
        int totalNightContrib = nbNightNodes + nbNightWay + nbNightRel;
        System.out.println(
                "\nNombre contributions de nuit : " + totalNightContrib);

        // Number of weekend contributions
        query = "SELECT COUNT(*) FROM node WHERE uid = " + uid
                + " AND (extract(dow from datemodif) = 5 OR extract(dow from datemodif) = 6);";
        int nbWeekendNodes = this.countFromDB(query);
        query = "SELECT COUNT(*) FROM way WHERE uid = " + uid
                + " AND (extract(dow from datemodif) = 5 OR extract(dow from datemodif) = 6)";
        int nbWeekendWay = this.countFromDB(query);
        query = "SELECT COUNT(*) FROM relation WHERE uid = " + uid
                + " AND (extract(dow from datemodif) = 5 OR extract(dow from datemodif) = 6)";
        int nbWeekendRel = this.countFromDB(query);
        int totalWeekendContrib = nbWeekendNodes + nbWeekendWay + nbWeekendRel;
        System.out.println(
                "\nNombre contributions en weekend : " + totalWeekendContrib);

        // Number of changesets
        query = "SELECt COUNT(*) FROM changeset WHERE uid=" + uid;
        int nbChangeset = this.countFromDB(query);
        System.out.println("Nb of changesets : " + nbChangeset);

        // Update Postgres contributor table
        String update = "UPDATE contributor SET ";
        update += "n_total_contrib = " + totalContrib + ",";

        update += " n_created_node =" + nbCreatedNodes + ",";
        update += " n_created_way =" + nbCreatedWay + ",";
        update += " n_created_relation = " + nbCreatedRel + ",";

        update += "n_deleted_node=" + nbDeletedNodes + ",";
        update += " n_deleted_way=" + nbDeletedWay + ",";
        update += " n_deleted_relation=" + nbDeletedRel + ",";

        update += " n_modified_node =" + nbModifNodes + ",";
        update += " n_modified_way =" + nbModifWay + ",";
        update += " n_modified_relation =" + nbModifRel + ",";

        update += " n_weekend_contrib =" + totalWeekendContrib + ",";
        int totalWeekContrib = totalContrib - totalWeekendContrib;
        update += " n_week_contrib =" + totalWeekContrib + ",";

        update += " n_night_contrib =" + totalNightContrib + ",";
        int totalDayContrib = totalContrib - totalNightContrib;
        update += " n_day_contrib =" + totalDayContrib + ",";

        update += " n_changeset =" + nbChangeset;

        update += " WHERE uid = " + uid;
        this.updatePostgresIndicators(update);

    }

    public Set<double[]> getIndicatorValues(String city) throws Exception {
        java.sql.Connection conn;
        try {
            Set<double[]> rows = new HashSet<double[]>();
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT id , v_contrib, uid, n_tags, perimeter, area, shortest_length ,median_length,elongation,convexity,compacity,area_mbr,n_intersects,"
                    + " r_intersects,  n_touches,  r_touches,  n_contains,  r_contains,  n_is_within,  n_intersects_lulc,  r_intersects_lulc,  n_touches_lulc,  r_touches_lulc,"
                    + "n_contains_lulc,  r_contains_lulc,  n_is_within_lulc,  n_users,  n_del,  n_geom_edit,  n_tag_edit,  n_tag_add,  n_tag_del, n_versions_last_4_years,"
                    + "n_last_user_created,  n_last_user_modif,  n_last_user_del,  n_last_user_changesets,  r_last_user_tot_ways_created,  r_last_user_tot_ways_modif,  r_last_user_tot_ways_del,"
                    + " r_last_user_changesets FROM indicators." + city
                    + " WHERE v_contrib IS NOT NULL";
            // System.out.println(query);
            ResultSet r = s.executeQuery(query);
            ResultSetMetaData rsmd = r.getMetaData();

            while (r.next()) {
                double[] values = new double[rsmd.getColumnCount()];
                for (int i = 1; i < values.length; i++) {
                    values[i - 1] = r.getDouble(i);
                }
                rows.add(values);
            }

            s.close();
            conn.close();
            return rows;

        } catch (Exception e) {
            throw e;
        }

    }

    public Set<double[]> getNormalizedIndicators(String city) throws Exception {
        java.sql.Connection conn;
        try {
            Set<double[]> rows = new HashSet<double[]>();
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT id, ";
            query += "v_contrib_out_of_max, ";
            query += " n_tags_out_of_max, ";
            query += "perimeter_out_of_max, ";
            query += "area_out_of_max, ";
            query += "shortest_length_out_of_perimeter ,";
            query += "median_length_out_of_perimeter,";
            query += "elongation,";
            query += "convexity,";
            query += "compacity,";
            query += "area_mbr_out_of_max,";
            query += "r_intersects,";
            query += "r_touches,";
            query += "r_contains,";
            query += "r_intersects_lulc,";
            query += " r_touches_lulc,";
            query += " r_contains_lulc, ";
            query += " n_users_out_of_max,  ";
            query += "n_geom_edit_out_of_max,  ";
            query += "n_tag_edit_out_of_max,  ";
            query += "n_tag_add_out_of_max,  ";
            query += "n_tag_del_out_of_max, ";
            query += "n_versions_last_4_years_out_of_current_version,";
            query += "n_users_out_of_max, ";
            query += "r_last_user_tot_ways_created, ";
            query += " r_last_user_tot_ways_modif,  ";
            query += "r_last_user_tot_ways_del,";
            query += " r_last_user_changesets, ";
            query += "r_week_contrib, ";
            query += "r_day_contrib ";
            query += "FROM indicators." + city
                    + "_normalized WHERE v_contrib IS NOT NULL";
            // System.out.println(query);
            ResultSet r = s.executeQuery(query);
            ResultSetMetaData rsmd = r.getMetaData();

            while (r.next()) {
                double[] values = new double[rsmd.getColumnCount()];
                for (int i = 1; i < values.length; i++) {
                    values[i - 1] = r.getDouble(i);
                }
                rows.add(values);
            }

            s.close();
            conn.close();
            return rows;

        } catch (Exception e) {
            throw e;
        }
    }

    public Set<double[]> getNormalizedIndicators(String columns, String city)
            throws Exception {
        java.sql.Connection conn;
        try {
            Set<double[]> rows = new HashSet<double[]>();
            String url = "jdbc:postgresql://" + loader.host + ":" + loader.port
                    + "/" + loader.dbName;
            conn = DriverManager.getConnection(url, loader.dbUser,
                    loader.dbPwd);
            Statement s = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT " + columns;
            query += " FROM indicators." + city
                    + "_all_with_vandalized_data WHERE v_contrib IS NOT NULL";
            // query += " FROM indicators." + city
            // + "normalized_with_vandalism WHERE v_contrib
            // IS NOT NULL";
            System.out.println(query);
            ResultSet r = s.executeQuery(query);
            ResultSetMetaData rsmd = r.getMetaData();

            while (r.next()) {
                double[] values = new double[rsmd.getColumnCount()];
                // System.out.println("rsmd.getColumnCount() = " +
                // rsmd.getColumnCount());
                for (int i = 1; i <= values.length; i++) {
                    values[i - 1] = r.getDouble(i);
                }
                rows.add(values);
                // System.out.println("perimètre out of max " + values[0]);
                // System.out.println("area out of max " + values[1]);

            }

            s.close();
            conn.close();
            return rows;

        } catch (Exception e) {
            throw e;
        }

    }
}
