#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;

uniform vec3 singleColor;

void main()
{

    gl_FragColor = vec4(singleColor , 1.0);
}