#version 130

in vec3 aPos;
in vec3 aColor;
in vec2 aTex;

out vec3 outColor;
out vec2 outTex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

void main()
{
    gl_Position = proj * view * model * vec4(aPos.x, aPos.y, aPos.z, 1.0);
    outColor = aColor;
    outTex = aTex;
}