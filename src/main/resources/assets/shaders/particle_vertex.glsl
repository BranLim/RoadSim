#type vertex
#version 330 core

layout(location=0) in vec2 aPos;

uniform mat4 uProjection;
uniform mat4 uTransformation;

void main(){
    gl_Position = uProjection * uTransformation * vec4(aPos, 0.0, 1.0);
}