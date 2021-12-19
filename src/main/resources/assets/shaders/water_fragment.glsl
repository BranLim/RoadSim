#type fragment
#version 330 core

in vec4 clipSpace;
in vec2 textureCoordinates;
in vec3 toCameraPosition;
in vec3 toLightPosition;

out vec4 outColour;

uniform sampler2D uReflectionTexture;
uniform sampler2D uRefractionTexture;
uniform sampler2D uDuDvTexture;
uniform sampler2D uNormalMap;
uniform float uWaveOffset;
uniform vec3 uSunColour;

const float waveStrength = 0.02;
const float shineDampen = 20.;
const float reflectivity = 0.6;

void main(){
    vec2 normaliseDeviceCoordinate = (clipSpace.xy/clipSpace.w) / 2.0 + 0.5;

    vec2 reflectTexCoords = vec2(normaliseDeviceCoordinate.x, -normaliseDeviceCoordinate.y);
    vec2 refractTexCoords = vec2(normaliseDeviceCoordinate.x, normaliseDeviceCoordinate.y);

    vec2 distortedTextureCoordinates = texture(uDuDvTexture, vec2(textureCoordinates.x + uWaveOffset, textureCoordinates.y)).rg * 0.1;
    distortedTextureCoordinates = textureCoordinates + vec2 (distortedTextureCoordinates.x, distortedTextureCoordinates.y+uWaveOffset);
    vec2 totalDistortion = (texture(uDuDvTexture, distortedTextureCoordinates).rg * 2.0 - 1.0) * waveStrength;

    reflectTexCoords += totalDistortion;
    reflectTexCoords.x = clamp(reflectTexCoords.x, 0.001, 0.999);
    reflectTexCoords.y = clamp(reflectTexCoords.y, -0.999, -0.001);

    refractTexCoords += totalDistortion;
    refractTexCoords = clamp(refractTexCoords, 0.001, 0.999);

    vec3 viewVector = normalize(toCameraPosition);
    float refractiveFactor = dot(viewVector, vec3(0.0, 1.0, 0.0));
    refractiveFactor = pow(refractiveFactor, 0.5);

    vec4 normalMapColour = texture(uNormalMap, distortedTextureCoordinates);
    vec3 normal = vec3(normalMapColour.r*2.0-1.0, normalMapColour.b, normalMapColour.g*2.0-1.0);
    normal = normalize(normal);

    vec3 reflectedLight = reflect(normalize(toLightPosition), normal);
    float specular = max(dot(reflectedLight, viewVector), 0.0);
    specular = pow(specular, shineDampen);
    vec3 specularHighlight = uSunColour * specular * reflectivity;

    outColour = mix(texture(uReflectionTexture, reflectTexCoords), texture(uRefractionTexture, refractTexCoords), refractiveFactor);
    outColour = mix(outColour, vec4(0.0, 0.2, 0.4, 1.0), 0.15) + vec4(specularHighlight, 0.0);

}