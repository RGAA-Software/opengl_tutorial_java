#version 130

in vec3 outColor;
in vec2 outTex;

uniform sampler2D image1;
uniform sampler2D image2;

void main()
{
    vec4 color = texture(image1, outTex);
    if (color.a < 0.1) {
        discard;
    }
    gl_FragColor = color.rgba;
}