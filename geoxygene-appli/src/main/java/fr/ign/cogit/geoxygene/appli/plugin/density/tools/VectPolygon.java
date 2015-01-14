package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

public class VectPolygon extends Vector<GM_Polygon> {

  private static final long serialVersionUID = 1L;

  public VectPolygon() {
    super();
  }
  
  @Override
  public synchronized boolean add(GM_Polygon poly) {
    
    for (int i=0; i<size(); i++) {
      if(get(i).area()>poly.area()){
        super.add(i, poly);
        return true;
      }
    }
    
    return super.add(poly);
  }
  
  public void write(String src) throws IOException{
    //src = "C:\\Users\\SIMON\\Dropbox\\Ecole\\ProjetRech\\Voronoi.xls";
    System.out.println(src);
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
  
}
