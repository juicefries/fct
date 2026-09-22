#version 330 core

in vec2 TexCoord;

uniform sampler2D uTexture;
uniform float uOpacity;

out vec4 FragColor;

void main() {
    FragColor = texture(uTexture, TexCoord);
    FragColor.a *= uOpacity;
}
