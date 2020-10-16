/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.osm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.locationtech.jts.geom.Geometry;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.cartagen.core.dataset.CartAGenDataSet;
import fr.ign.cogit.cartagen.core.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.core.dataset.postgis.PostgisDB;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.IGeneObjPoint;
import fr.ign.cogit.cartagen.core.genericschema.land.ISimpleLandUseArea;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.osm.schema.OsmGeneObj;
import fr.ign.cogit.cartagen.osm.schema.landuse.OsmLandUseTypology;
import fr.ign.cogit.cartagen.osm.schema.network.OsmNetworkSection;
import fr.ign.cogit.cartagen.osm.schema.urban.OsmBuilding;
import fr.ign.cogit.cartagen.spatialanalysis.clustering.AdjacencyClustering;
import fr.ign.cogit.cartagen.util.LastSessionParameters;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.panel.OsmFileFilter;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.ProjectFramePlugin;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.Criterion;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ClassificationResult;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.ConclusionIntervals;
import fr.ign.cogit.geoxygene.contrib.multicriteriadecision.classifying.electretri.RobustELECTRETRIMethod;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.osm.contributor.OSMContributor;
import fr.ign.cogit.geoxygene.osm.importexport.OSMLoader;
import fr.ign.cogit.geoxygene.osm.importexport.OSMLoader.OSMLoaderType;
import fr.ign.cogit.geoxygene.osm.importexport.OSMLoader.OsmLoadingTask;
import fr.ign.cogit.geoxygene.osm.lodanalysis.LoDCategory;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.CoalescenceCriterion;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.EdgeLengthMedianCriterion;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.FeatureTypeCriterion;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.GranularityCriterion;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.LoDMultiCriteria;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.SizeCriterion;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.SourceCriterion;
import fr.ign.cogit.geoxygene.osm.lodanalysis.individual.VertexDensityCriterion;
import fr.ign.cogit.geoxygene.osm.quality.history.BusStopHistRelAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.ATMQualityAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.ATMQualityAssessment.BankBuilding;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.BuildingConsistencyQualityAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.BuildingConsistencyQualityAssessment.POIBuilding;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.BusStopQualityAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.MotorJuncQualityAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.SchoolQualityAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.StationQualityAssessment;
import fr.ign.cogit.geoxygene.osm.quality.spatialrelations.StoreQualityAssessment;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.osm.util.I18N;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Stroke;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.util.SelectionUtil;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.JtsGeOxygene;
import fr.ign.cogit.geoxygene.util.math.Combination;
import fr.ign.cogit.geoxygene.util.math.CombinationSet;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

public class OSMPlugin implements ProjectFramePlugin, GeOxygeneApplicationPlugin {

    private GeOxygeneApplication application = null;
    private Runnable fillLayersTask;
    private List<OSMFile> recentFiles;
    private OSMLoader loader;
    private JDialog dialog;
    private JTextArea taskOutput;
    private JProgressBar progressBar;
    private JLabel taskLabel;
    private OsmLoadingTask currentTask = OsmLoadingTask.POINTS;

    @Override
    public void initialize(GeOxygeneApplication application) {
        this.application = application;
        loadRecentFiles();
        JMenu menu = new JMenu("OSM");
        menu.add(new JMenuItem(new ImportOSMFileAction()));
        JMenu menuRecent = new JMenu("Import recent file");
        for (OSMFile recentDoc : recentFiles) {
            menuRecent.add(new JMenuItem(new ImportOSMNamedFileAction(recentDoc)));
        }
        menu.add(menuRecent);
        menu.addSeparator();
        menu.add(new JMenuItem(new BrowseTagsAction()));
        menu.addSeparator();
        JMenu correctionMenu = new JMenu("Data Correction");
        correctionMenu.add(new JMenuItem(new AggrBuildingsAction()));
        correctionMenu.add(new JMenuItem(new PlanarNetworkAction()));
        correctionMenu.add(new JMenuItem(new RailSideTracksAction()));
        menu.add(correctionMenu);
        menu.addSeparator();
        JMenu analysisMenu = new JMenu("LoD Analysis");
        analysisMenu.add(new JMenuItem(new InferLoDAction()));
        analysisMenu.add(new JMenuItem(new LodSensitivityAction()));
        JMenu userMenu = new JMenu("Contributor Analysis");
        userMenu.add(new JMenuItem(new ShowUsersAction()));
        userMenu.add(new JMenuItem(new ShowUserContribsAction()));
        userMenu.add(new JMenuItem(new ShowUserContribsDayAction()));
        JMenu utilMenu = new JMenu("Tools");
        utilMenu.add(new JMenuItem(new CreateFeatureTypeAction()));
        JMenu qualMenu = new JMenu("Quality");
        qualMenu.add(new JMenuItem(new ATMQualityAction()));
        qualMenu.add(new JMenuItem(new StoresQualityAction()));
        qualMenu.add(new JMenuItem(new SchoolQualityAction()));
        qualMenu.add(new JMenuItem(new BusStopQualityAction()));
        qualMenu.add(new JMenuItem(new MultiPointQualityAction()));
        JMenu histMenu = new JMenu("history & relations");
        qualMenu.add(histMenu);
        histMenu.add(new JMenuItem(new StationsHistRelAction()));
        histMenu.add(new JMenuItem(new MotorJuncHistRelAction()));
        histMenu.add(new JMenuItem(new BusStopHistRelAction()));
        menu.add(analysisMenu);
        menu.add(qualMenu);
        menu.add(userMenu);
        menu.addSeparator();
        menu.add(utilMenu);

        application.getMainFrame().getMenuBar().add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
    }

    @Override
    public void initialize(ProjectFrame projectFrame) {
        // TODO ajoute un écouteur pour ouvrir le browser de tags.

    }

    /**
     * Create a new empty dataset, with its zone details, in which data can be
     * added later.
     * 
     * @author GTouya
     * 
     */
    public class ImportOSMFileAction extends AbstractAction implements PropertyChangeListener {

        /****/
        private static final long serialVersionUID = 1L;

        public File osmFile;
        public String epsg;
        public String tagFilter;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            ImportOsmFileFrame dialogImportOsmFrame = new ImportOsmFileFrame(this);
            if (dialogImportOsmFrame.getAction().equals("OK")) {
                //
                CartAGenDoc doc = CartAGenDoc.getInstance();
                String name = null;
                if (doc.getName() == null) {
                    name = osmFile.getName().substring(0, osmFile.getName().length() - 4);
                    doc.setName(name);
                    if (dialogImportOsmFrame.createPostGis()) {
                        try {
                            doc.setPostGisDb(PostgisDB.get(name, true));
                        } catch (Exception e) {
                            // do nothing
                        }
                    }
                }
                try {
                    OSMFile file = new OSMFile(osmFile, epsg, dialogImportOsmFrame.createPostGis(), tagFilter);
                    if (!recentFiles.contains(file))
                        recentFiles.add(0, file);
                    else {
                        recentFiles.remove(file);
                        recentFiles.add(0, file);
                    }
                    saveRecentFiles();
                } catch (TransformerException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                fillLayersTask = new Runnable() {
                    @Override
                    public void run() {
                        application.getMainFrame().getGui().setCursor(null);
                    }
                };

                loader = new OSMLoader(osmFile, application.getMainFrame().getSelectedProjectFrame().getSld(),
                        fillLayersTask, epsg, tagFilter, OSMLoaderType.XML);
                createProgressDialog();
                loader.setDialog(dialog);
                dialog.setVisible(true);
                application.getMainFrame().getGui().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                loader.addPropertyChangeListener(this);
                loader.execute();

            }
        }

        public ImportOSMFileAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Import OSM data from file in a new Dataset");
            this.putValue(Action.NAME, "Import OSM File");
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                taskOutput.append(String.format("Completed %d%% of task.\n", loader.getProgress()));
                if (!currentTask.equals(loader.getCurrentTask())) {
                    currentTask = loader.getCurrentTask();
                    taskLabel.setText(currentTask.getLabel() + " loading...");
                }
            }
        }

        class ImportOsmFileFrame extends JDialog implements ActionListener {

            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            ImportOSMFileAction importPlugin;

            private String action;

            /** Fields. */
            private JTextField txtPath, txtEpsg;
            private Map<String, String> currentProjections = new HashMap<String, String>();
            private JButton browseBtn, usedBtn, cancelBtn, okBtn;
            private JComboBox<String> usedCombo;
            private JCheckBox postGisCheck;
            private JTextField txtTagFilter;
            private JCheckBox chkTagFilter;

            /**
             * Constructor, build the frame.
             * 
             * @param importPlugin
             */
            public ImportOsmFileFrame(ImportOSMFileAction importPlugin) {
                this.importPlugin = importPlugin;

                setModal(true);
                setTitle("Import OSM File");
                setIconImage(
                        new ImageIcon(GeOxygeneApplication.class.getResource("/images/icons/map_add.png")).getImage());

                initPanel();

                pack();
                setLocation(300, 300);
                setSize(600, 200);
                setVisible(true);
            }

            @Override
            public void actionPerformed(ActionEvent evt) {
                Object source = evt.getSource();
                if (source == cancelBtn) {
                    action = "cancel";
                    dispose();
                } else if (source == usedBtn) {
                    txtEpsg.setText(currentProjections.get(usedCombo.getSelectedItem()));
                } else if (source == browseBtn) {
                    JFileChooser fc = new JFileChooser();
                    fc.setFileFilter(new OsmFileFilter());
                    // fc.setCurrentDirectory(new
                    // File("src/main/resources/xml/"));
                    fc.setCurrentDirectory(new File(application.getProperties().getLastOpenedFile()));
                    int returnVal = fc.showSaveDialog(application.getMainFrame().getGui());
                    if (returnVal != JFileChooser.APPROVE_OPTION) {
                        return;
                    }
                    File file = fc.getSelectedFile();
                    this.txtPath.setText(file.getAbsolutePath());
                } else if (source == okBtn) {
                    action = "OK";
                    importPlugin.epsg = txtEpsg.getText();
                    importPlugin.osmFile = new File(txtPath.getText());
                    if (this.chkTagFilter.isSelected() && !this.txtTagFilter.getText().equals(""))
                        importPlugin.tagFilter = this.txtTagFilter.getText();
                    dispose();
                }
            }

            /**
             * Initialize and display fields.
             */
            private void initPanel() {

                FormLayout layout = new FormLayout(
                        "20dlu, pref, pref, 10dlu, pref, pref, pref, pref, pref, pref, 20dlu", // Colonnes
                        "10dlu, pref, pref, 20dlu, pref, pref, 20dlu"); // Lignes
                setLayout(layout);

                CellConstraints cc = new CellConstraints();

                currentProjections.put("Lambert93", "2154");
                currentProjections.put("Lambert Belge 2008", "3812");
                currentProjections.put("UTM 18N (New York)", "32618");
                currentProjections.put("UTM 51N (Philippines)", "32651");
                currentProjections.put("UTM 16N (Chicago)", "32616");
                currentProjections.put("UTM 28N (Dakar)", "32628");
                currentProjections.put("UTM 32N (Italie)", "32632");
                currentProjections.put("UTM 33N (Vienna)", "32633");
                currentProjections.put("UTM 35N (Ukraine)", "32635");
                currentProjections.put("UTM 23S (Rio)", "32723");
                currentProjections.put("UTM 55S (Melbourne)", "32755");
                currentProjections.put("Gauss-Krueger zone 4 (East Germany)", "31468");
                currentProjections.put("Gauss-Krueger zone 3 (Center Germany)", "31467");
                this.setPreferredSize(new Dimension(350, 150));

                // JPanel filePanel = new JPanel();
                add(new JLabel("File : "), cc.xy(2, 2));
                txtPath = new JTextField(40);
                add(txtPath, cc.xyw(3, 2, 6));
                browseBtn = new JButton(new ImageIcon(this.getClass().getResource("/images/icons/magnifier.png")));
                browseBtn.addActionListener(this);
                browseBtn.setActionCommand("browse");
                add(browseBtn, cc.xy(9, 2));
                add(new JLabel(" (*.osm) "), cc.xy(10, 2));

                add(new JLabel("EPSG of projection: "), cc.xy(2, 3));
                txtEpsg = new JTextField();
                txtEpsg.setPreferredSize(new Dimension(60, 20));
                txtEpsg.setMaximumSize(new Dimension(60, 20));
                txtEpsg.setMinimumSize(new Dimension(60, 20));
                txtEpsg.setText("2154");
                add(txtEpsg, cc.xy(3, 3));

                add(new JLabel("( "), cc.xy(5, 3));
                usedBtn = new JButton("<<");
                usedBtn.addActionListener(this);
                usedBtn.setActionCommand("used");
                add(usedBtn, cc.xy(6, 3));
                usedCombo = new JComboBox<String>((String[]) currentProjections.keySet().toArray(new String[0]));
                usedCombo.setPreferredSize(new Dimension(130, 20));
                usedCombo.setMaximumSize(new Dimension(130, 20));
                usedCombo.setMinimumSize(new Dimension(130, 20));
                add(usedCombo, cc.xy(7, 3));
                add(new JLabel(" )  "), cc.xy(8, 3));

                chkTagFilter = new JCheckBox("Filter tags");
                chkTagFilter.setSelected(false);
                add(chkTagFilter, cc.xy(2, 4));
                txtTagFilter = new JTextField();
                txtTagFilter.setPreferredSize(new Dimension(60, 20));
                txtTagFilter.setMaximumSize(new Dimension(60, 20));
                txtTagFilter.setMinimumSize(new Dimension(60, 20));
                txtTagFilter.setText("");
                add(txtTagFilter, cc.xy(3, 4));

                postGisCheck = new JCheckBox("Create PostGis Db");
                postGisCheck.setSelected(false);
                add(postGisCheck, cc.xy(3, 5));

                // Define a panel with the OK and Cancel buttons
                JPanel btnPanel = new JPanel();
                okBtn = new JButton("OK");
                okBtn.addActionListener(this);
                okBtn.setActionCommand("ok");
                cancelBtn = new JButton(I18N.getString("MainLabels.lblCancel"));
                cancelBtn.addActionListener(this);
                cancelBtn.setActionCommand("cancel");
                btnPanel.add(okBtn);
                btnPanel.add(cancelBtn);
                btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
                add(btnPanel, cc.xy(3, 6));

            }

            /**
             * @return name of action (ok, cancel).
             */
            public String getAction() {
                return action;
            }

            public boolean createPostGis() {
                return postGisCheck.isSelected();
            }
        }
    }

    /**
     * Create a new empty dataset, with its zone details, in which data can be
     * added later.
     * 
     * @author GTouya
     * 
     */
    class BrowseTagsAction extends AbstractAction {

        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            int x = (int) application.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getBounds()
                    .getCenterX();
            int y = (int) application.getMainFrame().getSelectedProjectFrame().getLayerViewPanel().getBounds()
                    .getCenterY();
            List<OSMFeature> selectedObjs = new ArrayList<OSMFeature>();
            for (IFeature obj : SelectionUtil.getSelectedObjects(application)) {
                if (!(obj instanceof OSMFeature)) {
                    continue;
                }
                selectedObjs.add((OSMFeature) obj);
            }
            try {
                OSMTagBrowser browser = new OSMTagBrowser(new Point(x, y), selectedObjs);
                browser.setVisible(true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        public BrowseTagsAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Browse the OSM tags of the selected OSM features");
            this.putValue(Action.NAME, "Browse Tags");
        }
    }

    /**
     * @author GTouya
     * 
     */
    class ImportOSMNamedFileAction extends AbstractAction implements PropertyChangeListener {
        private OSMFile file;
        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            CartAGenDoc doc = CartAGenDoc.getInstance();
            String name = null;
            if (doc.getName() == null) {
                name = file.getFile().getName().substring(0, file.getFile().getName().length() - 4);
                doc.setName(name);
                if (file.isCreateDb()) {
                    try {
                        doc.setPostGisDb(PostgisDB.get(name, true));
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
            try {
                if (!recentFiles.contains(file))
                    recentFiles.add(0, file);
                else {
                    recentFiles.remove(file);
                    recentFiles.add(0, file);
                }
                saveRecentFiles();
            } catch (TransformerException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            fillLayersTask = new Runnable() {
                @Override
                public void run() {
                    application.getMainFrame().getGui().setCursor(null);
                }
            };

            loader = new OSMLoader(file.getFile(), application.getMainFrame().getSelectedProjectFrame().getSld(),
                    fillLayersTask, file.getEpsg(), file.getTagFilter(), OSMLoaderType.XML);
            createProgressDialog();
            loader.setDialog(dialog);
            dialog.setVisible(true);
            application.getMainFrame().getGui().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            loader.addPropertyChangeListener(this);
            loader.execute();
        }

        public ImportOSMNamedFileAction(OSMFile file) {
            this.putValue(Action.NAME, file.getFile().getPath());
            this.file = file;
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) {
                int progress = (Integer) evt.getNewValue();
                progressBar.setValue(progress);
                taskOutput.append(String.format("Completed %d%% of task.\n", loader.getProgress()));
                if (!currentTask.equals(loader.getCurrentTask())) {
                    currentTask = loader.getCurrentTask();
                    taskLabel.setText(currentTask.getLabel() + " loading...");
                }
            }
        }
    }

    /**
     * Create a new empty dataset, with its zone details, in which data can be
     * added later.
     * 
     * @author GTouya
     * 
     */
    class AggrBuildingsAction extends AbstractAction {

        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        @SuppressWarnings("unchecked")
        @Override
        public void actionPerformed(ActionEvent arg0) {
            try {
                // get the building population

                IPopulation<IBuilding> pop = new Population<IBuilding>();
                for (IBuilding b : CartAGenDoc.getInstance().getCurrentDataset().getBuildings()) {
                    if (b.getGeom() != null)
                        pop.add(b);
                }
                AdjacencyClustering clusters = new AdjacencyClustering(pop);
                System.out.println("début clustering");
                Set<Set<IGeneObj>> clusterSet = clusters.getClusters();
                System.out.println(clusterSet.size() + " clusters");
                for (Set<IGeneObj> cluster : clusterSet) {
                    OsmBuilding remainingB = null;
                    int maxTags = 0;
                    List<Geometry> list = new ArrayList<Geometry>();
                    for (IGeneObj build : cluster) {
                        list.add(JtsGeOxygene.makeJtsGeom(build.getGeom()));
                        build.eliminate();
                        if (((OsmGeneObj) build).getTags().size() > maxTags) {
                            maxTags = ((OsmGeneObj) build).getTags().size();
                            remainingB = (OsmBuilding) build;
                        }
                    }
                    if (remainingB == null) {
                        return;
                    }
                    // union of the geometries
                    Geometry jtsUnion = JtsAlgorithms.union(list);
                    IGeometry union = JtsGeOxygene.makeGeOxygeneGeom(jtsUnion);
                    if (union instanceof IPolygon) {
                        remainingB.cancelElimination();
                        remainingB.setGeom(union);
                    } else if (union instanceof IMultiSurface) {
                        for (int i = 0; i < ((IMultiSurface<IOrientableSurface>) union).getList().size(); i++) {
                            IGeometry newGeom = ((IMultiSurface<IOrientableSurface>) union).getList().get(i);
                            if (i == 0) {
                                remainingB.cancelElimination();
                                remainingB.setGeom(newGeom);
                            } else {
                                OsmBuilding newBuilding = new OsmBuilding(remainingB.getContributor(), newGeom,
                                        remainingB.getId(), remainingB.getChangeSet(), remainingB.getVersion(),
                                        remainingB.getUid(), remainingB.getDate());
                                pop.add(newBuilding);
                            }
                        }
                    }
                    // update initial geometry
                    remainingB.setInitialGeom(remainingB.getGeom());
                    System.out.println("1 cluster traité");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        public AggrBuildingsAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Aggregate the intersecting buildings to correct the digitising problems");
            this.putValue(Action.NAME, "Aggregate buildings");
        }
    }

    /**
     * Identify untagged sidetracks and mark the identified objects as
     * sidetracks. Uses the landuse objects typed as "railway" to find the
     * missing sidetracks.
     * 
     * @author GTouya
     * 
     */
    class RailSideTracksAction extends AbstractAction {

        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            IPopulation<IRailwayLine> railways = CartAGenDoc.getInstance().getCurrentDataset().getRailwayLines();
            for (ISimpleLandUseArea parcel : CartAGenDoc.getInstance().getCurrentDataset()
                    .getLandUseAreas(OsmLandUseTypology.RAILWAY.ordinal())) {
                Collection<IRailwayLine> inside = railways.select(parcel.getGeom());
                if (inside == null)
                    continue;
                for (IRailwayLine rail : inside) {
                    if (rail.isSidetrack())
                        continue;
                    if (((OsmGeneObj) rail).getTags().containsKey("usage"))
                        continue;
                    if (((OsmGeneObj) rail).getTags().containsKey("name"))
                        continue;
                    // FIXME to be improved as poorly tagged main railways are
                    // converted
                    // into sidetracks.
                    rail.setSidetrack(true);
                }
            }
        }

        public RailSideTracksAction() {
            this.putValue(Action.NAME, "Identify untagged sidetracks");
        }
    }

    /**
     * Make the chosen network planar cutting sections at intersections with
     * other sections. Cut sections keep the same tags.
     * 
     * @author GTouya
     * 
     */
    class PlanarNetworkAction extends AbstractAction {

        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            PlanarNetworkFrame frame = new PlanarNetworkFrame();
            frame.setVisible(true);
        }

        public PlanarNetworkAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Make the chosen network planar cutting sections at intersections");
            this.putValue(Action.NAME, "Make network planar");
        }
    }

    class PlanarNetworkFrame extends JFrame implements ActionListener {

        /****/
        private static final long serialVersionUID = 1L;
        private JComboBox combo;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("cancel")) {
                this.setVisible(false);
            } else {
                IPopulation<IGeneObj> iterable = new Population<IGeneObj>();
                IPopulation<IGeneObj> pop = CartAGenDoc.getInstance().getCurrentDataset()
                        .getCartagenPop((String) this.combo.getSelectedItem());
                iterable.addAll(pop);
                CarteTopo carteTopo = new CarteTopo("make OSM planar");
                carteTopo.importClasseGeo(pop, true);
                carteTopo.rendPlanaire(1.0);
                for (IGeneObj obj : iterable) {
                    if (!(obj instanceof OsmNetworkSection)) {
                        continue;
                    }
                    OsmNetworkSection section = (OsmNetworkSection) obj;
                    try {
                        // test if the section has been cut by topological map
                        if (section.getCorrespondants().size() == 1) {
                            continue;
                        }

                        // update the section geometry with the first edge of
                        // the
                        // topological
                        // map
                        section.setGeom(section.getCorrespondants().get(0).getGeom());

                        // loop on the other edges to make new instances
                        Class<? extends IGeneObj> classObj = obj.getClass();
                        Constructor<? extends IGeneObj> constr;

                        constr = classObj.getConstructor(String.class, IGeometry.class, int.class, int.class, int.class,
                                int.class, Date.class);

                        for (int i = 1; i < section.getCorrespondants().size(); i++) {
                            ILineString newLine = (ILineString) section.getCorrespondants().get(i).getGeom();
                            OsmNetworkSection newObj = (OsmNetworkSection) constr.newInstance(section.getContributor(),
                                    newLine, section.getId(), section.getChangeSet(), section.getVersion(),
                                    section.getUid(), section.getDate());
                            newObj.setTags(section.getTags());
                            newObj.setImportance(section.getImportance());
                            pop.add(newObj);
                        }

                    } catch (SecurityException e1) {
                        e1.printStackTrace();
                    } catch (NoSuchMethodException e1) {
                        e1.printStackTrace();
                    } catch (IllegalArgumentException e1) {
                        e1.printStackTrace();
                    } catch (InstantiationException e1) {
                        e1.printStackTrace();
                    } catch (IllegalAccessException e1) {
                        e1.printStackTrace();
                    } catch (InvocationTargetException e1) {
                        e1.printStackTrace();
                    }
                }
                this.setVisible(false);
            }
        }

        PlanarNetworkFrame() {
            super("Make network planar");
            this.setSize(200, 200);
            this.combo = new JComboBox(new String[] { CartAGenDataSet.ROADS_POP, CartAGenDataSet.RAILWAY_LINES_POP,
                    CartAGenDataSet.PATHS_POP, CartAGenDataSet.WATER_LINES_POP });
            this.combo.setMaximumSize(new Dimension(140, 30));
            this.combo.setMinimumSize(new Dimension(140, 30));
            this.combo.setPreferredSize(new Dimension(140, 30));

            // a panel for the buttons
            JPanel pButtons = new JPanel();
            JButton btnOk = new JButton("OK");
            btnOk.addActionListener(this);
            btnOk.setActionCommand("ok");
            JButton btnCancel = new JButton("Cancel");
            btnCancel.addActionListener(this);
            btnCancel.setActionCommand("cancel");
            pButtons.add(btnOk);
            pButtons.add(btnCancel);
            pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

            // layout of the frame
            this.getContentPane().add(this.combo);
            this.getContentPane().add(Box.createVerticalGlue());
            this.getContentPane().add(pButtons);
            this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
            this.pack();
        }
    }

    /**
     * Carry out an LOD inference on the selected features with every possible
     * combination of criteria and compute statistics on the results to study
     * the LOD inference sensitivity to the choice of the criteria.
     * 
     * @author GTouya
     * 
     */
    class LodSensitivityAction extends AbstractAction {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            OsmGeneObj obj = (OsmGeneObj) SelectionUtil.getSelectedObjects(application).iterator().next();
            DefaultCategoryDataset dataset = buildEmptyDataset();
            // on veut tirer quatre critères au moins sur les sept disponibles
            // (7!/(4!*3!) = 35 possibilités pour 4 critères, 21 pour 5 critères
            // et 7
            // pour 6 critères, soit 63 en tout).
            Set<Criterion> criteria = new HashSet<Criterion>();
            criteria.add(new CoalescenceCriterion("Coalescence"));
            criteria.add(new EdgeLengthMedianCriterion("EdgeLengthMedian"));
            criteria.add(new FeatureTypeCriterion("FeatureType"));
            criteria.add(new GranularityCriterion("Granularity"));
            criteria.add(new SizeCriterion("Size"));
            criteria.add(new SourceCriterion("Source"));
            criteria.add(new VertexDensityCriterion("VertexDensity"));
            CombinationSet combSet = new CombinationSet(criteria);

            // on calcule d'abord les combinaisons à 4 critères
            for (Combination comb : combSet.getAllCombinationsOfNOrMoreElements(4)) {
                // on met de côté le cas à sept critères
                if (comb.getSize() == 7)
                    continue;
                @SuppressWarnings("unchecked")
                ClassificationResult decision = computeDecision((Set<Criterion>) comb.getSubSet(), obj);
                dataset.incrementValue(1.0, "Number of inferences",
                        LoDCategory.valueOf(decision.getCategory()).ordinal() + 1);
            }
            JFreeChart chart = ChartFactory.createBarChart("LoD Inference Sensitivity", "LoD Category",
                    "Number of inferences", dataset, PlotOrientation.VERTICAL, false, false, false);
            // on calcule l'inférence à sept critères
            ClassificationResult decision = computeDecision(criteria, obj);
            System.out.println(decision.getCategory());
            // on met en valeur la colonne du diagramme correspondant à la
            // décision à
            // 7 critères
            CategoryPlot plot = chart.getCategoryPlot();
            final BarRenderer renderer = new CustomRenderer(LoDCategory.valueOf(decision.getCategory()).ordinal(),
                    Color.BLUE);
            plot.setRenderer(renderer);

            String nom = obj.toString() + " Satisfaction Distribution";
            JDialog dialog = new JDialog();
            dialog.setTitle(nom);
            dialog.add(new ChartPanel(chart));
            dialog.setSize(400, 300);
            dialog.setVisible(true);
            dialog.setAlwaysOnTop(true);
        }

        /**
         * A custom renderer that returns a different color for each item in a
         * single series.
         */
        class CustomRenderer extends BarRenderer {

            /****/
            private static final long serialVersionUID = 1L;
            /** The colors. */
            private int columnToHighlight;
            private Color highlightColor;

            /**
             * Creates a new renderer.
             * 
             * @param colors
             *            the colors.
             */
            public CustomRenderer(final int columnToHighlight, final Color highlightColor) {
                this.columnToHighlight = columnToHighlight;
                this.highlightColor = highlightColor;
            }

            /**
             * Returns the paint for an item. Overrides the default behaviour
             * inherited from AbstractSeriesRenderer.
             * 
             * @param row
             *            the series.
             * @param column
             *            the category.
             * 
             * @return The item color.
             */
            @Override
            public Paint getItemPaint(final int row, final int column) {
                if (column == columnToHighlight)
                    return highlightColor;
                return Color.RED;
            }
        }

        /**
         * Build an empty dataset for printing the distribution in a chart.
         * 
         * @return
         */
        private DefaultCategoryDataset buildEmptyDataset() {
            DefaultCategoryDataset newDataset = new DefaultCategoryDataset();
            newDataset.addValue(0.0, "Number of inferences", new Integer(LoDCategory.STREET.ordinal() + 1));
            newDataset.addValue(0.0, "Number of inferences", new Integer(LoDCategory.CITY.ordinal() + 1));
            newDataset.addValue(0.0, "Number of inferences", new Integer(LoDCategory.COUNTY.ordinal() + 1));
            newDataset.addValue(0.0, "Number of inferences", new Integer(LoDCategory.REGION.ordinal() + 1));
            newDataset.addValue(0.0, "Number of inferences", new Integer(LoDCategory.COUNTRY.ordinal() + 1));

            return newDataset;
        }

        private ClassificationResult computeDecision(Set<Criterion> criteria, OsmGeneObj obj) {
            RobustELECTRETRIMethod electre = new RobustELECTRETRIMethod();
            ConclusionIntervals conclusion = LoDMultiCriteria.initConclusion(criteria);
            electre.setCriteriaParamsFromCriteria(criteria);

            // make the decision
            // on remplit les valeurs courantes à partir du block courant
            Map<String, Double> valeursCourantes = new HashMap<String, Double>();
            for (Criterion crit : criteria) {
                Map<String, Object> param = LoDMultiCriteria.initParameters((OsmGeneObj) obj, crit);
                valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
            }
            return electre.decision(criteria, valeursCourantes, conclusion);
        }

        public LodSensitivityAction() {
            this.putValue(Action.NAME, "Level of Detail inference sensitivity");
        }
    }

    /**
     * Infer the LoD of the selected features using the multicriteria method.
     * 
     * @author GTouya
     * 
     */
    class InferLoDAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;
        private JTextArea txtArea;
        private DecimalFormat df;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            List<OsmGeneObj> selectedObjs = new ArrayList<OsmGeneObj>();
            for (IFeature obj : SelectionUtil.getSelectedObjects(application)) {
                if (!(obj instanceof OsmGeneObj)) {
                    continue;
                }
                selectedObjs.add((OsmGeneObj) obj);
            }
            try {
                RobustELECTRETRIMethod electreStd = LoDMultiCriteria.buildELECTRETRIMethod();
                RobustELECTRETRIMethod electrePt = LoDMultiCriteria.buildELECTRETRIMethodForPts();
                ConclusionIntervals conclusionStd = LoDMultiCriteria.initConclusion(electreStd.getCriteria());
                ConclusionIntervals conclusionPt = LoDMultiCriteria.initConclusion(electrePt.getCriteria());

                // make an output dialog to display results
                JDialog dialog = new JDialog(application.getMainFrame().getGui());
                txtArea = new JTextArea();
                txtArea.setPreferredSize(new Dimension(200, 800));
                txtArea.setMaximumSize(new Dimension(200, 800));
                txtArea.setMinimumSize(new Dimension(200, 800));
                dialog.add(new JScrollPane(txtArea));
                dialog.setAlwaysOnTop(true);
                dialog.setSize(300, 300);

                // loop on the selected objects
                for (OsmGeneObj obj : selectedObjs) {
                    RobustELECTRETRIMethod electreMethod = electreStd;
                    ConclusionIntervals conclusion = conclusionStd;
                    if (obj instanceof IGeneObjPoint) {
                        electreMethod = electrePt;
                        conclusion = conclusionPt;
                    }
                    Map<String, Double> valeursCourantes = new HashMap<String, Double>();
                    for (Criterion crit : electreMethod.getCriteria()) {
                        Map<String, Object> param = LoDMultiCriteria.initParameters(obj, crit);
                        valeursCourantes.put(crit.getName(), new Double(crit.value(param)));
                    }
                    ClassificationResult decision = electreMethod.decision(electreMethod.getCriteria(),
                            valeursCourantes, conclusion);
                    txtArea.append(obj.getClass().getSimpleName() + ":" + obj.toString() + ": "
                            + decision.getCategory().toString() + " (robustness=" + df.format(decision.getRobustness())
                            + ")");
                    txtArea.append("\n");
                }
                dialog.setVisible(true);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        public InferLoDAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Infer the LoD of the selected OSM features");
            this.putValue(Action.NAME, "Infer LoD");
            df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);
            df.setDecimalSeparatorAlwaysShown(true);
        }
    }

    private StyledLayerDescriptor compileOsmSlds() throws JAXBException {
        // load road sld
        StyledLayerDescriptor defaultSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/roads_sld.xml")); //$NON-NLS-1$
        // load buildings sld
        StyledLayerDescriptor buildingSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/buildings_sld.xml")); //$NON-NLS-1$
        for (Layer layer : buildingSld.getLayers())
            defaultSld.add(layer);
        // load waterway sld
        StyledLayerDescriptor waterSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/waterway_sld.xml")); //$NON-NLS-1$
        for (Layer layer : waterSld.getLayers())
            defaultSld.add(layer);
        // load landuse sld
        StyledLayerDescriptor landuseSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/landuse_sld.xml")); //$NON-NLS-1$
        for (Layer layer : landuseSld.getLayers())
            defaultSld.add(layer);
        // load point features sld
        StyledLayerDescriptor ptsSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/point_features_sld.xml")); //$NON-NLS-1$
        for (Layer layer : ptsSld.getLayers())
            defaultSld.add(layer);
        // load railways sld
        StyledLayerDescriptor railSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/railway_sld.xml")); //$NON-NLS-1$
        for (Layer layer : railSld.getLayers())
            defaultSld.add(layer);
        // load natural sld
        StyledLayerDescriptor naturalSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/natural_sld.xml")); //$NON-NLS-1$
        for (Layer layer : naturalSld.getLayers())
            defaultSld.add(layer);
        // load leisure sld
        StyledLayerDescriptor leisureSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/leisure_sld.xml")); //$NON-NLS-1$
        for (Layer layer : leisureSld.getLayers())
            defaultSld.add(layer);
        // load airport sld
        StyledLayerDescriptor airportSld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/airport_sld.xml")); //$NON-NLS-1$
        for (Layer layer : airportSld.getLayers())
            defaultSld.add(layer);
        // load amenity sld
        StyledLayerDescriptor amenitySld = StyledLayerDescriptor
                .unmarshall(OSMLoader.class.getClassLoader().getResourceAsStream("sld/amenity_sld.xml")); //$NON-NLS-1$
        for (Layer layer : amenitySld.getLayers())
            defaultSld.add(layer);
        // TODO fill with the other SLDs
        return defaultSld;
    }

    /**
     * Create the complete feature type attributes from the existing tags in the
     * dataset for each layer.
     * 
     * @author GTouya
     * 
     */
    class CreateFeatureTypeAction extends AbstractAction {

        /**
        * 
        */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            StyledLayerDescriptor sld = application.getMainFrame().getSelectedProjectFrame().getSld();
            for (Layer layer : sld.getLayers()) {
                // get layer feature type
                GF_FeatureType ft = layer.getFeatureCollection().getFeatureType();
                // add the default metadata as attribute
                ft.addFeatureAttribute(new AttributeType("osmId", "Long"));
                ft.addFeatureAttribute(new AttributeType("contributor", "String"));
                ft.addFeatureAttribute(new AttributeType("changeSet", "Integer"));
                ft.addFeatureAttribute(new AttributeType("version", "Integer"));
                ft.addFeatureAttribute(new AttributeType("uid", "Integer"));
                ft.addFeatureAttribute(new AttributeType("date", "Date"));
                // add the tags as attribute
                Set<String> layerTags = new HashSet<String>();
                for (IFeature feat : layer.getFeatureCollection()) {
                    layerTags.addAll(((OsmGeneObj) feat).getTags().keySet());
                    feat.setFeatureType(ft);
                }
                for (String tag : layerTags)
                    ft.addFeatureAttribute(new AttributeType(tag, "String"));
            }
        }

        public CreateFeatureTypeAction() {
            this.putValue(Action.NAME, "Create FeatureType information");
        }
    }

    private void loadRecentFiles() {
        this.recentFiles = new ArrayList<OSMFile>();
        LastSessionParameters params = LastSessionParameters.getInstance();
        if (params.hasParameter("Recent OSM file 1")) {
            String path = (String) params.getParameterValue("Recent OSM file 1");
            Map<String, String> attrs = params.getParameterAttributes("Recent OSM file 1");
            String epsg = attrs.get("epsg");
            boolean createDb = false;
            if (attrs.get("createDb").equals("true"))
                createDb = true;
            String tagFilter = attrs.get("tagFilter");
            this.recentFiles.add(new OSMFile(new File(path), epsg, createDb, tagFilter));
        }
        if (params.hasParameter("Recent OSM file 2")) {
            String path = (String) params.getParameterValue("Recent OSM file 2");
            Map<String, String> attrs = params.getParameterAttributes("Recent OSM file 2");
            String epsg = attrs.get("epsg");
            boolean createDb = false;
            if (attrs.get("createDb").equals("true"))
                createDb = true;
            String tagFilter = attrs.get("tagFilter");
            this.recentFiles.add(new OSMFile(new File(path), epsg, createDb, tagFilter));
        }
        if (params.hasParameter("Recent OSM file 3")) {
            String path = (String) params.getParameterValue("Recent OSM file 3");
            Map<String, String> attrs = params.getParameterAttributes("Recent OSM file 3");
            String epsg = attrs.get("epsg");
            boolean createDb = false;
            if (attrs.get("createDb").equals("true"))
                createDb = true;
            String tagFilter = attrs.get("tagFilter");
            this.recentFiles.add(new OSMFile(new File(path), epsg, createDb, tagFilter));
        }
        if (params.hasParameter("Recent OSM file 4")) {
            String path = (String) params.getParameterValue("Recent OSM file 4");
            Map<String, String> attrs = params.getParameterAttributes("Recent OSM file 4");
            String epsg = attrs.get("epsg");
            boolean createDb = false;
            if (attrs.get("createDb").equals("true"))
                createDb = true;
            String tagFilter = attrs.get("tagFilter");
            this.recentFiles.add(new OSMFile(new File(path), epsg, createDb, tagFilter));
        }
        if (params.hasParameter("Recent OSM file 5")) {
            String path = (String) params.getParameterValue("Recent OSM file 5");
            Map<String, String> attrs = params.getParameterAttributes("Recent OSM file 5");
            String epsg = attrs.get("epsg");
            boolean createDb = false;
            if (attrs.get("createDb").equals("true"))
                createDb = true;
            String tagFilter = attrs.get("tagFilter");
            this.recentFiles.add(new OSMFile(new File(path), epsg, createDb, tagFilter));
        }
    }

    private void saveRecentFiles() throws TransformerException, IOException {
        LastSessionParameters params = LastSessionParameters.getInstance();
        for (int i = 1; i <= Math.min(5, recentFiles.size()); i++) {
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("epsg", recentFiles.get(i - 1).getEpsg());
            attributes.put("createDb", String.valueOf(recentFiles.get(i - 1).isCreateDb()));
            attributes.put("tagFilter", recentFiles.get(i - 1).getTagFilter());
            params.setParameter("Recent OSM file " + i, recentFiles.get(i - 1).getFile().getPath(), attributes);
        }
    }

    private void createProgressDialog() {
        JPanel panel = new JPanel(new BorderLayout());

        taskLabel = new JLabel(loader.getCurrentTask().getLabel() + " loading...");
        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5, 5, 5, 5));
        taskOutput.setEditable(false);

        JPanel panel1 = new JPanel();
        panel1.add(taskLabel);
        panel1.add(progressBar);

        panel.add(panel1, BorderLayout.PAGE_START);
        panel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog = new JDialog(application.getMainFrame().getGui());
        dialog.add(panel);
        dialog.pack();
    }

    class OSMFile {
        private File file;
        private String epsg;
        private boolean createDb;
        private String tagFilter;

        public OSMFile(File file, String epsg, boolean createDb, String tagFilter) {
            super();
            this.file = file;
            this.epsg = epsg;
            this.createDb = createDb;
            this.tagFilter = tagFilter;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((file == null) ? 0 : file.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            OSMFile other = (OSMFile) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (file == null) {
                if (other.file != null)
                    return false;
            } else if (!file.equals(other.file))
                return false;
            return true;
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public String getEpsg() {
            return epsg;
        }

        public void setEpsg(String epsg) {
            this.epsg = epsg;
        }

        public boolean isCreateDb() {
            return createDb;
        }

        public void setCreateDb(boolean createDb) {
            this.createDb = createDb;
        }

        public String getTagFilter() {
            return tagFilter;
        }

        public void setTagFilter(String tagFilter) {
            this.tagFilter = tagFilter;
        }

        private OSMPlugin getOuterType() {
            return OSMPlugin.this;
        }

    }

    /**
     * Show the users of the current dataset in a new layer with the centroid as
     * a geometry.
     * 
     * @author GTouya
     * 
     */
    class ShowUsersAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            Collection<OSMFeature> features = new HashSet<>();
            for (Layer layer : application.getMainFrame().getSelectedProjectFrame().getLayers()) {
                for (IFeature obj : layer.getFeatureCollection())
                    features.add((OSMFeature) obj);
            }

            // changing to supertype for findContributors, hackish way
            Collection<OSMDefaultFeature> df = (Collection<OSMDefaultFeature>) (Collection<?>) features;

            // get the contributors from the contributions
            Collection<OSMContributor> contributors = OSMContributor.findContributors(df);
            System.out.println(contributors.size() + " contributors found");

            Collection<DefaultFeature> contributorFeats = new HashSet<>();
            FeatureType ft = new FeatureType();
            ft.setGeometryType(IPoint.class);
            ft.setNomClasse("contributors");
            SchemaDefaultFeature schema = new SchemaDefaultFeature();
            schema.setFeatureType(ft);
            ft.setSchema(schema);
            Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
            schema.setAttLookup(attLookup);
            AttributeType attName = new AttributeType();
            attName.setNomField("name");
            attName.setMemberName("name");
            attName.setValueType("String");
            ft.addFeatureAttribute(attName);
            AttributeType attNb = new AttributeType();
            attNb.setNomField("nbContributions");
            attNb.setMemberName("nbContributions");
            attNb.setValueType("Integer");
            ft.addFeatureAttribute(attNb);
            schema.getAttLookup().put(0, new String[] { "name", "nbContributions" });
            for (OSMContributor contributor : contributors) {
                DefaultFeature feat = new DefaultFeature(contributor.getContributionsCentre());
                feat.setFeatureType(ft);
                feat.setAttributes(new Object[2]);
                feat.setSchema(schema);
                feat.setAttribute(attName, contributor.getName());
                System.out.println(contributor.getName());
                feat.setAttribute(attNb, contributor.getContributions().size());
                System.out.println(contributor.getContributions().size());
                contributorFeats.add(feat);
            }

            ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();
            Layer layer = pFrame.getSld().createLayer("contributors", IPoint.class, Color.RED);
            StyledLayerDescriptor defaultSld;
            try {
                defaultSld = StyledLayerDescriptor.unmarshall(
                        IGeneObj.class.getClassLoader().getResourceAsStream("sld/sld_osm_contributors.xml"));
                layer.getStyles().addAll(defaultSld.getLayer("contributors").getStyles());
            } catch (JAXBException e1) {
                e1.printStackTrace();
            }
            IPopulation<IFeature> pop = new Population<>("contributors");
            pop.setFeatureType(ft);
            pop.addAll(contributorFeats);
            pFrame.getSld().getDataSet().addPopulation(pop);
            pFrame.getSld().add(layer);
        }

        public ShowUsersAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Show OSM users of the dataset with the centroid of their contributions as geometry");
            this.putValue(Action.NAME, "Show OSM users");
        }
    }

    /**
     * Show the contributions of one OSM user in a new layer, with dots centred
     * on each contribution
     * 
     * @author GTouya
     * 
     */
    class ShowUserContribsAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // String contribName = JOptionPane
            // .showInputDialog("Enter the name of the user");
            String contribName = "Balaïtous";
            Collection<OSMFeature> features = new HashSet<>();
            for (Layer layer : application.getMainFrame().getSelectedProjectFrame().getLayers()) {
                for (IFeature obj : layer.getFeatureCollection())
                    features.add((OSMFeature) obj);
            }

            // changing to supertype for findContributors, hackish way
            Collection<OSMDefaultFeature> df = (Collection<OSMDefaultFeature>) (Collection<?>) features;

            // get the contributors from the contributions
            Collection<OSMContributor> contributors = OSMContributor.findContributors(df);
            System.out.println(contributors.size() + " contributors found");
            String layerName = contribName + "_contributions";

            Collection<DefaultFeature> contributorFeats = new HashSet<>();
            FeatureType ft = new FeatureType();
            ft.setGeometryType(IPoint.class);
            ft.setNomClasse(layerName);

            for (OSMContributor contributor : contributors) {
                if (contributor.getName().equals(contribName)) {
                    for (OSMFeature obj : contributor.getContributions()) {
                        DefaultFeature feat = new DefaultFeature(obj.getGeom().centroid().toGM_Point());
                        feat.setFeatureType(ft);
                        contributorFeats.add(feat);
                    }
                }
            }

            ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();
            Layer layer = pFrame.getSld().createLayer(layerName, IPoint.class, Color.RED);
            // create the style of dots
            List<Style> styles = new ArrayList<>();
            FeatureTypeStyle ftStyle = new FeatureTypeStyle();
            ftStyle.setName("dot");
            Rule rule = new Rule();
            PointSymbolizer symbo = new PointSymbolizer();
            symbo.setGeometryPropertyName("geom");
            symbo.setUnitOfMeasure(Symbolizer.PIXEL);
            Graphic graphic = new Graphic();
            Mark mark = new Mark();
            mark.setWellKnownName("circle");
            Fill fill = new Fill();
            fill.setColor(Color.RED);
            mark.setFill(fill);
            Stroke stroke = new Stroke();
            stroke.setColor(Color.RED);
            stroke.setStrokeWidth(0);
            mark.setStroke(stroke);
            graphic.getMarks().add(mark);
            graphic.setSize(graphic.getSize() * 5);
            symbo.setGraphic(graphic);
            rule.getSymbolizers().add(symbo);
            ftStyle.getRules().add(rule);
            Style style = new UserStyle();
            style.addFeatureTypeStyle(ftStyle);
            styles.add(style);
            layer.getStyles().addAll(styles);

            IPopulation<IFeature> pop = new Population<>(layerName);
            pop.setFeatureType(ft);
            pop.addAll(contributorFeats);
            pFrame.getSld().getDataSet().addPopulation(pop);
            pFrame.getSld().add(layer);
        }

        public ShowUserContribsAction() {
            this.putValue(Action.SHORT_DESCRIPTION, "Show one OSM user contributions in a new layer");
            this.putValue(Action.NAME, "Show one user contributions");
        }
    }

    /**
     * Show the contributions of one OSM user in a new layer, with dots centred
     * on each contribution, differentiated into week and weekend contributions
     * 
     * @author GTouya
     * 
     */
    class ShowUserContribsDayAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // String contribName = JOptionPane
            // .showInputDialog("Enter the name of the user");
            String contribName = "Balaïtous";
            Collection<OSMFeature> features = new HashSet<>();
            for (Layer layer : application.getMainFrame().getSelectedProjectFrame().getLayers()) {
                for (IFeature obj : layer.getFeatureCollection())
                    features.add((OSMFeature) obj);
            }

            // changing to supertype for findContributors, hackish way
            Collection<OSMDefaultFeature> df = (Collection<OSMDefaultFeature>) (Collection<?>) features;

            // get the contributors from the contributions
            Collection<OSMContributor> contributors = OSMContributor.findContributors(df);
            System.out.println(contributors.size() + " contributors found");
            String layerName = contribName + "_contributions";
            String layerNameWeek = layerName + "_week";
            String layerNameWeekend = layerName + "_weekend";

            Collection<DefaultFeature> contributorFeatsWeek = new HashSet<>();
            Collection<DefaultFeature> contributorFeatsWeekEnd = new HashSet<>();
            FeatureType ft = new FeatureType();
            ft.setGeometryType(IPoint.class);
            ft.setNomClasse(layerName);

            for (OSMContributor contributor : contributors) {
                if (contributor.getName().equals(contribName)) {
                    for (OSMFeature obj : contributor.getWeekContributions()) {
                        DefaultFeature feat = new DefaultFeature(obj.getGeom().centroid().toGM_Point());
                        feat.setFeatureType(ft);
                        contributorFeatsWeek.add(feat);
                    }
                    for (OSMFeature obj : contributor.getWeekEndContributions()) {
                        DefaultFeature feat = new DefaultFeature(obj.getGeom().centroid().toGM_Point());
                        feat.setFeatureType(ft);
                        contributorFeatsWeekEnd.add(feat);
                    }
                }
            }

            ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();
            Layer layerWeek = pFrame.getSld().createLayer(layerNameWeek, IPoint.class, Color.GREEN);
            Layer layerWeekend = pFrame.getSld().createLayer(layerNameWeekend, IPoint.class, Color.RED);
            // create the style of dots
            List<Style> styles1 = new ArrayList<>();
            FeatureTypeStyle ftStyle1 = new FeatureTypeStyle();
            ftStyle1.setName("dot");
            Rule rule1 = new Rule();
            PointSymbolizer symbo1 = new PointSymbolizer();
            symbo1.setGeometryPropertyName("geom");
            symbo1.setUnitOfMeasure(Symbolizer.PIXEL);
            Graphic graphic1 = new Graphic();
            Mark mark1 = new Mark();
            mark1.setWellKnownName("circle");
            Fill fill1 = new Fill();
            fill1.setColor(Color.GREEN);
            mark1.setFill(fill1);
            Stroke stroke1 = new Stroke();
            stroke1.setColor(Color.RED);
            stroke1.setStrokeWidth(0);
            mark1.setStroke(stroke1);
            graphic1.getMarks().add(mark1);
            graphic1.setSize(graphic1.getSize() * 2);
            symbo1.setGraphic(graphic1);
            rule1.getSymbolizers().add(symbo1);
            ftStyle1.getRules().add(rule1);
            Style style1 = new UserStyle();
            style1.addFeatureTypeStyle(ftStyle1);
            styles1.add(style1);
            layerWeek.getStyles().addAll(styles1);

            List<Style> styles2 = new ArrayList<>();
            FeatureTypeStyle ftStyle2 = new FeatureTypeStyle();
            ftStyle2.setName("dot");
            Rule rule2 = new Rule();
            PointSymbolizer symbo2 = new PointSymbolizer();
            symbo2.setGeometryPropertyName("geom");
            symbo2.setUnitOfMeasure(Symbolizer.PIXEL);
            Graphic graphic2 = new Graphic();
            Mark mark2 = new Mark();
            mark2.setWellKnownName("circle");
            Fill fill2 = new Fill();
            fill2.setColor(Color.RED);
            mark2.setFill(fill2);
            Stroke stroke2 = new Stroke();
            stroke2.setColor(Color.RED);
            stroke2.setStrokeWidth(0);
            mark2.setStroke(stroke2);
            graphic2.getMarks().add(mark2);
            graphic2.setSize(graphic2.getSize() * 2);
            symbo2.setGraphic(graphic2);
            rule2.getSymbolizers().add(symbo2);
            ftStyle2.getRules().add(rule2);
            Style style2 = new UserStyle();
            style2.addFeatureTypeStyle(ftStyle2);
            styles2.add(style2);
            layerWeekend.getStyles().addAll(styles2);

            IPopulation<IFeature> popWeek = new Population<>(layerNameWeek);
            IPopulation<IFeature> popWeekend = new Population<>(layerNameWeekend);
            popWeek.setFeatureType(ft);
            popWeek.addAll(contributorFeatsWeek);
            pFrame.getSld().getDataSet().addPopulation(popWeek);
            pFrame.getSld().add(layerWeek);
            popWeekend.setFeatureType(ft);
            popWeekend.addAll(contributorFeatsWeekEnd);
            pFrame.getSld().getDataSet().addPopulation(popWeekend);
            pFrame.getSld().add(layerWeekend);
        }

        public ShowUserContribsDayAction() {
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Show one OSM user week and weekend contributions in two new layers");
            this.putValue(Action.NAME, "Show one user week and weekend contributions");
        }
    }

    class ATMQualityAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("atms").getFeatureCollection();
            IFeatureCollection<IFeature> atms = new FT_FeatureCollection<>();
            for (IFeature point : points) {
                if (point.getAttribute("type").equals("atm"))
                    atms.add(point);
            }
            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature point : application.getMainFrame().getSelectedProjectFrame().getLayer("paris_buildings")
                    .getFeatureCollection()) {
                buildings.add(point);
            }

            ATMQualityAssessment atmAssessment = new ATMQualityAssessment(atms, buildings);
            atmAssessment.assessIndividualPositions();
            atmAssessment.assessBuildingConsistency();

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("ATM positions", 0);
                WritableSheet sheet2 = workbook.createSheet("Consistency in banks", 1);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "distance to centroid"));
                sheet1.addCell(new Label(2, 0, "distance to edge"));
                int i = 1;
                for (IFeature atm : atmAssessment.getFeatures()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = atmAssessment.getDistToCentroid().get(atm);
                    if (centroidDist == null)
                        sheet1.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = atmAssessment.getDistToEdge().get(atm);
                    if (edgeDist == null)
                        sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(2, i, edgeDist));
                    i++;
                }

                sheet2.addCell(new Label(0, 0, "OSM id of building"));
                sheet2.addCell(new Label(1, 0, "number of ATMs"));
                sheet2.addCell(new Label(2, 0, "distance to centroid mean"));
                sheet2.addCell(new Label(3, 0, "distance to centroid std"));
                sheet2.addCell(new Label(4, 0, "distance to edge mean"));
                sheet2.addCell(new Label(5, 0, "distance to edge std"));
                i = 1;
                for (BankBuilding bank : atmAssessment.getBanks()) {
                    sheet2.addCell(new jxl.write.Number(0, i, (Long) bank.getBuilding().getAttribute("osm_id")));
                    sheet2.addCell(new jxl.write.Number(1, i, bank.getAtms().size()));
                    sheet2.addCell(new jxl.write.Number(2, i, bank.getMeanDistanceCentroid()));
                    sheet2.addCell(new jxl.write.Number(3, i, bank.getStdDistanceCentroid()));
                    sheet2.addCell(new jxl.write.Number(4, i, bank.getMeanDistanceEdge()));
                    sheet2.addCell(new jxl.write.Number(5, i, bank.getStdDistanceEdge()));
                    i++;
                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
        }

        public ATMQualityAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess ATM points quality compared to the related building and store results in Excel file");
            this.putValue(Action.NAME, "Assess ATM points quality");
        }
    }

    class StoresQualityAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("bars_cafes_restos").getFeatureCollection();
            IFeatureCollection<IFeature> cafes = new FT_FeatureCollection<>();
            cafes.addAll(points);
            points = application.getMainFrame().getSelectedProjectFrame().getLayer("cinemas").getFeatureCollection();
            IFeatureCollection<IFeature> cinemas = new FT_FeatureCollection<>();
            cinemas.addAll(points);
            points = application.getMainFrame().getSelectedProjectFrame().getLayer("gifts").getFeatureCollection();
            IFeatureCollection<IFeature> gifts = new FT_FeatureCollection<>();
            gifts.addAll(points);
            points = application.getMainFrame().getSelectedProjectFrame().getLayer("hairdresser")
                    .getFeatureCollection();
            IFeatureCollection<IFeature> hairdressers = new FT_FeatureCollection<>();
            hairdressers.addAll(points);

            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature point : application.getMainFrame().getSelectedProjectFrame().getLayer("paris_buildings")
                    .getFeatureCollection()) {
                buildings.add(point);
            }

            StoreQualityAssessment storeAssessment = new StoreQualityAssessment(cinemas, cafes, gifts, hairdressers,
                    buildings);
            storeAssessment.assessIndividualPositions();

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("cinema positions", 0);
                WritableSheet sheet2 = workbook.createSheet("cafes positions", 1);
                WritableSheet sheet3 = workbook.createSheet("gifts positions", 2);
                WritableSheet sheet4 = workbook.createSheet("hairdresser positions", 2);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "distance to centroid"));
                sheet1.addCell(new Label(2, 0, "distance to edge"));
                int i = 1;
                System.out.println("store cinemas");
                for (IFeature atm : storeAssessment.getCinemas()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = storeAssessment.getDistToCentroid().get(atm);
                    if (centroidDist == null)
                        sheet1.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = storeAssessment.getDistToEdge().get(atm);
                    if (edgeDist == null)
                        sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(2, i, edgeDist));
                    i++;
                }

                sheet2.addCell(new Label(0, 0, "OSM id"));
                sheet2.addCell(new Label(1, 0, "distance to centroid"));
                sheet2.addCell(new Label(2, 0, "distance to edge"));
                i = 1;
                System.out.println("store cafes");
                for (IFeature atm : storeAssessment.getCafes()) {
                    sheet2.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = storeAssessment.getDistToCentroid().get(atm);
                    if (centroidDist == null)
                        sheet2.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet2.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = storeAssessment.getDistToEdge().get(atm);
                    if (edgeDist == null)
                        sheet2.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet2.addCell(new jxl.write.Number(2, i, edgeDist));
                    i++;
                }

                sheet3.addCell(new Label(0, 0, "OSM id"));
                sheet3.addCell(new Label(1, 0, "distance to centroid"));
                sheet3.addCell(new Label(2, 0, "distance to edge"));
                i = 1;
                System.out.println("store gifts");
                for (IFeature atm : storeAssessment.getGifts()) {
                    sheet3.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = storeAssessment.getDistToCentroid().get(atm);
                    if (centroidDist == null)
                        sheet3.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet3.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = storeAssessment.getDistToEdge().get(atm);
                    if (edgeDist == null)
                        sheet3.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet3.addCell(new jxl.write.Number(2, i, edgeDist));
                    i++;
                }

                sheet4.addCell(new Label(0, 0, "OSM id"));
                sheet4.addCell(new Label(1, 0, "distance to centroid"));
                sheet4.addCell(new Label(2, 0, "distance to edge"));
                i = 1;
                System.out.println("store hairdressers");
                for (IFeature atm : storeAssessment.getHairdresser()) {
                    sheet4.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = storeAssessment.getDistToCentroid().get(atm);
                    if (centroidDist == null)
                        sheet4.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet4.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = storeAssessment.getDistToEdge().get(atm);
                    if (edgeDist == null)
                        sheet4.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet4.addCell(new jxl.write.Number(2, i, edgeDist));
                    i++;
                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
        }

        public StoresQualityAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess amenity points quality compared to the related building and store results in Excel file");
            this.putValue(Action.NAME, "Assess amenities points quality");
        }
    }

    class SchoolQualityAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("schools").getFeatureCollection();
            IFeatureCollection<IFeature> schools = new FT_FeatureCollection<>();
            schools.addAll(points);
            points = application.getMainFrame().getSelectedProjectFrame().getLayer("school_areas")
                    .getFeatureCollection();
            IFeatureCollection<IFeature> schoolAreas = new FT_FeatureCollection<>();
            schoolAreas.addAll(points);

            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature point : application.getMainFrame().getSelectedProjectFrame().getLayer("paris_buildings")
                    .getFeatureCollection()) {
                buildings.add(point);
            }

            SchoolQualityAssessment schoolAssessment = new SchoolQualityAssessment(schools, schoolAreas, buildings);
            schoolAssessment.assessIndividualPositions();

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("positions", 0);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "distance to centroid"));
                sheet1.addCell(new Label(2, 0, "distance to edge"));
                sheet1.addCell(new Label(3, 0, "distance to school centroid"));
                sheet1.addCell(new Label(4, 0, "distance to school edge"));
                sheet1.addCell(new Label(5, 0, "school area"));
                sheet1.addCell(new Label(6, 0, "building area"));
                int i = 1;
                for (IFeature atm : schoolAssessment.getSchools()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = schoolAssessment.getDistToCentroid().get(atm);
                    if (centroidDist == null)
                        sheet1.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = schoolAssessment.getDistToEdge().get(atm);
                    if (edgeDist == null)
                        sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(2, i, edgeDist));
                    Double centroidSchoolDist = schoolAssessment.getDistToSchoolCentroid().get(atm);
                    if (centroidSchoolDist == null)
                        sheet1.addCell(new jxl.write.Number(3, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(3, i, centroidSchoolDist));
                    Double edgeSchoolDist = schoolAssessment.getDistToSchoolEdge().get(atm);
                    if (edgeSchoolDist == null)
                        sheet1.addCell(new jxl.write.Number(4, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(4, i, edgeSchoolDist));

                    Double schoolArea = schoolAssessment.getSchoolAreaMap().get(atm);
                    if (schoolArea != null)
                        sheet1.addCell(new jxl.write.Number(5, i, schoolArea));
                    Double buildingArea = schoolAssessment.getBuildingAreaMap().get(atm);
                    if (buildingArea != null)
                        sheet1.addCell(new jxl.write.Number(6, i, buildingArea));
                    i++;
                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }

        }

        public SchoolQualityAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess school points quality compared to the related building/school area and store results in Excel file");
            this.putValue(Action.NAME, "Assess school points quality");
        }
    }

    class BusStopQualityAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("bus_stops").getFeatureCollection();
            IFeatureCollection<IFeature> busStops = new FT_FeatureCollection<>();
            for (IFeature point : points) {
                if (point.getAttribute("type").equals("bus_stop"))
                    busStops.add(point);
            }
            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature point : application.getMainFrame().getSelectedProjectFrame().getLayer("paris_buildings")
                    .getFeatureCollection()) {
                buildings.add(point);
            }
            IFeatureCollection<IFeature> roads = new FT_FeatureCollection<>();
            for (IFeature road : application.getMainFrame().getSelectedProjectFrame().getLayer("roads_lambert93")
                    .getFeatureCollection()) {
                if (road.getAttribute("type").equals("steps"))
                    continue;
                if (road.getAttribute("type").equals("pedestrian"))
                    continue;
                roads.add(road);
            }

            BusStopQualityAssessment atmAssessment = new BusStopQualityAssessment(busStops, roads, buildings);
            atmAssessment.assessIndividualPositions();

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("bus stop positions", 0);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "distance to road"));
                sheet1.addCell(new Label(2, 0, "distance to building"));
                int i = 1;
                for (IFeature atm : atmAssessment.getBusStops()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long((String) atm.getAttribute("osm_id"))));
                    Double centroidDist = atmAssessment.getDistToRoads().get(atm);
                    if (centroidDist == null)
                        sheet1.addCell(new jxl.write.Number(1, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(1, i, centroidDist));
                    Double edgeDist = atmAssessment.getDistToBuildings().get(atm);
                    if (edgeDist == null)
                        sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                    else
                        sheet1.addCell(new jxl.write.Number(2, i, edgeDist));
                    i++;
                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }

        }

        public BusStopQualityAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess bus stop points quality compared to the nearest buildings & roads and store results in Excel file");
            this.putValue(Action.NAME, "Assess bus stop points quality");
        }
    }

    class MultiPointQualityAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("points_paris").getFeatureCollection();
            IFeatureCollection<IFeature> pois = new FT_FeatureCollection<>();
            pois.addAll(points);

            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature point : application.getMainFrame().getSelectedProjectFrame().getLayer("paris_buildings")
                    .getFeatureCollection()) {
                buildings.add(point);
            }

            BuildingConsistencyQualityAssessment atmAssessment = new BuildingConsistencyQualityAssessment(pois,
                    buildings);
            atmAssessment.assessBuildingConsistency();

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("building consistency", 0);

                sheet1.addCell(new Label(0, 0, "OSM id of building"));
                sheet1.addCell(new Label(1, 0, "number of ATMs"));
                sheet1.addCell(new Label(2, 0, "distance to centroid mean"));
                sheet1.addCell(new Label(3, 0, "distance to centroid std"));
                sheet1.addCell(new Label(4, 0, "distance to edge mean"));
                sheet1.addCell(new Label(5, 0, "distance to edge std"));
                sheet1.addCell(new Label(6, 0, "distance to centroid min"));
                sheet1.addCell(new Label(7, 0, "distance to edge min"));
                sheet1.addCell(new Label(8, 0, "distance to centroid max"));
                sheet1.addCell(new Label(9, 0, "distance to edge max"));
                int i = 1;
                for (POIBuilding bank : atmAssessment.getPoiBuildings()) {
                    sheet1.addCell(new jxl.write.Number(0, i, (Long) bank.getBuilding().getAttribute("osm_id")));
                    sheet1.addCell(new jxl.write.Number(1, i, bank.getPois().size()));
                    sheet1.addCell(new jxl.write.Number(2, i, bank.getMeanDistanceCentroid()));
                    sheet1.addCell(new jxl.write.Number(3, i, bank.getStdDistanceCentroid()));
                    sheet1.addCell(new jxl.write.Number(4, i, bank.getMeanDistanceEdge()));
                    sheet1.addCell(new jxl.write.Number(5, i, bank.getStdDistanceEdge()));
                    sheet1.addCell(new jxl.write.Number(6, i, bank.getMinDistanceCentroid()));
                    sheet1.addCell(new jxl.write.Number(7, i, bank.getMinDistanceEdge()));
                    sheet1.addCell(new jxl.write.Number(8, i, bank.getMaxDistanceCentroid()));
                    sheet1.addCell(new jxl.write.Number(9, i, bank.getMaxDistanceEdge()));
                    i++;
                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }
        }

        public MultiPointQualityAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess buildings that containing multiple points quality and store results in Excel file");
            this.putValue(Action.NAME, "Assess multiple points in a same building consistency");
        }
    }

    class BusStopHistRelAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();

            Map<String, List<IFeature>> busStops = new HashMap<>();
            Set<IFeature> remainingFeats = new HashSet<>();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("points_history_lambert93").getFeatureCollection();
            for (IFeature point : points) {
                if (busStops.keySet().contains(point.getAttribute("osm_id"))) {
                    String id = (String) point.getAttribute("osm_id");
                    List<IFeature> list = busStops.get(id);
                    list.add(point);
                } else if (point.getAttribute("type").equals("bus_stop")) {
                    List<IFeature> list = new ArrayList<>();
                    list.add(point);
                    busStops.put((String) point.getAttribute("osm_id"), list);
                } else
                    remainingFeats.add(point);
            }
            for (IFeature point : remainingFeats) {
                if (busStops.keySet().contains(point.getAttribute("osm_id"))) {
                    String id = (String) point.getAttribute("osm_id");
                    List<IFeature> list = busStops.get(id);
                    list.add(point);
                }
            }

            IFeatureCollection<IFeature> buildings = new FT_FeatureCollection<>();
            for (IFeature point : application.getMainFrame().getSelectedProjectFrame().getLayer("paris_buildings")
                    .getFeatureCollection()) {
                buildings.add(point);
            }
            IFeatureCollection<IFeature> roads = new FT_FeatureCollection<>();
            for (IFeature road : application.getMainFrame().getSelectedProjectFrame().getLayer("main_roads_lambert93")
                    .getFeatureCollection()) {
                if (road.getAttribute("type").equals("steps"))
                    continue;
                if (road.getAttribute("type").equals("pedestrian"))
                    continue;
                roads.add(road);
            }

            BusStopHistRelAssessment assessment = new BusStopHistRelAssessment(busStops, roads, buildings);
            assessment.assessIndividualPositions();

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("bus stop positions", 0);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "OSM version"));
                sheet1.addCell(new Label(2, 0, "distance to road"));
                sheet1.addCell(new Label(3, 0, "distance to building"));
                int i = 1;
                for (String id : assessment.getBusStops().keySet()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long(id)));
                    Map<Long, Double> mapDistToRoads = assessment.getDistToRoads().get(id);
                    Map<Long, Double> mapDistToBuildings = assessment.getDistToBuildings().get(id);
                    for (Long version : mapDistToRoads.keySet()) {
                        sheet1.addCell(new jxl.write.Number(0, i, new Long(id)));
                        sheet1.addCell(new jxl.write.Number(1, i, version));
                        Double roadDist = mapDistToRoads.get(version);
                        if (roadDist == null)
                            sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                        else
                            sheet1.addCell(new jxl.write.Number(2, i, roadDist));
                        Double buildDist = mapDistToBuildings.get(version);
                        if (buildDist == null)
                            sheet1.addCell(new jxl.write.Number(3, i, -1.0));
                        else
                            sheet1.addCell(new jxl.write.Number(3, i, buildDist));
                        i++;
                    }

                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }

        }

        public BusStopHistRelAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess bus stop points historical quality compared to the nearest buildings & roads and store results in Excel file");
            this.putValue(Action.NAME, "Assess bus stop points historical quality");
        }
    }

    class StationsHistRelAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();

            Map<String, List<IFeature>> stations = new HashMap<>();
            for (IFeature station : application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("points_history_lambert93").getFeatureCollection()) {
                List<IFeature> list = new ArrayList<>();
                stations.put((String) station.getAttribute("osm_id"), list);
            }

            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("points_history_lambert93").getFeatureCollection();
            for (IFeature point : points) {
                if (stations.keySet().contains(point.getAttribute("osm_id"))) {
                    String id = (String) point.getAttribute("osm_id");
                    List<IFeature> list = stations.get(id);
                    list.add(point);
                }
            }

            IFeatureCollection<IFeature> roads = new FT_FeatureCollection<>();
            for (IFeature road : application.getMainFrame().getSelectedProjectFrame().getLayer("subway_l93")
                    .getFeatureCollection()) {
                roads.add(road);
            }

            StationQualityAssessment assessment = new StationQualityAssessment(new FT_FeatureCollection<IFeature>(),
                    roads);
            assessment.assessIndividualPositionsWithHistory(stations);

            // an Excel sheet with the standard comparison table
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("stations positions", 0);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "OSM version"));
                sheet1.addCell(new Label(2, 0, "nb of subway lines"));
                int i = 1;
                for (String id : stations.keySet()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long(id)));
                    Map<Long, Integer> mapDistToRoads = assessment.getNbLinesHist().get(id);
                    for (Long version : mapDistToRoads.keySet()) {
                        sheet1.addCell(new jxl.write.Number(0, i, new Long(id)));
                        sheet1.addCell(new jxl.write.Number(1, i, version));
                        Integer nbSub = mapDistToRoads.get(version);
                        if (nbSub == null)
                            sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                        else
                            sheet1.addCell(new jxl.write.Number(2, i, nbSub));
                        i++;
                    }

                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }

        }

        public StationsHistRelAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess station points historical quality in relation to subway lines and store results in Excel file");
            this.putValue(Action.NAME, "Assess station points historical quality");
        }
    }

    class MotorJuncHistRelAction extends AbstractAction {

        /****/
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {

            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showSaveDialog(null);
            if (returnVal != JFileChooser.APPROVE_OPTION) {
                return;
            }
            File file = fc.getSelectedFile();

            Map<String, List<IFeature>> junctions = new HashMap<>();
            Set<IFeature> remainingFeats = new HashSet<>();
            IFeatureCollection<? extends IFeature> points = application.getMainFrame().getSelectedProjectFrame()
                    .getLayer("points_history_lambert93").getFeatureCollection();
            for (IFeature point : points) {
                if (junctions.keySet().contains(point.getAttribute("osm_id"))) {
                    String id = (String) point.getAttribute("osm_id");
                    List<IFeature> list = junctions.get(id);
                    list.add(point);
                } else if (point.getAttribute("type").equals("bus_stop")) {
                    List<IFeature> list = new ArrayList<>();
                    list.add(point);
                    junctions.put((String) point.getAttribute("osm_id"), list);
                } else
                    remainingFeats.add(point);
            }
            for (IFeature point : remainingFeats) {
                if (junctions.keySet().contains(point.getAttribute("osm_id"))) {
                    String id = (String) point.getAttribute("osm_id");
                    List<IFeature> list = junctions.get(id);
                    list.add(point);
                }
            }

            IFeatureCollection<IFeature> roads = new FT_FeatureCollection<>();
            for (IFeature road : application.getMainFrame().getSelectedProjectFrame().getLayer("roads_lambert93")
                    .getFeatureCollection()) {
                if (road.getAttribute("type").equals("steps"))
                    continue;
                if (road.getAttribute("type").equals("pedestrian"))
                    continue;
                roads.add(road);
            }

            System.out.println("start position assessment");
            System.out.println(junctions.size() + " features to assess");
            MotorJuncQualityAssessment assessment = new MotorJuncQualityAssessment(new FT_FeatureCollection<>(), roads);
            assessment.assessIndividualPositionsWithHistory(junctions);

            // an Excel sheet with the standard comparison table
            System.out.println("write the assessment");
            WritableWorkbook workbook;
            try {
                workbook = Workbook.createWorkbook(file);

                WritableSheet sheet1 = workbook.createSheet("bus stop positions", 0);

                sheet1.addCell(new Label(0, 0, "OSM id"));
                sheet1.addCell(new Label(1, 0, "OSM version"));
                sheet1.addCell(new Label(2, 0, "nb of motorways connected"));
                sheet1.addCell(new Label(3, 0, "nb of roads connected"));
                int i = 1;
                for (String id : junctions.keySet()) {
                    sheet1.addCell(new jxl.write.Number(0, i, new Long(id)));
                    Map<Long, Integer> mapNbMotor = assessment.getNbMotorwaysHist().get(id);
                    Map<Long, Integer> mapNbRoads = assessment.getNbRoadsHist().get(id);
                    for (Long version : mapNbMotor.keySet()) {
                        sheet1.addCell(new jxl.write.Number(0, i, new Long(id)));
                        sheet1.addCell(new jxl.write.Number(1, i, version));
                        Integer nbMotors = mapNbMotor.get(version);
                        if (nbMotors == null)
                            sheet1.addCell(new jxl.write.Number(2, i, -1.0));
                        else
                            sheet1.addCell(new jxl.write.Number(2, i, nbMotors));
                        Integer nbRoads = mapNbRoads.get(version);
                        if (nbRoads == null)
                            sheet1.addCell(new jxl.write.Number(3, i, -1.0));
                        else
                            sheet1.addCell(new jxl.write.Number(3, i, nbRoads));
                        i++;
                    }
                }

                workbook.write();
                workbook.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (WriteException e1) {
                e1.printStackTrace();
            }

        }

        public MotorJuncHistRelAction() {
            super();
            this.putValue(Action.SHORT_DESCRIPTION,
                    "Assess motorway junctions points historical quality in relation to roads and store results in Excel file");
            this.putValue(Action.NAME, "Assess motorway junctions points historical quality");
        }
    }
}
