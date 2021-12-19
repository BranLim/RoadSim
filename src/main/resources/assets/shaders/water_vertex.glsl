#type vertex
#version 330 core

in vec2 aPos;

out vec4 clipSpace;
out vec2 textureCoordinates;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;

const float tiling = 80.0;

void main(){

    clipSpace = uProjection * uView * uTransformation * vec4(aPos.x, 0.0, aPos.y, 1.0);
    gl_Position = clipSpace;
    textureCoordinates = vec2 (aPos.x/2.0+0.5, aPos.y/2.0+0.5) * tiling;

}