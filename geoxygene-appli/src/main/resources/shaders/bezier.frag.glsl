#version 400

in VertexData {
	vec2 uv;
	vec4 color;
	float lineWidth;
	float uMax;
	flat vec2 p0screen;
	flat vec2 p1screen;
	flat vec2 p2screen;
	flat vec2 n0screen;
	flat vec2 n2screen;
} fragmentIn;


uniform sampler2D colorTexture1;
uniform float globalOpacity = 1;
uniform float screenWidth;
uniform float screenHeight;
uniform float fboWidth;
uniform float fboHeight;

out vec4 outColor;


vec4 computeColor1( vec2 us, float uMax, vec4 gl_FragCoord, float screenWidth, float screenHeight, float fboWidth, float fboHeight );
	
float det( vec2 v1, vec2 v2 ) {
	return v2.y * v1.x - v1.y * v2.x;
}

float quadraticValue(float p0, float p1, float p2, float t) {
  return p0 * (1 - t) * (1 - t) + 2 * p1 * t * (1 - t) + p2 * t * t;
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
	return vec2( t[nMin], dis[nMin]);
}

void main() {
	//outColor = vec4( 0,0,1,1); return;
	vec2 p = vec2( gl_FragCoord.x , fboHeight - gl_FragCoord.y  );
	
	vec2 p0 = fragmentIn.p0screen;
	vec2 p1 = fragmentIn.p1screen;
	vec2 p2 = fragmentIn.p2screen;
	vec2 n0 = fragmentIn.n0screen;
	vec2 n2 = fragmentIn.n2screen;
	
	vec2 uv = DistanceToQBSpline(p0, p1, p2, p, n0, n2 );
	float lineSoftness = 1.0;
	uv.x = fragmentIn.uv.x + uv.x * ( fragmentIn.uv.y - fragmentIn.uv.x );
	uv.x /= 10;
	float screenRatio = fboWidth / screenWidth;
	uv.y =  (1 + uv.y / fragmentIn.lineWidth / screenRatio) /2 ;
//	if ( uv.y > 1 ) { outColor = vec4( 0, 0, 1, 1); return; }
//	if ( uv.y < 0 ) { outColor = vec4( 0, 1, 0, 1); return; }
	if ( uv.y < 0 || uv.y > 1 ) { discard; }
	
	vec4 tcolor = computeColor1( uv, fragmentIn.uMax, gl_FragCoord, screenWidth, screenHeight, fboWidth, fboHeight );
//	tcolor = vec4( uv.x, uv.y, 0 , 1);
	outColor = vec4( uv.xy,0 , fragmentIn.color.a  );
//	outColor = vec4( tcolor.rgb * fragmentIn.color.rgb, fragmentIn.color.a * tcolor.a * globalOpacity );
	
}


vec4 computeColor1( vec2 uv, float uMax, vec4 gl_FragCoord, float screenWidth, float screenHeight, float fboWidth, float fboHeight ) {
	return texture( colorTexture1, uv / vec2(10,1) );
}