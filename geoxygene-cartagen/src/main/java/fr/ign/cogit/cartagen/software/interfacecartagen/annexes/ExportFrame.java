/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.annexes;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.shp.ShapeType;
import org.geotools.data.shapefile.shp.ShapefileWriter;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;

import fr.ign.cogit.cartagen.core.genericschema.hydro.IWaterArea;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author julien Gaffuri
 * 
 */

public class ExportFrame extends JFrame {

  private String systemPath;
  private static final long serialVersionUID = 1L;
  static Logger logger = Logger.getLogger(ExportFrame.class.getName());

  public static ExportFrame get() {
    return CartagenApplication.getInstance().getFrameExport();
  }

  public ExportFrame() {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setResizable(false);
    this.setSize(new Dimension(400, 300));
    this.setLocation(100, 100);
    this.setTitle(CartagenApplication.getInstance().getFrame().getTitle()
        + " - export généralisation");
    this.setIconImage(CartagenApplication.getInstance().getFrame().getIcon());
    this.setVisible(false);

    this.setLayout(new GridBagLayout());

    GridBagConstraints c;
    c = new GridBagConstraints();
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.insets = new Insets(5, 5, 5, 5);

    // c.gridx=0;

    this.add(this.getLSuivi(), c);
    this.add(this.getBExportTout(), c);

    this.pack();

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        ExportFrame.this.setVisible(false);
      }

      @Override
      public void windowActivated(WindowEvent e) {
      }
    });

  }

  /**
	 */
  private JButton bExportTout;

  private JButton getBExportTout() {
    if (this.bExportTout == null) {
      this.bExportTout = new JButton("Exporter tout");
      this.bExportTout.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent arg0) {
          Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

              ExportFrame.logger.info("Début export (dans le répertoire "
                  + CartagenApplication.getInstance().getCheminDonnees()
                  + "/sortie)");

              ExportFrame.this.chooseDirectory();

              ExportFrame.this.getLSuivi().setText("Export en cours...");

              // cree le repertoire de sortie
              new File(CartagenApplication.getInstance().getCheminDonnees()
                  + "/sortie").mkdir();

              Geometry[] geoms;
              int i, nb;
              GeometryCollection col;
              FileOutputStream shp, shx;

              ExportFrame.logger.info("Export des villes");
              /*
               * try { // compte les non supprimes nb = 0; for (ITown a :
               * CartagenApplication.getInstance().getDataSet() .getVilles()) {
               * if (!a.isDeleted()) { nb++; } }
               * 
               * // collection des geometries geoms = new Geometry[nb]; i = 0;
               * for (ITown a : CartagenApplication.getInstance().getDataSet()
               * .getVilles()) { if (!a.isDeleted()) { geoms[i++] =
               * AdapterFactory.toGeometry( new GeometryFactory(), a.getGeom());
               * } } col = new
               * GeometryFactory().createGeometryCollection(geoms);
               * 
               * // ecriture des fichiers shp = new
               * FileOutputStream(CartagenApplication.getInstance()
               * .getCheminDonnees() + "sys/ville.shp"); shx = new
               * FileOutputStream(CartagenApplication.getInstance()
               * .getCheminDonnees() + "/sortie/ville.shx"); ShapefileWriter
               * writer = new ShapefileWriter(shp.getChannel(),
               * shx.getChannel()); writer.write(col, ShapeType.POLYGON); }
               * catch (IOException e) { e.printStackTrace(); } catch (Exception
               * e) { e.printStackTrace(); }
               * 
               * ExportFrame.logger.info("Export des ilots"); try { // compte
               * les non supprimes nb = 0; for (IBlock a :
               * GeneralisationDataSet.getInstance().getBlocks()) { if
               * (!a.isDeleted()) { nb++; } }
               * 
               * // collection des geometries geoms = new Geometry[nb]; i = 0;
               * for (IBlock a :
               * GeneralisationDataSet.getInstance().getBlocks()) { if
               * (!a.isDeleted()) { geoms[i++] = AdapterFactory.toGeometry( new
               * GeometryFactory(), a.getGeom()); } } col = new
               * GeometryFactory().createGeometryCollection(geoms);
               * 
               * // ecriture des fichiers shp = new
               * FileOutputStream(CartagenApplication.getInstance()
               * .getCheminDonnees() + "/sortie/ilot.shp"); shx = new
               * FileOutputStream(CartagenApplication.getInstance()
               * .getCheminDonnees() + "/sortie/ilot.shx"); ShapefileWriter
               * writer = new ShapefileWriter(shp.getChannel(),
               * shx.getChannel()); writer.write(col, ShapeType.POLYGON); }
               * catch (IOException e) { e.printStackTrace(); } catch (Exception
               * e) { e.printStackTrace(); }
               * 
               * ExportFrame.logger.info("Export des ilots grises"); try { //
               * compte les ilots grises nb = 0; for (IBlock a :
               * GeneralisationDataSet.getInstance().getBlocks()) { if
               * (((BlockAgent) AgentUtil.getAgentAgentFromGeneObj(a))
               * .isColored()) { nb++; } }
               * 
               * // collection des geometries geoms = new Geometry[nb]; i = 0;
               * for (IBlock a :
               * GeneralisationDataSet.getInstance().getBlocks()) { if
               * (((BlockAgent) AgentUtil.getAgentAgentFromGeneObj(a))
               * .isColored()) { geoms[i++] = AdapterFactory.toGeometry( new
               * GeometryFactory(), a.getGeom()); } }
               * 
               * col = new GeometryFactory().createGeometryCollection(geoms);
               * 
               * // ecriture des fichiers shp = new
               * FileOutputStream(CartagenApplication.getInstance()
               * .getCheminDonnees() + "/sortie/ilot_grise.shp"); shx = new
               * FileOutputStream(CartagenApplication.getInstance()
               * .getCheminDonnees() + "/sortie/ilot_grise.shx");
               * ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
               * shx.getChannel()); writer.write(col, ShapeType.POLYGON); }
               * catch (IOException e) { e.printStackTrace(); } catch (Exception
               * e) { e.printStackTrace(); }
               */
              ExportFrame.logger.info("Export des batiments generalises");
              try {
                // compte les non supprimes
                nb = 0;
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    geoms[i++] = AdapterFactory.toGeometry(
                        new GeometryFactory(), a.getGeom());
                  }
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()
                    // + "/sortie/batiment.shp");

                    ExportFrame.this.systemPath + "/Bat.shp");
                shx = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()
                    // + "/sortie/batiment.shp");

                    ExportFrame.this.systemPath + "/Bat.shx");
                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());

                writer.writeHeaders(col.getEnvelopeInternal(),
                    ShapeType.POLYGON, nb, 100000);
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    writer.writeGeometry(AdapterFactory.toGeometry(
                        new GeometryFactory(), a.getGeom()));
                  }
                }
                writer.close();
                // writer.write(col, ShapeType.POLYGON);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.logger.info("Export des batiments generalises");
              try {
                // compte les non supprimes
                nb = 0;
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    geoms[i++] = AdapterFactory.toGeometry(
                        new GeometryFactory(), a.getGeom());
                  }
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()
                    // + "/sortie/batiment.shp");

                    ExportFrame.this.systemPath + "/Bat.shp");
                shx = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()
                    // + "/sortie/batiment.shp");

                    ExportFrame.this.systemPath + "/Bat.shx");

                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());

                writer.writeHeaders(col.getEnvelopeInternal(),
                    ShapeType.POLYGON, nb, 100000);
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    writer.writeGeometry(AdapterFactory.toGeometry(
                        new GeometryFactory(), a.getGeom()));
                  }
                }
                writer.close();
                // writer.write(col, ShapeType.POLYGON);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.logger.info("Export des surfeces d'eau");
              try {
                // compte les non supprimes
                nb = 0;
                for (IWaterArea a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getWaterAreas()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (IWaterArea a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getWaterAreas()) {
                  if (!a.isDeleted()) {
                    geoms[i++] = AdapterFactory.toGeometry(
                        new GeometryFactory(), a.getGeom());
                  }
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()
                    // + "/sortie/batiment.shp");

                    ExportFrame.this.systemPath + "/surface_eau.shp");
                shx = new FileOutputStream(ExportFrame.this.systemPath
                    + "/surface_eau.shx");
                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());

                writer.writeHeaders(col.getEnvelopeInternal(),
                    ShapeType.POLYGON, nb, 100000);
                for (IBuilding a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getBuildings()) {
                  if (!a.isDeleted()) {
                    writer.writeGeometry(AdapterFactory.toGeometry(
                        new GeometryFactory(), a.getGeom()));
                  }
                }
                writer.close();
                // writer.write(col, ShapeType.POLYGON);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.logger.info("Export des troncons routier");
              try {
                // compte les non supprimes
                nb = 0;

                for (INetworkSection a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getRoadNetwork()
                    .getNonDeletedSections()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (INetworkSection tr : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getRoadNetwork()
                    .getNonDeletedSections()) {
                  if (tr.isDeleted()) {
                    continue;
                  }
                  LineString[] lss = new LineString[] { (LineString) AdapterFactory
                      .toGeometry(new GeometryFactory(), tr.getGeom()) };
                  geoms[i++] = new GeometryFactory().createMultiLineString(lss);
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/troncon_route.shp");
                shx = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/troncon_route.shx");
                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());
                writer.write(col, ShapeType.ARC);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.logger.info("Export des troncons d'eau");
              try {
                // compte les non supprimes
                nb = 0;

                for (INetworkSection a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getHydroNetwork()
                    .getNonDeletedSections()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (INetworkSection tr : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getHydroNetwork()
                    .getNonDeletedSections()) {
                  if (tr.isDeleted()) {
                    continue;
                  }
                  LineString[] lss = new LineString[] { (LineString) AdapterFactory
                      .toGeometry(new GeometryFactory(), tr.getGeom()) };
                  geoms[i++] = new GeometryFactory().createMultiLineString(lss);
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/trancon_eau.shp");
                shx = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/trancon_eau.shx");
                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());
                writer.write(col, ShapeType.ARC);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.logger.info("Export des lignes electriques");
              try {
                // compte les non supprimes
                nb = 0;

                for (INetworkSection a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getElectricityNetwork()
                    .getNonDeletedSections()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (INetworkSection tr : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getElectricityNetwork()
                    .getNonDeletedSections()) {
                  if (tr.isDeleted()) {
                    continue;
                  }
                  LineString[] lss = new LineString[] { (LineString) AdapterFactory
                      .toGeometry(new GeometryFactory(), tr.getGeom()) };
                  geoms[i++] = new GeometryFactory().createMultiLineString(lss);
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/lignes_electrique.shp");
                shx = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/lignes_electrique.shx");
                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());
                writer.write(col, ShapeType.ARC);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.logger.info("Export des lignes ferroviaire");
              try {
                // compte les non supprimes
                nb = 0;

                for (INetworkSection a : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getRailwayNetwork()
                    .getNonDeletedSections()) {
                  if (!a.isDeleted()) {
                    nb++;
                  }
                }

                // collection des geometries
                geoms = new Geometry[nb];
                i = 0;
                for (INetworkSection tr : CartAGenDocOld.getInstance()
                    .getCurrentDataset().getRailwayNetwork()
                    .getNonDeletedSections()) {
                  if (tr.isDeleted()) {
                    continue;
                  }
                  LineString[] lss = new LineString[] { (LineString) AdapterFactory
                      .toGeometry(new GeometryFactory(), tr.getGeom()) };
                  geoms[i++] = new GeometryFactory().createMultiLineString(lss);
                }
                col = new GeometryFactory().createGeometryCollection(geoms);

                // ecriture des fichiers
                shp = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/Reseau_Ferroviair.shp");
                shx = new FileOutputStream(// CartagenApplication.getInstance()
                    // .getCheminDonnees()+
                    ExportFrame.this.systemPath + "/Reseau_Ferroviair.shx");
                ShapefileWriter writer = new ShapefileWriter(shp.getChannel(),
                    shx.getChannel());
                writer.write(col, ShapeType.ARC);
              } catch (IOException e) {
                e.printStackTrace();
              } catch (Exception e) {
                e.printStackTrace();
              }

              ExportFrame.this.getLSuivi().setText("Export terminé!");
              ExportFrame.logger.info("Fin export");
            }
          });
          th.start();
        }
      });
    }
    return this.bExportTout;
  }

  /*
   * private JButton getBExportTout() { if (this.bExportTout == null) {
   * this.bExportTout = new JButton("Exporter tout");
   * this.bExportTout.addActionListener(new ActionListener() { public void
   * actionPerformed(ActionEvent arg0) { Thread th = new Thread(new Runnable() {
   * public void run() {
   * 
   * ExportFrame.logger.info("Début export (dans le répertoire " +
   * CartagenApplication.getInstance().getCheminDonnees() + "/sortie)");
   * 
   * 
   * chooseDirectory();
   * 
   * ExportFrame.this.getLSuivi().setText("Export en cours...");
   * 
   * // cree le repertoire de sortie new
   * File(CartagenApplication.getInstance().getCheminDonnees() +
   * "/sortie").mkdir();
   * 
   * Geometry[] geoms; int i, nb; GeometryCollection col; FileOutputStream shp,
   * shx;
   * 
   * ExportFrame.logger.info("Export des villes"); try { // compte les non
   * supprimes nb = 0; for (ITown a :
   * CartagenApplication.getInstance().getDataSet() .getVilles()) { if
   * (!a.isDeleted()) { nb++; } }
   * 
   * // collection des geometries geoms = new Geometry[nb]; i = 0; for (ITown a
   * : CartagenApplication.getInstance().getDataSet() .getVilles()) { if
   * (!a.isDeleted()) { geoms[i++] = AdapterFactory.toGeometry( new
   * GeometryFactory(), a.getGeom()); } } col = new
   * GeometryFactory().createGeometryCollection(geoms);
   * 
   * // ecriture des fichiers shp = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "sys/ville.shp"); shx = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/ville.shx"); ShapefileWriter writer = new
   * ShapefileWriter(shp.getChannel(), shx.getChannel()); writer.write(col,
   * ShapeType.POLYGON); } catch (IOException e) { e.printStackTrace(); } catch
   * (Exception e) { e.printStackTrace(); }
   * 
   * ExportFrame.logger.info("Export des ilots"); try { // compte les non
   * supprimes nb = 0; for (IBlock a :
   * GeneralisationDataSet.getInstance().getBlocks()) { if (!a.isDeleted()) {
   * nb++; } }
   * 
   * // collection des geometries geoms = new Geometry[nb]; i = 0; for (IBlock a
   * : GeneralisationDataSet.getInstance().getBlocks()) { if (!a.isDeleted()) {
   * geoms[i++] = AdapterFactory.toGeometry( new GeometryFactory(),
   * a.getGeom()); } } col = new
   * GeometryFactory().createGeometryCollection(geoms);
   * 
   * // ecriture des fichiers shp = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/ilot.shp"); shx = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/ilot.shx"); ShapefileWriter writer = new
   * ShapefileWriter(shp.getChannel(), shx.getChannel()); writer.write(col,
   * ShapeType.POLYGON); } catch (IOException e) { e.printStackTrace(); } catch
   * (Exception e) { e.printStackTrace(); }
   * 
   * ExportFrame.logger.info("Export des ilots grises"); try { // compte les
   * ilots grises nb = 0; for (IBlock a :
   * GeneralisationDataSet.getInstance().getBlocks()) { if (((BlockAgent)
   * AgentUtil.getAgentAgentFromGeneObj(a)) .isColored()) { nb++; } }
   * 
   * // collection des geometries geoms = new Geometry[nb]; i = 0; for (IBlock a
   * : GeneralisationDataSet.getInstance().getBlocks()) { if (((BlockAgent)
   * AgentUtil.getAgentAgentFromGeneObj(a)) .isColored()) { geoms[i++] =
   * AdapterFactory.toGeometry( new GeometryFactory(), a.getGeom()); } }
   * 
   * col = new GeometryFactory().createGeometryCollection(geoms);
   * 
   * // ecriture des fichiers shp = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/ilot_grise.shp"); shx = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/ilot_grise.shx"); ShapefileWriter writer = new
   * ShapefileWriter(shp.getChannel(), shx.getChannel()); writer.write(col,
   * ShapeType.POLYGON); } catch (IOException e) { e.printStackTrace(); } catch
   * (Exception e) { e.printStackTrace(); }
   * 
   * ExportFrame.logger.info("Export des batiments generalises"); try { //
   * compte les non supprimes nb = 0; for (IBuilding a :
   * GeneralisationDataSet.getInstance() .getBuildings()) { if (!a.isDeleted())
   * { nb++; } }
   * 
   * // collection des geometries geoms = new Geometry[nb]; i = 0; for
   * (IBuilding a : GeneralisationDataSet.getInstance() .getBuildings()) { if
   * (!a.isDeleted()) { geoms[i++] = AdapterFactory.toGeometry( new
   * GeometryFactory(), a.getGeom()); } } col = new
   * GeometryFactory().createGeometryCollection(geoms);
   * 
   * // ecriture des fichiers shp = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/batiment.shp"); shx = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/batiment.shx"); ShapefileWriter writer = new
   * ShapefileWriter(shp.getChannel(), shx.getChannel());
   * 
   * writer.writeHeaders(col.getEnvelopeInternal(), ShapeType.POLYGON, nb,
   * 100000); for (IBuilding a : GeneralisationDataSet.getInstance()
   * .getBuildings()) { if (!a.isDeleted()) {
   * writer.writeGeometry(AdapterFactory.toGeometry( new GeometryFactory(),
   * a.getGeom())); } } writer.close(); // writer.write(col, ShapeType.POLYGON);
   * } catch (IOException e) { e.printStackTrace(); } catch (Exception e) {
   * e.printStackTrace(); }
   * 
   * ExportFrame.logger.info("Export des troncons routier"); try { // compte les
   * non supprimes nb = 0;
   * 
   * 
   * for (INetworkSection a : GeneralisationDataSet.getInstance()
   * .getReseauRoutier().getTroncons()) { if (!a.isDeleted()) { nb++; } }
   * 
   * // collection des geometries geoms = new Geometry[nb]; i = 0; for
   * (INetworkSection tr : GeneralisationDataSet.getInstance()
   * .getReseauRoutier().getTroncons()) { if (tr.isDeleted()) { continue; }
   * LineString[] lss = new LineString[] { (LineString) AdapterFactory
   * .toGeometry(new GeometryFactory(), tr.getGeom()) }; geoms[i++] = new
   * GeometryFactory().createMultiLineString(lss); } col = new
   * GeometryFactory().createGeometryCollection(geoms);
   * 
   * // ecriture des fichiers shp = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/troncon_route.shp"); shx = new
   * FileOutputStream(CartagenApplication.getInstance() .getCheminDonnees() +
   * "/sortie/troncon_route.shx"); ShapefileWriter writer = new
   * ShapefileWriter(shp.getChannel(), shx.getChannel()); writer.write(col,
   * ShapeType.ARC); } catch (IOException e) { e.printStackTrace(); } catch
   * (Exception e) { e.printStackTrace(); }
   * 
   * ExportFrame.this.getLSuivi().setText("Export terminé!");
   * ExportFrame.logger.info("Fin export"); } }); th.start(); } }); } return
   * this.bExportTout; } /**
   */
  private JLabel lSuivi;

  protected JLabel getLSuivi() {
    if (this.lSuivi == null) {
      this.lSuivi = new JLabel("Export");
    }
    return this.lSuivi;
  }

  private boolean chooseDirectory() {

    // File chooser
    JFileChooser choix = new JFileChooser();
    choix.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    choix.setApproveButtonText("Load this dataset");
    int retour = choix.showDialog(null, "Select a dataset");
    if (retour == JFileChooser.APPROVE_OPTION) {
      this.systemPath = choix.getSelectedFile().getAbsolutePath();

    } else {
      this.systemPath = null;
    }

    /*
     * if (systemPath == null) { // Creation of the dataset list filesList =
     * LoaderUtil.listerRepertoire(new File(dataSet)); }
     */

    return true;

  }

}
