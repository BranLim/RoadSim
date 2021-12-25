#type vertex
#version 330 core

layout(location=0) in vec3 aPos;

uniform mat4 uMvp;

void main(){

    gl_Position = uMvp * vec4(aPos, 1.0);

}