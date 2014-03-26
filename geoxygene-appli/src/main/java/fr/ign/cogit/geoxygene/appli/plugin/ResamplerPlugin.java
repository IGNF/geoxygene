package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.util.Resampler;
import fr.ign.cogit.geoxygene.style.Layer;

public class ResamplerPlugin implements GeOxygeneApplicationPlugin,
    ActionListener {
  /** Logger. */
  static Logger logger = Logger.getLogger(ResamplerPlugin.class.getName());

  private GeOxygeneApplication application = null;

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu(I18N.getString("ResamplerPlugin.VectorMenu")); //$NON-NLS-1$
    JMenuItem resampleMenuItem = new JMenuItem(
        I18N.getString("ResamplerPlugin.ResamplerMenuItem")); //$NON-NLS-1$
    resampleMenuItem.addActionListener(this);
    menu.add(resampleMenuItem);
    application.getMainFrame().getMenuBar()
        .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);
  }

  @Override
  public void actionPerformed(final ActionEvent e) {
    ProjectFrame project = this.application.getMainFrame()
        .getSelectedProjectFrame();
    Set<Layer> selectedLayers = project.getLayerLegendPanel()
        .getSelectedLayers();
    if (selectedLayers.size() != 1) {
      ResamplerPlugin.logger
          .error("You need to select one (and only one) layer."); //$NON-NLS-1$
      JOptionPane.showMessageDialog(project.getGui(),
          I18N.getString("CubicSplinePlugin.SelectedLayerErrorMessage"),//$NON-NLS-1$
          "Selected layer error", JOptionPane.ERROR_MESSAGE); //$NON-NLS-1$
      return;
    }
    Layer layer = selectedLayers.iterator().next();

    double maxDistance = Double.parseDouble(JOptionPane.showInputDialog(I18N
        .getString("ResamplerPlugin.distanceMax"))); //$NON-NLS-1$

    Object[] possibilities = { I18N.getString("ResamplerPlugin.densify"), //$NON-NLS-1$
        I18N.getString("ResamplerPlugin.cut") }; //$NON-NLS-1$
    String s = (String) JOptionPane.showInputDialog(this.application
        .getMainFrame().getGui(),
        I18N.getString("ResamplerPlugin.resamplerMethodeChoice"), //$NON-NLS-1$
        "Customized Dialog", JOptionPane.PLAIN_MESSAGE, null, possibilities,
        I18N.getString("ResamplerPlugin.densify")); //$NON-NLS-1$

    FeatureType newFeatureTypeResampler = new FeatureType();
    newFeatureTypeResampler.setGeometryType(GM_LineString.class);

    if ((s != null) && (s.length() > 0)) {
      if (s.equalsIgnoreCase(I18N.getString("ResamplerPlugin.densify"))) { //$NON-NLS-1$
        Population<DefaultFeature> popResampled = new Population<DefaultFeature>(
            layer.getName() + "_resample_" + maxDistance); //$NON-NLS-1$
        popResampled.setClasse(DefaultFeature.class);
        popResampled.setPersistant(false);
        popResampled.setFeatureType(newFeatureTypeResampler);
        for (IFeature f : layer.getFeatureCollection()) {
          IGeometry geometry = f.getGeom();
          IDirectPositionList list = geometry.coord();
          IDirectPositionList list_resampled = Resampler.resample(list,
              maxDistance);
          ILineString ls_resampled = new GM_LineString(list_resampled);

          if (geometry.isPolygon() || geometry.isMultiSurface()) {
            IPolygon polygon = new GM_Polygon(ls_resampled);
            popResampled.getFeatureType().setGeometryType(GM_Polygon.class);
            popResampled.nouvelElement(polygon);
          } else if (geometry.isLineString() || geometry.isMultiCurve()) {
            popResampled.getFeatureType().setGeometryType(GM_LineString.class);
            popResampled.nouvelElement(ls_resampled);
          }
        }
        project.getDataSet().addPopulation(popResampled);
        project.addFeatureCollection(popResampled, popResampled.getNom(), null);

      } else if (s.equalsIgnoreCase(I18N.getString("ResamplerPlugin.cut"))) { //$NON-NLS-1$
        Population<DefaultFeature> popResampled = new Population<DefaultFeature>(
            layer.getName() + "_cut_" + maxDistance); //$NON-NLS-1$
        popResampled.setClasse(DefaultFeature.class);
        popResampled.setPersistant(false);
        popResampled.setFeatureType(newFeatureTypeResampler);

        for (IFeature f : layer.getFeatureCollection()) {
          IDirectPositionList list = f.getGeom().coord();
          IDirectPositionList list_cut = Resampler.resample(list, maxDistance);
          for (int i = 0; i < list_cut.size() - 1; i++) {
            ILineString ls_resampled = new GM_LineString(list_cut.get(i),
                list_cut.get(i + 1));
            popResampled.nouvelElement(ls_resampled);
          }
        }
        project.getDataSet().addPopulation(popResampled);
        project.addFeatureCollection(popResampled, popResampled.getNom(),
            layer.getCRS());
      }
    }
  }
}
