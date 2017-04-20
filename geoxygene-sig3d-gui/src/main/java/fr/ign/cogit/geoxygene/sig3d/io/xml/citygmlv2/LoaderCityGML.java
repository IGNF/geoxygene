package fr.ign.cogit.geoxygene.sig3d.io.xml.citygmlv2;

import java.io.File;

import javax.xml.bind.JAXBException;

import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.JAXBBuilder;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityModel;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.CG_VectorLayer;
import fr.ign.cogit.geoxygene.sig3d.representation.citygml.core.RP_CityObject;


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
 * @version 1.7
 * 
 * Class to load CityGML data in full schema (schema package) with it representation (representation.citygml package)
 */
public class LoaderCityGML {

  public static CG_VectorLayer read(File f, String context, String layerName)
      throws CityGMLReadException, JAXBException {

    Context.CITY_GML_CONTEXT = context;

    CityGMLReader reader = LoaderCityGML.getCityGMLInputFactory()
        .createCityGMLReader(f);
    
    
    System.out.println(reader.hasNext());

    CityGML citygml = reader.nextFeature();

    CG_CityModel cityModel = new CG_CityModel((CityModel) citygml);

    int nbElem = cityModel.size();

    for (CG_CityObject cGO : cityModel.getElements()) {


        if (cGO != null) {
          RP_CityObject.generateCityObjectRepresentation(cGO,
              cityModel.getlCGA());
        }


      
      System.out.println("Représentation generated");
    }

    for (int i = 0; i < nbElem; i++) {

      IFeature feat = cityModel.get(i);

      if (feat.getRepresentation() == null) {

        cityModel.remove(i);
        i--;
        nbElem--;

      }

    }
    
    

    return new CG_VectorLayer(cityModel, layerName);

  }

  private static CityGMLInputFactory in = null;

  private static CityGMLInputFactory getCityGMLInputFactory()
      throws JAXBException, CityGMLReadException {

    if (LoaderCityGML.in == null) {

      CityGMLContext ctx = new CityGMLContext();
      JAXBBuilder builder = ctx.createJAXBBuilder();

      LoaderCityGML.in = builder.createCityGMLInputFactory();

    }
    return LoaderCityGML.in;

  }
}
