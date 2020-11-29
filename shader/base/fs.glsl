#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;

uniform sampler2D image1;
uniform sampler2D image2;

uniform vec3 lightPos;
uniform vec3 lightColor;
uniform vec3 cameraPos;
uniform vec3 ambient;

void main()
{
    vec3 texColor = mix(texture(image1, outTex), texture(image2, outTex), 0.8).xyz;
    vec3 diffuse = max( dot( normalize((lightPos - outPos)), normalize(outNormal) ), 0)  * lightColor;
    float specularFactor = max( dot( reflect(normalize(outPos - lightPos), normalize(outNormal)), normalize(cameraPos - outPos)) , 0);
    specularFactor = pow(specularFactor, 64);
    vec3 specular = specularFactor * lightColor;
    gl_FragColor = vec4(texColor * (ambient + diffuse + specular), 1.0);
}