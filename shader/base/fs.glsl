#version 130

in vec3 outColor;
in vec2 outTex;

uniform sampler2D image1;
uniform sampler2D image2;

const float offset = 1.0 / 00.0;
const mediump vec3 luminanceWeighting = vec3(0.2125, 0.7154, 0.0721);
void main()
{
    vec4 textureColor = texture(image1, outTex);
    float saturation = 1.5;
    lowp float luminance = dot(textureColor.rgb, luminanceWeighting);
    lowp vec3 greyScaleColor = vec3(luminance);
    gl_FragColor = vec4(mix(greyScaleColor, textureColor.rgb, saturation), textureColor.w);

//    float m_contrast = 1.15;
//    gl_FragColor = vec4(((textureColor.rgb - vec3(0.5)) * m_contrast + vec3(0.5)), textureColor.w);

    vec4 processColor = gl_FragColor;

    float Gray = processColor.r*0.299 + processColor.g*0.587 + processColor.b*0.114;
    if (Gray < 0.1) {
        processColor.rgb = processColor.rgb * 0.4;
    }
    gl_FragColor = processColor;
    gl_FragColor = textureColor;
}