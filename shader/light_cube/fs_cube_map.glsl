#version 330 core

in vec3 outPos;

uniform samplerCube cube;

void main()
{
    gl_FragColor = vec4(vec3(texture(cube, outPos).r), 1.0);
}