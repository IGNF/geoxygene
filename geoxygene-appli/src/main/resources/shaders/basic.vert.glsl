#version 150 core

in vec3 vertexPosition;
in vec2 vertexUV;
in vec4 vertexColor;
out vec2 fragmentUV;
out vec4 fragmentColor;

void main() {
	gl_Position = vec4( vertexPosition, 1.0);
	fragmentUV = vertexUV;
	fragmentColor = vertexColor;
}