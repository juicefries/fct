#version 330
in vec2 vUV;
out vec4 fragColor;
uniform sampler2D glyphTexture;
uniform vec4 color;
uniform float uOpacity;
void main() {
    float a = texture(glyphTexture, vUV).r;
    fragColor = vec4(color.rgb, color.a * a * uOpacity);
}
