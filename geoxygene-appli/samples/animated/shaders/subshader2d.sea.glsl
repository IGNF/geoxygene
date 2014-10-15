#version 400

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

uniform int seatime;
float iGlobalTime  = float(seatime) / 5000.0;

// Sunset on the sea v.1.0.1 - Ray Marching & Ray Tracing experiment by Riccardo Gerosa aka h3r3 
// Blog: http://www.postronic.org/h3/ G+: https://plus.google.com/u/0/117369239966730363327 Twitter: @h3r3 http://twitter.com/h3r3
// More information about this shader can be found here: http://www.postronic.org/h3/pid65.html
// This GLSL shader is based on the work of T Whitted, JC Hart, K Perlin, I Quilez and many others
// This shader uses a Simplex Noise implementation by and I McEwan, A Arts (more info below)
// If you modify this code please update this header

float PI = 3.14159265;
float MAX_RAYMARCH_DIST = 150.0;
float MIN_RAYMARCH_DELTA = 0.00015; 
float GRADIENT_DELTA = 0.015;
float waveHeight1 = 0.005;
float waveHeight2 = 0.004;
float waveHeight3 = 0.001;

// --------------------- START of SIMPLEX NOISE
//
// Description : Array and textureless GLSL 2D simplex noise function.
//      Author : Ian McEwan, Ashima Arts.
//  Maintainer : ijm
//     Lastmod : 20110822 (ijm)
//     License : Copyright (C) 2011 Ashima Arts. All rights reserved.
//               Distributed under the MIT License. See LICENSE file.
//               https://github.com/ashima/webgl-noise
// 

vec3 mod289(vec3 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec2 mod289(vec2 x) {
  return x - floor(x * (1.0 / 289.0)) * 289.0;
}

vec3 permute(vec3 x) {
  return mod289(((x*34.0)+1.0)*x);
}

float snoise(vec2 v)
  {
  const vec4 C = vec4(0.211324865405187,  // (3.0-sqrt(3.0))/6.0
                      0.366025403784439,  // 0.5*(sqrt(3.0)-1.0)
                     -0.577350269189626,  // -1.0 + 2.0 * C.x
                      0.024390243902439); // 1.0 / 41.0
// First corner
  vec2 i  = floor(v + dot(v, C.yy) );
  vec2 x0 = v -   i + dot(i, C.xx);

// Other corners
  vec2 i1;
  //i1.x = step( x0.y, x0.x ); // x0.x > x0.y ? 1.0 : 0.0
  //i1.y = 1.0 - i1.x;
  i1 = (x0.x > x0.y) ? vec2(1.0, 0.0) : vec2(0.0, 1.0);
  // x0 = x0 - 0.0 + 0.0 * C.xx ;
  // x1 = x0 - i1 + 1.0 * C.xx ;
  // x2 = x0 - 1.0 + 2.0 * C.xx ;
  vec4 x12 = x0.xyxy + C.xxzz;
  x12.xy -= i1;

// Permutations
  i = mod289(i); // Avoid truncation effects in permutation
  vec3 p = permute( permute( i.y + vec3(0.0, i1.y, 1.0 ))
		+ i.x + vec3(0.0, i1.x, 1.0 ));

  vec3 m = max(0.5 - vec3(dot(x0,x0), dot(x12.xy,x12.xy), dot(x12.zw,x12.zw)), 0.0);
  m = m*m ;
  m = m*m ;

// Gradients: 41 points uniformly over a line, mapped onto a diamond.
// The ring size 17*17 = 289 is close to a multiple of 41 (41*7 = 287)

  vec3 x = 2.0 * fract(p * C.www) - 1.0;
  vec3 h = abs(x) - 0.5;
  vec3 ox = floor(x + 0.5);
  vec3 a0 = x - ox;

// Normalise gradients implicitly by scaling m
// Approximation of: m *= inversesqrt( a0*a0 + h*h );
  m *= 1.79284291400159 - 0.85373472095314 * ( a0*a0 + h*h );

// Compute final noise value at P
  vec3 g;
  g.x  = a0.x  * x0.x  + h.x  * x0.y;
  g.yz = a0.yz * x12.xz + h.yz * x12.yw;
  return 130.0 * dot(m, g);
}

// --------------------- END of SIMPLEX NOISE


float map(vec3 p) {
	return p.y + (0.5 + waveHeight1 + waveHeight2 + waveHeight3) 
		+ snoise(vec2(p.x + iGlobalTime * 0.4, p.z + iGlobalTime * 0.6)) * waveHeight1
		+ snoise(vec2(p.x * 1.6 - iGlobalTime * 0.4, p.z * 1.7 - iGlobalTime * 0.6)) * waveHeight2
	  	+ snoise(vec2(p.x * 6.6 - iGlobalTime * 1.0, p.z * 2.7 + iGlobalTime * 1.176)) * waveHeight3;
}

vec3 gradientNormalFast(vec3 p, float map_p) {
    return normalize(vec3(
        map_p - map(p - vec3(GRADIENT_DELTA, 0, 0)),
        map_p - map(p - vec3(0, GRADIENT_DELTA, 0)),
        map_p - map(p - vec3(0, 0, GRADIENT_DELTA))));
}

float intersect(vec3 p, vec3 ray_dir, out float map_p, out int iterations) {
	iterations = 0;
	if (ray_dir.y >= 0.0) { return -1.0; } // to see the sea you have to look down
	
	float distMin = (- 0.5 - p.y) / ray_dir.y;
	float distMid = distMin;
	for (int i = 0; i < 50; i++) {
		//iterations++;
		distMid += max(0.05 + float(i) * 0.002, map_p);
		map_p = map(p + ray_dir * distMid);
		if (map_p > 0.0) { 
			distMin = distMid + map_p;
		} else { 
			float distMax = distMid + map_p;
			// interval found, now bisect inside it
			for (int i = 0; i < 10; i++) {
				//iterations++;
				distMid = distMin + (distMax - distMin) / 2.0;
				map_p = map(p + ray_dir * distMid);
				if (abs(map_p) < MIN_RAYMARCH_DELTA) return distMid;
				if (map_p > 0.0) {
					distMin = distMid + map_p;
				} else {
					distMax = distMid + map_p;
				}
			}
			return distMid;
		}
	}
	return distMin;
}

vec4 computeColor( DataGradient fragmentData ) {
	float waveHeight = cos(iGlobalTime * 0.03) * 1.2 + 1.6;
	waveHeight1 *= waveHeight;
	waveHeight2 *= waveHeight;
	waveHeight3 *= waveHeight;
	vec2 iResolution = vec2( fragmentData.screenWidth, fragmentData.screenHeight); 
	
	
//	vec2 position = vec2((gl_FragCoord.x - iResolution.x / 2.0) / iResolution.y, (gl_FragCoord.y - iResolution.y / 2.0) / iResolution.y);
	vec2 position = vec2(fragmentData.worldUV.x - 0.5, fragmentData.worldUV.y - 0.5);
	vec3 ray_start = vec3(0, 0.8, -25);
	vec3 ray_dir = normalize(vec3(position,-1) - ray_start);
	
	const float dayspeed = 0.04;
	float subtime = max(-0.16, sin(iGlobalTime * dayspeed) * 0.2);
	float middayperc = max(0.0, sin(subtime));
	vec3 light1_pos = vec3(0.0, middayperc * 200.0, cos(subtime * dayspeed) * 200.0);
	float sunperc = pow(max(0.0, min(dot(ray_dir, normalize(light1_pos)), 1.0)), 190.0 + max(0.0,light1_pos.y * 4.3));
	vec3 suncolor = (1.0 - max(0.0, middayperc)) * vec3(1.5, 1.2, middayperc + 0.5) + max(0.0, middayperc) * vec3(1.0, 1.0, 1.0) * 4.0;
	vec3 skycolor = vec3(middayperc + 0.8, middayperc + 0.7, middayperc + 0.5);
	vec3 skycolor_now = suncolor * sunperc + (skycolor * (middayperc * 1.6 + 0.5)) * (1.0 - sunperc);
	vec4 color = vec4(0.0, 0.0, 0.0, 1.0);
	float map_p;
	int iterations;
	float dist = intersect(ray_start, ray_dir, map_p, iterations);
	if (dist > 0.0) {
		vec3 p = ray_start + ray_dir * dist;
		vec3 light1_dir = normalize(light1_pos - p);
        	vec3 n = gradientNormalFast(p, map_p);
		vec3 ambient = skycolor_now * 0.1;
        	vec3 diffuse1 = vec3(1.1, 1.1, 0.6) * max(0.0, dot(light1_dir, n)  * 2.8);
		vec3 r = reflect(light1_dir, n);
		vec3 specular1 = vec3(1.5, 1.2, 0.6) * (0.8 * pow(max(0.0, dot(r, ray_dir)), 200.0));	    
		float fog = min(max(p.z * 0.07, 0.0), 1.0);
        	color.rgb = (vec3(0.6,0.6,1.0) * diffuse1 + specular1 + ambient)  * (1.0 - fog) + skycolor_now * fog;
    	} else {
        	color.rgb = skycolor_now.rgb;
        color.rgb = vec3(1.0,0.0,0.0);
    	}
	return color;
}

