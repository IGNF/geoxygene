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
package fr.ign.cogit.geoxygene.appli.resources;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;

/**
 * Resolve the location of resources.
 */
public class ResourceLocationResolver {

    /**
     * Resolve the location of a resource identified by an URI. This URI can be
     * absolute, relative (in this case, root_URI must not be null) or based on
     * specific schemes. <br/>
     * For now, only the "geox:" scheme is resolved by this class. The default
     * location of this scheme is the resource folder of the application.
     * 
     * @param uri_to_resolve
     * @param root_URI
     * @return
     * @throws URISyntaxException
     * @throws MalformedURLException
     */
    public static URL resolve(URI uri_to_resolve, URI root_URI) {
        URL location = null;
        if (uri_to_resolve == null)
            return null;
        try {
            URI app_resources_URI = ResourceLocationResolver.class.getClassLoader().getResource(".").toURI();
            if (uri_to_resolve.getScheme() == null) {
                if (uri_to_resolve.toString().startsWith("/")) {
                    location = new URI("file:/").resolve(uri_to_resolve.toString()).toURL();
                } else {
                    if (root_URI != null) {
                        location = root_URI.resolve(uri_to_resolve).toURL();
                    }
                }
            } else if (uri_to_resolve.getScheme().equalsIgnoreCase(GeoxygeneConstants.GEOX_Const_GeoxResourceScheme)) {
                location = new URL(app_resources_URI.toURL(), uri_to_resolve.getSchemeSpecificPart());
            } else if (uri_to_resolve.getScheme().equalsIgnoreCase(GeoxygeneConstants.GEOX_Const_SLDLocalScheme)) {
                if (ResourcesManager.Root().getResourceByName(GeoxygeneConstants.GEOX_Const_CurrentStyleRootURIName) != null) {
                    URI current_sld_uri = (URI) ResourcesManager.Root().getResourceByName(GeoxygeneConstants.GEOX_Const_CurrentStyleRootURIName);
                    location = current_sld_uri.resolve(uri_to_resolve.getSchemeSpecificPart()).toURL();
                } else {
                    Logger.getRootLogger().error("Cannot resolve the sld URI " + uri_to_resolve + " when no GeoxConst_CurrentStyleRootURIName is defined in the ResourceManager");
                }
            } else if (uri_to_resolve.isAbsolute()) {
                location = uri_to_resolve.toURL();
            } else {
                Logger.getRootLogger().error("Undefined case : when trying to resolve the URI " + uri_to_resolve + " : the scheme of this URI is invalid.");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return location;
    }
}
