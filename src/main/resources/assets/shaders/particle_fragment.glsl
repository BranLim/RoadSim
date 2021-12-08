#type fragment
#version 330 core

in vec2 fTextureCoords;

out vec4 outColour;

uniform sampler2D uTexture;

void main(){
    outColour = texture(uTexture, fTextureCoords);
}