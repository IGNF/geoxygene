/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;

/**
 * Cette classe stocke les triangles issus de la triangulation de 
 * @author Simon
 *
 */
public class VectTriangle extends Vector<GM_Triangle> {

  private static final long serialVersionUID = 1L;

  public VectTriangle() {
    super();
  }
  
  @Override
  public synchronized boolean add(GM_Triangle tri) {
    
    for (int i=0; i<size(); i++) {
      if(get(i).area()>tri.area()){
        super.add(i, tri);
        return true;
      }
    }
    
    return super.add(tri);
  }
  
  public void write(String src) throws IOException{
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet("Resultats");
    
    for (int i=0; i<size(); i++) {
      HSSFRow row = sheet.createRow(i);
      HSSFCell cell = row.createCell(0);
      cell.setCellValue(get(i).area());
    }
    
    
    FileOutputStream fileOut;
      fileOut = new FileOutputStream(src);
      wb.write(fileOut);
      fileOut.close();
  }
  
  private double getSommeArea(int n){
    double s = 0;
    for(int i=0; i<n; i++){
      s += this.get(i).area();
    }
    return s;
  }
  
  public ChartPanel getChart(int[] n){
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    
    int[] res = new int[n.length];
    double sTotal = getSommeArea(this.size());
    
    int i=0;
    for( int j=0; j<this.size(); j++){
      if(getSommeArea(j)/sTotal<n[i]/100.){
        res[i]++;
      } else {
        if(i<n.length){
          i++;
          j--;
        }
      }
    }
    
    for (int i1 : n) {
      dataset.setValue(res[i1], "Triangles", n[i1]+"");
    }
    
    
    JFreeChart chart = ChartFactory.createBarChart("", 
    "Fractions", "Triangles", dataset, PlotOrientation.VERTICAL, 
    false, true, false);

    ChartPanel crepart = new ChartPanel(chart);
    
    return crepart; 
  }
  
}
