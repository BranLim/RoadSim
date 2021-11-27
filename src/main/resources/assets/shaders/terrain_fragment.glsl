#type fragment
#version 330 core

in vec3 fSurfaceNormal;
in vec3 fToCameraCentre;
in vec2 fTexCoord;
in float fVisibility;

uniform sampler2D uTexture;
uniform vec3 uSunDirection;
uniform vec3 uSunColour;
uniform bool uEnableFog;
uniform vec3 uSkyColour;

out vec4 outputColor;

void main(){
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitSunDirection = normalize(-uSunDirection);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitSunDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.2);

    vec3 ambient = ambientBrightness * uSunColour;

    outputColor = vec4 (ambient, 1.0) * texture(uTexture, fTexCoord);
    if (uEnableFog){
        outputColor = mix(vec4(uSkyColour, 1.0), outputColor, fVisibility);
    }
}