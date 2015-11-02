#version 330

uniform vec4 contrast;
uniform vec4 brightness;
uniform vec4 gamma;

vec3 mid = vec3(0.5);

vec4 colorFilter( vec4 col ) {
    return vec4( clamp( pow( ( ( col.rgb - mid + brightness.rgb) * contrast.rgb + mid ), 1.0/gamma.rgb), 0.0, 1.0), col.a );
}
