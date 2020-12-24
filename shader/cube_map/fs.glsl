#version 330 core

in vec4 outPos;

uniform vec3 lightPos;
uniform float farPlane;

void main() {
    float lightDistance = length(outPos.xyz - lightPos);
    lightDistance = lightDistance / farPlane;
    gl_FragDepth = lightDistance;
}
