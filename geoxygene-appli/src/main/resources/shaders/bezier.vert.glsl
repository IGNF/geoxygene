#version 400



uniform float m00 = 1.; // X homothetic value in 3x3 matrix
uniform float m02 = 0.; // X translation value in 3x3 matrix
uniform float m11 = 1.; // Y homothetic value in 3x3 matrix
uniform float m12 = 0.; // Y translation value in 3x3 matrix
uniform float screenWidth;
uniform float screenHeight;


in vec2 vertexPosition;
in vec2 vertexUV;
in vec4 vertexColor;
in float lineWidth;
in float uMax;
flat in vec2 p0;
flat in vec2 p1;
flat in vec2 p2;


out VertexData {
	vec2 uv;
	vec4 color;
	float lineWidth;
	float uMax;
	vec2 p0screen;
	vec2 p1screen;
	vec2 p2screen;
} vertexOut;

// transform world coordinates to [-1 +1]
vec2 worldToScreen( vec2 p ) {
	return vec2( -1 + 2 * (p.x * m00 + m02) / (screenWidth + 1), 1 - 2 * ( p.y * m11 + m12 ) / ( screenHeight + 1 ) );
}

// transform world coordinates to [0 1]
vec2 worldToIdentity( vec2 p ) {
	return vec2( (p.x * m00 + m02), screenHeight - ( p.y * m11 + m12 ) );
}

void main() {
	//gl_Position = vec4 ( vertexPosition.x, vertexPosition.y , 0f , 1f );
	gl_Position = vec4( worldToScreen( vertexPosition ), 0f, 1f);
	vertexOut.uv = vertexUV;
	vertexOut.color = vertexColor;
	vertexOut.lineWidth = lineWidth * m00 / 2.;
	vertexOut.uMax = uMax;
	vertexOut.p0screen = worldToIdentity(p0);
	vertexOut.p1screen = worldToIdentity(p1);
	vertexOut.p2screen = worldToIdentity(p2);
}
