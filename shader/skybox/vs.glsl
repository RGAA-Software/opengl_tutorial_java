#version 330 core

in vec3 aPos;

out vec3 outPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

void main()
{
    vec4 Position = proj * view * model * vec4(aPos, 1.0);
    gl_Position = Position.xyww;
    outPos = aPos;
}