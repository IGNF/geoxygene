#version 150 core
// _w : length in world coordinates (meters)
// _pix : length in pixels
// _mm : length in millimeters
// _tex : length in textures coordinates (0..1)

in vec2 fragmentUV;
in float fragmentCurvature;
in float fragmentThickness;
in vec4 fragmentColor;
in float uMax_w;

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

uniform float paperDensity = 0.3; // paper height scale factor
uniform float brushDensity = 1.0; // brush height scale factor
uniform float strokePressure = 1; // stroke pressure

float thicknessVariationSeed = 43.214548;
float thicknessVariationFrequency = 0.05;
float thicknessVariationAmplitude = 0.5;

float shiftVariationSeed = 28.84321871;
float shiftVariationFrequency = 0.05;
float shiftVariationAmplitude = 0.1;

float pressureVariationSeed = 128.84321871;
float pressureVariationFrequency = 0.05;
float pressureVariationAmplitude = 0.5;

out vec4 outColor;



/************************************************************
 *                       NOISE                              *
 ************************************************************/
float noise2D1D(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float noise1D1D(float co){
    return fract(sin(co * 78.233 + 12.9898) * 43758.5453);
}

vec4 rand(vec2 A,vec2 B,vec2 C,vec2 D){ 
        vec2 s=vec2(12.9898,78.233); 
        vec4 tmp=vec4(dot(A,s),dot(B,s),dot(C,s),dot(D,s)); 
        return fract(sin(tmp) * 43758.5453)* 2.0 - 1.0; 
 } 
 
 
float myNoise(vec2 coord,float d){ 
 
        vec2 C[4]; 
        float d1 = 1.0/d;
        C[0]=floor(coord*d)*d1; 
        C[1]=C[0]+vec2(d1,0.0); 
        C[2]=C[0]+vec2(d1,d1); 
        C[3]=C[0]+vec2(0.0,d1);
 
        vec2 p=fract(coord*d); 
        vec2 q=1.0-p; 
        vec4 w=vec4(q.x*q.y,p.x*q.y,p.x*p.y,q.x*p.y); 
        return dot(vec4(rand(C[0],C[1],C[2],C[3])),w); 
} 

float myNoise(float coord,float d){
	return myNoise( vec2(coord), d);
} 
 
 
/************************************************************
 *                       MAIN                               *
 ************************************************************/
// v is scaled from [0..1] to [0.5-width/2..0.5+width/2]
float vTextureScale( in float width, in float v ) {
	float scaledV = 0.5 + (v - 0.5) / width;
	if ( scaledV < 0 ) return 0;
	if ( scaledV > 1 ) return 1;
	return scaledV;
}

// return the computeStrokeWidth (0..1) depending on a linear coordinate
float computeStrokeWidth( in float u ) {
	return 1 - myNoise(u + thicknessVariationSeed, thicknessVariationFrequency) * thicknessVariationAmplitude;
}

// return the computeStrokeShift (-0.5..0.5) depending on a linear coordinate
float computeStrokeShift( in float u ) {
	return ( myNoise(u + shiftVariationSeed, shiftVariationFrequency) - 0.5) * shiftVariationAmplitude;
}

// return the computeStrokePressure (-0.5..0.5) depending on a linear coordinate
float computeStrokePressure( in float u ) {
	return strokePressure + ( myNoise(u + pressureVariationSeed, pressureVariationFrequency) - 0.5) * pressureVariationAmplitude;
}



void main() {
	float u_w = fragmentUV.x;
	float u_tex = 0;
	float strokeWidth = computeStrokeWidth( fragmentUV.x );
	float v_tex = vTextureScale( strokeWidth , fragmentUV.y ) + computeStrokeShift( fragmentUV.x ) * ( 1 - strokeWidth );
	vec4 partColor;

	float brushStartLength_w = brushStartWidth * brushScale;
	float brushEndLength_w = brushEndWidth * brushScale;
	float brushMiddleLength_w = (brushWidth - brushStartWidth - brushEndWidth) * brushScale;
	
	float brush0_tex = brushStartWidth / float(brushWidth);
	float brush1_tex = 1f - brushEndWidth / float(brushWidth);
	if ( u_w <= brushStartLength_w ) {
		u_tex = (u_w / brushStartLength_w) * brush0_tex;
		partColor = vec4(0.5,0,0,1);   
	} else if ( u_w >= uMax_w - brushEndLength_w ) {
		u_tex = ( u_w - uMax_w ) * ( 1 - brush1_tex ) / brushEndLength_w - 1;   
		partColor = vec4(0,0,0.5,1);   
	} else {
		float polylineMiddleLength_w = uMax_w - (brushStartLength_w + brushEndLength_w);
		int nbTiles = max ( int( round( polylineMiddleLength_w / brushMiddleLength_w ) ), 1 );
		int nTile = int((u_w - brushStartLength_w )/(polylineMiddleLength_w / float(nbTiles)));
		float tileSize_w = polylineMiddleLength_w / float(nbTiles);
		u_tex = mod( u_w - brushStartLength_w, tileSize_w) / tileSize_w * ( brush1_tex - brush0_tex ) + brush0_tex; 
		partColor = vec4(0,1,0,1);   
	}
	vec4 brushColor = texture( brushSampler, vec2(u_tex, v_tex));

	vec2 fragScreenCoordinates = vec2( gl_FragCoord.x / screenWidth, gl_FragCoord.y / screenHeight );
	vec4 paperColor = texture( paperSampler, fragScreenCoordinates / paperScale );
//	float paperHeight = ( paperColor.r + paperColor.g + paperColor.b ) / 3.;
	vec3 paperHeight = paperColor.rgb;

//	float brushHeight = ( brushColor.r + brushColor.g + brushColor.b ) / 3.;
	vec3 brushHeight = brushColor.rgb;
	
//	float penetration = 1. / computeStrokePressure( fragmentUV.x ) - ( 1 - brushHeight * brushDensity ) - ( paperHeight * paperDensity);
	vec3 penetration = vec3(1. / computeStrokePressure( fragmentUV.x )) - ( vec3(1.0) - brushHeight * brushDensity ) - ( paperHeight * paperDensity);
	
//	float f = smoothstep( -1, 1,  penetration );
	vec3 f = smoothstep( -1, 1,  penetration );
//	outColor = vec4( brushColor.rgb, 1);
//	outColor = vec4( vec3( 0.5 ), 1.0 );
//	outColor = vec4( fragmentUV, 0, 1. );
	outColor = vec4( f, 1-(f.x+f.y+f.z)/3. );
//	outColor = vec4( vec3( paperHeight ), 1.0 );

// outColor = vec4(	vec3( myNoise( fragmentUV.xy / vec2(10.,.1), 5. ) +0.5 ) ,	 1.0);
//	outColor  = vec4( fragmentUV, 0., 1. );
}


