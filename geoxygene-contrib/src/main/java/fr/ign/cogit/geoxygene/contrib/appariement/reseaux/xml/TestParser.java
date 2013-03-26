/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElementInterface;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStat;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.data.ResultNetworkStatElement;

public class TestParser {
  
  public static void main(String[] args) {
    
    try {
      
      JAXBContext context = JAXBContext.newInstance(ResultNetworkStat.class);
      Marshaller m = context.createMarshaller();
      m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    
      ResultNetworkStat resnet = new ResultNetworkStat();
      ResultNetworkStatElement res = new ResultNetworkStatElement(ResultNetworkStatElementInterface.NONE);
      res.setTotalNetworkElementNumber(-10);
      res.setCorrectMatchingNetworkElementNumber(-100);
      res.setNoMatchingNetworkElementNumber(-1000);
      res.setDoubtfulNetworkElementNumber(-10000);
      resnet.setEdgesEvaluationComp(res);
      
      m.marshal(resnet, System.out);
    
    } catch (JAXBException ex) {
      ex.printStackTrace();
    }
  }

}
