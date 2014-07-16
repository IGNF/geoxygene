#version 150 core

uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;
uniform float paperScale = 10; 

in vec3 vertexPosition;
in vec2 vertexUV;
in vec4 vertexColor;

out vec2 fragmentUV;

void main() {
	gl_Position = vec4( vertexPosition, 1);
	
	fragmentUV.x = vertexUV.x;
	fragmentUV.y = vertexUV.y;
}