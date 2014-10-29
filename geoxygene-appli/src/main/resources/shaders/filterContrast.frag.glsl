#version 400

uniform vec4 contrast;
uniform vec4 brightness;
uniform vec4 gamma;

vec3 mid = vec3(0.5);

vec4 colorFilter( vec4 col ) {
	return vec4( pow(((col.rgb + brightness.rgb) - mid ) * contrast.rgb + mid, 1.0/gamma.rgb), col.a );

	//return vec4( pow(( ( col - mid + brightness) * contrast + mid ), 1.0/gamma).rgb, col.a );
}
