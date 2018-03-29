/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.time.DateUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.xml.sax.SAXException;

import com.flickr4java.flickr.FlickrException;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Fill;
import fr.ign.cogit.geoxygene.style.Graphic;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.UserStyle;
import fr.ign.cogit.geoxygene.vgi.flickr.FlickRFeature;
import fr.ign.cogit.geoxygene.vgi.flickr.FlickRLoader;
import fr.ign.cogit.geoxygene.vgi.flickr.FlickRXmlLoader;
import fr.ign.cogit.geoxygene.vgi.foursquare.FoursquareFeature;
import fr.ign.cogit.geoxygene.vgi.foursquare.FoursquareLoader;
import fr.ign.cogit.geoxygene.vgi.panoramio.PanoramioFeature;
import fr.ign.cogit.geoxygene.vgi.panoramio.PanoramioLoader;
import fr.ign.cogit.geoxygene.vgi.twitter.TwitterFeature;
import fr.ign.cogit.geoxygene.vgi.twitter.TwitterLoader;
import twitter4j.TwitterException;

public class VGIPlugin
    implements ProjectFramePlugin, GeOxygeneApplicationPlugin {

  private GeOxygeneApplication application = null;
  private JCheckBoxMenuItem showPhotos, showPanoPhotos;
  private final static String FLICKR_LAYER = "FlickR photos";
  private final static String PANORAMIO_LAYER = "Panoramio photos";
  private final static String TWITTER_LAYER = "Tweets";
  private final static String FOURSQUARE_LAYER = "Foursquare venues";
  private static String FLICKR_API_KEY;
  private static String FLICKR_API_SECRET;
  private static String TWITTER_API_KEY, TWITTER_API_SECRET,
      TWITTER_ACCESS_TOKEN, TWITTER_TOKEN_SECRET, FOURSQUARE_CLIENT_ID,
      FOURSQUARE_SECRET;
  private static String proxyHost, proxyPort;

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu("VGI");
    JMenu flickrMenu = new JMenu("FlickR");
    flickrMenu.add(new JMenuItem(new LoadFlickRAction()));
    flickrMenu.add(new JMenuItem(new LoadFlickRFilesAction()));
    showPhotos = new JCheckBoxMenuItem(new ShowFlickRAction());
    flickrMenu.add(showPhotos);
    flickrMenu.add(new JMenuItem(new ExportFlickRAction()));
    JMenu panoramioMenu = new JMenu("Panoramio");
    panoramioMenu.add(new JMenuItem(new LoadPanoramioAction()));
    showPanoPhotos = new JCheckBoxMenuItem(new ShowPanoramioAction());
    panoramioMenu.add(showPanoPhotos);
    panoramioMenu.add(new JMenuItem(new ExportFlickRAction()));
    JMenu twitterMenu = new JMenu("Twitter");
    twitterMenu.add(new JMenuItem(new LoadTwitterAction()));
    twitterMenu.add(new JMenuItem(new ExportTwitterAction()));
    JMenu foursquareMenu = new JMenu("Foursquare");
    foursquareMenu.add(new JMenuItem(new LoadFoursquareAction()));
    menu.add(flickrMenu);
    menu.add(panoramioMenu);
    menu.add(twitterMenu);
    menu.add(foursquareMenu);
    application.getMainFrame().getMenuBar().add(menu,
        application.getMainFrame().getMenuBar().getMenuCount() - 2);
    loadApiKeys();
  }

  /**
   * Loads the API keys and secrets in a file in src/main/resources named
   * vgikeys.properties. If the file does not exist or is not fully filled, the
   * keys are left empty.
   */
  private void loadApiKeys() {
    Properties properties = new Properties();
    try {
      properties
          .load(new FileInputStream("src/main/resources/vgikeys.properties"));
      FLICKR_API_KEY = properties.getProperty("FLICKR_API_KEY");
      FLICKR_API_SECRET = properties.getProperty("FLICKR_API_SECRET");
      TWITTER_API_KEY = properties.getProperty("TWITTER_API_KEY");
      TWITTER_API_SECRET = properties.getProperty("TWITTER_API_SECRET");
      TWITTER_ACCESS_TOKEN = properties.getProperty("TWITTER_ACCESS_TOKEN");
      TWITTER_TOKEN_SECRET = properties.getProperty("TWITTER_TOKEN_SECRET");
      FOURSQUARE_CLIENT_ID = properties.getProperty("FOURSQUARE_CLIENT_ID");
      FOURSQUARE_SECRET = properties.getProperty("FOURSQUARE_SECRET");
      proxyHost = properties.getProperty("proxyHost");
      proxyPort = properties.getProperty("proxyPort");
    } catch (IOException e) {
      // Do nothing
    }

  }

  @Override
  public void initialize(ProjectFrame projectFrame) {
    // TODO Auto-generated method stub

  }

  /**
   * Load FlickR photos in the selected extent, and creates a new layer with the
   * features.
   * 
   * @author GTouya
   * 
   */
  class LoadFlickRAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LoadFlickRFrame frame = new LoadFlickRFrame();
      frame.setVisible(true);

    }

    public LoadFlickRAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load FlickR data and add as a new layer");
      this.putValue(Action.NAME, "Load FlickR data");
    }
  }

  /**
   * Load FlickR photos from xml files.
   * 
   * @author GTouya
   * 
   */
  class LoadFlickRFilesAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      JFileChooser chooser = new JFileChooser();
      chooser.setMultiSelectionEnabled(true);
      int returnVal = chooser.showOpenDialog(null);

      if (returnVal == JFileChooser.APPROVE_OPTION) {

        File[] files = chooser.getSelectedFiles();
        FlickRXmlLoader loader = new FlickRXmlLoader(FLICKR_API_KEY,
            FLICKR_API_SECRET, proxyHost, new Integer(proxyPort));
        List<FlickRFeature> features;
        try {
          features = loader.getPhotosFromXmlFiles(files);

          // create the road feature collection from the selected features
          IFeatureCollection<FlickRFeature> photos = new FT_FeatureCollection<>();
          FeatureType ft = new FeatureType();
          ft.setGeometryType(IPoint.class);
          photos.setFeatureType(ft);
          photos.addAll(features);

          // put photos in a new layer
          putPhotosInLayer(photos);
          System.out.println(photos.size() + " loaded photos");
        } catch (SAXException | IOException | ParserConfigurationException e) {
          e.printStackTrace();
        }
      }
    }

    public LoadFlickRFilesAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load FlickR data from XML files and add as a new layer");
      this.putValue(Action.NAME, "Load FlickR data from XML");
    }
  }

  class LoadFlickRFrame extends JFrame
      implements ActionListener, ChangeListener {

    /****/
    private static final long serialVersionUID = 1L;

    private JSpinner spinLongmin, spinLongmax, spinLatmin, spinLatmax,
        spinAccuracy;
    private JCheckBox checkBox;

    LoadFlickRFrame() {
      super("Load FlickR Data");
      this.setSize(400, 500);
      this.setAlwaysOnTop(true);

      // a panel for the selection box
      JPanel pSelBox = new JPanel();
      checkBox = new JCheckBox("use a selection box");
      checkBox.setSelected(false);
      checkBox.addChangeListener(this);
      JPanel pBounds = new JPanel();
      spinLongmin = new JSpinner(
          new SpinnerNumberModel(2.1, -180.0, 180.0, 0.0001));
      spinLongmin.setEnabled(false);
      spinLongmin.setMaximumSize(new Dimension(100, 20));
      spinLongmin.setMinimumSize(new Dimension(100, 20));
      spinLongmin.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor longminEditor = new JSpinner.NumberEditor(
          spinLongmin, "0.0000");
      spinLongmin.setEditor(longminEditor);
      spinLongmax = new JSpinner(
          new SpinnerNumberModel(2.2, -180.0, 180.0, 0.0001));
      spinLongmax.setEnabled(false);
      spinLongmax.setMaximumSize(new Dimension(100, 20));
      spinLongmax.setMinimumSize(new Dimension(100, 20));
      spinLongmax.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor longmaxEditor = new JSpinner.NumberEditor(
          spinLongmax, "0.0000");
      spinLongmax.setEditor(longmaxEditor);
      spinLatmin = new JSpinner(
          new SpinnerNumberModel(48.0, -90.0, 90.0, 0.0001));
      spinLatmin.setEnabled(false);
      spinLatmin.setMaximumSize(new Dimension(100, 20));
      spinLatmin.setMinimumSize(new Dimension(100, 20));
      spinLatmin.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor latminEditor = new JSpinner.NumberEditor(spinLatmin,
          "0.0000");
      spinLatmin.setEditor(latminEditor);
      spinLatmax = new JSpinner(
          new SpinnerNumberModel(49.0, -90.0, 90.0, 0.0001));
      spinLatmax.setEnabled(false);
      spinLatmax.setMaximumSize(new Dimension(100, 20));
      spinLatmax.setMinimumSize(new Dimension(100, 20));
      spinLatmax.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor latmaxEditor = new JSpinner.NumberEditor(spinLatmax,
          "0.0000");
      spinLatmax.setEditor(latmaxEditor);
      spinAccuracy = new JSpinner(new SpinnerNumberModel(0, 0, 16, 1));
      spinAccuracy.setEnabled(false);
      spinAccuracy.setMaximumSize(new Dimension(50, 20));
      spinAccuracy.setMinimumSize(new Dimension(50, 20));
      spinAccuracy.setPreferredSize(new Dimension(50, 20));
      spinAccuracy.setToolTipText("from 0 (world level) to 16 (street level)");
      JPanel pYBounds = new JPanel();
      pYBounds.add(spinLatmax);
      pYBounds.add(Box.createVerticalStrut(40));
      pYBounds.add(spinLatmin);
      pYBounds.setLayout(new BoxLayout(pYBounds, BoxLayout.Y_AXIS));
      pBounds.add(spinLongmin);
      pBounds.add(pYBounds);
      pBounds.add(spinLongmax);
      pBounds.setLayout(new BoxLayout(pBounds, BoxLayout.X_AXIS));
      pSelBox.add(checkBox);
      pSelBox.add(pBounds);
      pSelBox.add(new JLabel("accuracy: "));
      pSelBox.add(spinAccuracy);
      pSelBox.setLayout(new BoxLayout(pSelBox, BoxLayout.X_AXIS));

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
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pSelBox);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pButtons);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
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
      // create the road feature collection from the selected features
      IFeatureCollection<FlickRFeature> photos = new FT_FeatureCollection<>();
      FeatureType ft = new FeatureType();
      ft.setGeometryType(IPoint.class);
      photos.setFeatureType(ft);

      FlickRLoader loader = new FlickRLoader(FLICKR_API_KEY, FLICKR_API_SECRET,
          proxyHost, new Integer(proxyPort));
      try {
        List<FlickRFeature> features = loader.getPhotosFromExtent(
            (Double) spinLatmin.getValue(), (Double) spinLatmax.getValue(),
            (Double) spinLongmin.getValue(), (Double) spinLongmax.getValue(),
            (Integer) spinAccuracy.getValue());
        photos.addAll(features);
      } catch (FlickrException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ParserConfigurationException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      }
      System.out.println(photos.size() + " photos loaded");

      // put the photos in a new layer
      putPhotosInLayer(photos);
    }
  }

  private void putPhotosInLayer(IFeatureCollection<FlickRFeature> photos) {
    ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();
    Layer layer = pFrame.getSld().createLayer(FLICKR_LAYER, IPoint.class,
        Color.RED);
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
    Graphic graphic = new Graphic();
    Mark mark = new Mark();
    mark.setWellKnownName("circle");
    Fill fill = new Fill();
    fill.setColor(color);
    mark.setFill(fill);
    graphic.getMarks().add(mark);
    symbolizer.setGraphic(graphic);
    rule.getSymbolizers().add(symbolizer);
    layer.getStyles().add(rawStyle);

    IPopulation<IFeature> pop = new Population<>(FLICKR_LAYER);
    pop.addAll(photos);
    pFrame.getSld().getDataSet().addPopulation(pop);
    pFrame.getSld().add(layer);
  }

  /**
   * 
   * @author GTouya
   * 
   */
  class ShowFlickRAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      StyledLayerDescriptor sld = application.getMainFrame()
          .getSelectedProjectFrame().getSld();
      Layer layer = sld.getLayer(FLICKR_LAYER);
      List<Style> styles = layer.getStyles();
      if (showPhotos.isSelected()) {
        // add a new style in the SLD to show photos
        UserStyle style = new UserStyle();
        style.setName("photos");
        styles.add(style);
        FeatureTypeStyle fts = new FeatureTypeStyle();
        List<FeatureTypeStyle> ftsList = new ArrayList<FeatureTypeStyle>();
        ftsList.add(fts);
        style.setFeatureTypeStyles(ftsList);
        for (IFeature feat : layer.getFeatureCollection()) {
          if (feat instanceof FlickRFeature) {
            FlickRFeature flickrFeat = (FlickRFeature) feat;
            Rule rule = new Rule();
            fts.addRule(rule);
            rule.setName(flickrFeat.getPhoto().getFlickRId());
            PropertyIsEqualTo filter = new PropertyIsEqualTo(
                new PropertyName("flickRId"),
                new Literal(flickrFeat.getPhoto().getFlickRId()));
            rule.setFilter(filter);
            PointSymbolizer symbolizer = new PointSymbolizer();
            List<Symbolizer> symbolizers = new ArrayList<>();
            symbolizers.add(symbolizer);
            rule.setSymbolizers(symbolizers);
            Graphic graphic = new Graphic();
            symbolizer.setGraphic(graphic);
            symbolizer.setGeometryPropertyName("geom");
            symbolizer.setUnitOfMeasurePixel();
            graphic.setSize(50);
            ExternalGraphic externalGraphic = new ExternalGraphic();
            List<ExternalGraphic> externalGraphics = new ArrayList<>();
            externalGraphics.add(externalGraphic);
            graphic.setExternalGraphics(externalGraphics);
            externalGraphic.setHref(flickrFeat.getPhotoUrlSmall());
            externalGraphic.setFormat("image/jpg");
          }
        }
      } else {
        // remove the style from the SLD
        Style phStyle = null;
        for (Style style : styles) {
          if ("photos".equals(style.getName())) {
            phStyle = style;
            break;
          }
        }
        if (phStyle != null)
          styles.remove(phStyle);
      }
    }

    public ShowFlickRAction() {
      this.putValue(Action.NAME, "Show FlickR photos");
    }
  }

  class LoadTwitterFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JSpinner spinLong, spinLat, spinRadius;
    private JXDatePicker sincePicker, untilPicker;

    LoadTwitterFrame() {
      super("Load Twitter Data");
      this.setSize(400, 500);
      this.setAlwaysOnTop(true);

      // a panel for the selection box
      JPanel pSelBox = new JPanel();
      spinLong = new JSpinner(
          new SpinnerNumberModel(2.2475, -180.0, 180.0, 0.0001));
      spinLong.setMaximumSize(new Dimension(100, 20));
      spinLong.setMinimumSize(new Dimension(100, 20));
      spinLong.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor longmaxEditor = new JSpinner.NumberEditor(spinLong,
          "0.0000");
      spinLong.setEditor(longmaxEditor);
      spinLat = new JSpinner(
          new SpinnerNumberModel(48.8450, -90.0, 90.0, 0.0001));
      spinLat.setMaximumSize(new Dimension(100, 20));
      spinLat.setMinimumSize(new Dimension(100, 20));
      spinLat.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor latminEditor = new JSpinner.NumberEditor(spinLat,
          "0.0000");
      spinLat.setEditor(latminEditor);
      spinRadius = new JSpinner(new SpinnerNumberModel(4.0, 0.0, 1000.0, 0.01));
      spinRadius.setMaximumSize(new Dimension(100, 20));
      spinRadius.setMinimumSize(new Dimension(100, 20));
      spinRadius.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor radiusEditor = new JSpinner.NumberEditor(spinRadius,
          "0.00");
      spinRadius.setEditor(radiusEditor);

      pSelBox.add(new JLabel("latitude: "));
      pSelBox.add(spinLat);
      pSelBox.add(new JLabel("longitude: "));
      pSelBox.add(spinLong);
      pSelBox.add(new JLabel("radius (in km): "));
      pSelBox.add(spinRadius);
      pSelBox.setLayout(new BoxLayout(pSelBox, BoxLayout.X_AXIS));

      // a panel for the selection box
      JPanel pDates = new JPanel();
      Date today = new Date();
      Date weekAgo = DateUtils.addWeeks(today, -1);
      sincePicker = new JXDatePicker(weekAgo);
      untilPicker = new JXDatePicker(today);
      pDates.add(new JLabel("since: "));
      pDates.add(sincePicker);
      pDates.add(new JLabel("until: "));
      pDates.add(untilPicker);
      pDates.setLayout(new BoxLayout(pDates, BoxLayout.X_AXIS));

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
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pSelBox);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pDates);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pButtons);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    private void loadData() {
      // create the road feature collection from the selected features
      IFeatureCollection<TwitterFeature> tweets = new FT_FeatureCollection<>();
      FeatureType ft = new FeatureType();
      ft.setGeometryType(IPoint.class);
      tweets.setFeatureType(ft);

      TwitterLoader loader = new TwitterLoader();
      loader.setProxy(proxyHost, new Integer(proxyPort));
      loader.setApiKey(TWITTER_API_KEY);
      loader.setApiSecret(TWITTER_API_SECRET);
      loader.setAccessToken(TWITTER_ACCESS_TOKEN);
      loader.setTokenSecret(TWITTER_TOKEN_SECRET);
      try {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String since = sdf.format(sincePicker.getDate());
        String until = sdf.format(untilPicker.getDate());
        List<TwitterFeature> features = loader.getTweetsFromLocation(
            (Double) spinLat.getValue(), (Double) spinLong.getValue(),
            (Double) spinRadius.getValue(), since, until);
        tweets.addAll(features);
      } catch (TwitterException e) {
        e.printStackTrace();
      }

      // put the photos in a new layer
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();
      Layer layer = pFrame.getSld().createLayer(TWITTER_LAYER, IPoint.class,
          Color.RED);
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
      Graphic graphic = new Graphic();
      Mark mark = new Mark();
      mark.setWellKnownName("circle");
      Fill fill = new Fill();
      fill.setColor(color);
      mark.setFill(fill);
      graphic.getMarks().add(mark);
      symbolizer.setGraphic(graphic);
      rule.getSymbolizers().add(symbolizer);
      layer.getStyles().add(rawStyle);

      IPopulation<IFeature> pop = new Population<>(TWITTER_LAYER);
      pop.addAll(tweets);
      pFrame.getSld().getDataSet().addPopulation(pop);
      pFrame.getSld().add(layer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        loadData();
        this.dispose();
      } else if (e.getActionCommand().equals("cancel")) {
        this.dispose();
      }
    }
  }

  /**
   * Load Twitter tweets in the selected area as geographical features, and
   * creates a new layer with the features.
   * 
   * @author GTouya
   * 
   */
  class LoadTwitterAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LoadTwitterFrame frame = new LoadTwitterFrame();
      frame.setVisible(true);

    }

    public LoadTwitterAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load Twitter data and add as a new layer");
      this.putValue(Action.NAME, "Load Twitter data");
    }
  }

  /**
   * Export loaded FlickR photos into a PostGIS database.
   * 
   * @author GTouya
   * 
   */
  class ExportFlickRAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      ExportFlickRFrame frame = new ExportFlickRFrame();
      frame.setVisible(true);

    }

    public ExportFlickRAction() {
      this.putValue(Action.NAME, "Export FlickR data in PostGIS");
    }
  }

  class ExportFlickRFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JTextField txtHost, txtPort, txtDb, txtUser, txtPwd, txtSchema;
    private JComboBox<String> comboLayers;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("OK")) {
        // connect to the database
        String url = "jdbc:postgresql://" + txtHost.getText() + ":"
            + txtPort.getText() + "/" + txtDb.getText();
        try {
          Connection connection = DriverManager.getConnection(url,
              txtUser.getText(), txtPwd.getText());
          System.out.println("Connection to " + url + " established");

          // store the features
          for (IFeature feat : application.getMainFrame()
              .getSelectedProjectFrame()
              .getLayer(comboLayers.getSelectedItem().toString())
              .getFeatureCollection()) {
            if (feat instanceof FlickRFeature) {
              this.exportFeature(connection, (FlickRFeature) feat);
            }
          }
        } catch (SQLException e1) {
          System.out.println("problem connecting to DB");
          e1.printStackTrace();
        }

        this.dispose();
      } else {
        this.dispose();
      }

    }

    public ExportFlickRFrame() throws HeadlessException {
      super("Export FlickR Data to PostGIS DB");

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
      txtDb = new JTextField("calac_64");
      txtDb.setPreferredSize(new Dimension(120, 20));
      txtDb.setMinimumSize(new Dimension(120, 20));
      txtDb.setMaximumSize(new Dimension(120, 20));
      dbPanel.add(new JLabel("database name : "));
      dbPanel.add(txtDb);
      dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));
      dbPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      JPanel schemaPanel = new JPanel();
      txtSchema = new JTextField("flickr");
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
      txtPwd = new JTextField("cartagen");
      txtPwd.setPreferredSize(new Dimension(100, 20));
      txtPwd.setMinimumSize(new Dimension(100, 20));
      txtPwd.setMaximumSize(new Dimension(100, 20));
      pwdPanel.add(new JLabel("password : "));
      pwdPanel.add(txtPwd);
      pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.X_AXIS));
      pwdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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
      connectionPanel
          .setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
      for (Layer layer : application.getMainFrame().getSelectedProjectFrame()
          .getLayers())
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
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.setAlwaysOnTop(true);
      this.pack();
    }

    private void exportFeature(Connection connection, FlickRFeature feat) {
      String query1 = "INSERT INTO " + txtSchema.getText() + ".flickr_photo"
          + " (flickr_id, flickr_owner, server, secret, farm, title) VALUES ('"
          + feat.getPhoto().getFlickRId() + "', '" + feat.getPhoto().getOwner()
          + "', '" + feat.getPhoto().getServer() + "', '"
          + feat.getPhoto().getSecret() + "', '" + feat.getPhoto().getFarm()
          + "', '" + feat.getPhoto().getTitle() + "')";
      Statement stat;
      try {
        stat = connection.createStatement();
        stat.executeQuery(query1);
      } catch (SQLException e) {
        // do nothing
      }
      String query2 = "INSERT INTO " + txtSchema.getText() + ".flickr_feature"
          + " (photo, geom) VALUES ('" + feat.getPhoto().getFlickRId()
          + "', ST_SetSRID(ST_MakePoint(" + feat.getLongitude() + ", "
          + feat.getLatitude() + "), 4326))";
      try {
        stat = connection.createStatement();
        stat.executeQuery(query2);
      } catch (SQLException e) {
        // do nothing
      }
    }
  }

  /**
   * Export loaded Twitter data into a PostGIS database.
   * 
   * @author GTouya
   * 
   */
  class ExportTwitterAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      ExportTwitterFrame frame = new ExportTwitterFrame();
      frame.setVisible(true);

    }

    public ExportTwitterAction() {
      this.putValue(Action.NAME, "Export Twitter data in PostGIS");
    }
  }

  class ExportTwitterFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JTextField txtHost, txtPort, txtDb, txtUser, txtPwd, txtSchema;
    private JComboBox<String> comboLayers;
    private HashSet<Long> features, users;
    private HashSet<String> places;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("OK")) {
        // connect to the database
        String url = "jdbc:postgresql://" + txtHost.getText() + ":"
            + txtPort.getText() + "/" + txtDb.getText();
        try {
          Connection connection = DriverManager.getConnection(url,
              txtUser.getText(), txtPwd.getText());
          System.out.println("Connection to " + url + " established");

          // load the existing features
          this.loadDatabase(connection);

          // store the features
          for (IFeature feat : application.getMainFrame()
              .getSelectedProjectFrame()
              .getLayer(comboLayers.getSelectedItem().toString())
              .getFeatureCollection()) {
            if (feat instanceof TwitterFeature) {
              this.exportFeature(connection, (TwitterFeature) feat);
            }
          }
        } catch (SQLException e1) {
          System.out.println("problem connecting to DB");
          e1.printStackTrace();
        } catch (Exception e1) {
          System.out.println("problem during export");
          e1.printStackTrace();
        }

        this.dispose();
      } else {
        this.dispose();
      }

    }

    public ExportTwitterFrame() throws HeadlessException {
      super("Export Twitter Data to PostGIS DB");
      this.features = new HashSet<>();
      this.users = new HashSet<>();
      this.places = new HashSet<>();

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
      txtDb = new JTextField("calac_64");
      txtDb.setPreferredSize(new Dimension(120, 20));
      txtDb.setMinimumSize(new Dimension(120, 20));
      txtDb.setMaximumSize(new Dimension(120, 20));
      dbPanel.add(new JLabel("database name : "));
      dbPanel.add(txtDb);
      dbPanel.setLayout(new BoxLayout(dbPanel, BoxLayout.X_AXIS));
      dbPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
      JPanel schemaPanel = new JPanel();
      txtSchema = new JTextField("flickr");
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
      txtPwd = new JTextField("cartagen");
      txtPwd.setPreferredSize(new Dimension(100, 20));
      txtPwd.setMinimumSize(new Dimension(100, 20));
      txtPwd.setMaximumSize(new Dimension(100, 20));
      pwdPanel.add(new JLabel("password : "));
      pwdPanel.add(txtPwd);
      pwdPanel.setLayout(new BoxLayout(pwdPanel, BoxLayout.X_AXIS));
      pwdPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

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
      connectionPanel
          .setLayout(new BoxLayout(connectionPanel, BoxLayout.Y_AXIS));

      // define a panel with the OK and Cancel buttons
      JPanel btnPanel = new JPanel();
      DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>();
      for (Layer layer : application.getMainFrame().getSelectedProjectFrame()
          .getLayers())
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
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.setAlwaysOnTop(true);
      this.pack();
    }

    private void loadDatabase(Connection connection) {
      String queryTweet = "SELECT tweet_id FROM " + txtSchema.getText()
          + ".tweet";
      Statement stat;
      ResultSet rs = null;
      try {
        stat = connection.createStatement();
        rs = stat.executeQuery(queryTweet);
        while (rs.next())
          this.features.add(rs.getLong(1));
      } catch (SQLException e) {
        e.printStackTrace();
      }
      String queryUser = "SELECT user_id FROM " + txtSchema.getText()
          + ".twitter_user";
      try {
        stat = connection.createStatement();
        rs = stat.executeQuery(queryUser);
        while (rs.next())
          this.users.add(rs.getLong(1));
      } catch (SQLException e) {
        e.printStackTrace();
      }
      String queryPlace = "SELECT place_id FROM " + txtSchema.getText()
          + ".twitter_place";
      try {
        stat = connection.createStatement();
        rs = stat.executeQuery(queryPlace);
        while (rs.next())
          this.places.add(rs.getString(1));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }

    private void exportFeature(Connection connection, TwitterFeature feat) {
      if (this.features.contains(feat.getTweetId()))
        return;

      String wktGeomTweet = "POINT("
          + ((IPoint) feat.getGeom()).getPosition().getX() + " "
          + ((IPoint) feat.getGeom()).getPosition().getY() + ")";
      // System.out.println(feat.getCreatedAt().toString());
      DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",
          Locale.ENGLISH);
      String queryTweet = "INSERT INTO " + txtSchema.getText() + ".tweet"
          + " (tweet_id, text, user_id, created_at, retweet_count, favorite_count, geom, place_id) VALUES ("
          + feat.getTweetId() + ", '" + feat.getText() + "', "
          + feat.getUser().getId() + ", to_date('"
          + dateFormat.format(feat.getCreatedAt())
          + "', 'Dy Mon DD HH24:MI:SS YYYY'), " + feat.getRetweetCount() + ", "
          + feat.getFavoriteCount() + ", ST_GeomFromText('" + wktGeomTweet
          + "', 4326), '" + feat.getPlace().getId() + "')";
      Statement stat;
      try {
        stat = connection.createStatement();
        stat.executeQuery(queryTweet);
      } catch (SQLException e) {
        // do nothing
        // e.printStackTrace();
      }

      // store the user if necessary
      if (!this.users.contains(feat.getUser().getId())) {
        String queryUser = "INSERT INTO " + txtSchema.getText()
            + ".twitter_user"
            + " (user_id, name, location, followers, following, url, screen_name) VALUES ("
            + feat.getUser().getId() + ", '" + feat.getUser().getName() + "', '"
            + feat.getUser().getLocation() + "', '"
            + feat.getUser().getFollowersCount() + "', '"
            + feat.getUser().getFriendsCount() + "', '"
            + feat.getUser().getURL() + "', '" + feat.getUser().getScreenName()
            + "')";
        try {
          stat = connection.createStatement();
          stat.executeQuery(queryUser);
        } catch (SQLException e) {
          // do nothing
        }
      }

      // store the place if necessary
      if (!this.places.contains(feat.getPlace().getId())) {
        // System.out.println(feat.getPlace());
        String queryPlace = null;
        StringBuffer wktBBox = new StringBuffer("'POLYGON((");
        for (int i = 0; i < feat.getPlace()
            .getBoundingBoxCoordinates()[0].length; i++) {
          wktBBox.append(
              feat.getPlace().getBoundingBoxCoordinates()[0][i].getLongitude());
          wktBBox.append(" ");
          wktBBox.append(
              feat.getPlace().getBoundingBoxCoordinates()[0][i].getLatitude());
          wktBBox.append(",");
        }
        wktBBox.append(
            feat.getPlace().getBoundingBoxCoordinates()[0][0].getLongitude());
        wktBBox.append(" ");
        wktBBox.append(
            feat.getPlace().getBoundingBoxCoordinates()[0][0].getLatitude());
        wktBBox.append("))'");
        String containedWithin = "";
        if (feat.getPlace().getContainedWithIn() != null
            && feat.getPlace().getContainedWithIn().length > 0)
          containedWithin = feat.getPlace().getContainedWithIn()[0].getId();
        System.out.println("wktBBox: " + wktBBox);
        /*
         * System.out.println("wktBBox: " + wktBBox);
         * System.out.println(feat.getPlace().getBoundingBoxCoordinates(
         * ).length ); System.out
         * .println(feat.getPlace().getBoundingBoxCoordinates()[0]. length); for
         * (int i = 0; i < feat.getPlace() .getBoundingBoxCoordinates().length;
         * i++) { for (int j = 0; j < feat.getPlace()
         * .getBoundingBoxCoordinates()[0].length; j++) { System.out
         * .println(feat.getPlace().getBoundingBoxCoordinates()[i][j]); } }
         */
        if (feat.getPlace().getGeometryCoordinates() != null) {
          StringBuffer wktGeom = new StringBuffer("'POINT(");
          if (feat.getPlace().getGeometryType().equals("Polygon"))
            wktGeom = new StringBuffer("'POLYGON(");
          else if (feat.getPlace().getGeometryType().equals("Line"))
            wktGeom = new StringBuffer("'LINESTRING(");
          for (int i = 0; i < feat.getPlace()
              .getGeometryCoordinates()[0].length; i++) {
            wktGeom.append(
                feat.getPlace().getGeometryCoordinates()[0][i].getLongitude());
            wktGeom.append(" ");
            wktGeom.append(
                feat.getPlace().getGeometryCoordinates()[0][i].getLatitude());
            wktGeom.append(",");
          }
          wktGeom.deleteCharAt(wktGeom.length() - 1);
          wktGeom.append(")'");
          System.out.println("wktGeom: " + wktGeom);

          queryPlace = "INSERT INTO " + txtSchema.getText() + ".twitter_place"
              + " (place_id, contained_in, country, country_code, name, geom, "
              + "geometry_type, url, place_type, street_address, full_name, bounding_box) VALUES ("
              + feat.getPlace().getId() + ", '" + containedWithin + "', '"
              + feat.getPlace().getCountry() + "', '"
              + feat.getPlace().getCountryCode() + "', '"
              + feat.getPlace().getName() + "', ST_GeomFromText(" + wktGeom
              + ", 4326), '" + feat.getPlace().getGeometryType() + "', '"
              + feat.getPlace().getURL() + "', '"
              + feat.getPlace().getPlaceType() + "', '"
              + feat.getPlace().getStreetAddress() + "', '"
              + feat.getPlace().getFullName() + "', ST_GeomFromText(" + wktBBox
              + ", 4326))";
        } else {
          queryPlace = "INSERT INTO " + txtSchema.getText() + ".twitter_place"
              + " (place_id, contained_in, country, country_code, name, "
              + "url, place_type, street_address, full_name, bounding_box) VALUES ('"
              + feat.getPlace().getId() + "', '" + containedWithin + "', '"
              + feat.getPlace().getCountry() + "', '"
              + feat.getPlace().getCountryCode() + "', '"
              + feat.getPlace().getName() + "', '" + feat.getPlace().getURL()
              + "', '" + feat.getPlace().getPlaceType() + "', '"
              + feat.getPlace().getStreetAddress() + "', '"
              + feat.getPlace().getFullName() + "', ST_GeomFromText(" + wktBBox
              + ", 4326))";
        }
        try {
          stat = connection.createStatement();
          stat.executeQuery(queryPlace);
        } catch (SQLException e) {
          // do nothing
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * Load Panoramio photos in the selected extent, and creates a new layer with
   * the features.
   * 
   * @author GTouya
   * 
   */
  class LoadPanoramioAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LoadPanoramioFrame frame = new LoadPanoramioFrame();
      frame.setVisible(true);

    }

    public LoadPanoramioAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load Panoramio data and add as a new layer");
      this.putValue(Action.NAME, "Load Panoramio data");
    }
  }

  class LoadPanoramioFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;

    private JSpinner spinLongmin, spinLongmax, spinLatmin, spinLatmax;
    private JCheckBox chkMapFilter;
    private JComboBox<String> comboSize;

    LoadPanoramioFrame() {
      super("Load Panoramio Data");
      this.setSize(400, 500);
      this.setAlwaysOnTop(true);

      // a panel for the selection box
      JPanel pSelBox = new JPanel();
      chkMapFilter = new JCheckBox("use Panoramio map filter");
      chkMapFilter.setSelected(false);
      JPanel pBounds = new JPanel();
      spinLongmin = new JSpinner(
          new SpinnerNumberModel(2.2350, -180.0, 180.0, 0.0001));
      spinLongmin.setMaximumSize(new Dimension(100, 20));
      spinLongmin.setMinimumSize(new Dimension(100, 20));
      spinLongmin.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor longminEditor = new JSpinner.NumberEditor(
          spinLongmin, "0.0000");
      spinLongmin.setEditor(longminEditor);
      spinLongmax = new JSpinner(
          new SpinnerNumberModel(2.2475, -180.0, 180.0, 0.0001));
      spinLongmax.setMaximumSize(new Dimension(100, 20));
      spinLongmax.setMinimumSize(new Dimension(100, 20));
      spinLongmax.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor longmaxEditor = new JSpinner.NumberEditor(
          spinLongmax, "0.0000");
      spinLongmax.setEditor(longmaxEditor);
      spinLatmin = new JSpinner(
          new SpinnerNumberModel(48.84, -90.0, 90.0, 0.0001));
      spinLatmin.setMaximumSize(new Dimension(100, 20));
      spinLatmin.setMinimumSize(new Dimension(100, 20));
      spinLatmin.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor latminEditor = new JSpinner.NumberEditor(spinLatmin,
          "0.0000");
      spinLatmin.setEditor(latminEditor);
      spinLatmax = new JSpinner(
          new SpinnerNumberModel(48.85, -90.0, 90.0, 0.0001));
      spinLatmax.setMaximumSize(new Dimension(100, 20));
      spinLatmax.setMinimumSize(new Dimension(100, 20));
      spinLatmax.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor latmaxEditor = new JSpinner.NumberEditor(spinLatmax,
          "0.0000");
      spinLatmax.setEditor(latmaxEditor);
      comboSize = new JComboBox<>(new String[] { "original", "medium", "small",
          "thumbnail", "square", "mini_square" });
      comboSize.setMaximumSize(new Dimension(100, 20));
      comboSize.setMinimumSize(new Dimension(100, 20));
      comboSize.setPreferredSize(new Dimension(100, 20));
      comboSize.setSelectedIndex(0);
      JPanel pYBounds = new JPanel();
      pYBounds.add(spinLatmax);
      pYBounds.add(Box.createVerticalStrut(40));
      pYBounds.add(spinLatmin);
      pYBounds.setLayout(new BoxLayout(pYBounds, BoxLayout.Y_AXIS));
      pBounds.add(spinLongmin);
      pBounds.add(pYBounds);
      pBounds.add(spinLongmax);
      pBounds.setLayout(new BoxLayout(pBounds, BoxLayout.X_AXIS));
      pSelBox.add(chkMapFilter);
      pSelBox.add(pBounds);
      pSelBox.add(new JLabel("photo size: "));
      pSelBox.add(comboSize);
      pSelBox.setLayout(new BoxLayout(pSelBox, BoxLayout.X_AXIS));

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
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pSelBox);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pButtons);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        loadData();
        this.dispose();
      } else if (e.getActionCommand().equals("cancel")) {
        this.dispose();
      }
    }

    private void loadData() {
      // create the road feature collection from the selected features
      IFeatureCollection<PanoramioFeature> photos = new FT_FeatureCollection<>();
      FeatureType ft = new FeatureType();
      ft.setGeometryType(IPoint.class);
      photos.setFeatureType(ft);

      PanoramioLoader loader = new PanoramioLoader(proxyHost,
          new Integer(proxyPort));
      try {
        List<PanoramioFeature> features = loader.getPhotosFromExtent(
            (Double) spinLatmin.getValue(), (Double) spinLatmax.getValue(),
            (Double) spinLongmin.getValue(), (Double) spinLongmax.getValue(),
            (String) comboSize.getSelectedItem(), chkMapFilter.isSelected());
        photos.addAll(features);
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      System.out.println(photos.size() + " photos loaded");

      // put the photos in a new layer
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();
      Layer layer = pFrame.getSld().createLayer(PANORAMIO_LAYER, IPoint.class,
          Color.RED);
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
      Graphic graphic = new Graphic();
      Mark mark = new Mark();
      mark.setWellKnownName("circle");
      Fill fill = new Fill();
      fill.setColor(color);
      mark.setFill(fill);
      graphic.getMarks().add(mark);
      symbolizer.setGraphic(graphic);
      rule.getSymbolizers().add(symbolizer);
      layer.getStyles().add(rawStyle);

      IPopulation<IFeature> pop = new Population<>(PANORAMIO_LAYER);
      pop.addAll(photos);
      pFrame.getSld().getDataSet().addPopulation(pop);
      pFrame.getSld().add(layer);
    }
  }

  /**
   * 
   * @author GTouya
   * 
   */
  class ShowPanoramioAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {

      StyledLayerDescriptor sld = application.getMainFrame()
          .getSelectedProjectFrame().getSld();
      Layer layer = sld.getLayer(PANORAMIO_LAYER);
      List<Style> styles = layer.getStyles();
      if (showPanoPhotos.isSelected()) {
        // add a new style in the SLD to show photos
        UserStyle style = new UserStyle();
        style.setName("photos");
        styles.add(style);
        FeatureTypeStyle fts = new FeatureTypeStyle();
        List<FeatureTypeStyle> ftsList = new ArrayList<FeatureTypeStyle>();
        ftsList.add(fts);
        style.setFeatureTypeStyles(ftsList);
        for (IFeature feat : layer.getFeatureCollection()) {
          if (feat instanceof PanoramioFeature) {
            PanoramioFeature flickrFeat = (PanoramioFeature) feat;
            Rule rule = new Rule();
            fts.addRule(rule);
            rule.setName(flickrFeat.getStringId());
            PropertyIsEqualTo filter = new PropertyIsEqualTo(
                new PropertyName("photoId"),
                new Literal(flickrFeat.getStringId()));
            rule.setFilter(filter);
            PointSymbolizer symbolizer = new PointSymbolizer();
            List<Symbolizer> symbolizers = new ArrayList<>();
            symbolizers.add(symbolizer);
            rule.setSymbolizers(symbolizers);
            Graphic graphic = new Graphic();
            symbolizer.setGraphic(graphic);
            symbolizer.setGeometryPropertyName("geom");
            symbolizer.setUnitOfMeasurePixel();
            graphic.setSize(50);
            ExternalGraphic externalGraphic = new ExternalGraphic();
            List<ExternalGraphic> externalGraphics = new ArrayList<>();
            externalGraphics.add(externalGraphic);
            graphic.setExternalGraphics(externalGraphics);
            externalGraphic.setHref(flickrFeat.getPhotoUrlSmall());
            externalGraphic.setFormat("image/jpg");
          }
        }
      } else {
        // remove the style from the SLD
        Style phStyle = null;
        for (Style style : styles) {
          if ("photos".equals(style.getName())) {
            phStyle = style;
            break;
          }
        }
        if (phStyle != null)
          styles.remove(phStyle);
      }
    }

    public ShowPanoramioAction() {
      this.putValue(Action.NAME, "Show Panoramio photos");
    }
  }

  class LoadFoursquareFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JSpinner spinLong, spinLat, spinRadius;

    LoadFoursquareFrame() {
      super("Load Foursquare Data");
      this.setSize(400, 500);
      this.setAlwaysOnTop(true);

      // a panel for the selection box
      JPanel pSelBox = new JPanel();
      spinLong = new JSpinner(
          new SpinnerNumberModel(2.2475, -180.0, 180.0, 0.0001));
      spinLong.setMaximumSize(new Dimension(100, 20));
      spinLong.setMinimumSize(new Dimension(100, 20));
      spinLong.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor longmaxEditor = new JSpinner.NumberEditor(spinLong,
          "0.0000");
      spinLong.setEditor(longmaxEditor);
      spinLat = new JSpinner(
          new SpinnerNumberModel(48.8450, -90.0, 90.0, 0.0001));
      spinLat.setMaximumSize(new Dimension(100, 20));
      spinLat.setMinimumSize(new Dimension(100, 20));
      spinLat.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor latminEditor = new JSpinner.NumberEditor(spinLat,
          "0.0000");
      spinLat.setEditor(latminEditor);
      spinRadius = new JSpinner(new SpinnerNumberModel(4000, 0, 100000, 100));
      spinRadius.setMaximumSize(new Dimension(100, 20));
      spinRadius.setMinimumSize(new Dimension(100, 20));
      spinRadius.setPreferredSize(new Dimension(100, 20));
      JSpinner.NumberEditor radiusEditor = new JSpinner.NumberEditor(spinRadius,
          "0");
      spinRadius.setEditor(radiusEditor);

      pSelBox.add(new JLabel("latitude: "));
      pSelBox.add(spinLat);
      pSelBox.add(new JLabel("longitude: "));
      pSelBox.add(spinLong);
      pSelBox.add(new JLabel("radius (in m): "));
      pSelBox.add(spinRadius);
      pSelBox.setLayout(new BoxLayout(pSelBox, BoxLayout.X_AXIS));

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
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pSelBox);
      this.getContentPane().add(Box.createVerticalGlue());
      this.getContentPane().add(pButtons);
      this.getContentPane()
          .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    private void loadData() {
      // create the road feature collection from the selected features
      IFeatureCollection<FoursquareFeature> venues = new FT_FeatureCollection<>();
      FeatureType ft = new FeatureType();
      ft.setGeometryType(IPoint.class);
      venues.setFeatureType(ft);

      FoursquareLoader loader = new FoursquareLoader(proxyHost,
          new Integer(proxyPort), FOURSQUARE_CLIENT_ID, FOURSQUARE_SECRET);
      List<FoursquareFeature> features = new ArrayList<>();
      try {
        features = loader.exploreVenuesInArea((Double) spinLong.getValue(),
            (Double) spinLat.getValue(), (Integer) spinRadius.getValue());
      } catch (IOException e) {
        e.printStackTrace();
      }
      venues.addAll(features);

      // put the photos in a new layer
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();
      Layer layer = pFrame.getSld().createLayer(FOURSQUARE_LAYER, IPoint.class,
          Color.RED);
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
      Graphic graphic = new Graphic();
      Mark mark = new Mark();
      mark.setWellKnownName("circle");
      Fill fill = new Fill();
      fill.setColor(color);
      mark.setFill(fill);
      graphic.getMarks().add(mark);
      symbolizer.setGraphic(graphic);
      rule.getSymbolizers().add(symbolizer);
      layer.getStyles().add(rawStyle);

      IPopulation<IFeature> pop = new Population<>(FOURSQUARE_LAYER);
      pop.addAll(venues);
      pFrame.getSld().getDataSet().addPopulation(pop);
      pFrame.getSld().add(layer);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        loadData();
        this.dispose();
      } else if (e.getActionCommand().equals("cancel")) {
        this.dispose();
      }
    }
  }

  /**
   * Load Foursquare tweets in the selected area as geographical features, and
   * creates a new layer with the features.
   * 
   * @author GTouya
   * 
   */
  class LoadFoursquareAction extends AbstractAction {

    /**
    * 
    */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LoadFoursquareFrame frame = new LoadFoursquareFrame();
      frame.setVisible(true);

    }

    public LoadFoursquareAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Load Foursquare data and add as a new layer");
      this.putValue(Action.NAME, "Load Foursquare data");
    }
  }

}
