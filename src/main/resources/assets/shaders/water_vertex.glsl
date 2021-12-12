#type vertex
#version 330 core

in vec2 aPos;
out vec2 textureCoordinates;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;

void main(){
    gl_Position = uProjection * uView * uTransformation * vec4(aPos.x, 0.0, aPos.y, 1.0);
    textureCoordinates = vec2 (aPos.x/2.0+0.5, aPos.y/2.0+0.5);
}