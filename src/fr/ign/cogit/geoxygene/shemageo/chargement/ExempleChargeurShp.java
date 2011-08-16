/**
 * @author julien Gaffuri 20 juil. 2009
 */
package fr.ign.cogit.geoxygene.shemageo.chargement;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.schemageo.bati.Batiment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.shemageo.bati.BatimentImpl;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author julien Gaffuri 20 juil. 2009
 * 
 */
public class ExempleChargeurShp {
  private static Logger logger = Logger.getLogger(ExempleChargeurShp.class
      .getName());

  /**
   * chemin de repertoire de donnes shp exemple: E:/donnees/bourg_oisans
   */
  private String cheminShp = null;

  public ExempleChargeurShp(String cheminShp) {
    this.cheminShp = cheminShp;
  }

  public IDataSet charger() throws IOException {
    DataSet dataset = new DataSet();

    if (ExempleChargeurShp.logger.isInfoEnabled()) {
      ExempleChargeurShp.logger.info("chargement des batiments");
    }
    dataset.addPopulation(this.chargerBatiments());

    return dataset;
  }

  private String nomFichierBatiment;

  public void setNomFichierBatiment(String nomFichierBatiment) {
    this.nomFichierBatiment = nomFichierBatiment;
  }

  private IPopulation<Batiment> chargerBatiments() throws IOException {
    String chemin = this.cheminShp + this.nomFichierBatiment + ".shp";
    Population<Batiment> population = new Population<Batiment>();

    ShapefileReader shr = null;
    // DbaseFileReader dbr=null;
    try {
      ShpFiles shpf = new ShpFiles(chemin);
      shr = new ShapefileReader(shpf, true, false);
      // dbr = new DbaseFileReader(shpf, true, Charset.defaultCharset() );
    } catch (FileNotFoundException e) {
      if (ExempleChargeurShp.logger.isDebugEnabled()) {
        ExempleChargeurShp.logger.debug("fichier " + chemin + " non trouve.");
      }
      return null;
    }

    if (ExempleChargeurShp.logger.isInfoEnabled()) {
      ExempleChargeurShp.logger.info("Chargement: " + chemin);
    }

    // while (shr.hasNext() && dbr.hasNext()){
    while (shr.hasNext()) {
      Record objet = shr.nextRecord();

      IGeometry geom = null;
      try {
        geom = AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }

      if (geom instanceof IPolygon) {
        population.add(new BatimentImpl(geom));
      } else if (geom instanceof IMultiSurface<?>) {
        for (int i = 0; i < ((IMultiSurface<?>) geom).size(); i++) {
          population.add(new BatimentImpl(((IMultiSurface<?>) geom).get(i)));
        }
      } else {
        ExempleChargeurShp.logger.error("ERREUR lors du chargement de shp "
            + chemin + ". Type de geometrie " + geom.getClass().getName()
            + " non gere.");
      }
    }
    shr.close();
    // dbr.close();

    return population;
  }

}
