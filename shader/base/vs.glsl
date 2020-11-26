attribute vec3 aPos;
attribute vec3 aColor;
attribute vec2 aTex;

varying vec3 outColor;
varying vec2 outTex;

void main()
{
    gl_Position = vec4(aPos.x, aPos.y, aPos.z, 1.0);
    outColor = aColor;
    outTex = aTex;
}