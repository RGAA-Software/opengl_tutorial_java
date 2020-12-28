#version 330 core

in vec2 outTex;

uniform sampler2D image;

void main() {
    //Y = 0.299 R + 0.587 G + 0.114 B
    vec3 color = texture(image, outTex).rgb;
    float y = 0.299 * color.r + 0.587 * color.g + 0.114 * color.b;
    if (y > 0.7) {
        gl_FragColor = vec4(color, 1.0);
    } else {
        gl_FragColor = vec4(vec3(0), 1.0);
    }
}
