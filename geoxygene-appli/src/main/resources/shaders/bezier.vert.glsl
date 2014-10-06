#version 410



uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix

uniform float screenWidth;
uniform float screenHeight;
uniform float fboWidth=5000.0;
uniform float fboHeight=5000.0;
uniform float mapScaleDiv1000 = 0.; // map scale
uniform int brushWidth = 0.0; // brush texture width (pixels)
uniform int brushHeight = 0.0; // brush texture height (pixels)
uniform int brushStartWidth = 0.0; // brush texture width (pixels)
uniform int brushEndWidth = 0.0; // brush texture height (pixels)
uniform float brushScale = 0.0; // size in mm of one brush pixel

layout(location = 0) in vec2 vertexPosition;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec4 vertexColor;
layout(location = 3) in float lineWidth;
layout(location = 4) in float uMax;
layout(location = 5) in vec2 p0;
layout(location = 6) in vec2 p1;
layout(location = 7) in vec2 p2;
layout(location = 8) in vec2 n0;
layout(location = 9) in vec2 n2;
layout(location = 10) in vec2 vertexPaperUV;



out VertexData {
	vec4 position;
	vec2 uv;
	vec4 color;
	float lineWidth;
	float uMax;
	vec2 paperUV;
	flat vec2 p0screen;
	flat vec2 p1screen;
	flat vec2 p2screen;
	flat vec2 n0screen;
	flat vec2 n2screen;
} vertexOut;

float screenRatio = fboWidth / screenWidth;

// transform world coordinates to [-1.0 +1.0]
vec2 worldToScreen( vec2 p ) {
	return vec2( -1.0 + 2.0 * (p.x * m00 + m02) / (screenWidth + 1.0), 1.0 - 2.0 * ( p.y * m11 + m12 ) / ( screenHeight + 1.0 ) );
}

// transform point world coordinates to [0.0 1.0]
vec2 worldToIdentity( vec2 p ) {
	return vec2( (p.x * m00 + m02) * screenRatio,  ( p.y * m11 + m12 ) * screenRatio );
}

// transform vector world coordinates to [0.0 1.0]
vec2 worldToIdentityVector( vec2 p ) {
	return vec2( (p.x * m00 ) * screenRatio,  ( p.y * m11 ) * screenRatio );
}



void main() {
	//gl_Position = vec4 ( vertexPosition.x, vertexPosition.y , 0. , 1. );
	gl_Position = vec4( worldToScreen( vertexPosition ), 0., 1.);
	vertexOut.uv = vertexUV;
	vertexOut.color = vertexColor;
	vertexOut.lineWidth = lineWidth * m00 / 2.;
	vertexOut.uMax = uMax;
	vertexOut.paperUV = vertexPaperUV;
	vertexOut.p0screen = worldToIdentity(p0);
	vertexOut.p1screen = worldToIdentity(p1);
	vertexOut.p2screen = worldToIdentity(p2);
	vertexOut.n0screen = worldToIdentityVector(n0);
	vertexOut.n2screen = worldToIdentityVector(n2);
	vertexOut.position = gl_Position;
}
