package fr.ign.cogit.geoxygene.contrib.quality.util;

import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public class DisplayChart {

  /**
   * Création et affichage d'un histogramme avec JFreeChart
   * @param listIn
   */
  public static void histogramChart(List<Double> listIn, String listName) {

    double tabIn[] = new double[listIn.size()];
    for (int i = 0; i < listIn.size(); i++) {
      tabIn[i] = listIn.get(i);
    }

    // Création des datasets
    HistogramDataset dataset = new HistogramDataset();
    dataset.setType(HistogramType.RELATIVE_FREQUENCY);
    dataset.addSeries(listName, tabIn, 200);

    // Création de l'histogramme
    JFreeChart chart = ChartFactory.createHistogram("", null, null, dataset,
        PlotOrientation.VERTICAL, true, true, false);
    ChartFrame frame = new ChartFrame("Spatial Data Quality", chart);
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Création et affichage d'un nuage de points
   * @param listXIn
   * @param listYIn
   */
  public static void xySerieChart(List<Double> listXIn, String listXName,
      List<Double> listYIn, String listYName) {

    // Création des datasets
    XYSeries serie = new XYSeries(listXName + " / " + listYName);

    for (int i = 0; i < listXIn.size(); i++) {
      serie.add(listXIn.get(i), listYIn.get(i));
    }

    // Création du graphique
    XYSeriesCollection dataset = new XYSeriesCollection();
    dataset.addSeries(serie);
    JFreeChart chart = ChartFactory.createScatterPlot("", listXName, listYName,
        dataset, PlotOrientation.VERTICAL, true, true, true);
    ChartFrame frame = new ChartFrame("Spatial Data Quality", chart);
    frame.pack();
    frame.setVisible(true);
  }
}
