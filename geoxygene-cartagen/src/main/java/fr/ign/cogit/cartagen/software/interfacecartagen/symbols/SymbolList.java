/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.symbols;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.core.genericschema.SymbolShape;
import fr.ign.cogit.cartagen.software.interfacecartagen.dataloading.LoaderUtil;

/**
 * Classe permettant de lire les fichiers XML qui décrit les symbols
 * 
 * @author KJaara
 * 
 */

public class SymbolList {

  private static Logger logger = Logger.getLogger(LoaderUtil.class.getName());

  // arraylist thant contains all the symbols
  private ArrayList<SymbolShape> symbols = new ArrayList<SymbolShape>();

  private SymbolGroup group;

  // fill the arraylist "symbols" with the symbols of BDTopo scale 1:25 000
  public void fillListWithBDTopo25() {
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .parse(
              new File(
                  "src\\main\\java\\fr\\ign\\cogit\\cartagen\\software\\interfaceCartagen\\symbols\\SymbolsBDTopo25.xml"));
    } catch (FileNotFoundException e) {
      SymbolList.logger.error("Fichier non trouvé: " + "SymbolsBDTopo25.xml");
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (docXML == null) {
      SymbolList.logger
          .error("Erreur lors de la lecture de: "
              + "src\\main\\java\\fr\\ign\\cogit\\generalisation\\interfaces\\dataLoading\\SymbolsBDTopo25.xml");
      return;
    }
    // we put all the symbols in symbolsElement
    Element symbolsElement = (Element) docXML.getElementsByTagName("Root")
        .item(0);
    if (this.symbols == null) {
      return;
    }

    Element symbol = (Element) symbolsElement.getElementsByTagName("Symbol")
        .item(0);

    int index = 0;

    while (symbol != null) {

      // we store the characteristics of every symbol in "symbolShape"
      SymbolShape symbolShape = new SymbolShape();
      symbolShape.description = symbol.getElementsByTagName("Description")
          .item(0).getFirstChild().getNodeValue();
      symbolShape.varName = symbol.getElementsByTagName("Variable_name")
          .item(0).getFirstChild().getNodeValue();
      symbolShape.symbolId = Integer.parseInt(symbol.getElementsByTagName("ID")
          .item(0).getFirstChild().getNodeValue());
      Element style_L = (Element) symbol.getElementsByTagName("style-L")
          .item(0);

      String tmp;

      if (style_L != null) {

        tmp = style_L.getAttribute("ext_colour");

        // traduction of CMYK color into rgb color
        if (tmp != "") {

          String[] rgbString = tmp.split(" ");
          int r = Integer.parseInt(rgbString[0]);
          int g = Integer.parseInt(rgbString[1]);
          int b = Integer.parseInt(rgbString[2]);
          symbolShape.ext_colour = new Color(r, g, b);
        }

        tmp = style_L.getAttribute("ext_dash");
        if (tmp != "") {
          symbolShape.ext_dash = Double.parseDouble(tmp);
        }

        tmp = style_L.getAttribute("ext_dash_space");
        if (tmp != "") {
          symbolShape.ext_dash_space = Double.parseDouble(tmp);
        }

        tmp = style_L.getAttribute("ext_endstyle");
        if (tmp != "") {
          symbolShape.ext_endstyle = style_L.getAttribute("ext_endstyle");
        }

        if (style_L.getAttribute("ext_isdashed") != "") {
          symbolShape.ext_isdashed = Boolean.getBoolean(style_L
              .getAttribute("ext_isdashed"));
        }

        if (style_L.getAttribute("ext_joinstyle") != "") {
          symbolShape.ext_joinstyle = style_L.getAttribute("ext_joinstyle");
        }

        if (style_L.getAttribute("ext_priority") != "") {
          symbolShape.ext_priority = Integer.parseInt(style_L
              .getAttribute("ext_priority"));
        }

        if (style_L.getAttribute("ext_width") != "") {
          symbolShape.ext_width = Double.parseDouble(style_L
              .getAttribute("ext_width"));
        }

        if (style_L.getAttribute("has_int") != "") {
          symbolShape.has_int = Boolean.getBoolean(style_L
              .getAttribute("has_int"));
        }

        tmp = style_L.getAttribute("int_colour");
        if (tmp != "") {

          if (tmp != "") {

            String[] CMYKString = tmp.split(" ");
            int[] p_colorvalue = new int[4];

            p_colorvalue[0] = Integer.parseInt(CMYKString[0]);
            p_colorvalue[1] = Integer.parseInt(CMYKString[1]);
            p_colorvalue[2] = Integer.parseInt(CMYKString[2]);
            p_colorvalue[3] = Integer.parseInt(CMYKString[3]);
            int[] rgbColor = this.toRGB(p_colorvalue);
            symbolShape.int_colour = new Color(rgbColor[0], rgbColor[1],
                rgbColor[2]);
          }

        }

        if (style_L.getAttribute("int_dash") != "") {
          symbolShape.int_dash = Double.parseDouble(style_L
              .getAttribute("int_dash"));
        }

        if (style_L.getAttribute("int_dash_space") != "") {
          symbolShape.int_dash_space = Double.parseDouble(style_L
              .getAttribute("int_dash_space"));
        }

        if (style_L.getAttribute("int_endstyle") != "") {
          symbolShape.int_endstyle = style_L.getAttribute("int_endstyle");
        }

        if (style_L.getAttribute("int_isdashed") != "") {
          symbolShape.int_isdashed = Boolean.getBoolean(style_L
              .getAttribute("int_isdashed"));
        }

        if (style_L.getAttribute("int_priority") != "") {
          symbolShape.int_priority = Integer.parseInt(style_L
              .getAttribute("int_priority"));
        }

        if (style_L.getAttribute("int_width") != "") {
          symbolShape.int_width = Double.parseDouble(style_L
              .getAttribute("int_width"));
        }

        tmp = style_L.getAttribute("sep_colour");
        if (tmp != "") {

          if (tmp != "") {

            String[] rgbString = tmp.split(" ");
            int r = Integer.parseInt(rgbString[0]);
            int g = Integer.parseInt(rgbString[1]);
            int b = Integer.parseInt(rgbString[2]);
            symbolShape.sep_colour = new Color(r, g, b);
          }

        }

        if (style_L.getAttribute("sep_priority") != "") {
          symbolShape.sep_priority = Integer.parseInt(style_L
              .getAttribute("sep_priority"));
        }

        if (style_L.getAttribute("sep_width") != "") {
          symbolShape.sep_width = Double.parseDouble(style_L
              .getAttribute("sep_width"));
        }

      }

      this.symbols.add(symbolShape);

      index++;
      symbol = (Element) symbolsElement.getElementsByTagName("Symbol").item(
          index);

    }

    SymbolList.logger.info("symbols.size=" + this.symbols.size());
    // JOptionPane.showMessageDialog(null,symbols.size()+"");

  }

  public SymbolShape getSymbolShapeBySymbolVarName(String symbolVarName) {
    // SymbolShape result=new SymbolShape();

    for (int i = 0; i < this.symbols.size(); i++) {
      SymbolShape tmp = this.symbols.get(i);
      if (tmp.varName == symbolVarName) {
        return tmp;
      }
    }

    return null;
  }

  public int[] toRGB(int[] p_colorvalue) {
    int[] l_res = { 0, 0, 0 };
    if (p_colorvalue.length >= 4) {
      int l_black = 100 - p_colorvalue[3];
      l_res[0] = l_black * (100 - p_colorvalue[0]) * 255;
      l_res[1] = l_black * (100 - p_colorvalue[1]) * 255;
      l_res[2] = l_black * (100 - p_colorvalue[2]) * 255;
    }
    return this.normalize(l_res);
  }

  private int[] normalize(int[] p_colors) {
    for (int l_i = 0; l_i < p_colors.length; l_i++) {
      if (p_colors[l_i] > 255) {
        p_colors[l_i] = 255;
      } else if (p_colors[l_i] < 0) {
        p_colors[l_i] = 0;
      }
    }
    int[] colors = new int[] { p_colors[0], p_colors[1], p_colors[2] };
    return colors;
  }

  public int getSymbolShapeIdBySymbolVarName(String symbolVarName) {
    // SymbolShape result=new SymbolShape();

    for (int i = 0; i < this.symbols.size(); i++) {
      SymbolShape tmp = this.symbols.get(i);
      if (tmp.varName.compareTo(symbolVarName) == 0) {
        return tmp.symbolId;
      }
    }

    return -1;
  }

  public SymbolShape getSymbolShapeBySymbolID(int Id) {

    for (int i = 0; i < this.symbols.size(); i++) {
      SymbolShape tmp = this.symbols.get(i);
      if (tmp.symbolId == Id) {
        return tmp;
      }
    }

    return null;
  }

  public int getSize() {
    // TODO Auto-generated method stub
    return this.symbols.size();
  }

  public void clear() {
    this.symbols.clear();

  }

  public void setGroup(SymbolGroup group) {
    this.group = group;
  }

  public SymbolGroup getGroup() {
    return group;
  }

  public void fillListWithBDCarto() {
    // TODO Auto-generated method stub

  }

  public void fillListWithOneSymbol() {

    SymbolShape symbolShape1 = new SymbolShape();
    symbolShape1.int_priority = 3;
    symbolShape1.symbolId = 0;
    symbolShape1.ext_colour = Color.red;
    symbolShape1.int_colour = Color.yellow;
    symbolShape1.int_width = 0.35;
    symbolShape1.has_int = true;
    symbolShape1.ext_priority = 2;

    symbolShape1.ext_width = 0.55;

    SymbolShape symbolShape2 = new SymbolShape();
    symbolShape2.int_priority = 1;
    symbolShape2.symbolId = 1;
    symbolShape2.ext_colour = Color.black;
    symbolShape2.int_colour = Color.black;
    symbolShape2.int_width = 0.3;
    symbolShape2.has_int = true;
    symbolShape2.ext_priority = 1;
    symbolShape2.ext_width = 0.3;

    symbols.add(symbolShape1);
    symbols.add(symbolShape2);

    // fillListWithBDTopo25();

  }

  public static SymbolList getSymbolList(SymbolGroup symbolGroup) {
    SymbolList symbols = new SymbolList();
    symbols.setGroup(symbolGroup);

    if (symbolGroup.equals(SymbolGroup.BD_TOPO_25)) {
      symbols.fillListWithBDTopo25();
    } else if (symbolGroup.equals(SymbolGroup.Simple)) {
      symbols.fillListWithOneSymbol();
    }
    return symbols;
  }

}
