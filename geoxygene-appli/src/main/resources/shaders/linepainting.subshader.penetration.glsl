#version 150

float thicknessVariationSeed = 43.214548;
uniform float thicknessVariationWavelength = 500;
uniform float thicknessVariationAmplitude = 0.5;

float shiftVariationSeed = 28.84321871;
uniform float shiftVariationWavelength = 500;
uniform float shiftVariationAmplitude = 0.1;

float pressureVariationSeed = 128.84321871;
uniform float pressureVariationWavelength = 500;
uniform float pressureVariationAmplitude = 0.5;

/* Data structure sent to subshaders */
struct DataPainting {
	float screenWidth; 		// screen width in pixels 
	float screenHeight;		// screen height in pixels
	float mapScaleDiv1000;  // map scale divide by 1000 (e.g. 1:100000 maps, this value is 100)
	int brushWidth; 	    // brush texture width in pixels
	int brushHeight;        // brush texture height in pixels
	int brushStartWidth;    // start texture length in pixels for the brush
	int brushEndWidth;      // end texture length in pixels for the brush
	float brushScale;       // size in mm of one brush pixel
	float paperScale;       // scaling factor for paper
	float sharpness;        // brush-paper blending sharpness
	
	float paperDensity;     // paper height scale factor
	float brushDensity;     // brush height scale factor
	float strokePressure;   // stroke pressure
	vec4 position;          // current point position in world coordinates
	vec2 uv;                // UV coordinates texture (u in world coordinates, v between 0 and 1)
	vec4 color;             // point color
	float thickness;        // line thickness in world coordinates
	float uMax;             // maximum u coordinate in one polyline (in wolrd coordinates)
	vec2 tan;               // tangent vector at the given point (in world coordinates)
	
};



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
 
 
 vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec4 mod289(vec4 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec4 permute(vec4 x) {
     return mod289(((x*34.0)+1.0)*x);
}

vec4 taylorInvSqrt(vec4 r)
{
  return 1.79284291400159 - 0.85373472095314 * r;
}
//https://github.com/hughsk/glsl-noise/blob/master/simplex/3d.glsl

float snoise(vec3 v)
  {
  const vec2 C = vec2(1.0/6.0, 1.0/3.0) ;
  const vec4 D = vec4(0.0, 0.5, 1.0, 2.0);

// First corner
  vec3 i = floor(v + dot(v, C.yyy) );
  vec3 x0 = v - i + dot(i, C.xxx) ;

// Other corners
  vec3 g = step(x0.yzx, x0.xyz);
  vec3 l = 1.0 - g;
  vec3 i1 = min( g.xyz, l.zxy );
  vec3 i2 = max( g.xyz, l.zxy );

  // x0 = x0 - 0.0 + 0.0 * C.xxx;
  // x1 = x0 - i1 + 1.0 * C.xxx;
  // x2 = x0 - i2 + 2.0 * C.xxx;
  // x3 = x0 - 1.0 + 3.0 * C.xxx;
  vec3 x1 = x0 - i1 + C.xxx;
  vec3 x2 = x0 - i2 + C.yyy; // 2.0*C.x = 1/3 = C.y
  vec3 x3 = x0 - D.yyy; // -1.0+3.0*C.x = -0.5 = -D.y

// Permutations
  i = mod289(i);
  vec4 p = permute( permute( permute(
             i.z + vec4(0.0, i1.z, i2.z, 1.0 ))
           + i.y + vec4(0.0, i1.y, i2.y, 1.0 ))
           + i.x + vec4(0.0, i1.x, i2.x, 1.0 ));

// Gradients: 7x7 points over a square, mapped onto an octahedron.
// The ring size 17*17 = 289 is close to a multiple of 49 (49*6 = 294)
  float n_ = 0.142857142857; // 1.0/7.0
  vec3 ns = n_ * D.wyz - D.xzx;

  vec4 j = p - 49.0 * floor(p * ns.z * ns.z); // mod(p,7*7)

  vec4 x_ = floor(j * ns.z);
  vec4 y_ = floor(j - 7.0 * x_ ); // mod(j,N)

  vec4 x = x_ *ns.x + ns.yyyy;
  vec4 y = y_ *ns.x + ns.yyyy;
  vec4 h = 1.0 - abs(x) - abs(y);

  vec4 b0 = vec4( x.xy, y.xy );
  vec4 b1 = vec4( x.zw, y.zw );

  //vec4 s0 = vec4(lessThan(b0,0.0))*2.0 - 1.0;
  //vec4 s1 = vec4(lessThan(b1,0.0))*2.0 - 1.0;
  vec4 s0 = floor(b0)*2.0 + 1.0;
  vec4 s1 = floor(b1)*2.0 + 1.0;
  vec4 sh = -step(h, vec4(0.0));

  vec4 a0 = b0.xzyw + s0.xzyw*sh.xxyy ;
  vec4 a1 = b1.xzyw + s1.xzyw*sh.zzww ;

  vec3 p0 = vec3(a0.xy,h.x);
  vec3 p1 = vec3(a0.zw,h.y);
  vec3 p2 = vec3(a1.xy,h.z);
  vec3 p3 = vec3(a1.zw,h.w);

//Normalise gradients
  vec4 norm = taylorInvSqrt(vec4(dot(p0,p0), dot(p1,p1), dot(p2, p2), dot(p3,p3)));
  p0 *= norm.x;
  p1 *= norm.y;
  p2 *= norm.z;
  p3 *= norm.w;

// Mix final noise value
  vec4 m = max(0.6 - vec4(dot(x0,x0), dot(x1,x1), dot(x2,x2), dot(x3,x3)), 0.0);
  m = m * m;
  return 42.0 * dot( m*m, vec4( dot(p0,x0), dot(p1,x1),
                                dot(p2,x2), dot(p3,x3) ) );
  } 
 
/**************************************************************************************/

// v is scaled from [0..1] to [0.5-width/2..0.5+width/2]
float vTextureScale( in float width, in float v ) {
	float scaledV = 0.5 + (v - 0.5) / width;
	if ( scaledV < 0 ) return 0;
	if ( scaledV > 1 ) return 1;
	return scaledV;
}

// return the computeStrokeWidth (0..1) depending on a linear coordinate
float computeStrokeWidth( in float u, in float uMax ) {

	vec3 p = vec3((u + thicknessVariationSeed)/thicknessVariationWavelength, 0, 0);
	return 1 - snoise(p)* thicknessVariationAmplitude;
}

// return the computeStrokeShift (-0.5..0.5) depending on a linear coordinate
float computeStrokeShift( in float u, in float uMax ) {
	return ( snoise(vec3((u + shiftVariationSeed)/shiftVariationWavelength,0,0)) ) * shiftVariationAmplitude;
}

// return the computeStrokePressure (-0.5..0.5) depending on a linear coordinate
float computeStrokePressure( in float u, in float uMax ) {
	
	float var = 1-clamp((snoise(vec3((u+pressureVariationSeed)/pressureVariationWavelength,0,0))*0.5+0.5)*pressureVariationAmplitude, 0.0, 10.0);
	return var;
		
//	return strokePressure*(0.5+0.5*var)*	smoothstep(0, 150, fragmentIn.uv.x)*	(1-smoothstep(fragmentIn.uMax-150, fragmentIn.uMax, fragmentIn.uv.x));
	//fragmentIn.uv.x/fragmentIn.uMax;
	//return strokePressure*(snoise(vec3(u + pressureVariationSeed)/pressureVariationWavelength) * (1-pressureVariationAmplitude));
}

/************************************************************************************/
vec2 computeBrushTextureCoordinates( DataPainting fragmentData ) {
	float u_w = fragmentData.uv.x;
	float u_tex = 0;
	float strokeWidth = computeStrokeWidth( fragmentData.uv.x, fragmentData.uMax);
	float v_tex = vTextureScale( strokeWidth , fragmentData.uv.y ) + strokeWidth * ( 1 - strokeWidth );

	float brushStartLength_w = fragmentData.brushStartWidth * fragmentData.brushScale;
	float brushEndLength_w = fragmentData.brushEndWidth * fragmentData.brushScale;
	float brushMiddleLength_w = (fragmentData.brushWidth - fragmentData.brushStartWidth - fragmentData.brushEndWidth) * fragmentData.brushScale;
	
	float brush0_tex = fragmentData.brushStartWidth / float(fragmentData.brushWidth);
	float brush1_tex = 1f - fragmentData.brushEndWidth / float(fragmentData.brushWidth);
	if ( u_w <= brushStartLength_w ) {
		u_tex = (u_w / brushStartLength_w) * brush0_tex;
	} else if ( u_w >= fragmentData.uMax - brushEndLength_w ) {
		u_tex = ( u_w - fragmentData.uMax ) * ( 1 - brush1_tex ) / brushEndLength_w - 1;   
	} else {
		float polylineMiddleLength_w = fragmentData.uMax - (brushStartLength_w + brushEndLength_w);
		int nbTiles = max ( int( round( polylineMiddleLength_w / brushMiddleLength_w ) ), 1 );
		int nTile = int((u_w - brushStartLength_w )/(polylineMiddleLength_w / float(nbTiles)));
		float tileSize_w = polylineMiddleLength_w / float(nbTiles);
		u_tex = mod( u_w - brushStartLength_w, tileSize_w) / tileSize_w * ( brush1_tex - brush0_tex ) + brush0_tex; 
	}
	return vec2(u_tex, v_tex);
}

/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {

	vec3 paperHeightField = paperColor.rgb;
	float brushHeightField = ( brushColor.r + brushColor.g + brushColor.b ) / 3.;
//	vec3 brushHeightField = 1-brushColor.rgb;
	
//	float penetration = 1. / computeStrokePressure( fragmentData.uv.x, fragmentData.uMax ) - ( 1 - brushHeightField * fragmentData.brushDensity ) - ( paperHeightField * fragmentData.paperDensity);
//vec3 penetration = vec3(1. / computeStrokePressure( fragmentData.uv.x, fragmentData.uMax )) - ( vec3(1.0) - brushHeightField * fragmentData.brushDensity ) - ( paperHeightField * fragmentData.paperDensity);
	
	
	float bh = fragmentData.strokePressure * computeStrokePressure( fragmentData.uv.x, fragmentData.uMax ) * brushHeightField * fragmentData.brushDensity;
	float ph = (0.5 + (paperHeightField.x-0.5) * fragmentData.paperDensity);
	float penetration = clamp( ph - (1-bh), -1, 1);
	float f = smoothstep( 0.0 - fragmentData.sharpness, 0.0 + fragmentData.sharpness,  penetration );
	
	//return vec4(1,0,0,1);
	return vec4( fragmentData.color.rgb * brushColor.rgb, fragmentData.color.a * ( 1-f ) );
}
