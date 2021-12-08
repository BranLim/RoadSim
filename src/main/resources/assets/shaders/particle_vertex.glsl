#type vertex
#version 330 core

layout(location=0) in vec2 aPos;

uniform mat4 uProjection;
uniform mat4 uTransformation;

out vec2 fTextureCoords;

void main(){

    fTextureCoords = aPos + vec2 (0.5, 0.5);
    fTextureCoords.y = 1.0 - fTextureCoords.y;

    gl_Position = uProjection * uTransformation * vec4(aPos, 0.0, 1.0);
}