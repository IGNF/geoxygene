package fr.ign.cogit.geoxygene.appli.plugin.density.tools;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
/**
 * Table Model pour la classe FrameAHull
 * @see FrameAHull
 * @author Simon
 *
 */
public class ListAHull extends AbstractTableModel {
  
  private static final long serialVersionUID = 1L;
  private Vector<ValueTableAHull> v;
  
  /**
   * Construit l'objet ListAHull
   * @param start - la valeur de départ
   * @param end - la valeur de fin
   * @param step - l'incrément
   */
  public ListAHull(int start, int end, int step) {
    super();
    if(start>end){
      int tmp = start;
      start = end;
      end = tmp;
    }
    v = new Vector<>();
    for(int i = start; i<=end; i=i+step){
      v.add(new ValueTableAHull(i));
    }
  }
  
  @Override
  public String getColumnName(int col) {
    switch (col) {
      case 0:  return "Alpha";
      case 1:  return "Valid";
      case 2:  return "Superficie";  
      case 3:  return "Nb Points Hors";
      case 4:  return "ptsHors/ptsTot";
      case 5:  return "alphaHull/MCR";
      default: return null;
    }
  }
  
  
  public Vector<ValueTableAHull> getV() {
    return v;
  }

  @Override
  public int getColumnCount() {
    return 6;
  }

  @Override
  public int getRowCount() {
    return v.size();
  }

  @Override
  public Class<?> getColumnClass(int col) {
    switch (col) {
      case 0:  return Integer.class;
      case 1:  return Boolean.class;
      case 2:  return Double.class;  
      case 3:  return Integer.class;
      case 4:  return Double.class;
      case 5:  return Double.class;
      default: return null;
    }
  }
  
  @Override
  public Object getValueAt(int row, int col) {
    switch (col) {
      case 0:  return v.get(row).getAlpha();
      case 1:  return v.get(row).isValid();
      case 2:  return v.get(row).getSuperficie();
      case 3:  return v.get(row).getnPtsHors();
      case 4:  return v.get(row).getRapNbPtHors();
      case 5:  return v.get(row).getRapMCR();
      default: return null;
    }
  }
  
  public void write(String src)throws IOException{
    
    System.out.println(src);
    HSSFWorkbook wb = new HSSFWorkbook();
    HSSFSheet sheet = wb.createSheet("Resultats");
    
    HSSFCell cell;
    
    HSSFRow row = sheet.createRow(0);
    for(int j=0; j<getColumnCount(); j++){
      cell = row.createCell(j);
      cell.setCellValue(getColumnName(j));
    }
    
    for(int i=0; i<getRowCount(); i++) {
      row = sheet.createRow(i+1);
      for(int j=0; j<getColumnCount(); j++){
        cell = row.createCell(j);
        cell.setCellValue(getValueAt(i, j).toString());
      }
    }

    FileOutputStream fileOut;
      fileOut = new FileOutputStream(src);
      wb.write(fileOut);
      fileOut.close();

  }

}
