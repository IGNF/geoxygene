/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

public class ConvergenceGraph extends ApplicationFrame {
  private static final long serialVersionUID = 1L;

  public ConvergenceGraph() {
    super("");
    IntervalXYDataset dataset = this.createDataset();
    JFreeChart chart = ChartFactory.createXYBarChart("", // titre
        "", // titre axe X
        false, // si c'est des dates en X
        "", // titre axe Y
        dataset, // donnees
        PlotOrientation.VERTICAL, // sens d'affichage
        false, // affichage legende
        false, // affichage tooltips
        false); // affichage generer URLs (?)

    // chart.setBackgroundPaint(Color.LIGHT_GRAY);
    XYPlot plot = (XYPlot) chart.getPlot();

    NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
    domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    // add the chart to a panel...
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(new java.awt.Dimension(500, 300));
    this.setContentPane(chartPanel);

    chartPanel.setMouseZoomable(false);

  }

  private IntervalXYDataset createDataset() {
    XYSeries series = new XYSeries("1");
    for (int i = 0; i < 500; i++) {
      series.add(i, 5.0 + i * i);
    }

    XYSeriesCollection collection = new XYSeriesCollection();
    collection.addSeries(series);
    return new XYBarDataset(collection, 0.9);
  }

  public static void main(String[] args) {
    ConvergenceGraph gc = new ConvergenceGraph();
    gc.pack();
    RefineryUtilities.centerFrameOnScreen(gc);
    gc.setVisible(true);
  }

}
