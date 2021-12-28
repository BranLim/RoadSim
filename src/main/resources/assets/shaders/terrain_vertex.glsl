#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aTexCoord;
layout(location=2) in vec3 aSurfaceNormal;

const int MAX_LIGHTS = 5;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform mat4 uToShadowMapSpace;
uniform bool uEnableFog;
uniform vec3 uSunDirection;
uniform vec3 uLightPosition[MAX_LIGHTS];
uniform vec4 uClipPlane;

out vec3 fragPosition;
out vec2 fTexCoord;
out vec3 fSurfaceNormal;
out vec3 fToCameraCentre;
out float fVisibility;
out vec3 toSun;
out vec3 toLightSource[MAX_LIGHTS];
out vec4 shadowCoordinates;

const float density = 0.0035;
const float gradient = 3;
const float shadowDistance = 200;
const float transitionDistance = 10.0;

void main()
{
    vec4 worldPosition = uTransformation * vec4(aPos, 1.0);
    fragPosition = worldPosition.xyz;

    gl_ClipDistance[0] = dot(worldPosition, uClipPlane);

    vec4 positionRelativeToCamera = uView * worldPosition;
    shadowCoordinates = uToShadowMapSpace * worldPosition;

    gl_Position = uProjection * positionRelativeToCamera;

    fTexCoord = aTexCoord * 20.0;
    fSurfaceNormal = (uTransformation * vec4(aSurfaceNormal, 0.0)).xyz;

    for (int i = 0; i< MAX_LIGHTS; i++){
        toLightSource[i] = uLightPosition[i] - worldPosition.xyz;
    }

    float distance = length(positionRelativeToCamera);
    if (uEnableFog){
        float visibility = exp(-pow((distance*density), gradient));
        fVisibility = clamp(visibility, 0.0, 1.0);
    }

    distance = distance - (shadowDistance - transitionDistance);
    distance = distance / transitionDistance;
    shadowCoordinates.w = clamp(1.0 -  distance, 0.0, 1.0);

}

