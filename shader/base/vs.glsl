#version 130

in vec3 aPos;
in vec3 aColor;
in vec2 aTex;

out vec3 outColor;
out vec2 outTex;

void main()
{
    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
    outColor = aColor;
    outTex = aTex;
}