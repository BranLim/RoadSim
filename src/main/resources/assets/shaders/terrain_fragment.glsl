#type fragment
#version 330 core

const int MAX_LIGHTS = 5;

in vec3 fSurfaceNormal;
in vec2 fTexCoord;
in float fVisibility;
in vec3 toLightSource[MAX_LIGHTS];

uniform sampler2D uTexture;
uniform vec3 uSunDirection;
uniform vec3 uSunColour;
uniform bool uEnableFog;
uniform vec3 uFogColour;
uniform vec3 uLightColour[MAX_LIGHTS];

out vec4 outputColor;

void main(){
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitSunDirection = normalize(-uSunDirection);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitSunDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.2);

    vec3 ambient = ambientBrightness * uSunColour;

    vec3 totalDiffuse = vec3(0.0f);
    for (int i = 0; i< MAX_LIGHTS;i++){
        vec3 unitLightVector = normalize(toLightSource[i]);

        float diffuseLightIntensity = dot(unitSurfaceNormal, unitLightVector);
        float diffuseBrightness = max(diffuseLightIntensity, 0.0f);

        float distance = length(toLightSource[i]);
        float attenuation = 1.0/(1.0 + 0.01 * distance + 0.0 * (distance * distance));

        vec3 diffuseLight = diffuseBrightness * attenuation * uLightColour[i];
        totalDiffuse = totalDiffuse + diffuseLight;
    }

    vec3 finalDiffuse = max((ambient + totalDiffuse), 0.2);
    outputColor = vec4 (finalDiffuse, 1.0) * texture(uTexture, fTexCoord);

    if (uEnableFog){
        outputColor = mix(vec4(uFogColour, 1.0), outputColor, fVisibility);
    }
}