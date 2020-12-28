#version 330 core

in vec2 outTex;

uniform sampler2D image;
uniform sampler2D image2;

const float offset = 1.0 / 300.0;

uniform float weight[5] = float[] (0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162);

void main() {

    vec2 tex_offset = 1.0 / textureSize(image2, 0); // gets size of single texel
    vec3 result_h = texture(image2, outTex).rgb * weight[0];
    vec3 result_v = texture(image2, outTex).rgb * weight[0];

    for(int i = 1; i < 5; ++i)
    {
        result_h += texture(image2, outTex + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
        result_h += texture(image2, outTex - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
    }
    for(int i = 1; i < 5; ++i)
    {
        result_v += texture(image2, outTex + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
        result_v += texture(image2, outTex - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
    }

    gl_FragColor = texture(image, outTex) + vec4(result_h + result_v, 1.0);
}
