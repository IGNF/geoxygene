package fr.ign.cogit.geoxygene.sig3d.representation.texture;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.media.j3d.Texture;
import javax.media.j3d.Texture2D;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import com.sun.j3d.utils.image.TextureLoader;

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
 * Exemple de classe permettant de charger une seule fois une texture et évite
 * d'en recharger 1 par objet Utile pour les textures "répétées" Class to manage
 * texture loading to avoid having different instance for a same texture
 */
public class TextureManager {

  /**
   * Liste des noms associés aux textures (chemin sur le disque)
   */
  private static List<String> lPathTextures = new ArrayList<String>();

  /**
   * Liste des différentes textures chargées
   */
  private static List<Texture2D> lTextures = TextureManager.getIMG();

  /**
   * Permet de récupérer à partir d'une texture le chemin de celle-ci (Si elle a
   * été préalablement chargée par le TextureManager. Si ce n'est pas le cas,
   * renvoie null.
   * 
   * @param tex la texture dont on veut récupérer le chemin
   * @return le chemin de la texture ou null si elle n'est pas dans le manager
   */
  public static String getTexturePath(Texture2D tex) {

    int nbTextures = TextureManager.lTextures.size();
    for (int i = 0; i < nbTextures; i++) {
      if (tex.equals(TextureManager.lTextures.get(i))) {
        return TextureManager.lPathTextures.get(i);
      }

    }

    return null;
  }

  /**
   * On tente d'initialiser une texture à partir d'un chemin. En retour on
   * obtient l'objet Texture2D correspondant à ce chemin. Si la texture n'est
   * pas chargée par le Manager, il la crée, si elle est chargée, il renvoie
   * l'instance existante. L'objet Texture2D sert aux représentations d'objets
   * texturés génériquement. Voir classe ObjetSurfaceTexture.
   * 
   * @param path le chemin de la texture
   * @return l'objet Texture2D renvoyé.
   */
  public static Texture2D textureLoading(String path) {
    int nbTextures = TextureManager.lTextures.size();

    for (int i = 0; i < nbTextures; i++) {

      if (TextureManager.lPathTextures.get(i).equals(path)) {

        return TextureManager.lTextures.get(i);
      }

    }

    TextureLoader loader = new TextureLoader(path, null);
    Texture2D texture = (Texture2D) loader.getTexture();
    texture.setBoundaryModeS(Texture.WRAP);
    texture.setBoundaryModeT(Texture.WRAP);
    texture.setEnable(true);
    texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
    texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);

    TextureManager.lTextures.add(texture);
    TextureManager.lPathTextures.add(path);

    return texture;

  }
  
  
  /**
   * On tente d'initialiser une texture à partir d'un chemin. En retour on
   * obtient l'objet Texture2D correspondant à ce chemin. Si la texture n'est
   * pas chargée par le Manager, il la crée, si elle est chargée, il renvoie
   * l'instance existante. L'objet Texture2D sert aux représentations d'objets
   * texturés génériquement. Voir classe ObjetSurfaceTexture.
   * 
   * @param path le chemin de la texture
   * @return l'objet Texture2D renvoyé.
   */
  public static Texture2D textureNoReapetLoading(String path) {
    int nbTextures = TextureManager.lTextures.size();

    for (int i = 0; i < nbTextures; i++) {

      if (TextureManager.lPathTextures.get(i).equals(path)) {

        return TextureManager.lTextures.get(i);
      }

    }

    TextureLoader loader = new TextureLoader(path, null);
    Texture2D texture = (Texture2D) loader.getTexture();
    texture.setBoundaryModeS(Texture.CLAMP_TO_BOUNDARY);
    texture.setBoundaryModeT(Texture.CLAMP_TO_BOUNDARY);
    texture.setEnable(true);
    texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
    texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);

    TextureManager.lTextures.add(texture);
    TextureManager.lPathTextures.add(path);

    return texture;

  }

  public static List<Texture2D> getlTextures() {
    return TextureManager.lTextures;
  }

  public static List<String> getlTexturesName() {
    return TextureManager.lPathTextures;
  }

  /**
   * Petite fonction pour récupèrer les textures d'un dossier
   * 
   * @return renvoie la liste des textures dans le dossier textures
   * @throws IOException
   * @throws MalformedURLException
   */
  private static List<Texture2D> getIMG() {

    List<Texture2D> formatsDisponibles = new ArrayList<Texture2D>();

    File directoryToScan = new File("./src/main/resources/texture");

    File[] lf = directoryToScan.listFiles();

    if (lf == null) {
      return formatsDisponibles;
    }

    int nbFichiers = lf.length;

    for (int j = 0; j < nbFichiers; j++) {

      File f = lf[j];

      String nom = f.getName();

      int pos = nom.lastIndexOf('.');

      if (pos == -1) {

        continue;
      }

      String extension = nom.substring(pos);

      if (extension.equalsIgnoreCase(".jpg")
          || extension.equalsIgnoreCase(".bmp")
          || extension.equalsIgnoreCase(".jpeg")) {

        TextureLoader loader;
        try {
          loader = new TextureLoader(f.getPath(), null);

          Texture2D texture = (Texture2D) loader.getTexture();
          texture.setBoundaryModeS(Texture.WRAP );
          texture.setBoundaryModeT(Texture.WRAP );
          texture.setEnable(true);
          texture.setMagFilter(Texture.BASE_LEVEL_LINEAR);
          texture.setMinFilter(Texture.BASE_LEVEL_LINEAR);

          formatsDisponibles.add(texture);
          TextureManager.lPathTextures.add(f.getPath());
        } catch (Exception e) {
          e.printStackTrace();
          continue;
        }

      }
    }

    return formatsDisponibles;
  }

  /**
   * Renvoie un composent à partir d'une texture
   * 
   * @param tex la texture dont on souhaite obtenir le composant
   * @return un composant permettant de visualiser le style de la couche dans la
   *         table de contenu
   */
  public static JComponent componentFromTexture(Texture2D tex) {

    return TextureManager.componentFromTexturePath(TextureManager
        .getTexturePath(tex));
  }

  /**
   * Renvoie un composent à partir de l'URL d'une texture
   * 
   * @param texPath l'url de la texture que l'on souhaite visualiser dans le
   *          composant
   * @return un composant permettant de visualiser le style de la couche dans la
   *         table de contenu
   */
  public static JComponent componentFromTexturePath(String texPath) {

    File f = new File(texPath);

    if (!f.exists()) {
      JButton jb = new JButton();
      jb.setBackground(Color.black);
      jb.setHorizontalAlignment(SwingConstants.HORIZONTAL);
      return jb;
    }

    BufferedImage img;
    try {
      img = ImageIO.read(f);

      Image imgF = img.getScaledInstance(50, 50, Image.SCALE_FAST);

      ImageIcon im = new ImageIcon(imgF);

      JLabel label = new JLabel();
      label.setIcon(im);
      label.setHorizontalAlignment(SwingConstants.HORIZONTAL);

      // On renvoie le panel
      return label;
    } catch (Exception e) {
      JButton jb = new JButton();
      jb.setBackground(Color.black);
      jb.setHorizontalAlignment(SwingConstants.HORIZONTAL);
      return jb;
    }
  }

}
