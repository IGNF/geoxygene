#version 410

uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
layout (location = 0) in vec4 fragmentColor;
layout (location = 1) in vec2 fragmentTextureCoord;

out vec4 outColor;

void main(void) {
	// outColor = vec4( fragmentColor.rgb, globalOpacity );
	outColor = vec4( fragmentColor.rgb, globalOpacity * objectOpacity * fragmentColor.a );
}