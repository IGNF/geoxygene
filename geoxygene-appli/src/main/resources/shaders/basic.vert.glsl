#version 410

layout (location = 0) in vec3 vertexPosition;
layout (location = 1) in vec2 vertexUV;
layout (location = 2) in vec4 vertexColor;
out vec2 fragmentUV;
out vec4 fragmentColor;

void main() {
	gl_Position = vec4( vertexPosition, 1.0);
	fragmentUV = vertexUV;
	fragmentColor = vertexColor;
}