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
uniform float globalOpacity = 1.0;

const int c_terramarch_steps = 64;
const int c_grassmarch_steps = 32;
const float c_maxdist = 200.;
const float c_grassmaxdist = 3.;
const float c_scale = .05;
const float c_height = 6.;
const float c_rslope = 1. / (c_scale * c_height);
const float c_gscale =  15.;
const float c_gheight = 1.5;
const float c_rgslope = 1. / (c_gscale * c_gheight);
const vec3 c_skycolor = vec3(.59, .79, 1.);

float t = time / 1000.;
uniform float strokeDistance = 20.0;
uniform float strokeThickness = 0.05;
uniform float strokeLength = 0.5;
uniform float strokeStartShift = 0.05;
uniform float strokeStartLength = 0.05;
uniform float strokeEndLength = 0.05;
uniform float dotSize = 0.05;
uniform int nbDots = 15;

float ambient = .8;

float hash(in float p) { return fract(sin(p) * 43758.2317); }
float hash(in vec2 p) { return hash(dot(p, vec2(87.1, 313.7))); }
vec2 hash2(in float p) {
	float x = hash(p);
	return vec2(x, hash(p+x));
}
vec2 hash2(in vec2 p) { return hash2(dot(p, vec2(87.1, 313.7))); }

float noise(in vec2 p) {
	vec2 F = floor(p), f = fract(p);
	f = f * f * (3. - 2. * f);
	return mix(
		mix(hash(F), 			 hash(F+vec2(1.,0.)), f.x),
		mix(hash(F+vec2(0.,1.)), hash(F+vec2(1.)),	  f.x), f.y);
}

vec2 noise2(in vec2 p) {
	vec2 F = floor(p), f = fract(p);
	f = f * f * (3. - 2. * f);
	return mix(
		mix(hash2(F), 			  hash2(F+vec2(1.,0.)), f.x),
		mix(hash2(F+vec2(0.,1.)), hash2(F+vec2(1.)),	f.x), f.y);
}

float fnoise(in vec2 p) {
	return .5 * noise(p) + .25 * noise(p*2.03) + .125 * noise(p*3.99);
}

float dotism(float x) {
	float v = 1.;
	int i;
	float d = 0;
	for ( i = 0; i < nbDots; i++) {
		d = float(i * i *i) / float(nbDots * nbDots * nbDots);
		v = v * ((1.0-smoothstep( d - dotSize, d, x)) + smoothstep( d, d+dotSize, x));
	}
	return v;
}

vec4 computeColor( DataGradient fragmentData ) {
//return vec4( vec3(mod(fragmentData.worldUV.x* fragmentData.worldUVRange.x,100.0) / 100.0) ,1.0);
	float worldU = fragmentData.worldUV.x * fragmentData.worldUVRange.x;
	float unitV = fragmentData.worldUV.y;
	float trait = mod( worldU, strokeDistance ) - strokeDistance / 2.0; // u => -strokeDistance / 2 .. +strokeDistance / 2
	trait = smoothstep( -strokeThickness, 0, trait ) * (1-smoothstep( 0, strokeThickness, trait )); // stroke thickness 
	trait = trait * (1-smoothstep(strokeLength - strokeEndLength, strokeLength, unitV )); // stroke length
	trait = smoothstep(strokeStartShift, strokeStartShift + strokeStartLength, unitV) * trait; // smooth start of the stroke
	trait = trait * dotism(unitV);
	float opacity = trait;
	return vec4(fragmentData.color.rgb, fragmentData.color.a * opacity );
}


