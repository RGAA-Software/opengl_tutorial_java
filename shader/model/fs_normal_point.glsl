#version 330 core


in vec2 outTex;

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

uniform Light light[5];
//
//struct Material {
//    sampler2D image;
//    int type;
//};
//
//uniform Material material[6];
//uniform int materialSize;


uniform sampler2D diffuseImage;
uniform sampler2D specularImage;
uniform sampler2D normalImage;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

in vec3 posInTBN;
in vec3 lightPosInTBN;
in vec3 cameraPosInTBN;

in vec3 outNormal;

void main()
{
    vec3 diffuseColor = vec3(0, 0, 0);
    vec3 specularColor = vec3(0, 0, 0);
    vec3 normal = vec3(0, 0, 0);
    vec3 normalColor = vec3(0, 0, 0);

    diffuseColor = texture(diffuseImage, outTex).rgb;
    specularColor = texture(specularImage, outTex).rgb;
    normalColor = texture(normalImage, outTex).rgb;
    normal = normalize(normalColor * 2.0 - 1.0);

//    for (int i = 0; i < materialSize; i++) {
//        int type = material[i].type;
//        if (type == 1) {
//            // diffuse
//            vec3 color = vec3(texture(material[i].image, outTex).xyz);
//            diffuseColor = diffuseColor + color;
//
//        } else if (type == 2) {
//            // specular
//            vec3 color = vec3(texture(material[i].image, outTex).xyz);
//            specularColor = specularColor + color;
//        } else if (type == 3) {
//            // normal
//            normalColor = texture(material[i].image, outTex).xyz;
//            normal = normalize(normalColor * 2.0 - 1.0);
//        }
//    }

//    vec4 ambient = vec4(light[0].ambient, 1) * vec4(diffuseColor, 1);
    vec4 ambient = vec4(vec3(0.1), 1) * vec4(diffuseColor, 1);

    vec3 toLightDir = lightPosInTBN - posInTBN;

    float diffuseFactor = max( dot( normalize(toLightDir), normalize(normal) ), 0);
    vec3 diffuse = diffuseFactor * diffuseColor * vec3(0.9);//light[0].diffuse;
//
//    float specularFactor = max( dot( normalize(reflect(-toLightDir, normal)), normalize(cameraPosInTBN - posInTBN)) , 0);
//    specularFactor = pow(specularFactor, 128);

    vec3 toCameraDir = normalize(cameraPosInTBN - posInTBN);
    vec3 halfWay = normalize(normalize(toLightDir) + toCameraDir);
    float specularFactor = pow( max(dot(normalize(normal), halfWay), 0), 128);

    vec3 specular = specularFactor * specularColor * vec3(0.3);
    vec3 targetColor = (diffuse + specular);
    FragColor = vec4(targetColor, 1.0) + ambient;
    //BrightColor = vec4(0, 0, 0, 1);

    //FragColor = vec4(normalColor, 1);
}