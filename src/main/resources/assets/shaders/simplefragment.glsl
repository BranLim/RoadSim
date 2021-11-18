#type fragment
#version 330 core

in vec4 fColor;
in vec2 fTextCoord;

uniform sampler2D fTexture;

out vec4 color;

void main()
{
    color = texture(fTexture, fTextCoord );
}