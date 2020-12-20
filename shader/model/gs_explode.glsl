#version 330 core

layout (triangles) in;
layout (line_strip, max_vertices = 6) out;

in Attrib {
    vec2 aTex;
    vec3 aPos;
    vec3 aNormal;
} attib_in[];

out vec3 outPos;
out vec3 outNormal;
out vec2 outTex;

uniform float time;

vec3 get_normal() {
    vec3 v1 = vec3(gl_in[0].gl_Position) - vec3(gl_in[1].gl_Position);
    vec3 v2 = vec3(gl_in[2].gl_Position) - vec3(gl_in[1].gl_Position);
    return normalize(cross(v1, v2));
}

vec4 explode(vec4 pos, vec3 normal) {
    return pos + vec4(normal, 1.0) * (sin(time) + 1) / 2;
}

void gen_normal_line(int index) {
    outTex = attib_in[index].aTex;
    outNormal = attib_in[index].aNormal;
    outPos = attib_in[index].aPos;
    gl_Position = gl_in[index].gl_Position;///*gl_in[0].gl_Position;//*/explode(gl_in[0].gl_Position, get_normal());
    EmitVertex();

    gl_Position = gl_in[index].gl_Position + vec4(outNormal, 0) * 0.005;
    EmitVertex();
    EndPrimitive();
}

void main() {
//    outTex = attib_in[0].aTex;
//    outNormal = attib_in[0].aNormal;
//    outPos = attib_in[0].aPos;
//    gl_Position = /*gl_in[0].gl_Position;//*/explode(gl_in[0].gl_Position, get_normal());
//    EmitVertex();
//
//    outTex = attib_in[1].aTex;
//    outNormal = attib_in[1].aNormal;
//    outPos = attib_in[1].aPos;
//    gl_Position = /*gl_in[1].gl_Position;//*/explode(gl_in[1].gl_Position, get_normal());
//    EmitVertex();
//
//    outTex = attib_in[2].aTex;
//    outNormal = attib_in[2].aNormal;
//    outPos = attib_in[2].aPos;
//    gl_Position = /*gl_in[2].gl_Position;//*/explode(gl_in[2].gl_Position, get_normal());
//    EmitVertex();
    gen_normal_line(0);
    gen_normal_line(1);
    gen_normal_line(2);
    EndPrimitive();
}
