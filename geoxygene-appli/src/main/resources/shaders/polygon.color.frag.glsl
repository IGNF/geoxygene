#version 400

uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	// outColor = vec4( fragmentColor.rgb, globalOpacity );
	outColor = vec4( fragmentColor.rgb, fragmentColor.a * globalOpacity );
}