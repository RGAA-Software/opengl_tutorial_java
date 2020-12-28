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
uniform Light light[5];
uniform int lightSize;

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
    float shadow = 0;

    float sampleRadius = 0.005;
    int sampleSize = 20;

    for (int i = 0; i < sampleSize; i++) {
        vec3 fragToLight = outPos - light[0].position;
        float depth = texture(shadowMap, fragToLight + sampleOffsetDirections[i] * sampleRadius).r;
        float currentDepth = length(outPos - light[0].position) / 25;
        float inShadow = currentDepth - 0.005 < depth ? 0 : 1.0;
        shadow += inShadow;
    }

    return shadow / float(sampleSize);
}

void main()
{
    vec4 texColor = texture(image, outTex);
    vec4 finalColor = vec4(vec3(0), 1);
    for (int i = 0; i < lightSize; i++) {
        vec3 toLightDir = light[i].position - outPos;

        //    vec3 toLightDir = normalize(light.position - outPos);
        float diffuseFactor = max(dot(normalize(outNormal), normalize(toLightDir)), 0);

        vec3 toCameraDir = normalize(cameraPos - outPos);
        vec3 halfWay = normalize(normalize(toLightDir) + toCameraDir);
        float specularFactor = pow( max(dot(normalize(outNormal), halfWay), 0), 32);

        //vec3 totalColor = light[i].ambient + light[i].diffuse * diffuseFactor;

        vec4 specColor = vec4(light[i].specular * specularFactor, 1);

        float shadow = calculateInShadow();
        float distance = length(outPos - light[i].position);
        float attenuation = 1.0 / (1.0 + light[i].linear * distance + light[i].quadratic * (distance * distance));

        vec4 targetColor = vec4(light[i].diffuse, 1.0) * diffuseFactor * texColor + specColor;
        finalColor = finalColor + targetColor * (1 - shadow) * attenuation;
    }

    vec3 color = finalColor.rgb + light[0].ambient * texColor.rgb * 5;
    gl_FragColor = vec4(color, 1.0);
}