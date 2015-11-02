/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.resources;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ResourceDictionary")
@XmlAccessorType(XmlAccessType.FIELD)
public class ResourceDictionary {
    
    @XmlElement(name="Name")
    String name;
    
    @XmlElement(name="DictionaryProperties")
    List<String> dictProperties;

    @XmlElement(name="ResourceEntry")
    List<DictionaryEntry> dictentries;
    
    public String getName() {
        return this.name;
    }
    
    public List<DictionaryEntry> getEntries() {
        return this.dictentries;
    }
    
    public static final ResourceDictionary unmarshall(File xmlFile){
        try {
            JAXBContext jc = JAXBContext.newInstance(
                    DictionaryEntry.class, ResourceDictionary.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            //FIXME we should use XMLConstants.W3C_XML_SCHEMA_NS_URI but due to javax.xml version conflicts we use the direct URL.
            //Disabled : rewrite the Dictionary Schema.
//            SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
//            URL schemaurl =  ResourceDictionary.class.getClassLoader().getResource(GeoxygeneConstants.GEOX_Const_ResourceDictionariesSchemaLocation);
//            Schema schema = sf.newSchema(new File(schemaurl.getFile())); 
//            unmarshaller.setSchema(schema);
            ResourceDictionary dic = (ResourceDictionary) unmarshaller
                    .unmarshal(xmlFile);
            return dic;

        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    


    
}


