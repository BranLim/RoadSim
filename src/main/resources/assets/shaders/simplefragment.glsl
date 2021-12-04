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
in vec3 fToCameraCentre;
in vec3 toLightSource[MAX_LIGHTS];

uniform sampler2D uTexture;

uniform vec3 uGlobalLightDirection;
uniform vec3 uGlobalLightColour;
uniform float uReflectivity;
uniform float uShineDampen;
uniform vec3 uLightColour[MAX_LIGHTS];
uniform bool enableSpotlight;
uniform Spotlight spotlight;

out vec4 color;

vec3 calculateSpecularReflection(vec3 unitToLightDirection, vec3 unitSurfaceNormal, vec3 unitToCamera, vec3 lightColour);
vec3 calculateSpotlightSpecular(vec3 fragPosition, vec3 surfaceNormal, vec3 unitToCamera);
vec3 calculateSpotlight(vec3 fragPosition, vec3 surfaceNormal);

vec3 calculateSpecularReflection(vec3 unitToLightDirection, vec3 unitSurfaceNormal, vec3 unitToCamera, vec3 lightColour){
    vec3 reflectedLight = reflect(unitToLightDirection, unitSurfaceNormal);
    vec3 unitReflectedLight = normalize(reflectedLight);
    float specularFactor = max(dot(unitReflectedLight, unitToCamera), 0.0);
    vec3 finalSpecular = uReflectivity * pow(specularFactor, uShineDampen) * lightColour;

    return finalSpecular;
}

vec3 calculateSpotlightSpecular(vec3 fragPosition, vec3 surfaceNormal, vec3 unitToCamera){
    vec3 finalSpecular = vec3(0);
    vec3 unitLightVector = normalize(spotlight.position - fragPosition);
    vec3 unitLightDirection =  normalize(-spotlight.direction);

    float theta = dot(unitLightVector, unitLightDirection);

    if (theta > spotlight.cutOff){

        vec3 reflectedLight = reflect(unitLightDirection, surfaceNormal);
        vec3 unitReflectedLight = normalize(reflectedLight);

        float specularSpotLightIntensity = dot(unitReflectedLight, unitToCamera);
        float specularFactor = max(specularSpotLightIntensity, 0.0);
        finalSpecular = uReflectivity * pow(specularFactor, uShineDampen)  * spotlight.colour;
    }

    return finalSpecular;
}

vec3 calculateSpotlight(vec3 fragPosition, vec3 surfaceNormal){
    vec3 finalColour = vec3(0);
    vec3 toLightSource = spotlight.position - fragPosition;
    vec3 unitLightVector = normalize(toLightSource);

    float distance = length(toLightSource);
    float attenuation = 1.0/(1.0 + 0.01 * distance + 0.0 * (distance * distance));

    float theta = dot(unitLightVector, normalize(-spotlight.direction));

    if (theta > spotlight.cutOff){
        float diffuseSpotLightIntensity = dot(surfaceNormal, unitLightVector);
        float diffuseSpotLightBrightness = max(diffuseSpotLightIntensity, 0.0);
        finalColour = diffuseSpotLightBrightness * spotlight.colour;
    }

    return finalColour;
}


void main()
{
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitGlobalLightDirection = normalize(-uGlobalLightDirection);
    vec3 unitToCamera = normalize(fToCameraCentre);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitGlobalLightDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.0f);

    vec3 ambient = ambientBrightness * uGlobalLightColour;
    vec3 specular = calculateSpecularReflection(unitGlobalLightDirection, unitSurfaceNormal, unitToCamera, uGlobalLightColour);

    vec3 totalDiffuse = vec3(0.0f);
    vec3 totalSpecular = vec3(0.0f);
    for (int i = 0; i < MAX_LIGHTS; i++){

        vec3 unitLightVector = normalize(toLightSource[i]);
        vec3 unitLightDirection = normalize(-unitLightVector);

        float distance = length(toLightSource[i]);
        float attenuation = 1.0/(1.0 + 0.01 * distance + 0.0 * (distance * distance));

        float lightIntensity = dot(unitSurfaceNormal, unitLightVector);
        float lightBrightness = max(lightIntensity, 0.0f);

        vec3 diffuse = lightBrightness * attenuation * uLightColour[i];

        vec3 specular = calculateSpecularReflection(unitLightDirection, unitSurfaceNormal, unitToCamera, uLightColour[i]);
        totalDiffuse = totalDiffuse + diffuse;
        totalSpecular = totalSpecular + specular;
    }
    if (enableSpotlight){
        totalDiffuse += calculateSpotlight(fragPosition, unitSurfaceNormal);
        totalSpecular += calculateSpotlightSpecular(fragPosition, unitSurfaceNormal, unitToCamera);
    }

    vec3 finalDiffuse = max((ambient + totalDiffuse), 0.0);
    vec3 finalSpecular = specular + totalSpecular;

    color = vec4(finalDiffuse, 1.0) * texture(uTexture, fTexCoord) + vec4(finalSpecular, 1.0);
}
