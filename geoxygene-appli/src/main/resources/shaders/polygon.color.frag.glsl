#version 150 core

uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
in vec4 fragmentColor;

// specify location 0. This fragment shader is used
// in normal rendering and FBO rendering with COLOR_ATTACHMENT0
//layout (location = 0) out vec4 outColor;
out vec4 outColor;

void main(void) {
	outColor = vec4( fragmentColor.rgb, globalOpacity * objectOpacity * fragmentColor.a);
}