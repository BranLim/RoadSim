#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aTextCoord;
layout(location=2) in vec3 aSurfaceNormal;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform vec3 uLightPosition;

out vec2 fTextCoord;
out vec3 fSurfaceNormal;
out vec3 fLightDirection;
out vec3 fToCameraCentre;

void main()
{
    vec4 worldPosition = uTransformation * vec4(aPos, 1.0);
    gl_Position = uProjection * uView * worldPosition;

    fToCameraCentre = (inverse(uView) * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
    fTextCoord = aTextCoord;
    fSurfaceNormal = (worldPosition * vec4(aSurfaceNormal, 0.0)).xyz;
    fLightDirection = uLightPosition - worldPosition.xyz;

}

