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

package fr.ign.cogit.geoxygene.appli.render.texture;

import fr.ign.cogit.geoxygene.appli.gl.ContrastSubshaderFilter;
import fr.ign.cogit.geoxygene.appli.gl.DefaultSubshader1D;
import fr.ign.cogit.geoxygene.appli.gl.DefaultSubshader2D;
import fr.ign.cogit.geoxygene.appli.gl.IdentitySubshaderFilter;
import fr.ign.cogit.geoxygene.appli.gl.RandomVariationSubshader;
import fr.ign.cogit.geoxygene.appli.gl.Subshader;
import fr.ign.cogit.geoxygene.appli.gl.UserSubshader;
import fr.ign.cogit.geoxygene.style.expressive.DefaultFill2DShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.DefaultLineShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.RandomVariationShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserFill2DShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserLineShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserShaderDescriptor;
import fr.ign.cogit.geoxygene.style.filter.LayerFilter;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterContrast;
import fr.ign.cogit.geoxygene.style.filter.LayerFilterIdentity;

/**
 * @author JeT
 * 
 */
public class ShaderFactory {

    public static Subshader createShader(ShaderDescriptor descriptor) {
        if (descriptor instanceof RandomVariationShaderDescriptor) {
            return createRndShader((RandomVariationShaderDescriptor) descriptor);

        }
        if (descriptor instanceof DefaultLineShaderDescriptor) {
            return createDefaultShader((DefaultLineShaderDescriptor) descriptor);

        }
        if (descriptor instanceof UserLineShaderDescriptor) {
            return createUserShader((UserShaderDescriptor) descriptor);

        }
        if (descriptor instanceof UserFill2DShaderDescriptor) {
            return createUserFillSubshader((UserFill2DShaderDescriptor) descriptor);

        }
        if (descriptor instanceof DefaultFill2DShaderDescriptor) {
            return createDefaultFillSubshader((DefaultFill2DShaderDescriptor) descriptor);

        }
        throw new IllegalStateException("Do not handle "
                + descriptor.getClass().getSimpleName() + " shader descriptor");
    }

    public static RandomVariationSubshader createRndShader(
            RandomVariationShaderDescriptor descriptor) {
        return new RandomVariationSubshader(descriptor);
    }

    public static DefaultSubshader1D createDefaultShader(
            DefaultLineShaderDescriptor descriptor) {
        return new DefaultSubshader1D(descriptor);
    }

    public static UserSubshader createUserShader(UserShaderDescriptor descriptor) {
        return new UserSubshader(descriptor);
    }

    public static UserSubshader createUserFillSubshader(
            UserFill2DShaderDescriptor descriptor) {
        return new UserSubshader(descriptor);
    }

    public static DefaultSubshader2D createDefaultFillSubshader(
            DefaultFill2DShaderDescriptor descriptor) {
        return new DefaultSubshader2D(descriptor);
    }

    public static Subshader createFilterShader(LayerFilter filter) {
        // special case for the null filter
        if (filter == null || filter instanceof LayerFilterIdentity) {
            return createFilterShaderIdentity((LayerFilterIdentity) filter);

        }
        if (filter instanceof LayerFilterContrast) {
            return createFilterShaderContrast((LayerFilterContrast) filter);

        }
        throw new IllegalStateException("Do not handle "
                + filter.getClass().getSimpleName()
                + " filter shader descriptor");
    }

    private static Subshader createFilterShaderContrast(
            LayerFilterContrast filter) {
        return new ContrastSubshaderFilter(filter);
    }

    private static Subshader createFilterShaderIdentity(
            LayerFilterIdentity filter) {
        return new IdentitySubshaderFilter(filter);
    }

}
