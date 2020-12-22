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
    sampler2D diffuse_1;
    sampler2D diffuse_2;
    sampler2D diffuse_3;
    sampler2D specular_1;
    sampler2D specular_2;
    sampler2D specular_3;
};

uniform Material material;

uniform samplerCube skybox;

void main()
{
    vec3 diffuseColor1 = vec3(texture(material.diffuse_1, outTex));
    vec3 diffuseColor2 = vec3(texture(material.diffuse_2, outTex));
    vec3 diffuseColor3 = vec3(texture(material.diffuse_3, outTex));

    vec3 specularColor1 = vec3(texture(material.specular_1, outTex));
    vec3 specularColor2 = vec3(texture(material.specular_2, outTex));
    vec3 specularColor3 = vec3(texture(material.specular_3, outTex));


    float cosTheta = dot(normalize(light.direction), normalize((outPos - light.position)));
    vec3 diffuseColorCompose = diffuseColor1 + diffuseColor2 + diffuseColor3;
    vec4 ambient = vec4(0.05,0.05, 0.05, 1) * vec4(diffuseColorCompose, 1);

    vec3 specularColorCompose = specularColor1 + specularColor2 + specularColor3;
    vec3 specularC ;//= specularColor3;

    float diffuseFactor = max( dot( normalize(-light.direction), normalize(outNormal) ), 0) / 2.5;
    vec3 diffuse = diffuseFactor * diffuseColorCompose * light.diffuse;

    float specularFactor = max( dot( normalize(reflect(light.direction, outNormal)), normalize(cameraPos - outPos)) , 0);
    specularFactor = pow(specularFactor, 16) / 2;
    vec3 specular = specularFactor * specularC * light.specular;


    gl_FragColor = vec4((diffuse + specular), 1.0) + ambient;
}