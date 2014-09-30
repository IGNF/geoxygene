package fr.ign.cogit.geoxygene.style.expressive;

import java.util.List;

public interface UserShaderDescriptor {

    /**
     * @return the filename
     */
    public abstract String getFilename();

    /**
     * @param filename
     *            the filename to set
     */
    public abstract void setFilename(String filename);

    /**
     * @return the parameters
     */
    public abstract List<ParameterDescriptor> getParameters();

}