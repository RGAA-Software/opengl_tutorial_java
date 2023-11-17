#version 130

in vec3 outColor;
in vec2 outTex;

uniform sampler2D image1;
uniform sampler2D image2;


void main()
{
    vec4 textureColor = texture(image2, outTex);

    gl_FragColor = textureColor;
}