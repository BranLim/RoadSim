#type vertex
#version 330 core
layout(location=0) in vec3 aPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform bool uEnableFog;

out vec3 fTexCoord;
out float fVisibility;

const float density = 0.0035;
const float gradient = 5;

void main()
{
    fTexCoord = aPos;
    vec4 positionRelativeToCamera = uView * vec4(aPos, 1.0f);
    vec4 pos = uProjection * positionRelativeToCamera;


    gl_Position = pos.xyww;

    if (uEnableFog){
        float distance = length(positionRelativeToCamera);
        float visibility = exp(-pow((distance*density), gradient));
        fVisibility = clamp(visibility, 0.0, 1.0);
    }
}

