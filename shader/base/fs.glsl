#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;

uniform sampler2D image1;
uniform sampler2D image2;
uniform int emission;

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

void main()
{
    vec3 diffuseColor = vec3(texture(image1, outTex));
    vec3 specularColor;
    if (emission == 0) {
        specularColor = vec3(texture(image2, outTex));
    } else if (emission == 1) {
        specularColor = diffuseColor;
    }

    float cosTheta = dot(normalize(light.direction), normalize((outPos - light.position)));
    vec3 ambient = diffuseColor * light.ambient;

    float diffuseFactor = max( dot( normalize(-light.direction), normalize(outNormal) ), 0);
    vec3 diffuse = diffuseFactor * diffuseColor * light.diffuse;

    float specularFactor = max( dot( reflect(normalize(light.direction), normalize(outNormal)), normalize(light.position - outPos)) , 0);
    specularFactor = pow(specularFactor, 64);
    vec3 specular = specularFactor * specularColor * light.specular;


    if (cosTheta > light.cosCutoff) {

        gl_FragColor = vec4(ambient + diffuse + specular, 1.0);
    } else if (cosTheta > light.outerCutoff && cosTheta < light.cosCutoff) {
        float diff = light.cosCutoff - light.outerCutoff;
        float distanceFromInner = light.cosCutoff - cosTheta;
        float progress = 1 - distanceFromInner / diff;

        gl_FragColor = vec4((ambient + diffuse + specular) * progress, 1.0);

    } else {
        gl_FragColor = vec4(0, 0, 0, 1);
    }

//    float distance = length(light.position - outPos);
//    float attenuation = 1.0 / (1 + light.linear * distance + light.quadratic * distance * distance);

}