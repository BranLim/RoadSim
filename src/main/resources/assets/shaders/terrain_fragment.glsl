#type fragment
#version 330 core

in vec3 fSurfaceNormal;
in vec3 fToCameraCentre;
in vec2 fTexCoord;


uniform sampler2D uTexture;
uniform vec3 sunDirection;
uniform vec3 sunColour;

out vec4 outputColor;

void main(){
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitSunDirection = normalize(-sunDirection);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitSunDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.2);

    vec3 ambient = ambientBrightness * sunColour;

    outputColor = vec4 (ambient, 1.0) * texture(uTexture, fTexCoord);
}