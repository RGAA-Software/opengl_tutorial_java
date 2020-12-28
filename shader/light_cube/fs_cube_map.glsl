#version 330 core

in vec3 outPos;

uniform samplerCube cube;

layout (location = 0) out vec4 FragColor;
layout (location = 1) out vec4 BrightColor;

void main()
{
    FragColor = vec4(vec3(texture(cube, outPos).r), 1.0);
    BrightColor = FragColor;//vec4(FragColor.r/2, FragColor.g/2, FragColor.b, 1.0);
}