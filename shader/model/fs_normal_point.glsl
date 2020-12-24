#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;

//uniform vec3 lightPos;
//uniform vec3 lightColor;
uniform vec3 cameraPos;
//uniform vec3 ambient;

//struct Material {
//    vec3 ambient;
//    vec3 diffuse;
//    vec3 specular;
//    float shininess;
//};

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

struct Material {
    sampler2D image;
    int type;
};

uniform Material material[6];

uniform samplerCube skybox;

in vec4 outLightViewPos;
uniform sampler2D shadowMap;

float calculateShadow(vec4 fragPosInLightSpace) {
    vec3 projCoord = fragPosInLightSpace.xyz ;/// fragPosInLightSpace.z;
    projCoord = projCoord * 0.5 + 0.5;
    float currentDepth = projCoord.z;
    float shadowMapDepth = texture(shadowMap, projCoord.xy).r;
    return currentDepth - 0.05 > shadowMapDepth ? 1.0 : 0.0;
}

void main()
{
    vec3 diffuseColor = vec3(0, 0, 0);
    vec3 specularColor = vec3(0, 0, 0);

    for (int i = 0; i < 6; i++) {
        int type = material[i].type;
        if (type == 1) {
            // diffuse
            vec3 color = vec3(texture(material[i].image, outTex).xyz);
            diffuseColor = diffuseColor + color;

        } else if (type == 2) {
            // specular
            vec3 color = vec3(texture(material[i].image, outTex).xyz);
            specularColor = specularColor + color;
        }
    }


    vec4 ambient = vec4(0.1,0.1, 0.1, 1) * vec4(diffuseColor, 1);

    vec3 toLightDir = light.position - outPos;

    float diffuseFactor = max( dot( normalize(toLightDir), normalize(outNormal) ), 0);
    vec3 diffuse = diffuseFactor * diffuseColor * light.diffuse;
//
//    float specularFactor = max( dot( normalize(reflect(-toLightDir, outNormal)), normalize(cameraPos - outPos)) , 0);
//    specularFactor = pow(specularFactor, 32);

    vec3 toCameraDir = normalize(cameraPos - outPos);
    vec3 halfWay = normalize(normalize(toLightDir) + toCameraDir);
    float specularFactor = pow( max(dot(normalize(outNormal), halfWay), 0), 32);

    vec3 specular = specularFactor * specularColor * light.specular;

    //float shadow = calculateShadow(outLightViewPos);
    vec3 targetColor = (diffuse + specular);// * (1 - shadow);
    gl_FragColor = vec4(targetColor, 1.0) + ambient;
}