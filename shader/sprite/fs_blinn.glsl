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

void main()
{
    vec4 texColor = texture(image, outTex);

    vec3 toLightDir = normalize(light.position - outPos);
    float diffuseFactor = max(dot(normalize(outNormal), toLightDir), 0);

    vec3 toCameraDir = normalize(cameraPos - outPos);
    vec3 halfWay = normalize(toLightDir + toCameraDir);
    float specularFactor = pow( max(dot(normalize(outNormal), halfWay), 0), 32);

    vec3 totalColor = light.ambient + light.diffuse * diffuseFactor;

//    vec3 reflect = reflect(-toLightDir, outNormal);
//    float reflectFactor = pow( max(dot(toCameraDir, normalize(reflect)), 0), 8 );

    vec4 specColor = vec4(light.specular * specularFactor, 1);
    gl_FragColor =  vec4(totalColor, 1) * texColor + specColor;

}