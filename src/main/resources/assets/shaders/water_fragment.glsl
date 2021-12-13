#type fragment
#version 330 core

in vec4 clipSpace;

out vec4 outColour;

uniform sampler2D uRefractionTexture;
uniform sampler2D uReflectionTexture;

void main(){
    vec2 normaliseDeviceCoordinate = (clipSpace.xy/clipSpace.w)/2.0+0.5;
    vec2 refractTexCoords = vec2(normaliseDeviceCoordinate.x, normaliseDeviceCoordinate.y);
    vec2 reflectTexCoords = vec2(normaliseDeviceCoordinate.x, -normaliseDeviceCoordinate.y);
    outColour = mix(texture(uReflectionTexture, reflectTexCoords), texture(uReflectionTexture, refractTexCoords), 0.5);
}