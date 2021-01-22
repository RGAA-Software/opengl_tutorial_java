#version 330 core

in vec2 outTex;

uniform sampler2D image;

const float offset = 1.0 / 300.0;

uniform float weight[5] = float[] (0.2270270270, 0.1945945946, 0.1216216216, 0.0540540541, 0.0162162162);


// 来自 https://www.shadertoy.com/view/XdfGDH
// 正态分布概率密度函数
float normpdf(in float x, in float sigma) {
    return 0.39894*exp(-0.5*x*x/(sigma*sigma))/sigma;
}
vec3 gaussianblur(int size, sampler2D texture, vec2 resolution) {
    //declare stuff
    const int mSize = 25;
    const int kSize = (mSize-1)/2;
    float kernel[mSize];
    vec3 final_colour = vec3(0.0);

    //create the 1-D kernel
    float sigma = 7.0;
    float Z = 0.0;
    for (int j = 0; j <= kSize; ++j)
    {
        kernel[kSize+j] = kernel[kSize-j] = normpdf(float(j), sigma);
    }

    //get the normalization factor (as the gaussian has been clamped)
    for (int j = 0; j < mSize; ++j)
    {
        Z += kernel[j];
    }

    //read out the texels
    for (int i=-kSize; i <= kSize; ++i)
    {
        for (int j=-kSize; j <= kSize; ++j)
        {
            final_colour += kernel[kSize+j]*kernel[kSize+i]*texture2D(texture, (gl_FragCoord.xy+vec2(float(i),float(j))) / resolution.xy).rgb;
        }
    }

    return final_colour/(Z*Z);
}

// 来自：https://gl-transitions.com/editor/LinearBlur
vec4 blur(vec2 _uv, sampler2D texture) {
    float disp = 0.;
    float intensity = .2;
    const int passes = 5;
    vec4 c1 = vec4(0.0);
    disp = intensity*(0.5-distance(0.5, .1));

    for (int xi=0; xi<passes; xi++) {
        float x = float(xi) / float(passes) - 0.5;
        for (int yi=0; yi<passes; yi++) {
            float y = float(yi) / float(passes) - 0.5;
            vec2 v = vec2(x, y);
            float d = disp;
            c1 += texture2D(texture, _uv + d*v);
        }
    }
    c1 /= float(passes*passes);
    return c1;
}


void main() {


//    vec3 color = gaussianblur(10, image, textureSize(image, 0));
//    gl_FragColor = vec4(color, 1.0);

    gl_FragColor = blur(outTex, image);

//    vec2 tex_offset = 1.0 / textureSize(image, 0); // gets size of single texel
//    vec3 result_h = texture(image, outTex).rgb * weight[0];
//    vec3 result_v = texture(image, outTex).rgb * weight[0];
//
//    for(int i = 1; i < 5; ++i)
//    {
//        result_h += texture(image, outTex + vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
//        result_h += texture(image, outTex - vec2(tex_offset.x * i, 0.0)).rgb * weight[i];
//    }
//    for(int i = 1; i < 5; ++i)
//    {
//        result_v += texture(image, outTex + vec2(0.0, tex_offset.y * i)).rgb * weight[i];
//        result_v += texture(image, outTex - vec2(0.0, tex_offset.y * i)).rgb * weight[i];
//    }
//
//    gl_FragColor = vec4(result_h + result_v, 1.0);


//    vec2 offsets[9] = vec2[](
//    vec2(-offset,  offset), // top-left
//    vec2( 0.0f,    offset), // top-center
//    vec2( offset,  offset), // top-right
//    vec2(-offset,  0.0f),   // center-left
//    vec2( 0.0f,    0.0f),   // center-center
//    vec2( offset,  0.0f),   // center-right
//    vec2(-offset, -offset), // bottom-left
//    vec2( 0.0f,   -offset), // bottom-center
//    vec2( offset, -offset)  // bottom-right
//    );
//
//    float kernel[9] = float[](
//    1.0 / 16, 2.0 / 16, 1.0 / 16,
//    2.0 / 16, 4.0 / 16, 2.0 / 16,
//    1.0 / 16, 2.0 / 16, 1.0 / 16
//    );
//    vec3 sampleTex[9];
//    for(int i = 0; i < 9; i++)
//    {
//        sampleTex[i] = vec3(texture(image, outTex.st + offsets[i]));
//    }
//    vec3 col = vec3(0.0);
//    for(int i = 0; i < 9; i++)
//    col += sampleTex[i] * kernel[i];
//
//    gl_FragColor = vec4(col.rgb, 1.0);
}
