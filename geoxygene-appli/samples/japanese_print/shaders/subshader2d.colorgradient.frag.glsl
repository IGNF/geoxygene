#version 330

struct DataGradient {
	float screenWidth; 		// screen width in pixels 
	float screenHeight;		// screen height in pixels
	vec4 color;
	vec2 textureUV;
	vec2 worldUV;
	vec2 gradient;
	vec2 worldUVMin;
	vec2 worldUVRange;
};

uniform int time = 0;

// Color spaces RGB/HSV Conversion
vec3 rgb2hsv(vec3 c)
{
    vec4 K = vec4(0.0, -1.0 / 3.0, 2.0 / 3.0, -1.0);
    vec4 p = mix(vec4(c.bg, K.wz), vec4(c.gb, K.xy), step(c.b, c.g));
    vec4 q = mix(vec4(p.xyw, c.r), vec4(c.r, p.yzx), step(p.x, c.r));

    float d = q.x - min(q.w, q.y);
    float e = 1.0e-10;
    return vec3(abs(q.z + (q.w - q.y) / (6.0 * d + e)), d / (q.x + e), q.x);
}

vec3 hsv2rgb(vec3 c)
{
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

//
vec3 blueGradient( float v ) {
  float w = sin( v / 6.28 );
  vec3 col1 = vec3( 0.8, 0.9, 1.0 );
  vec3 col2 = vec3( 0.6, 0.7, 0.9 );
  return vec3( col1 * (1-w) + col2 * w );
}

vec3 greenGradient( float v ) {
  float w = sin( v / 6.28 );
  vec3 col1 = vec3( 0.48, 0.65, 0.65 );
  vec3 col2 = vec3( 0.48, 0.65, 0.65 );
  return vec3( col1 * (1-w) + col2 * w );
}

vec4 computeAnimatedWaterColor( DataGradient fragmentData ) {
	float dmax = 2500;
	float distance = fragmentData.worldUV.y * fragmentData.worldUVRange.y;
	float dd = 1.0 - smoothstep( 0, dmax, distance );
	float sec = mod( 5*dd - time*2. / 5000.0, 1.0 );
	//return vec4( blueGradient( sec ) * (dd) + (1-dd) * vec3(1.0,1.0,1.0) , 1.0 );
	return vec4( greenGradient( sec ) * (dd) + (1-dd) * vec3(1.0,1.0,1.0) , 1.0 );
}


vec4 computeColor( DataGradient fragmentData ) {
	return computeAnimatedWaterColor( fragmentData );
}

