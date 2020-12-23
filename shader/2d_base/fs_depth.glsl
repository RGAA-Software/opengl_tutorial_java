#version 330 core

in vec2 outTex;

uniform sampler2D image;

void main() {
    gl_FragColor = vec4(vec3(texture(image, outTex).r), 1.0);
}
