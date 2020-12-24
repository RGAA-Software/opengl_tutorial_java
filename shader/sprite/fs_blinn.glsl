#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;

uniform sampler2D image;
uniform vec3 cameraPos;

struct Light {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 position;
    vec3 direction;
    float cosCutoff;
    float outerCutoff;

    float constant;
    float linear;
    float quadratic;
};

//uniform Material material;
uniform Light light;

in vec4 outLightViewPos;
uniform sampler2D shadowMap;

float calculateShadow(vec4 fragPosInLightSpace) {
    vec3 projCoord = fragPosInLightSpace.xyz / fragPosInLightSpace.w;
    projCoord = projCoord * 0.5 + 0.5;
    float currentDepth = projCoord.z;
    float shadowMapDepth = texture(shadowMap, projCoord.xy).r;
    return currentDepth - 0.0005 > shadowMapDepth ? 1.0 : 0.0;
}

float calculateShadowPCF(vec4 fragPosInLightSpace) {
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    float shadow;
    vec3 projCoord = fragPosInLightSpace.xyz / fragPosInLightSpace.w;
    projCoord = projCoord * 0.5 + 0.5;

    for (int x = -1; x <= 1; x++) {
        for (int y = -1; y <= 1; y++) {
            float pcfDepth = texture(shadowMap, projCoord.xy + vec2(x, y) * texelSize).r;
            float currentDepth = projCoord.z;
            float inShadow = currentDepth - 0.003 > pcfDepth ? 1.0 : 0.0;
            shadow += inShadow;
        }
    }
    return shadow / 9.0;
}

void main()
{
    vec4 texColor = texture(image, outTex);

//    vec3 toLightDir = normalize(light.position - outPos);
    float diffuseFactor = max(dot(normalize(outNormal), -normalize(light.direction)), 0);

    vec3 toCameraDir = normalize(cameraPos - outPos);
    vec3 halfWay = normalize(-normalize(light.direction) + toCameraDir);
    float specularFactor = pow( max(dot(normalize(outNormal), halfWay), 0), 32);

    vec3 totalColor = light.ambient + light.diffuse * diffuseFactor;

//    vec3 reflect = reflect(-toLightDir, outNormal);
//    float reflectFactor = pow( max(dot(toCameraDir, normalize(reflect)), 0), 8 );

    vec4 specColor = vec4(light.specular * specularFactor, 1);


    vec4 targetColor = vec4(light.diffuse, 1.0) * diffuseFactor * texColor + specColor;
    gl_FragColor =  vec4(light.ambient, 1.0) * texColor + targetColor * (1 - calculateShadowPCF(outLightViewPos));//vec4(totalColor, 1) * texColor + specColor;

//    gl_FragColor = vec4(vec3(gl_FragCood.z), 1.0);

}