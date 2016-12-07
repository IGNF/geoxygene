package fr.ign.cogit.geoxygene.util.conversion;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;

public class FeatureCollectionToCSV {

  public static void export(IFeatureCollection<IFeature> featC, String fileOut)
      throws IOException {

    FileWriter fw = new FileWriter(fileOut);

    String entete = "";

    DefaultFeature feat = (DefaultFeature) featC.get(0);

    List<GF_AttributeType> lAtt = feat.getFeatureType().getFeatureAttributes();

    int nbAtt = lAtt.size();

    entete = entete + lAtt.get(0).getMemberName() + ";";

    for (int i = 1; i < nbAtt; i++) {

      entete = entete + ";" + lAtt.get(i).getMemberName();

    }

    // //////Ecriture des en-têtes

    fw.write(entete + "\n");

    for (IFeature featTemp : featC) {

      String line = featTemp.getAttribute(lAtt.get(0)).toString();
      for (int i = 1; i < nbAtt; i++) {

        line = line + ";" + featTemp.getAttribute(lAtt.get(i));

      }

      fw.write(line + "\n");

    }

    fw.flush();
    fw.close();

  }

}
