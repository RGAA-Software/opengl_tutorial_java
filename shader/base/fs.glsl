#version 130

in vec3 outColor;
in vec2 outTex;

uniform sampler2D image1;
uniform sampler2D image2;

void main()
{
    gl_FragColor = mix(texture(image1, outTex), texture(image2, outTex), 0.95);
}