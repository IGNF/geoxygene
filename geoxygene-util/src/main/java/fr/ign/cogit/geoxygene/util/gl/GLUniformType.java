package fr.ign.cogit.geoxygene.util.gl;

public enum GLUniformType {
    GL_BOOL("bool"), GL_INT("int"), GL_UINT("uint"), GL_FLOAT("float"), GL_DOUBLE("double"), GL_BVEC2("bvec2"), GL_BVEC3("bvec3"), GL_BVEC4("bvec4"), GL_IVEC2("ivec2"), GL_IVEC3("ivec3"), GL_IVEC4(
            "ivec4"), GL_UVEC2("uvec2"), GL_UVEC3("uvec3"), GL_UVEC4("uvec4"), GL_VEC2("vec2"), GL_VEC3("vec3"), GL_VEC4("vec4"), GL_DVEC2("dvec2"), GL_DVEC3("dvec3"), GL_DVEC4("dvec4"), GL_MAT2(
            "mat2"), GL_MAT3("mat3"), GL_MAT4("mat4"), GL_VOID("void"), GL_SAMPLER1D("sampler1D"), GL_SAMPLER2D("sampler2D"), GL_SAMPLERCUBE("samplerCube"), GL_SAMPLER1DSHADOW("sampler1DShadow"), GL_SAMPLER2DSHADOW(
            "sampler2DShadow");

    private final String type;

    private GLUniformType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static GLUniformType fromString(String type) {
        if (type != null && !type.isEmpty()) {
            for (GLUniformType t : GLUniformType.values()) {
                if (t.type.equalsIgnoreCase(type)) {
                    return t;
                }
            }
        }
        return null;
    }
}
