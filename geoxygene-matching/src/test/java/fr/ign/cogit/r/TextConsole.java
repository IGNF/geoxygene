package fr.ign.cogit.r;

import java.io.*;

import org.rosuda.JRI.Rengine;
import org.rosuda.JRI.RMainLoopCallbacks;


/**
 * Text Console for R outputs
 * 
 * The methods are callbacks called by the R engine.
 */
public class TextConsole implements RMainLoopCallbacks {

  public void rBusy(Rengine re, int which) {
      System.out.println("rBusy(" + which + ")");
  }

  public String rReadConsole(Rengine re, String prompt, int addToHistory) {
      System.out.print(prompt);
      try {
          BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
          String s = br.readLine();
          return (s == null || s.length() == 0) ? s : s + "\n";
      } catch (Exception e) {
          System.out.println("jriReadConsole exception: " + e.getMessage());
      }
      return null;
  }

  public void rShowMessage(Rengine re, String message) {
      System.out.println("rShowMessage \"" + message + "\"");
  }

  /**
   * Should return the path to a file.
   */
  public String rChooseFile(Rengine re, int newFile) {
      System.out.println("rChooseFile");
      return "";
  }

  public void rFlushConsole(Rengine re) {
      System.out.println("rFlushConsole");
  }

  public void rLoadHistory(Rengine re, String filename) {
      System.out.println("rLoadHistory");
  }

  public void rSaveHistory(Rengine re, String filename) {
      System.out.println("rSaveHistory");
  }

  public void rWriteConsole(Rengine re, String text, int arg2) {
      System.out.print(text);
  }
}