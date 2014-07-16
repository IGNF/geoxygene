#version 150 core


uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;
uniform float mapScaleDiv1000 = 0.; // map scale
uniform int brushWidth = 0; // brush texture width (pixels)
uniform int brushHeight = 0; // brush texture height (pixels)
uniform int brushStartWidth = 0; // brush texture width (pixels)
uniform int brushEndWidth = 0; // brush texture height (pixels)
uniform float brushScale = 0; // size in mm of one brush pixel


in vec2 vertexPosition;
in vec2 vertexUV;
in vec2 vertexNormal;
in float vertexCurvature;
in float vertexThickness;
in vec4 vertexColor;
in float uMax;
in vec2 vertexPaperUV;

out vec4 fragmentPosition;
out vec2 fragmentUV;
out vec2 fragmentPaperUV;
out vec4 fragmentColor;
out float fragmentCurvature;
out float fragmentThickness;
out float uMax_w;

void main() {
	//gl_Position = vec4 ( vertexPosition , 1f );
	gl_Position = vec4( -1 + 2 * (vertexPosition.x * m00 + m02) / (screenWidth + 1), 1 - 2 * ( vertexPosition.y * m11 + m12 ) / ( screenHeight + 1 ), 0, 1);
	fragmentPosition = gl_Position;
	fragmentUV = vertexUV;
	fragmentPaperUV = vertexPaperUV;
	fragmentCurvature = vertexCurvature;
	fragmentThickness = vertexThickness;
	fragmentColor = vertexColor;
	uMax_w = uMax;
}