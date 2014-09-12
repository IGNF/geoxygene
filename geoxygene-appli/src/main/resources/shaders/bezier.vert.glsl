#version 400



uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;
uniform float fboWidth=5000;
uniform float fboHeight=5000;


in vec2 vertexPosition;
in vec2 vertexUV;
in vec4 vertexColor;
in float lineWidth;
in float uMax;
in vec2 p0;
in vec2 p1;
in vec2 p2;
in vec2 n0;
in vec2 n2;


out VertexData {
	vec2 uv;
	vec4 color;
	float lineWidth;
	float uMax;
	flat vec2 p0screen;
	flat vec2 p1screen;
	flat vec2 p2screen;
	flat vec2 n0screen;
	flat vec2 n2screen;
} vertexOut;

float screenRatio = fboWidth / screenWidth;

// transform world coordinates to [-1 +1]
vec2 worldToScreen( vec2 p ) {
	return vec2( -1 + 2 * (p.x * m00 + m02) / (screenWidth + 1), 1 - 2 * ( p.y * m11 + m12 ) / ( screenHeight + 1 ) );
}

// transform point world coordinates to [0 1]
vec2 worldToIdentity( vec2 p ) {
	return vec2( (p.x * m00 + m02) * screenRatio,  ( p.y * m11 + m12 ) * screenRatio );
}

// transform vector world coordinates to [0 1]
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
	vertexOut.p0screen = worldToIdentity(p0);
	vertexOut.p1screen = worldToIdentity(p1);
	vertexOut.p2screen = worldToIdentity(p2);
	vertexOut.n0screen = worldToIdentityVector(n0);
	vertexOut.n2screen = worldToIdentityVector(n2);
}
