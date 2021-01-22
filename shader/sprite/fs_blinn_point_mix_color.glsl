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

const float offset = 1.0 / 300.0;

//vec4 blurEffect() {
//    vec2 offsets[9] = vec2[](
//        vec2(-offset,  offset), // top-left
//        vec2( 0.0f,    offset), // top-center
//        vec2( offset,  offset), // top-right
//        vec2(-offset,  0.0f),   // center-left
//        vec2( 0.0f,    0.0f),   // center-center
//        vec2( offset,  0.0f),   // center-right
//        vec2(-offset, -offset), // bottom-left
//        vec2( 0.0f,   -offset), // bottom-center
//        vec2( offset, -offset)  // bottom-right
//    );
//
//    float kernel[9] = float[](
//    -1, -1, -1,
//    -1,  9, -1,
//    -1, -1, -1
//    );
//
//    vec3 sampleTex[9];
//    for(int i = 0; i < 9; i++)
//    {
//        sampleTex[i] = vec3(texture(screenTexture, TexCoords.st + offsets[i]));
//    }
//    vec3 col = vec3(0.0);
//    for(int i = 0; i < 9; i++) {
//        col += sampleTex[i] * kernel[i];
//    }
//    return vec4(col, 1.0);
//}

void main()
{
    vec4 texColor = texture(image, outTex);
    vec4 mixColor = texture(imageMix, outTex);
//    if (mixColor.r < 0.0001 && mixColor.g < 0.0001 && mixColor.b < 0.0001) {
//        FragColor = texColor;
//    } else {
//    }
    vec4 samplerColor = texture(imageSampler, vec2(pow(1 -outMixColor.r, 3) * 5, 0.2));
//    FragColor = vec4(mix(texColor.xyz, vec3(samplerColor.rgb), 0.9), 1.0);
    FragColor = vec4(samplerColor.rgb, 1.0);
}