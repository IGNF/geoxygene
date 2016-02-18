#version 330

uniform sampler2D foregroundTexture;
uniform sampler2D backgroundTexture;
uniform float globalOpacity = 1;
uniform float objectOpacity = 1;

out vec4 outColor;
in vec4 fragmentColor;
in vec2 fragmentTextureCoord;

vec4 colorFilter( vec4 col );

void main(void) {

	vec3 resultColor = vec3(0);
	vec4 foregroundColor = colorFilter( texture( foregroundTexture, fragmentTextureCoord.xy ) );
	vec4 backgroundColor = texture( backgroundTexture, fragmentTextureCoord.xy );

	resultColor = foregroundColor.rgb * foregroundColor.a + backgroundColor.rgb * (1.0 - foregroundColor.a );

	outColor = vec4( resultColor, backgroundColor.a );
}