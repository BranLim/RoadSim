#type fragment
#version 330 core

in vec3 fSurfaceNormal;
in vec3 fLightDirection;
in vec2 fTextCoord;
in vec3 fToCameraCentre;

uniform sampler2D fTexture;
uniform vec3 uLightColour;
uniform vec3 uGlobalLightDirection;
uniform vec3 uGlobalLightColour;
uniform float uReflectivity;
uniform float uShineDampen;

out vec4 color;

vec3 calculateSpecularReflection(vec3 unitToGlobalLightDirection, vec3 unitSurfaceNormal, vec3 unitToCamera);


vec3 calculateSpecularReflection(vec3 unitToGlobalLightDirection, vec3 unitSurfaceNormal, vec3 unitToCamera){
    vec3 reflectedLight = reflect(unitToGlobalLightDirection, unitSurfaceNormal);
    vec3 unitReflectedLight = normalize(reflectedLight);
    float specularFactor = max(dot(reflectedLight, unitToCamera), 0.0);
    vec3 finalSpecular = uReflectivity * pow(specularFactor,uShineDampen) * uGlobalLightColour;

    return finalSpecular;
}

void main()
{
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitGlobalLightDirection = normalize(-uGlobalLightDirection);
    vec3 unitLightDirection = normalize(fLightDirection);
    vec3 unitToCamera = normalize(fToCameraCentre);

    float distance = length(fLightDirection);

    float attenuation = 1.0/(1.0 + 0.01 * distance + 0.0 * (distance * distance) );
    float dotGln = dot(unitSurfaceNormal, unitGlobalLightDirection);

    float dotln = dot(unitSurfaceNormal, unitLightDirection);

    float brightness = max(dotln, 0.0f);
    float globalBrightness = max(dotGln, 0.2f);

    vec3 diffuse = (globalBrightness * uGlobalLightColour) + (brightness * attenuation * uLightColour);
    vec3 specular = calculateSpecularReflection(unitGlobalLightDirection, unitSurfaceNormal, unitToCamera);

    color = vec4(diffuse, 1.0) * texture(fTexture, fTextCoord) + vec4(specular, 1.0);
}