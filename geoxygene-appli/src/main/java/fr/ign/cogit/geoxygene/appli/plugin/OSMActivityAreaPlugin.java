package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.datatools.CRSConversion;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.osm.contributor.ActivityArea;
import fr.ign.cogit.geoxygene.osm.importexport.OSMNode;
import fr.ign.cogit.geoxygene.osm.importexport.OSMResource;
import fr.ign.cogit.geoxygene.osm.schema.OSMDefaultFeature;
import fr.ign.cogit.geoxygene.osm.schema.OSMFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.GraphicFill;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class OSMActivityAreaPlugin implements ProjectFramePlugin, GeOxygeneApplicationPlugin, ActionListener {
	private GeOxygeneApplication application = null;
	private final static String CONTRIBUTOR_NODE_LAYER = "Contributor Nodes";
	private final static String CONTRIBUTOR_AREA_LAYER = "Contributor Activity Areas";

	@Override
	public void initialize(GeOxygeneApplication application) {
		this.application = application;
		JMenu menu = new JMenu("OSM Activity Area");
		JMenu activityByContributorMenu = new JMenu("By contributor");
		activityByContributorMenu.add(new JMenuItem(new LoadContributorAreaAction()));
		menu.add(activityByContributorMenu);
		application.getMainFrame().getMenuBar().add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);

	}

	@Override
	public void initialize(ProjectFrame projectFrame) {
		// TODO Auto-generated method stub

	}

	/**
	 * Load contributor nodes in the selected extent, and creates a new layer
	 * with the features.
	 * 
	 * @author QTTruong
	 * 
	 */
	class LoadContributorAreaAction extends AbstractAction {

		/**
		* 
		*/
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			LoadContributorAreaActionFrame frame = new LoadContributorAreaActionFrame();
			frame.setVisible(true);

		}

		public LoadContributorAreaAction() {
			this.putValue(Action.SHORT_DESCRIPTION, "Load OSM contributor nodes and add as a new layer");
			this.putValue(Action.NAME, "Load OSM contributor nodes ");
		}
	}

	class LoadContributorAreaActionFrame extends JFrame implements ActionListener, ChangeListener {

		/**
		 * Load contributor nodes in the selected extent, and creates a new
		 * layer with the features.
		 * 
		 * @author QTTruong
		 * 
		 */
		class LoadContributorAreaAction extends AbstractAction {

			/**
			* 
			*/
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				LoadContributorAreaActionFrame frame = new LoadContributorAreaActionFrame();
				frame.setVisible(true);

			}

			public LoadContributorAreaAction() {
				this.putValue(Action.SHORT_DESCRIPTION, "Load OSM contributor nodes and add as a new layer");
				this.putValue(Action.NAME, "Load OSM contributor nodes ");
			}
		}

		/****/
		private static final long serialVersionUID = 1L;
		private JTextField txtHost, txtPort, txtDb, txtUser, txtPwd, txtSchema, txtMinTemp, txtMaxTemp, txtMinLon,
				txtMinLat, txtMaxLon, txtMaxLat, txtUid;
		private JComboBox<String> comboLayers;

		private JSpinner spinLongmin, spinLongmax, spinLatmin, spinLatmax, spinAccuracy;
		private JCheckBox checkBox;

		LoadContributorAreaActionFrame() throws HeadlessException {
			super("Load a contributor OSM data from PostGIS DB");
			// define a panel with the connection information
			JPanel connectionPanel = new JPanel();
			// hôte
			JPanel hostPanel = new JPanel();
			txtHost = new JTextField("localhost");
			txtHost.setPreferredSize(new Dimension(100, 20));
			txtHost.setMinimumSize(new Dimension(100, 20));
			txtHost.setMaximumSize(new Dimension(100, 20));
			hostPanel.add(new JLabel("host : "));
			hostPanel.add(txtHost);
			hostPanel.setLayout(new BoxLayout(hostPanel, BoxLayout.X_AXIS));
			hostPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			// port
			JPanel portPanel = new JPanel();
			txtPort = new JTextField("5432");
			txtPort.setPreferredSize(new Dimension(80, 20));
			txtPort.setMinimumSize(new Dimension(80, 20));
			txtPort.setMaximumSize(new Dimension(80, 20));
			portPanel.add(new JLabel("port : "));
			portPanel.add(txtPort);
			portPanel.setLayout(new BoxLayout(portPanel, BoxLayout.X_AXIS));
			portPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			// database
			JPanel dbPanel = new JPanel();
			txtDb = new JTextField("paris");
			txtDb.setPreferredSize(new Dimension(120, 20));
			txtDb.setMinimumSize(new Dimension(120, 20));
			txtDb.setMaximumSize(new Dimension(120, 20));
			dbPanel.add(new JLabel("database name : "));
			dbPanel.add(txtDb);
			dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));
			dbPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			JPanel schemaPanel = new JPanel();
			txtSchema = new JTextField("public");
			txtSchema.setPreferredSize(new Dimension(120, 20));
			txtSchema.setMinimumSize(new Dimension(120, 20));
			txtSchema.setMaximumSize(new Dimension(120, 20));
			schemaPanel.add(new JLabel("schema name : "));
			schemaPanel.add(txtSchema);
			schemaPanel.setLayout(new BoxLayout(schemaPanel, BoxLayout.X_AXIS));
			schemaPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			// user
			JPanel userPanel = new JPanel();
			txtUser = new JTextField("postgres");
			txtUser.setPreferredSize(new Dimension(100, 20));
			txtUser.setMinimumSize(new Dimension(100, 20));
			txtUser.setMaximumSize(new Dimension(100, 20));
			userPanel.add(new JLabel("user : "));
			userPanel.add(txtUser);
			userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.X_AXIS));
			userPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
			// password
			JPanel pwdPanel = new JPanel();
			txtPwd = new JTextField("postgres");
			txtPwd.setPreferredSize(new Dimension(100, 20));
			txtPwd.setMinimumSize(new Dimension(100, 20));
			txtPwd.setMaximumSize(new Dimension(100, 20));
			pwdPanel.add(new JLabel("password : "));
			pwdPanel.add(txtPwd);
			pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.X_AXIS));
			pwdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			// SpatioTemporal window
			JPanel minTempPanel = new JPanel();
			txtMinTemp = new JTextField("2010-01-01");
			txtMinTemp.setPreferredSize(new Dimension(100, 20));
			txtMinTemp.setMinimumSize(new Dimension(100, 20));
			txtMinTemp.setMaximumSize(new Dimension(100, 20));
			minTempPanel.add(new JLabel("Date min : "));
			minTempPanel.add(txtMinTemp);
			minTempPanel.setLayout(new BoxLayout(minTempPanel, BoxLayout.X_AXIS));
			minTempPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JPanel maxTempPanel = new JPanel();
			txtMaxTemp = new JTextField("2015-01-01");
			txtMaxTemp.setPreferredSize(new Dimension(100, 20));
			txtMaxTemp.setMinimumSize(new Dimension(100, 20));
			txtMaxTemp.setMaximumSize(new Dimension(100, 20));
			maxTempPanel.add(new JLabel("Date max : "));
			maxTempPanel.add(txtMaxTemp);
			maxTempPanel.setLayout(new BoxLayout(maxTempPanel, BoxLayout.X_AXIS));
			maxTempPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JPanel minLonPanel = new JPanel();
			txtMinLon = new JTextField("2.3250");
			txtMinLon.setPreferredSize(new Dimension(100, 20));
			txtMinLon.setMinimumSize(new Dimension(100, 20));
			txtMinLon.setMaximumSize(new Dimension(100, 20));
			minLonPanel.add(new JLabel("Longitude Min (XMIN) : "));
			minLonPanel.add(txtMinLon);
			minLonPanel.setLayout(new BoxLayout(minLonPanel, BoxLayout.X_AXIS));
			minLonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JPanel minLatPanel = new JPanel();
			txtMinLat = new JTextField("48.8350");
			txtMinLat.setPreferredSize(new Dimension(100, 20));
			txtMinLat.setMinimumSize(new Dimension(100, 20));
			txtMinLat.setMaximumSize(new Dimension(100, 20));
			minLatPanel.add(new JLabel("Latitude Min (YMIN) : "));
			minLatPanel.add(txtMinLat);
			minLatPanel.setLayout(new BoxLayout(minLatPanel, BoxLayout.X_AXIS));
			minLatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JPanel maxLonPanel = new JPanel();
			txtMaxLon = new JTextField("2.3700");
			txtMaxLon.setPreferredSize(new Dimension(100, 20));
			txtMaxLon.setMinimumSize(new Dimension(100, 20));
			txtMaxLon.setMaximumSize(new Dimension(100, 20));
			maxLonPanel.add(new JLabel("Longitude Max (XMAX) : "));
			maxLonPanel.add(txtMaxLon);
			maxLonPanel.setLayout(new BoxLayout(maxLonPanel, BoxLayout.X_AXIS));
			maxLonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			JPanel maxLatPanel = new JPanel();
			txtMaxLat = new JTextField("48.8800");
			txtMaxLat.setPreferredSize(new Dimension(100, 20));
			txtMaxLat.setMinimumSize(new Dimension(100, 20));
			txtMaxLat.setMaximumSize(new Dimension(100, 20));
			maxLatPanel.add(new JLabel("Latitude Max (YMAX) : "));
			maxLatPanel.add(txtMaxLat);
			maxLatPanel.setLayout(new BoxLayout(maxLatPanel, BoxLayout.X_AXIS));
			maxLatPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			// SpatioTemporal window
			JPanel uidPanel = new JPanel();
			txtUid = new JTextField("1556219");
			txtUid.setPreferredSize(new Dimension(100, 20));
			txtUid.setMinimumSize(new Dimension(100, 20));
			txtUid.setMaximumSize(new Dimension(100, 20));
			uidPanel.add(new JLabel("UserID : "));
			uidPanel.add(txtUid);
			uidPanel.setLayout(new BoxLayout(uidPanel, BoxLayout.X_AXIS));
			uidPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			connectionPanel.add(hostPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(portPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(dbPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(schemaPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(userPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(pwdPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(minTempPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(maxTempPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(minLonPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(minLatPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(maxLonPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(maxLatPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.add(uidPanel);
			connectionPanel.add(Box.createVerticalGlue());
			connectionPanel.setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));

			// define a panel with the OK and Cancel buttons
			JPanel btnPanel = new JPanel();
			DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
			for (Layer layer : application.getMainFrame().getSelectedProjectFrame().getLayers())
				comboModel.addElement(layer.getName());
			this.comboLayers = new JComboBox<>(comboModel);
			comboLayers.setPreferredSize(new Dimension(100, 20));
			comboLayers.setMinimumSize(new Dimension(100, 20));
			comboLayers.setMaximumSize(new Dimension(100, 20));
			JButton okBtn = new JButton("OK");
			okBtn.addActionListener(this);
			okBtn.setActionCommand("OK");
			JButton cancelBtn = new JButton("Cancel");
			cancelBtn.addActionListener(this);
			cancelBtn.setActionCommand("cancel");
			btnPanel.add(comboLayers);
			btnPanel.add(okBtn);
			btnPanel.add(cancelBtn);
			btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));

			this.getContentPane().add(connectionPanel);
			this.getContentPane().add(Box.createVerticalGlue());
			this.getContentPane().add(btnPanel);
			this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
			this.setAlwaysOnTop(true);
			this.pack();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("OK")) {
				System.out.println("Clic OK");
				loadData();
				this.dispose();
			} else if (e.getActionCommand().equals("cancel")) {
				this.dispose();
			}
		}

		@Override
		public void stateChanged(ChangeEvent arg0) {
			if (checkBox.isSelected()) {
				spinLongmax.setEnabled(true);
				spinLongmin.setEnabled(true);
				spinLatmax.setEnabled(true);
				spinLatmin.setEnabled(true);
				spinAccuracy.setEnabled(true);
			} else {
				spinLongmax.setEnabled(false);
				spinLongmin.setEnabled(false);
				spinLatmax.setEnabled(false);
				spinLatmin.setEnabled(false);
				spinAccuracy.setEnabled(false);
			}
		}

		private void loadData() {
			ActivityArea.host = txtHost.getText();
			ActivityArea.port = txtPort.getText();
			ActivityArea.dbName = txtDb.getText();
			ActivityArea.dbUser = txtUser.getText();
			ActivityArea.dbPwd = txtPwd.getText();
			List<Double> bbox = new ArrayList<Double>();
			bbox.add(Double.valueOf(txtMinLon.getText()));
			bbox.add(Double.valueOf(txtMinLat.getText()));
			bbox.add(Double.valueOf(txtMaxLon.getText()));
			bbox.add(Double.valueOf(txtMaxLat.getText()));

			List<String> timespan = new ArrayList<String>();
			timespan.add(txtMinTemp.getText());
			timespan.add(txtMaxTemp.getText());
			List<OSMResource> osmContributorResource = null;
			// Loads contributor's node contributions
			try {
				Double[] bboxArray = new Double[bbox.size()];
				bboxArray = bbox.toArray(bboxArray);
				String[] timespanArray = new String[timespan.size()];
				timespanArray = timespan.toArray(timespanArray);
				osmContributorResource = ActivityArea.selectNodesByUid(Long.parseLong(txtUid.getText()), bboxArray,
						timespanArray);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Loads contributor's activity area
			IGeometry actArea = null;
			try {
				actArea = ActivityArea.getActivityAreas(osmContributorResource, 1000);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// create the feature collection from OSM nodes
			IFeatureCollection<OSMFeature> points = new FT_FeatureCollection<OSMFeature>();
			FeatureType ft = new FeatureType();
			ft.setGeometryType(IPoint.class);
			points.setFeatureType(ft);
			for (OSMResource resource : osmContributorResource) {
				double latitude = ((OSMNode) resource.getGeom()).getLatitude();
				double longitude = ((OSMNode) resource.getGeom()).getLongitude();

				IPoint ipoint = new GM_Point(CRSConversion.wgs84ToLambert93(latitude, longitude));

				points.add(new OSMDefaultFeature(resource.getContributeur(), ipoint, (int) resource.getId(),
						resource.getChangeSet(), resource.getVersion(), resource.getUid(), resource.getDate(),
						resource.getTags()));
			}
			// put the nodes in a new layer
			ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();
			Layer layer = pFrame.getSld().createLayer(CONTRIBUTOR_NODE_LAYER, IPoint.class, Color.RED);

			// create the feature collection from activity areas
			IFeatureCollection<DefaultFeature> area = new FT_FeatureCollection<DefaultFeature>();
			FeatureType ftArea = new FeatureType();
			ftArea.setGeometryType(IPolygon.class);
			area.setFeatureType(ftArea);

			if (actArea instanceof IPolygon)
				area.add(new DefaultFeature(actArea));
			else if (actArea instanceof IMultiSurface<?>) {
				for (IPolygon simple : ((IMultiSurface<IPolygon>) actArea)) {
					if (simple == null)
						continue;
					area.add(new DefaultFeature(simple));
				}
			}

			// put the activity areas in a new layer
			Layer layerArea = pFrame.getSld().createLayer(CONTRIBUTOR_AREA_LAYER, GM_Polygon.class,
					new Color(0.5f, 1.f, 0.5f), Color.green, 0.5f, 4);
			PolygonSymbolizer symbolizerArea = (PolygonSymbolizer) layerArea.getSymbolizer();
			GraphicFill graphicFill = new GraphicFill();

			Graphic graphic = new Graphic();
			graphic.setSize(8f);
			// Mark markStar = new Mark();
			// markStar.setWellKnownName("star"); //$NON-NLS-1$
			// Fill fillStar = new Fill();
			// fillStar.setColor(Color.YELLOW);
			// markStar.setFill(fillStar);
			// graphic.getMarks().add(markStar);

			graphicFill.getGraphics().add(graphic);
			symbolizerArea.getFill().setColor(Color.GREEN);
			symbolizerArea.getFill().setGraphicFill(graphicFill);

			// create the layer style
			Style rawStyle = new UserStyle();
			FeatureTypeStyle ftStyle = new FeatureTypeStyle();
			rawStyle.getFeatureTypeStyles().add(ftStyle);
			Rule rule = new Rule();
			ftStyle.getRules().add(rule);
			Color color = Color.RED;
			PointSymbolizer symbolizer = new PointSymbolizer();
			symbolizer.setGeometryPropertyName("geom");
			symbolizer.setUnitOfMeasure(Symbolizer.PIXEL);
			graphic = new Graphic();
			Mark mark = new Mark();
			mark.setWellKnownName("circle");
			Fill fill = new Fill();
			fill.setColor(color);
			mark.setFill(fill);
			graphic.getMarks().add(mark);
			symbolizer.setGraphic(graphic);
			rule.getSymbolizers().add(symbolizer);
			layer.getStyles().add(rawStyle);

			IPopulation<IFeature> pop = new Population<>(CONTRIBUTOR_NODE_LAYER);
			pop.addAll(points);
			pop.setFeatureType(ft);
			pFrame.getSld().getDataSet().addPopulation(pop);
			System.out.println("Nb point pop = " + pop.size());
			pFrame.getSld().add(layer);

			IPopulation<IFeature> popArea = new Population<>(CONTRIBUTOR_AREA_LAYER);
			popArea.addAll(area);
			popArea.setFeatureType(ftArea);
			pFrame.getSld().getDataSet().addPopulation(popArea);
			System.out.println("Nb area pop = " + popArea.size());
			pFrame.getSld().add(layerArea);

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}
