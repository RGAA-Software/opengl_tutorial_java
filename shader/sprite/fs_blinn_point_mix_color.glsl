#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;
in vec3 outMixColor;

uniform sampler2D image;
uniform sampler2D imageMix;
uniform sampler2D imageSampler;
uniform vec3 cameraPos;

layout (location = 0) out vec4 FragColor;

void main()
{
    vec4 texColor = texture(image, outTex);
    vec4 mixColor = texture(imageMix, outTex);
//    if (mixColor.r < 0.0001 && mixColor.g < 0.0001 && mixColor.b < 0.0001) {
//        FragColor = texColor;
//    } else {
//    }

    float x = clamp(pow(1 - outMixColor.r, 2) * 2.5, 0, 1);
    vec4 samplerColor = texture(imageSampler, vec2(x, 0.2));

//    samplerColor = texture(imageSampler, vec2(1 - outMixColor.r, 0.2));
//    FragColor = vec4(mix(texColor.xyz, vec3(samplerColor.rgb), 0.95), 1.0);
    FragColor = vec4(samplerColor.rgb, 1.0);
//    FragColor = vec4(mixColor.rgb, 1);

//    FragColor = vec4(outMixColor.rrr, 1.0);
}