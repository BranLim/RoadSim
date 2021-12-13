#type vertex
#version 330 core

in vec2 aPos;
out vec4 clipSpace;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;

void main(){

    clipSpace = uProjection * uView * uTransformation * vec4(aPos.x, 0.0, aPos.y, 1.0);
    gl_Position = clipSpace;

}