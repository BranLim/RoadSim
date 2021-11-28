#type fragment
#version 330 core

in vec3 fSurfaceNormal;
in vec3 fToCameraCentre;
in vec2 fTexCoord;
in float fVisibility;
in vec3 toLightSource[5];

uniform sampler2D uTexture;
uniform vec3 uSunDirection;
uniform vec3 uSunColour;
uniform bool uEnableFog;
uniform vec3 uFogColour;
uniform vec3 uLightColour[5];

out vec4 outputColor;

void main(){
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitSunDirection = normalize(-uSunDirection);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitSunDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.05);

    vec3 ambient = ambientBrightness * uSunColour;

    vec3 totalDiffuse = vec3(0.f);
    for (int i = 0; i< 5;i++){
        vec3 unitLightVector = normalize(toLightSource[i]);
        float diffuseLightIntensity = dot(unitSurfaceNormal, unitLightVector);
        float diffuseBrightness = max(diffuseLightIntensity, 0.1f);

        vec3 lightDirection = -unitLightVector;
        vec3 diffuseLight = diffuseBrightness * uLightColour[i];
        totalDiffuse+= diffuseLight;
    }

    vec3 finalDiffuse = ambient + totalDiffuse;
    outputColor = vec4 (ambient, 1.0) * texture(uTexture, fTexCoord);

    if (uEnableFog){
        outputColor = mix(vec4(uFogColour, 1.0), outputColor, fVisibility);
    }
}