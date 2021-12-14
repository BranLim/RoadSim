#type fragment
#version 330 core

in vec4 clipSpace;

out vec4 outColour;

uniform sampler2D uReflectionTexture;
uniform sampler2D uRefractionTexture;


void main(){
    vec2 normaliseDeviceCoordinate = (clipSpace.xy/clipSpace.w)/2.0+0.5;

    vec2 reflectTexCoords = vec2(normaliseDeviceCoordinate.x, 1.0 - normaliseDeviceCoordinate.y);
    vec2 refractTexCoords = vec2(normaliseDeviceCoordinate.x, normaliseDeviceCoordinate.y);

    outColour = mix(texture(uReflectionTexture, reflectTexCoords), texture(uRefractionTexture, refractTexCoords), 0.5);
}