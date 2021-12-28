#type fragment
#version 330 core

struct Spotlight{
    vec3 position;
    vec3 direction;
    vec3 colour;
    float cutOff;
    float outerCutOff;
};

const int MAX_LIGHTS = 5;

in vec3 fragPosition;
in vec3 fSurfaceNormal;
in vec2 fTexCoord;
in float fVisibility;
in vec3 toLightSource[MAX_LIGHTS];
in vec4 shadowCoordinates;

uniform sampler2D uTexture;
uniform vec3 uSunDirection;
uniform vec3 uSunColour;
uniform bool uEnableFog;
uniform vec3 uFogColour;
uniform vec3 uLightColour[MAX_LIGHTS];
uniform bool enableSpotlight;
uniform Spotlight spotlight;
uniform sampler2D uShadowMap;
uniform float uShadowMapResolution;

out vec4 outputColor;

const int pcfCount = 2;
const float totalTexels = (pcfCount * 2.0 + 1.0) * (pcfCount * 2.0 + 1.0);

vec3 calculateSpotlight(vec3 fragPosition, vec3 surfaceNormal, vec3 viewDirection);

void main(){

    float texelSize = 1.0/uShadowMapResolution;
    float total = 0.0;
    for (int x = -pcfCount; x<= pcfCount; x++){
        for (int y = -pcfCount; y<=pcfCount;y++){
            float objectNearestLight = texture(uShadowMap, shadowCoordinates.xy+ vec2(x, y) * texelSize).r;
            if (shadowCoordinates.z > objectNearestLight){
                total += 1.0;
            }
        }
    }
    total /= totalTexels;

    float lightFactor = 1.0f - (total * shadowCoordinates.w);

    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitSunDirection = normalize(-uSunDirection);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitSunDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.0);

    vec3 ambient = ambientBrightness * uSunColour;

    vec3 totalDiffuse = vec3(0.0f);
    for (int i = 0; i < MAX_LIGHTS;i++){
        vec3 unitLightVector = normalize(toLightSource[i]);

        float diffuseLightIntensity = dot(unitSurfaceNormal, unitLightVector);
        float diffuseBrightness = max(diffuseLightIntensity, 0.0f);

        float distance = length(toLightSource[i]);
        float attenuation = 1.0/(1.0 + 0.01 * distance + 0.0 * (distance * distance));

        vec3 diffuseLight = diffuseBrightness * attenuation * uLightColour[i];
        totalDiffuse = totalDiffuse + diffuseLight;
    }
    if (enableSpotlight){
        totalDiffuse += calculateSpotlight(fragPosition, unitSurfaceNormal, vec3(0));
    }

    vec3 finalDiffuse = max((ambient + (totalDiffuse * lightFactor)), 0.1);
    outputColor = vec4 (finalDiffuse, 1.0) * texture(uTexture, fTexCoord);

    if (uEnableFog){
        outputColor = mix(vec4(uFogColour, 1.0), outputColor, fVisibility);
    }
}

vec3 calculateSpotlight(vec3 fragPosition, vec3 surfaceNormal, vec3 viewDirection){
    vec3 diffuseLight = vec3(0);
    vec3 unitLightVector = normalize(spotlight.position - fragPosition);
    float theta = dot(unitLightVector, normalize(-spotlight.direction));

    float epsilon = spotlight.cutOff - spotlight.outerCutOff;
    float intensity = clamp((theta - spotlight.outerCutOff) / epsilon, 0.0, 1.0);

    diffuseLight = intensity * spotlight.colour;

    return diffuseLight;
}