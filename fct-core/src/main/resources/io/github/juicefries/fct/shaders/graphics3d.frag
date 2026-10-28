#version 330 core

uniform sampler2D image;
uniform float uOpacity;
uniform int uUseTexture;

in vec2 vUv;
in vec3 vColor;

out vec4 FragColor;

void main() {
    vec4 texel = uUseTexture == 1 ? texture(image, vUv) : vec4(1.0);
    FragColor = vec4(texel.rgb * vColor, texel.a * uOpacity);
}
