#version 410

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

uniform int nb_stripes;
uniform int time;
uniform float globalOpacity = 1.0;

#define PI 3.1415926535897932384626433832795

// v is scaled from [0..1] to [0.5-width/2..0.5+width/2.0]
float vTextureScale( in float width, in float v ) {
	float scaledV = 0.5 + (v - 0.5) / width;
	if ( scaledV < 0.0 ) return 0.0;
	if ( scaledV > 1.0 ) return 1.0;
	return scaledV;
}

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




/************************************************************************************/
vec2 computeBrushTextureCoordinates( DataPainting fragmentData ) {
	vec2 uv = vec2( fragmentData.uv.x / 100.0, fragmentData.uv.y );
	return uv;
}

/************************************************************************************/
vec4 computeFragmentColorStripes( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {

	float v = fragmentData.uv.y;
	if ( int( v * nb_stripes) % 2 == 0.0  ) { discard; } 
	return vec4( brushColor.rgb , 1.0 );
}

/************************************************************************************/
vec4 computeFragmentColorAnimatedStripes( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {

	float v = fragmentData.uv.y;
	int n = time / 100 % 20;
	if ( int( v * n) % 2 == 0.0  ) { discard; } 
	return vec4( brushColor.rgb , 1 );
}

/************************************************************************************/
vec4 computeFragmentColorDots( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {

	float dotSize = fragmentData.thickness;
	float dotSpace = 2 * fragmentData.thickness;
	float x = mod( fragmentData.uv.x, dotSpace ) - dotSpace / 2.0;
	float y = (fragmentData.uv.y * 2.0 - 1.0 ) * fragmentData.thickness;
	float d = sqrt( x * x + y * y ); 
	if ( d >  dotSize  ) { discard; } 
	return vec4( brushColor.rgb , brushColor.a );
}

/************************************************************************************/
#define TAU 6.28318530718
#define MAX_ITER 1

vec4 computeFragmentColorWater( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {
	vec2 sp = vec2( fragmentData.uv.x, fragmentData.uv.y * fragmentData.thickness);
	
    sp.x = mod( sp.x, 1.0 );
    sp.y = mod( sp.y, 1.0 );
	
	vec2 p = sp;
	vec2 i = p;
	float c = 1.0;
	float inten = .5;

	for (int n = 0; n < MAX_ITER; n++) 
	{
		float t = time * (1.0 - (3.5 / float(n+1)));
		i = p + vec2(cos(t - i.x) + sin(t + i.y), sin(t - i.y) + cos(t + i.x));
		c += 1.0/length(vec2(p.x / (sin(i.x+t)/inten),p.y / (cos(i.y+t)/inten)));
	}
	c /= float(MAX_ITER);
	c = 1.55-sqrt(c);
	vec3 colour = vec3(pow(abs(c), 6.0));
	return vec4(clamp(colour + vec3(0.0, 0.35, 0.5), 0.0, 1.0), 1.0);
}

/************************************************************************************/
vec4 computeFragmentColorMandelbrot( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {

    vec2 p = vec2( fragmentData.uv.x / 100 , -1 + 2 * fragmentData.uv.y );

    //float zoo = 1.0/250.0;
	
	float zoo = 1.0/250.0;
	float ftime = time /200.;
	zoo = 1.0/(400.0 - 150.0*sin(0.15*ftime-0.3));
	
	vec2 cc = vec2(-0.533516,0.526141) + p*zoo;

	vec2 t2c = vec2(-0.5,2.0);
	t2c += 0.5*vec2( cos(0.13*(ftime-10.0)), sin(0.13*(ftime-10.0)) );
		
    // iterate
    vec2 z  = vec2(0.0);
    vec2 dz = vec2(0.0);
	float trap1 = 0.0;
	float trap2 = 1e20;
	float co2 = 0.0;
    for( int i=0; i<150; i++ )
    {
        if( dot(z,z)>1024.0 ) continue;

		// Z' -> 2·Z·Z' + 1
        dz = 2.0*vec2(z.x*dz.x-z.y*dz.y, z.x*dz.y + z.y*dz.x ) + vec2(1.0,0.0);
			
        // Z -> Z² + c			
        z = cc + vec2( z.x*z.x - z.y*z.y, 2.0*z.x*z.y );
			
        // trap 1
		float d1 = abs(dot(z-vec2(0.0,1.0),vec2(0.707)));
		float ff = step( d1, 1.0 );
		co2 += ff;
		trap1 += ff*d1;

		//trap2
		trap2 = min( trap2, dot(z-t2c,z-t2c) );
    }

    // distance, d(c) = |Z|·log|Z|/|Z'|
	float d = sqrt( dot(z,z)/dot(dz,dz) )*log(dot(z,z));
	
	float c1 = pow( clamp( 2.00*d/zoo,    0.0, 1.0 ), 0.5 );
	float c2 = pow( clamp( 1.5*trap1/co2, 0.0, 1.0 ), 2.0 );
	float c3 = pow( clamp( 0.4*trap2, 0.0, 1.0 ), 0.25 );

	vec3 col1 = 0.5 + 0.5*sin( 3.0 + 4.0*c2 + vec3(0.0,0.5,1.0) );
	vec3 col2 = 0.5 + 0.5*sin( 4.1 + 2.0*c3 + vec3(1.0,0.5,0.0) );
	vec3 col = 2.0*sqrt(c1*col1*col2);

	return vec4( col, 1.0 );
}





/************************************************************************************/
vec4 computeFragmentColor( in vec4 brushColor, in vec4 paperColor, in DataPainting fragmentData ) {
	return computeFragmentColorWater( brushColor, paperColor, fragmentData );
//	return computeFragmentColorMandelbrot( brushColor, paperColor, fragmentData );
//	return computeFragmentColorStripes( brushColor, paperColor, fragmentData );
//	return computeFragmentColorDots( brushColor, paperColor, fragmentData );
}
