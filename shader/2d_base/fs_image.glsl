#version 330 core

in vec2 outTex;

uniform sampler2D image;

void main() {
    gl_FragColor = texture(image, outTex);
}
