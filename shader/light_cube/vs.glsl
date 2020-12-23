#version 130

in vec3 cubePos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform mat4 orthoProj;

uniform int renderShadowMap;
void main()
{
    if (renderShadowMap != 1) {
        gl_Position = proj * view * model * vec4(cubePos, 1.0);
    } else {
        gl_Position = orthoProj * view * model * vec4(cubePos, 1.0);
    }
//    gl_Position = proj * view * model * vec4(cubePos, 1.0);
}