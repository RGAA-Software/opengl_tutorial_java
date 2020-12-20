#version 330 core

layout(points) in;
layout(triangle_strip, max_vertices = 5) out;

out vec4 color;

void main() {
//    gl_Position = gl_in[0].gl_Position + vec4(-0.1, 0, 0, 0);
//    // no use at line strip, only useful for point
//    //gl_PointSize = gl_in[0].gl_PointSize;
//    EmitVertex();
//
//    gl_Position = gl_in[0].gl_Position + vec4(0.1, 0, 0, 0);
//    //gl_PointSize = gl_in[0].gl_PointSize;
//    EmitVertex();
//
//    EndPrimitive();

    vec4 position = gl_in[0].gl_Position;
    color = vec4(1, 0.5, 0.5, 1);
    gl_Position = position + vec4(-0.1, -0.1, 0.0, 0.0);// 1:左下
    EmitVertex();
    gl_Position = position + vec4( 0.1, -0.1, 0.0, 0.0);    // 2:右下
    EmitVertex();
    gl_Position = position + vec4(-0.1,  0.1, 0.0, 0.0);    // 3:左上
    EmitVertex();
    gl_Position = position + vec4( 0.1,  0.1, 0.0, 0.0);    // 4:右上
    EmitVertex();

    color = vec4(1, 1, 1, 1);
    gl_Position = position + vec4( 0.0,  0.4, 0.0, 0.0);    // 5:顶部
    EmitVertex();
    EndPrimitive();
}
