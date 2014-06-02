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
uniform float sharpness = 0; // brush-paper blending sharpness

uniform float paperDensity = 0.3; // paper height scale factor
uniform float brushDensity = 1.0; // brush height scale factor
uniform float strokePressure = 1; // stroke pressure

float thicknessVariationSeed = 43.214548;
uniform float thicknessVariationFrequency = 0.05;
uniform float thicknessVariationAmplitude = 0.5;

float shiftVariationSeed = 28.84321871;
uniform float shiftVariationFrequency = 0.05;
uniform float shiftVariationAmplitude = 0.1;

float pressureVariationSeed = 128.84321871;
uniform float pressureVariationFrequency = 0.05;
uniform float pressureVariationAmplitude = 0.5;

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
	vec3 p = vec3((u + thicknessVariationSeed)*thicknessVariationFrequency, 0, 0);
	return 1 - snoise(p)* thicknessVariationAmplitude;
}

// return the computeStrokeShift (-0.5..0.5) depending on a linear coordinate
float computeStrokeShift( in float u ) {
	return ( snoise(vec3((u + shiftVariationSeed)*shiftVariationFrequency,0,0)) ) * shiftVariationAmplitude;
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
	vec3 f = smoothstep( -sharpness, sharpness,  penetration );
//	outColor = vec4( brushColor.rgb, 1);
//	outColor = vec4( vec3( 0.5 ), 1.0 );
//	outColor = vec4( fragmentUV, 0, 1. );
	outColor = vec4( f, 1-(f.x+f.y+f.z)/3. );
//	outColor = vec4( vec3( paperHeight ), 1.0 );

// outColor = vec4(	vec3( myNoise( fragmentUV.xy / vec2(10.,.1), 5. ) +0.5 ) ,	 1.0);
//	outColor  = vec4( fragmentUV, 0., 1. );
//outColor = vec4(max(computeStrokeShift( fragmentUV.x ),0),max(-computeStrokeShift( fragmentUV.x ),0),0, 1);
}


