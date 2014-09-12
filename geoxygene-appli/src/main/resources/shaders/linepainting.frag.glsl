#version 150 core
// _w : length in world coordinates (meters)
// _pix : length in pixels
// _mm : length in millimeters
// _tex : length in textures coordinates (0..1)

in VertexData {
	vec4 position;
	vec2 uv;
	vec2 paperUV;
	vec4 color;
	float curvature;
	float thickness;
	float uMax;
} fragmentIn;

uniform float screenWidth;
uniform float screenHeight;
uniform sampler2D paperSampler;
uniform sampler2D brushSampler;
uniform float mapScaleDiv1000 = 0.; // map scale
uniform int brushWidth = 0; // brush texture width (pixels)
uniform int brushHeight = 0; // brush texture height (pixels)
uniform int brushStartWidth = 0; // brush texture width (pixels)
uniform int brushEndWidth = 0; // brush texture height (pixels)
uniform float brushScale = 0; // size in mm of one brush pixel
uniform float paperScale = 0; // scaling factor for paper
uniform float sharpness = 0; // brush-paper blending sharpness

uniform float paperDensity = 0.3; // paper height scale factor
uniform float brushDensity = 1.0; // brush height scale factor
uniform float strokePressure = 1; // stroke pressure


out vec4 outColor;

struct Data {
	float screenWidth;
	float screenHeight;
	float mapScaleDiv1000; // map scale
	int brushWidth; // brush texture width (pixels)
	int brushHeight; // brush texture height (pixels)
	int brushStartWidth; // brush texture width (pixels)
	int brushEndWidth; // brush texture height (pixels)
	float brushScale; // size in mm of one brush pixel
	float paperScale; // scaling factor for paper
	float sharpness; // brush-paper blending sharpness
	
	float paperDensity; // paper height scale factor
	float brushDensity; // brush height scale factor
	float strokePressure; // stroke pressure
	vec4 position;
	vec2 uv;
	vec2 paperUV;
	vec4 color;
	float curvature;
	float thickness;
	float uMax;
};

/***********************************************************
 *             Generic algorithm methods                   *
 * these methods have to be defined in another mini shader *
 ***********************************************************/ 
 
vec2 computeBrushTextureCoordinates( in Data fragmentIn );
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in Data fragmentData );


/************************************************************
 *                       MAIN                               *
 ************************************************************/

void main() {
	Data fragmentData = Data(screenWidth, screenHeight, mapScaleDiv1000, brushWidth, brushHeight,
		brushStartWidth, brushEndWidth, brushScale, paperScale, sharpness, paperDensity, brushDensity, strokePressure,
		fragmentIn.position, fragmentIn.uv, fragmentIn.paperUV, fragmentIn.color, fragmentIn.curvature, fragmentIn.thickness, fragmentIn.uMax
		);
	vec2 brushUV = computeBrushTextureCoordinates( fragmentData );
	
	vec4 brushColor = texture( brushSampler, brushUV );
	vec4 paperColor = texture( paperSampler, fragmentIn.paperUV );
	
	outColor = computeFragmentColor( brushColor, paperColor, fragmentData );
	
}

