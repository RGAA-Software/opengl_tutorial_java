#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;
in vec3 outMixColor;

uniform sampler2D image;
uniform sampler2D imageMix;
uniform vec3 cameraPos;

layout (location = 0) out vec4 FragColor;

void main()
{
    vec4 texColor = texture(image, outTex);
    vec4 mixColor = texture(imageMix, outTex);
    if (mixColor.r < 0.0001 && mixColor.g < 0.0001 && mixColor.b < 0.0001) {
        FragColor = texColor;
    } else {
        FragColor = vec4(mix(texColor.xyz, vec3(outMixColor), 0.9), 1.0);
    }
}