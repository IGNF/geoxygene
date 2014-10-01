#version 150 core

uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;

in vec3 vertexPosition;
in vec2 vertexTextureCoord;
in vec4 vertexColor;

out vec4 fragmentColor;
out vec2 fragmentTextureCoord;

void main(void) {
	gl_Position = vec4( -1.0 + 2.0 * (vertexPosition.x * m00 + m02) / (screenWidth + 1.0), 1.0 - 2.0 * ( vertexPosition.y * m11 + m12 ) / ( screenHeight + 1.0 ), 0.0, 1.0);
	fragmentColor = vertexColor;
	fragmentTextureCoord = vertexTextureCoord;
}

