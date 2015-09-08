// line painting and bezier (vertex and fragment) shaders share
// the same API.
// Line painting is associated with <StrokeTextureExpressiveRendering> tag in SLD 
// Bezier is associated with <ExpressiveStroke> tag in SLD (and previously <BasicTextureExpressiveRendering>
// They only differ by how lines are tesselated. Bezier is better (smoother) than LinePainting
// LinePainting should be now considered as deprecated 

#version 330


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
uniform float brushScale = 0.0; // size in mm of one brush pixel


layout(location = 0) in vec2 vertexPosition;
layout(location = 1) in vec2 vertexUV;
layout(location = 2) in vec2 vertexNormal;
layout(location = 3) in float vertexCurvature;
layout(location = 4) in float vertexThickness;
layout(location = 5) in vec4 vertexColor;
layout(location = 6) in float uMax;
layout(location = 7) in vec2 vertexPaperUV;

out VertexData {
	vec4 position;
	vec2 uv;
	vec4 color;
	float curvature;
	float thickness;
	float uMax;
	vec2 paperUV;
} vertexOut;

void main() {

	//gl_Position = vec4 ( vertexPosition , 1f );
	gl_Position = vec4( -1.0 + 2.0 * (vertexPosition.x * m00 + m02) / (screenWidth + 1.0), 1.0 - 2.0 * ( vertexPosition.y * m11 + m12 ) / ( screenHeight + 1.0 ), 0.0, 1.0);
	vertexOut.position = gl_Position;
	vertexOut.uv = vertexUV ;
	vertexOut.paperUV = vertexPaperUV;
	vertexOut.curvature = vertexCurvature;
	vertexOut.thickness = vertexThickness;
	vertexOut.color = vertexColor;
	vertexOut.uMax = uMax;
	
}