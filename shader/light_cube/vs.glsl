#version 130

in vec3 cubePos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;

void main()
{
    gl_Position = proj * view * model * vec4(cubePos, 1.0);
}