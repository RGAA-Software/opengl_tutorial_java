#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTex;

out vec3 outPos;
out vec3 outNormal;
out vec2 outTex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform mat4 orthoProj;

uniform int renderShadowMap;

void main()
{
    if (renderShadowMap == 1) {
        gl_Position = orthoProj * view * model * vec4(aPos, 1.0);
    } else {
        gl_Position = proj * view * model * vec4(aPos, 1.0);
    }
    outPos = vec3(model * vec4(aPos, 1.0));
    outNormal = mat3(transpose(inverse(model))) * aNormal;
    outTex = aTex;
}