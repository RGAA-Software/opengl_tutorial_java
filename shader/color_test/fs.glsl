#version 330 core

uniform vec3 rgbColor;
uniform vec3 bgrColor;

void main()
{
    if (gl_FragCoord.x > 150.0) {
        gl_FragColor = vec4(bgrColor/255.0, 1.0);
    } else {
        gl_FragColor = vec4(rgbColor/255.0, 1.0);
    }
}