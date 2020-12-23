#version 330 core

in vec3 aPos;
in vec3 aColor;
in vec2 aTex;

out vec3 fragColor;
out vec2 outTex;

uniform mat4 model;

void main() {

    gl_Position = model * vec4(aPos, 1.0);
    fragColor = aColor;
    outTex = aTex;
}
