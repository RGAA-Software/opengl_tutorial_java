#version 130

in vec3 outColor;
in vec2 outTex;

uniform vec3 lightColor;

in vec4 outLightViewPos;
uniform sampler2D shadowMap;

float calculateShadow(vec4 fragPosInLightSpace) {
    vec3 projCoord = fragPosInLightSpace.xyz / fragPosInLightSpace.w;
    projCoord = projCoord * 0.5 + 0.5;
    float currentDepth = projCoord.z;
    float shadowMapDepth = texture(shadowMap, projCoord.xy).r;
    return currentDepth - 0.05 > shadowMapDepth ? 1.0 : 0.0;
}

void main()
{
    float shadow = calculateShadow(outLightViewPos);
    gl_FragColor = vec4(lightColor * (1 - shadow), 1);
}