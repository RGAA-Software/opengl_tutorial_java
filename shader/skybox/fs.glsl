#version 330 core

in vec3 outPos;

uniform samplerCube skybox;

void main()
{
    gl_FragColor = texture(skybox, outPos);
}