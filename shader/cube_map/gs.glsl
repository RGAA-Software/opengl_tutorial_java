#version 330 core

layout(triangles) in;
layout(triangle_strip, max_vertices=18) out;

uniform mat4 cubeProj;
uniform mat4 cubeViews[6];

out vec4 outPos;

void main() {
    for (int face = 0; face < 6; face++) {
        gl_Layer = face;
        for (int i = 0; i < 3; i++) {
            outPos = gl_in[i].gl_Position;
            gl_Position = cubeProj * cubeViews[face] * outPos;
            EmitVertex();
        }
        EndPrimitive();
    }
}
