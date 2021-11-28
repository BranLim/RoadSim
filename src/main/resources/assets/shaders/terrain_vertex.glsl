#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aTextCoord;
layout(location=2) in vec3 aSurfaceNormal;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform bool uEnableFog;

out vec2 fTexCoord;
out vec3 fSurfaceNormal;
out vec3 fToCameraCentre;
out float fVisibility;

const float density = 0.0035;
const float gradient = 3;

void main()
{
    vec4 worldPosition = uTransformation * vec4(aPos, 1.0);
    vec4 positionRelativeToCamera = uView * worldPosition;

    gl_Position = uProjection * positionRelativeToCamera;

    fToCameraCentre = (inverse(uView) * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
    fTexCoord = aTextCoord * 40.0f;
    fSurfaceNormal = (worldPosition * vec4(aSurfaceNormal, 0.0)).xyz;

    if (uEnableFog){
        float distance = length(positionRelativeToCamera);
        float visibility = exp(-pow((distance*density), gradient));
        fVisibility = clamp(visibility, 0.0, 1.0);
    }
}

