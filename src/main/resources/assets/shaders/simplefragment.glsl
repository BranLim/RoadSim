#type fragment
#version 330 core

in vec3 fSurfaceNormal;
in vec3 fLightDirection;
in vec2 fTextCoord;

uniform sampler2D fTexture;
uniform vec3 fLightColour;

out vec4 color;

void main()
{
    vec3 unitSurfaceNormal = normalize(fSurfaceNormal);
    vec3 unitLightDirection = normalize(fLightDirection);
    float distance = length(fSurfaceNormal);

    float attuenation = 1.0/(1.0 + 0.2 * distance + 0.01 * distance);

    float dotln = dot(unitSurfaceNormal, unitLightDirection);
    float brightness = max(dotln, 0.0f);
    vec3 diffuse = brightness * attuenation * fLightColour;

    color = vec4(diffuse, 1.0) * texture(fTexture, fTextCoord);
}