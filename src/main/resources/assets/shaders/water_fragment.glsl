#type fragment
#version 330 core

in vec4 clipSpace;
in vec2 textureCoordinates;

out vec4 outColour;

uniform sampler2D uReflectionTexture;
uniform sampler2D uRefractionTexture;
uniform sampler2D uDuDvTexture;
uniform float uWaveOffset;

const float waveStrength = 0.02;

void main(){
    vec2 normaliseDeviceCoordinate = (clipSpace.xy/clipSpace.w) / 2.0 + 0.5;

    vec2 reflectTexCoords = vec2(normaliseDeviceCoordinate.x, -normaliseDeviceCoordinate.y);
    vec2 refractTexCoords = vec2(normaliseDeviceCoordinate.x, normaliseDeviceCoordinate.y);

    vec2 distortion1 = (texture(uDuDvTexture, vec2(textureCoordinates.x + uWaveOffset, textureCoordinates.y)).rg * 2.0 -1.0) * waveStrength;
    vec2 distortion2 = (texture(uDuDvTexture, vec2(-textureCoordinates.x + uWaveOffset, textureCoordinates.y+ uWaveOffset)).rg * 2.0 -1.0) * waveStrength;
    vec2 totalDistortion = distortion1 + distortion2;

    reflectTexCoords += totalDistortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

    refractTexCoords += totalDistortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    outColour = mix(texture(uReflectionTexture, reflectTexCoords), texture(uRefractionTexture, refractTexCoords), 0.5);
    outColour = mix(outColour, vec4(0.0,0.2, 0.4, 1.0), 0.15);
}