#type vertex
#version 330 core

layout(location=0) in vec2 aPos;
layout(location=1) in mat4 modelView;

out vec2 fTextureCoords;

uniform mat4 uProjection;

void main(){

    fTextureCoords = aPos + vec2 (0.5, 0.5);
    fTextureCoords.y = 1.0 - fTextureCoords.y;

    gl_Position = uProjection * modelView * vec4(aPos, 0.0, 1.0);
}