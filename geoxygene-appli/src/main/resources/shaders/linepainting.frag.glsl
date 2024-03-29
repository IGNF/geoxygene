// line painting and bezier (vertex and fragment) shaders share
// the same API.
// Line painting is associated with <StrokeTextureExpressiveRendering> tag in SLD 
// Bezier is associated with <ExpressiveStroke> tag in SLD (and previously <BasicTextureExpressiveRendering>
// They only differ by how lines are tesselated. Bezier is better (smoother) than LinePainting
// LinePainting should be now considered as deprecated 
#version 330
// _w : length in world coordinates (meters)
// _pix : length in pixels
// _mm : length in millimeters
// _tex : length in textures coordinates (0..1)

in VertexData {
	vec4 position;
	vec2 uv;
	vec4 color;
	float curvature;
	float thickness;
	float uMax;
	vec2 paperUV;
} fragmentIn;

uniform float screenWidth;
uniform float screenHeight;
uniform float fboWidth;
uniform float fboHeight;
uniform sampler2D paperTexture;
uniform sampler2D brushTexture;
uniform float mapScaleDiv1000 = 0.; // map scale
uniform int brushWidth = 0; // brush texture width (pixels)
uniform int brushHeight = 0; // brush texture height (pixels)
uniform int brushStartWidth = 0; // brush texture width (pixels)
uniform int brushEndWidth = 0; // brush texture height (pixels)
uniform float brushScale = 0; // size in mm of one brush pixel
uniform float paperScale = 0; // scaling factor for paper
uniform float strokeSoftness = 0; // brush-paper blending strokeSoftness

uniform float paperRoughness = 0.3; // paper height scale factor
uniform float brushRoughness = 1.0; // brush height scale factor
uniform float strokePressure = 1; // stroke pressure


out vec4 outColor;

/* Data structure sent to subshaders */
struct DataPainting {
    float screenWidth;      // screen width in pixels 
    float screenHeight;     // screen height in pixels
    float mapScaleDiv1000;  // map scale divide by 1000 (e.g. 1:100000 maps, this value is 100)
    int brushWidth;         // brush texture width in pixels
    int brushHeight;        // brush texture height in pixels
    int brushStartWidth;    // start texture length in pixels for the brush
    int brushEndWidth;      // end texture length in pixels for the brush
    float brushScale;       // size in mm of one brush pixel
    float paperScale;       // scaling factor for paper
    float strokeSoftness;   // brush-paper blending strokeSoftness
    
    float paperRoughness;     // paper height scale factor
    float brushRoughness;     // brush height scale factor
    float strokePressure;   // stroke pressure
    vec4 position;          // current point position in world coordinates
    vec2 uv;                // UV coordinates texture (u in world coordinates, v between 0 and 1)
    vec4 color;             // point color
    float thickness;        // line thickness in world coordinates
    float uMax;             // maximum u coordinate in one polyline (in wolrd coordinates)
    vec2 tan;               // tangent vector at the given point (in world coordinates)
    float curvature;        // signed curvature estimation
    
};


/***********************************************************
 *             Generic algorithm methods                   *
 * these methods have to be defined in another mini shader *
 ***********************************************************/ 
 
vec2 computeBrushTextureCoordinates( in DataPainting fragmentIn );
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData );

/************************************************************
 *                       MAIN                               *
 ************************************************************/

void main() {
 
	DataPainting fragmentData = DataPainting(screenWidth, screenHeight, mapScaleDiv1000, brushWidth, brushHeight,
		brushStartWidth, brushEndWidth, brushScale, paperScale, strokeSoftness, paperRoughness, brushRoughness, strokePressure,
		fragmentIn.position, fragmentIn.uv, fragmentIn.color, fragmentIn.thickness, fragmentIn.uMax, vec2(0,0), 0.0
		);
	vec2 brushUV = computeBrushTextureCoordinates( fragmentData );
	
	vec4 brushColor = texture( brushTexture, brushUV );
	vec4 paperColor = texture( paperTexture, fragmentIn.paperUV);
	
	outColor = computeFragmentColor( brushColor, paperColor, fragmentData );
}

