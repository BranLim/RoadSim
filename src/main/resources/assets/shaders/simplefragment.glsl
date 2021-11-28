#type fragment
#version 330 core

const int MAX_LIGHTS = 5;

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

out vec4 color;

vec3 calculateSpecularReflection(vec3 unitToLightDirection, vec3 unitSurfaceNormal, vec3 unitToCamera);


vec3 calculateSpecularReflection(vec3 unitToLightDirection, vec3 unitSurfaceNormal, vec3 unitToCamera){
    vec3 reflectedLight = reflect(unitToLightDirection, unitSurfaceNormal);
    vec3 unitReflectedLight = normalize(reflectedLight);
    float specularFactor = max(dot(reflectedLight, unitToCamera), 0.0);
    vec3 finalSpecular = uReflectivity * pow(specularFactor, uShineDampen) * uGlobalLightColour;

    return finalSpecular;
}

void main()
{
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitGlobalLightDirection = normalize(-uGlobalLightDirection);
    vec3 unitToCamera = normalize(fToCameraCentre);

    float ambientLightIntensity = dot(unitSurfaceNormal, unitGlobalLightDirection);
    float ambientBrightness = max(ambientLightIntensity, 0.0f);

    vec3 ambient = ambientBrightness * uGlobalLightColour;
    vec3 specular = calculateSpecularReflection(unitGlobalLightDirection, unitSurfaceNormal, unitToCamera);

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

        vec3 specular = calculateSpecularReflection(unitLightDirection, unitSurfaceNormal, unitToCamera);
        totalDiffuse = totalDiffuse + diffuse;
        totalSpecular = totalSpecular+ specular;
    }
    vec3 finalDiffuse = max((ambient + totalDiffuse), 0.0);
    vec3 finalSpecular = specular + totalSpecular;

    color = vec4(finalDiffuse, 1.0) * texture(uTexture, fTexCoord) + vec4(finalSpecular, 1.0);
}