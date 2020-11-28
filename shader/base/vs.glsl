#version 130

in vec3 aPos;
in vec3 aNormal;
in vec2 aTex;

out vec3 outColor;
out vec3 outNormal;
out vec2 outTex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

void main()
{
    gl_Position = proj * view * model * vec4(aPos, 1.0);
    outNormal = aNormal;
    outTex = aTex;
}