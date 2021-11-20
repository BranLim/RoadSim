#type fragment
#version 330 core

in vec3 fSurfaceNormal;
in vec3 fLightDirection;
in vec2 fTextCoord;
in vec3 fToCameraCentre;

uniform sampler2D fTexture;
uniform vec3 fLightColour;
uniform vec3 uGlobalLightDirection;

/*
uniform float reflectivity;
uniform float dampenShine;
*/
out vec4 color;

void main()
{
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitGlobalLightDirection = normalize(-uGlobalLightDirection);
    vec3 unitLightDirection = normalize(fLightDirection);
    vec3 unitToCamera = normalize(fToCameraCentre);

    float distance = length(fLightDirection);

    //float attenuation = 1.0/(1.0 + 0.01 * distance + 0.001 * distance);
    float attenuation = 1.0/(1.0 + 0.01 * distance + 0.0 * (distance * distance) );
    float dotGln = dot(unitSurfaceNormal, unitGlobalLightDirection);

    float dotln = dot(unitSurfaceNormal, unitLightDirection);

    float brightness = max(dotln, 0.0f);
    float globalBrightness = max(dotGln, 0.2f);

    vec3 diffuse = globalBrightness + (brightness * attenuation * fLightColour);

    color = vec4(diffuse, 1.0) * texture(fTexture, fTextCoord);
}