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

    vec3 specularColor1 = vec3(texture(material.specular_1, outTex));



    vec3 diffuseColorCompose = diffuseColor1;
    vec3 ambient = vec3(0.1, 0.1, 0.1) * diffuseColorCompose;


    vec3 specularColorCompose = specularColor1;

    float diffuseFactor = max( dot( normalize(-light.direction), normalize(outNormal) ), 0);
    vec3 diffuse = diffuseFactor * diffuseColorCompose * vec3(0.8);//light.diffuse;

    float specularFactor = max( dot( reflect(normalize(light.direction), normalize(outNormal)), normalize(cameraPos - outPos)) , 0);
    specularFactor = pow(specularFactor, 64);
    vec3 specular = specularFactor * specularColorCompose * vec3(0.6);//light.specular;

    gl_FragColor = vec4(ambient + (diffuse + specular), 1.0);
//    vec3 I = normalize(outPos - cameraPos);
//    vec3 R = reflect(I, normalize(outNormal));
//    gl_FragColor = vec4(texture(skybox, R).rgb, 1.0);
}