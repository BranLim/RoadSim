#type vertex
#version 330 core
layout(location=0) in vec3 aPos;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;

out vec3 fTexCoord;

void main()
{
    fTexCoord = aPos;
    gl_Position = uProjection * uView * vec4(aPos, 1.0);
}

