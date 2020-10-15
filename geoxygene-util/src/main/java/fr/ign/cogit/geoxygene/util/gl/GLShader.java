package fr.ign.cogit.geoxygene.util.gl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

public class GLShader {

    private int id = 0;
    protected String name = null;
    protected int type = -1;
    protected String source = null;

    protected List<String> uniforms_names = new ArrayList<>(0);
    protected List<Integer> uniforms_types = new ArrayList<>(0);

    public GLShader(String _name, int _gltype) {
        this.name = _name;
        this.type = _gltype;
    }

    public GLShader(String _name, String type_as_string) {
        this.name = _name;
        switch (type_as_string) {
        case "GL_VERTEX_SHADER":
            this.type = GL20.GL_VERTEX_SHADER;
            break;
        case "GL_FRAGMENT_SHADER":
            this.type = GL20.GL_FRAGMENT_SHADER;
            break;
        case "GL_GEOMETRY_SHADER":
            this.type = GL32.GL_GEOMETRY_SHADER;
            break;
        default:
            LogManager.getRootLogger().error("Unknow GL_TYPE " + type_as_string);
            break;
        }
    }

    public int getId() {
        if (this.id != 0) {
            return this.id;
        }
        try {
            this.id = GLTools.createShader(this.type, this.source, null);
            GLTools.glCheckError("When creating the shader " + this.name);
        } catch (GLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getSource() {
        return this.source;
    }

    public String getUniformName(int id) {
        return this.uniforms_names.get(id);
    }

    public int getUniformType(int id) {
        return this.uniforms_types.get(id);
    }

    public int getUniformsCount() {
        assert this.uniforms_names.size() == this.uniforms_types.size();
        return this.uniforms_names.size();
    }

}
