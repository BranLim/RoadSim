#type fragment
#version 330 core

in vec3 fTexCoord;

uniform samplerCube uSkybox;
uniform vec3 uFogColour;
uniform bool uEnableFog;

out vec4 color;

const float lowerLimit = 0.0f;
const float upperLimit = 0.2f;

void main()
{
    vec4 finalColour = texture(uSkybox, fTexCoord);
    if (uEnableFog){
        float factor = (fTexCoord.y - lowerLimit) / (upperLimit-lowerLimit);
        factor = clamp(factor, 0.0, 1.0);

        color = mix(vec4(uFogColour, 1.0), finalColour, factor);
    } else {
        color = finalColour;
    }

}