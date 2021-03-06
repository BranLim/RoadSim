#type vertex
#version 330 core

in vec2 aPos;

out vec4 clipSpace;
out vec2 textureCoordinates;
out vec3 toCameraPosition;
out vec3 toLightPosition;

uniform mat4 uProjection;
uniform mat4 uView;
uniform mat4 uTransformation;
uniform vec3 uCameraPosition;
uniform vec3 uSunDirection;

const float tiling = 80.0;

void main(){

    vec4 worldPosition = uTransformation * vec4(aPos.x, 0.0, aPos.y, 1.0);
    clipSpace = uProjection * uView * worldPosition;
    gl_Position = clipSpace;
    textureCoordinates = vec2 (aPos.x/2.0+0.5, aPos.y/2.0+0.5) * tiling;
    toCameraPosition = uCameraPosition - worldPosition.xyz;

    toLightPosition = worldPosition.xyz - uSunDirection;
}