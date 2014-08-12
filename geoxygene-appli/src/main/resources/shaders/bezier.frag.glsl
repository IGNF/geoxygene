#version 400

in VertexData {
	vec2 uv;
	vec4 color;
	float lineWidth;
	float uMax;
	vec2 p0screen;
	vec2 p1screen;
	vec2 p2screen;
} fragmentIn;


uniform sampler2D colorTexture1;
uniform float globalOpacity = 1;
uniform float screenWidth;
uniform float screenHeight;

out vec4 outColor;

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


vec2 DistanceToQBSpline(in vec2 P0, in vec2 P1, in vec2 P2, in vec2 p)
{
	float dis = 1e20;
	
	vec2  sb = (P1 - P0) * 2.0;
	vec2  sc = P0 - P1 * 2.0 + P2;
	vec2  sd = P1 - P0;
	float dotSc = dot(sc, sc);
	vec2  D = P0 - p;
	float sB = 3.0 * dot(sd, sc);
	float sC = 2.0 * dot(sd, sd);
	
	// bezier curve is a straight line
	if ( abs(dotSc) < 0.001 ) {
		float normsd = sqrt(sC/2);
			return vec2( dot(D, sd) / normsd, abs( D.y * sd.x - D.x * sd.y ) / normsd );
	}
	
	float sA = 1.0 / dotSc;
	

	float a = sA;
	float b = sB;
	float c = sC + dot(D, sc);
	float d = dot(D, sd);

   	float res[3];
	int n = solveCubic(b*a, c*a, d*a, res);

	float t = clamp(res[0],0.0, 1.0);
	vec2 pos = P0 + (sb + sc*t)*t;
	dis = min(dis, length(pos - p));
	return vec2(t, dis);
   	if(n>1) {
		t = clamp(res[1],0.0, 1.0);
		pos = P0 + (sb + sc*t)*t;
		dis = min(dis, length(pos - p));
		    
		t = clamp(res[2],0.0, 1.0);
		pos = P0 + (sb + sc*t)*t;
		dis = min(dis, length(pos - p));	    
   	}

   	return vec2(t, dis);
}

void main() {
	
	vec2 p = vec2( gl_FragCoord.x, gl_FragCoord.y );
	
	vec2 p0 = fragmentIn.p0screen;
	vec2 p1 = fragmentIn.p1screen;
	vec2 p2 = fragmentIn.p2screen;
	
	vec2 uv = DistanceToQBSpline(p0, p1, p2, p);
	
	float lineSoftness = 1.0;
	uv.x = ( fragmentIn.uv.x + uv.x ) / fragmentIn.uMax;
	uv.y = ( 1 - uv.y / fragmentIn.lineWidth );
	if ( uv.y < 0 ) discard;
	uv.y = uv.y / 2 + 0.5; // rescale from -1..+1 to 0..+1
		 
	vec4 tcolor = texture(colorTexture1, uv );
	tcolor.r = 1;
	outColor = vec4( tcolor.rgb, tcolor.a * globalOpacity );
	
}


