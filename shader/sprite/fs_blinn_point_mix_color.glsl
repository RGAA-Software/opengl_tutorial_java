#version 330 core

in vec3 outPos;
in vec2 outTex;
in vec3 outNormal;
in vec3 outMixColor;

uniform sampler2D image;
uniform sampler2D imageMix;
uniform sampler2D imageHeight;
uniform sampler2D imageSampler;

uniform vec3 cameraPos;

layout (location = 0) out vec4 FragColor;

void main()
{
    vec4 texColor = texture(image, outTex);
    vec4 mixColor = texture(imageMix, outTex);


    if (mixColor.r < 0.0001 && mixColor.g < 0.0001 && mixColor.b < 0.0001) {
        FragColor = texColor;
    } else {
//        if (gl_FragCoord.x > 400 && gl_FragCoord.x < 600) {
//            //FragColor = vec4(mix(texColor.xyz, vec3(outMixColor), 0.9), 1.0);
//            float height = texture(imageHeight, outTex).r;
//            if (height < 0) {
//                height = 0;
//            }
//            vec4 samplerColor = texture(imageSampler, vec2(1.0 - height, 0.2));
//            FragColor = vec4(mix(texColor.xyz, samplerColor.rgb, 0.7), 1.0);
//        } else {
//            FragColor = texColor;
//        }

        //FragColor = vec4(mix(texColor.xyz, vec3(outMixColor), 0.9), 1.0);
        float height = texture(imageHeight, outTex).r;
        if (height < 0) {
            height = 0;
        }
        vec4 samplerColor = texture(imageSampler, vec2(1.0 - height, 0.2));
        FragColor = vec4(mix(texColor.xyz, samplerColor.rgb, 0.7), 1.0);

    }
}