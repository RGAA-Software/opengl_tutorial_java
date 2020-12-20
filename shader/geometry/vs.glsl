#version 330 core

in vec3 aPos;

out vec2 position;


void main() {
    gl_PointSize = 10;
    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1);
}
