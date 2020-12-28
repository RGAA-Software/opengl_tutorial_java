#version 330 core

in vec3 outColor;
in vec2 outTex;

uniform vec3 lightColor;

in vec4 outLightViewPos;
uniform sampler2D shadowMap;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

float calculateShadow(vec4 fragPosInLightSpace) {
    vec3 projCoord = fragPosInLightSpace.xyz / fragPosInLightSpace.w;
    projCoord = projCoord * 0.5 + 0.5;
    float currentDepth = projCoord.z;
    float shadowMapDepth = texture(shadowMap, projCoord.xy).r;
    return currentDepth - 0.05 > shadowMapDepth ? 1.0 : 0.0;
}

void main()
{
    //float shadow = calculateShadow(outLightViewPos);
    FragColor = vec4(lightColor, 1);
    BrightColor = FragColor;//vec4(FragColor.r/2, FragColor.g/2, FragColor.b, 1.0);
}