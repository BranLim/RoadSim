#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aTextCoord;
layout(location=2) in vec3 aSurfaceNormal;

const int MAX_LIGHTS = 5;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform vec3 uLightPosition[MAX_LIGHTS];

out vec2 fTexCoord;
out vec3 fSurfaceNormal;
out vec3 fToCameraCentre;
out vec3 toLightSource[MAX_LIGHTS];

void main()
{
    vec4 worldPosition = uTransformation * vec4(aPos, 1.0);
    gl_Position = uProjection * uView * worldPosition;

    fToCameraCentre = (inverse(uView) * vec4(0.0, 0.0, 0.0, 1.0)).xyz;
    fTexCoord = aTextCoord;
    fSurfaceNormal = (uTransformation * vec4(aSurfaceNormal, 0.0)).xyz;

    for (int i=0; i< MAX_LIGHTS;i++){
        toLightSource[i] = uLightPosition[i] - worldPosition.xyz;
    }

}

