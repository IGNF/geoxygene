#version 330 core

layout (location = 0) in vec3 vertexPosition;
layout (location = 1) in vec2 vertexTextureCoord;
layout (location = 2) in vec4 vertexColor;
uniform int antialiasingSize = 1;

out vec4 fragmentColor;
out vec2 fragmentTextureCoord;

void main(void) {
	gl_Position = vec4( vertexPosition, 1.0 );
	fragmentColor = vertexColor;
	fragmentTextureCoord = vertexTextureCoord;
}