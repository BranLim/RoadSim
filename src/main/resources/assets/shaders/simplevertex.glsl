#type vertex
#version 330 core
layout(location=0) in vec3 aPos;
layout(location=1) in vec2 aTextCoord;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;

out vec2 fTextCoord;

void main()
{
    gl_Position = uProjection * uView * uTransformation * vec4(aPos, 1.0);
    fTextCoord = aTextCoord;
}

