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

struct Material {
    sampler2D image;
    int type;
};

uniform Material material[6];

uniform int materialSize;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

in vec3 posInTBN;
in vec3 lightPosInTBN;
in vec3 cameraPosInTBN;

void main()
{
    vec3 diffuseColor = vec3(0, 0, 0);
    vec3 specularColor = vec3(0, 0, 0);
    vec3 normal = vec3(0, 0, 0);

    for (int i = 0; i < materialSize; i++) {
        int type = material[i].type;
        if (type == 1) {
            // diffuse
            vec3 color = vec3(texture(material[i].image, outTex).xyz);
            diffuseColor = diffuseColor + color;

        } else if (type == 2) {
            // specular
            vec3 color = vec3(texture(material[i].image, outTex).xyz);
            specularColor = specularColor + color;
        } else if (type == 3) {
            // normal
            vec3 color = texture(material[i].image, outTex).xyz;
            normal = normalize(color * 2.0 - 1.0);
        }
    }



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
    BrightColor = vec4(0, 0, 0, 1);
}