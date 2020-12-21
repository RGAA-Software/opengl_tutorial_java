#version 330 core

in vec3 aPos;
in vec3 aColor;
in vec2 aOffset;

out vec3 fragColor;

void main() {

    vec3 div = aPos * gl_InstanceID / 100.0;
    gl_Position = vec4(div + vec3(aOffset, 0), 1.0);
    fragColor = aColor;
}
