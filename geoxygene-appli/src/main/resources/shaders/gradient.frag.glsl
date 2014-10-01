#version 150 core

uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
uniform sampler2D gradientTexture;

in VertexData {
	vec4 color;
	vec2 gradientUV;
} fragmentIn;

// specify location 0. This fragment shader is used
// in normal rendering and FBO rendering with COLOR_ATTACHMENT0
//layout (location = 0.0) out vec4 outColor;
out vec4 outColor;

struct DataGradient {
	vec4 color;
	vec2 gradientUV;
	float gradientValue;
};

void main(void) {
	vec4 tcolor = texture(gradientTexture, fragmentIn.gradientUV );
	outColor = vec4( tcolor.rgb, objectOpacity * globalOpacity * tcolor.a);
}