#version 130

in vec3 outColor;
in vec2 outTex;

uniform sampler2D image1;
uniform sampler2D image2;

void main()
{
    float offset = 1.0 / 300.0;
    vec2 points[9] = vec2[](
        vec2(-offset,  offset), // 左上
        vec2( 0.0f,    offset), // 正上
        vec2( offset,  offset), // 右上
        vec2(-offset,  0.0f),   // 左
        vec2( 0.0f,    0.0f),   // 中
        vec2( offset,  0.0f),   // 右
        vec2(-offset, -offset), // 左下
        vec2( 0.0f,   -offset), // 正下
        vec2( offset, -offset)  // 右下
    );

    float kernel[9] = float[] (
    -1, -1, -1,
    -1,  9, -1,
    -1, -1, -1
    );

    if (gl_FragCoord.x > 300) {
        vec3 sampled_color[9];
        for (int i = 0; i < 9; i++) {
            sampled_color[i] = vec3(mix(texture(image1, outTex + points[i]), texture(image2, outTex + points[i]), 0.95));
        }
        vec3 col = vec3(0.0);
        for(int i = 0; i < 9; i++) {
            col += sampled_color[i] * kernel[i];
        }

        gl_FragColor = vec4(col, 1.0);
//        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    } else {
        gl_FragColor = mix(texture(image1, outTex), texture(image2, outTex), 0.95);
    }
}