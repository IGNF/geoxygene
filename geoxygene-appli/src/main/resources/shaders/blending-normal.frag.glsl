#version 330

uniform sampler2D foregroundTexture;
uniform sampler2D backgroundTexture;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;
uniform int antialiasingSize = 1;
uniform int screenWidth;
uniform int screenHeight;

out vec4 outColor;
in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

const float EPSILON = 0.001;
const vec3 identityElement = vec3( 1.0, 1.0, 1.0 );

vec4 colorFilter( vec4 col );

void main(void) {
//    outColor = vec4( 0.5f, 0.6f,  1f, 1.0 );
//    return;

	vec3 resultColor = vec3(0);
	vec4 foregroundColor = colorFilter( texture( foregroundTexture, fragmentTextureCoord.xy ) );
	vec4 backgroundColor = texture( backgroundTexture, fragmentTextureCoord.xy );
	foregroundColor.rgb = foregroundColor.rgb * foregroundColor.a;
	backgroundColor.rgb = backgroundColor.rgb * backgroundColor.a;
	float sumAlpha = foregroundColor.a + backgroundColor.a;

	resultColor = foregroundColor.rgb * foregroundColor.a + backgroundColor.rgb * (1.0 - foregroundColor.a );

	outColor = vec4( resultColor, backgroundColor.a );
}