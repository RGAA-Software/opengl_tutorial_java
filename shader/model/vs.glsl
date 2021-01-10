#version 330 core

in vec3 aPos;
in vec3 aNormal;
in vec2 aTex;
in vec3 aTangent;
in vec3 aBiTangent;

out vec3 outPos;
out vec3 outNormal;
out vec2 outTex;

out vec3 posInTBN;
out vec3 lightPosInTBN;
out vec3 cameraPosInTBN;

out vec4 outLightViewPos;

uniform mat4 model;
uniform mat4 view;
uniform mat4 proj;
uniform mat4 orthoProj;
uniform mat4 orthoView;

uniform int renderShadowMap;

uniform vec3 lightPos;
uniform vec3 cameraPos;

void main()
{
    gl_Position = proj * view * model * vec4(aPos, 1.0);
    outTex = aTex;

    outPos = vec3(model * vec4(aPos, 1.0));

    mat3 normalMatrix = transpose(inverse(mat3(model)));
    outNormal = normalMatrix * aNormal;

    vec3 T = normalize(normalMatrix * aTangent);
    vec3 N = normalize(normalMatrix * aNormal);
    T = normalize(T - dot(T, N) * N);
    vec3 B = cross(N, T);

    mat3 TBN = transpose(mat3(T, B, N));
    cameraPosInTBN = TBN * cameraPos;
    lightPosInTBN  = TBN * lightPos;
    posInTBN  = TBN * outPos;
}