#version 130

in vec3 outColor;
in vec2 outTex;

uniform vec3 lightColor;

void main()
{
    gl_FragColor = vec4(lightColor, 1);
}