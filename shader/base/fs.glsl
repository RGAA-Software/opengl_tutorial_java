#version 130

varying vec3 outColor;
varying vec2 outTex;

uniform sampler2D image1;
uniform sampler2D image2;

void main()
{
    gl_FragColor = mix(texture(image1, outTex), texture(image2, outTex), 0.7);
}