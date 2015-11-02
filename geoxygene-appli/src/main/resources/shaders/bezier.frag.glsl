// line painting and bezier (vertex and fragment) shaders share
// the same API.
// Line painting is associated with <StrokeTextureExpressiveRendering> tag in SLD 
// Bezier is associated with <ExpressiveStroke> tag in SLD (and previously <BasicTextureExpressiveRendering>
// They only differ by how lines are tesselated. Bezier is better (smoother) than LinePainting
// LinePainting should be now considered as deprecated 
#version 330

in VertexData {
	vec4 position;
	vec2 uv;
	vec4 color;
	float lineWidth;
	float uMax;
	vec2 paperUV;
    vec2 p0;
    vec2 p1;
    vec2 p2;
    vec2 n0;
    vec2 n2;
	flat vec2 p0screen;
	flat vec2 p1screen;
	flat vec2 p2screen;
	flat vec2 n0screen;
	flat vec2 n2screen;
} fragmentIn;

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

uniform float screenWidth;
uniform float screenHeight;
uniform float fboWidth;
uniform float fboHeight;
uniform sampler2D brushTexture;
uniform sampler2D paperTexture;
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

/*************************************************************************************
 *                                     BEZIER                                        *
 *************************************************************************************/	
float det( vec2 v1, vec2 v2 ) {
	return v2.y * v1.x - v1.y * v2.x;
}

float quadraticValue(float p0, float p1, float p2, float t) {
  return p0 * (1 - t) * (1 - t) + 2 * p1 * t * (1 - t) + p2 * t * t;
}

float quadraticDerivative(float p0, float p1, float p2, float t) {
  return -2 * p0 * (1 - t) + 2 * p1 * (1 - 2 * t) + 2 * p2 * t;
}

float quadraticSecondDerivative(float p0, float p1, float p2, float t) {
  return 2 * p0 - 4 * p1 + 2 * p2;
}

float cuberoot( float x )
{
	if( x<0.0 ) return -pow(-x,1.0/3.0);
	return pow(x,1.0/3.0);
}

int solveCubic(in float a, in float b, in float c, out float r[3])
{
	float  p = b - a*a / 3.0;
	float  q = a * (2.0*a*a - 9.0*b) / 27.0 + c;
	float p3 = p*p*p;
	float  d = q*q + 4.0*p3 / 27.0;
	float offset = -a / 3.0;
	if(d >= 0.0) { // Single solution
		float z = sqrt(d);
		float u = (-q + z) / 2.0;
		float v = (-q - z) / 2.0;
		u = cuberoot(u);
		v = cuberoot(v);
		r[0] = offset + u + v;
		return 1;
	}
	float u = sqrt(-p / 3.0);
	float v = acos(-sqrt( -27.0 / p3) * q / 2.0) / 3.0;
	float m = cos(v), n = sin(v)*1.732050808;
	r[0] = offset + u * (m + m);
	r[1] = offset - u * (n + m);
	r[2] = offset + u * (n - m);
	return 3;
}


vec2 DistanceToQBSpline(in vec2 P0, in vec2 P1, in vec2 P2, in vec2 p, in vec2 n0, in vec2 n2)
{
	vec2  sb = (P1 - P0) * 2.0;
	vec2  sc = P0 - P1 * 2.0 + P2;
	vec2  sd = P1 - P0;
	vec2 P0P2 = P2 - P0;
	float dotSc = dot(sc, sc);
	vec2  D = P0 - p;
	float sB = 3.0 * dot(sd, sc);
	float sC = 2.0 * dot(sd, sd);
	
	// singular case: bezier curve is a straight line
	if ( abs(dotSc) < 0.001 ) {
		float normsd = sqrt(sC/2);
		float u = abs( dot( P0P2, D ) / (dot(P0P2,P0P2) ));
//		return vec2(u,500000);
			return vec2(u, ( D.y * sd.x - D.x * sd.y ) / normsd );
	}
	
	float sA = 1.0 / dotSc;
	

	float a = sA;
	float b = sB;
	float c = sC + dot(D, sc);
	float d = dot(D, sd);
	float cusp = sign(det (P2-P0, P1-P0));

   	float t[3];
	int n = solveCubic(b*a, c*a, d*a, t);

   	float dis[3];
   	int nMin = 0;
   	int i;
	for ( i = 0; i < n; i++ ) {
		vec2 pos = P0 + ( sb + sc * t[i] ) * t[i];
		vec2 tan = 2 * ( t[i] * sc - sb );
		vec2 dp = pos - p;
		dis[i] = -cusp * sign( dot( dp, tan ) ) * length( dp );
		if ( abs(dis[i]) < abs(dis[nMin]) ) {
			nMin = i;
		}
	} 
	if ( t[nMin] < 0.01 ) { // special case to avoid numeric precision artifacts
		float normsd = sqrt(sC/2);
		float u = abs( dot( P0P2, D ) / (dot(P0P2,P0P2) ));
			return vec2(t[nMin], ( D.y * sd.x - D.x * sd.y ) / normsd );
	}

	
	return vec2( t[nMin], dis[nMin]);
}




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
	vec2 p = vec2( gl_FragCoord.x , fboHeight - gl_FragCoord.y  );
	
	vec2 p0 = fragmentIn.p0screen;
	vec2 p1 = fragmentIn.p1screen;
	vec2 p2 = fragmentIn.p2screen;
	vec2 n0 = fragmentIn.n0screen;
	vec2 n2 = fragmentIn.n2screen;
	
	vec2 uv = DistanceToQBSpline(p0, p1, p2, p, n0, n2 );
	float lineSoftness = 1.0;
	float t = uv.x; // t solution closest point [0..1]
	uv.x = fragmentIn.uv.x + t * ( fragmentIn.uv.y - fragmentIn.uv.x ); // scale uv
	float screenRatio = fboWidth / screenWidth;
	uv.y =  (1 + uv.y / fragmentIn.lineWidth / screenRatio) /2.0 ;
//	if ( uv.y > 1.0 ) { outColor = vec4( 0.0, 0.0, 1.0, 1.0); return; }
//	if ( uv.y < 0.0 ) { outColor = vec4( 0.0, 1.0, 0.0, 1.0); return; }
	if ( uv.y < 0.0 || uv.y > 1.0 ) { discard; }

    // compute tangent
	vec2 tangent = vec2( quadraticDerivative(fragmentIn.p0.x, fragmentIn.p1.x, fragmentIn.p2.x, t), quadraticDerivative(fragmentIn.p0.y, fragmentIn.p1.y, fragmentIn.p2.y, t) );
	
/*
	// compute false curvature as the min of the cross product between p0p1.tangent and p2p1.tangent
	vec2 tangent1 = normalize(tangent);
	vec2 p1p21= normalize( fragmentIn.p2 - fragmentIn.p1);
	vec2 p1p01= normalize( fragmentIn.p0 - fragmentIn.p1);
    float nn1 = tangent1.x * p1p21.y - tangent1.y * p1p21.x;
    float nn2 = tangent1.x * p1p01.y - tangent1.y * p1p01.x;
    float curvature = nn1;
    if (abs(nn2) < abs(nn1)) { curvature = nn2; } 
*/
	// exact curvature computation
    float xpp1 = quadraticSecondDerivative(fragmentIn.p0.x, fragmentIn.p1.x, fragmentIn.p2.x, t);
    float ypp1 = quadraticSecondDerivative(fragmentIn.p0.y, fragmentIn.p1.y, fragmentIn.p2.y, t);
    float c2 = (tangent.x * ypp1 - tangent.y * xpp1 )/ pow( length(tangent), 3); 
    float curvature = sqrt( abs(c2) ) * sign( c2 );

	
	DataPainting fragmentData = DataPainting(screenWidth, screenHeight, mapScaleDiv1000, brushWidth, brushHeight,
		brushStartWidth, brushEndWidth, brushScale, paperScale, strokeSoftness, paperRoughness, brushRoughness, strokePressure,
		fragmentIn.position, uv, fragmentIn.color, fragmentIn.lineWidth, fragmentIn.uMax, tangent, curvature );
	vec2 brushUV = computeBrushTextureCoordinates( fragmentData );
	
	vec4 brushColor = texture( brushTexture, brushUV );
	vec4 paperColor = texture( paperTexture, fragmentIn.paperUV );
	
	outColor = computeFragmentColor( brushColor, paperColor, fragmentData );

}
