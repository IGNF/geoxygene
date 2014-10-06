#version 400 core

uniform float globalOpacity = 1.0;
uniform float objectOpacity = 1.0;
layout (location = 0) in vec4 fragmentColor;
layout (location = 1) in vec2 fragmentTextureCoord;

// specify location 0. This fragment shader is used
// in normal rendering and FBO rendering with COLOR_ATTACHMENT0
layout (location = 0) out vec4 outColor;
//out vec4 outColor;

void main(void) {
	// outColor = vec4( fragmentColor.rgb, globalOpacity );
	outColor = vec4( fragmentColor.rgb, globalOpacity * objectOpacity * fragmentColor.a );
}