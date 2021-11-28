#type vertex
#version 330 core
layout(location=0) in vec3 aPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform bool uEnableFog;

out vec3 fTexCoord;

void main()
{
    fTexCoord = aPos;
    vec4 positionRelativeToCamera = uView * vec4(aPos, 1.0f);
    vec4 pos = uProjection * positionRelativeToCamera;

    gl_Position = pos.xyww;
}

