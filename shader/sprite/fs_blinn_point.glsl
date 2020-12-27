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
uniform samplerCube shadowMap;

vec3 sampleOffsetDirections[20] = vec3[]
(
vec3( 1,  1,  1), vec3( 1, -1,  1), vec3(-1, -1,  1), vec3(-1,  1,  1),
vec3( 1,  1, -1), vec3( 1, -1, -1), vec3(-1, -1, -1), vec3(-1,  1, -1),
vec3( 1,  1,  0), vec3( 1, -1,  0), vec3(-1, -1,  0), vec3(-1,  1,  0),
vec3( 1,  0,  1), vec3(-1,  0,  1), vec3( 1,  0, -1), vec3(-1,  0, -1),
vec3( 0,  1,  1), vec3( 0, -1,  1), vec3( 0, -1, -1), vec3( 0,  1, -1)
);

float calculateInShadow() {
    float shadow;
    vec3 fragToLight = outPos - light.position;
    float sampleRadius = 0.005;
    int sampleSize = 20;

    for (int i = 0; i < sampleSize; i++) {
        float depth = texture(shadowMap, fragToLight + sampleOffsetDirections[i] * sampleRadius).r;
        float currentDepth = length(outPos - light.position) / 25;
        float inShadow = currentDepth - 0.005 < depth ? 0 : 1.0;
        shadow += inShadow;
    }

    return shadow / float(sampleSize);
}

void main()
{
    vec4 texColor = texture(image, outTex);

    vec3 toLightDir = light.position - outPos;

//    vec3 toLightDir = normalize(light.position - outPos);
    float diffuseFactor = max(dot(normalize(outNormal), normalize(toLightDir)), 0);

    vec3 toCameraDir = normalize(cameraPos - outPos);
    vec3 halfWay = normalize(normalize(toLightDir) + toCameraDir);
    float specularFactor = pow( max(dot(normalize(outNormal), halfWay), 0), 32);

    vec3 totalColor = light.ambient + light.diffuse * diffuseFactor;

    vec4 specColor = vec4(light.specular * specularFactor, 1);

    float shadow = calculateInShadow();

    float distance = length(outPos - light.position);
    float attenuation = 1.0 / (1.0 + light.linear * distance + light.quadratic * (distance * distance));

    vec4 targetColor = vec4(light.diffuse, 1.0) * diffuseFactor * texColor + specColor;
    gl_FragColor =  vec4(light.ambient, 1.0) * texColor + targetColor * (1 - shadow) * attenuation;
}