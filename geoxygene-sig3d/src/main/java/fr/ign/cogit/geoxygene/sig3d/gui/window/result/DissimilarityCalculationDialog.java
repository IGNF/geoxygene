package fr.ign.cogit.geoxygene.sig3d.gui.window.result;

import javax.swing.JDialog;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 *  
 * @version 0.1
 * 
 * Fenetre permettant d'afficher le résultat du calcul de différence entre
 * formes d'objets
 * 
 * Window rendering the result of shape dissimilaritie
 * 
 */
public class DissimilarityCalculationDialog extends JDialog {

  private static final long serialVersionUID = 1L;

  /**
   * Affiche un graphique à l'aide de 2 nuages de points
   * 
   * @param title the frame title.
   */
  public DissimilarityCalculationDialog(final String title,
      DirectPositionList dpl1, DirectPositionList dpl2) {

    super();
    final XYSeries series = new XYSeries("Objet 1");
    int nbElem = dpl1.size();

    for (int i = 0; i < nbElem - 1; i++) {

      series.add((dpl1.get(i + 1).getX() + dpl1.get(i).getX()) / 2, dpl1.get(i)
          .getY());

    }

    final XYSeries series2 = new XYSeries("Objet 2");
    int nbElem2 = dpl2.size();

    for (int i = 0; i < nbElem2 - 1; i++) {

      series2.add((dpl2.get(i + 1).getX() + dpl2.get(i).getX()) / 2, dpl2
          .get(i).getY());

    }

    double valeur = 0;
    // Affiche la différence en norme L2 des 2 graphiques
    for (int i = 0; i < nbElem; i++) {

      valeur = valeur + Math.pow(dpl1.get(i).getY() - dpl2.get(i).getY(), 2);

    }

    valeur = Math.sqrt(valeur) / (1024 * 512);

    final XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(series);
    dataset.addSeries(series2);

    final JFreeChart chart = ChartFactory.createXYLineChart("XY Series Demo",
        "Distance : " + valeur, Messages.getString("Result.PointFD"), dataset,
        PlotOrientation.VERTICAL, true, true, false);

    final ChartPanel chartPanel = new ChartPanel(chart);

    chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    this.setContentPane(chartPanel);

  }

}
