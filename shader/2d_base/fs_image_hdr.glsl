#version 330 core

in vec2 outTex;

uniform sampler2D image;

void main() {
//    gl_FragColor = vec4(texture(image, outTex).rgb, 1.0);

    const float gamma = 2.2;
    vec3 hdrColor = texture(image, outTex).rgb;
    // Reinhard色调映射
    vec3 mapped = hdrColor / (hdrColor + vec3(1.0));
    // Gamma校正
    mapped = pow(mapped, vec3(1.0 / gamma));
    gl_FragColor = vec4(mapped, 1.0);

//    const float gamma = 2.2;
//    vec3 hdrColor = texture(image, outTex).rgb;
//    float exposure = 0.8;
//    // exposure tone mapping
//    vec3 mapped = vec3(1.0) - exp(-hdrColor * exposure);
//    // gamma correction
//    mapped = pow(mapped, vec3(1.0 / gamma));
//
//    gl_FragColor = vec4(mapped, 1.0);

}
