package fr.ign.cogit.geoxygene.wps.contrib.datamatching.ppio;

import org.apache.log4j.Logger;
import org.geoserver.wps.ppio.XStreamPPIO;
import org.xml.sax.ContentHandler;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;

/**
 * 
 * 
 *
 */
public class ResultNetworkStatPPIO extends XStreamPPIO {
  
  /** LOGGER. */
  private final static Logger LOGGER = Logger.getLogger(ResultNetworkStatPPIO.class.getName());

  /**
   * Default constructor.
   */
  protected ResultNetworkStatPPIO() {
    super(ResultNetworkStat.class);
  }

  @Override
  protected XStream buildXStream() {
      XStream xstream = new XStream() {
          protected MapperWrapper wrapMapper(MapperWrapper next) {
              return new UppercaseTagMapper(next);
          };
      };
      xstream.alias("ResultNetworkStat", ResultNetworkStat.class);
      return xstream;
  }

}
