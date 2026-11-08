#version 330 core

uniform vec4 uColor;
uniform int uMode;
uniform vec4 uColors[16];
uniform float uFractions[16];
uniform int uColorCount;
uniform vec2 uStart;
uniform vec2 uEnd;
uniform vec2 uCenter;
uniform vec2 uFocus;
uniform float uRadius;
uniform int uCycleMethod;
uniform sampler2D uTexture;
uniform int uHasTexture;
uniform float uOpacity;

in vec2 TexCoord;
in vec2 WorldPos;
out vec4 FragColor;

float applyCycle(float t) {
    if (uCycleMethod == 0) {
        return clamp(t, 0.0, 1.0);
    } else if (uCycleMethod == 1) {
        float period = 2.0;
        float val = mod(t, period);
        return val > 1.0 ? 2.0 - val : val;
    } else {
        return mod(t, 1.0);
    }
}

// Catmull-Rom 基函数（4-tap bicubic 优化版）
vec4 cubic(float v) {
    vec4 n = vec4(1.0, 2.0, 3.0, 4.0) - v;
    vec4 s = n * n * n;
    float x = s.x;
    float y = s.y - 4.0 * s.x;
    float z = s.z - 4.0 * s.y + 6.0 * s.x;
    float w = 6.0 - x - y - z;
    return vec4(x, y, z, w) * (1.0 / 6.0);
}

// 双三次采样：放大时不丢失纹理细节
vec4 textureBicubic(sampler2D tex, vec2 texCoords) {
    vec2 texSize = vec2(textureSize(tex, 0));
    vec2 invTexSize = 1.0 / texSize;
    texCoords = texCoords * texSize - 0.5;
    vec2 fxy = fract(texCoords);
    texCoords -= fxy;
    vec4 xcubic = cubic(fxy.x);
    vec4 ycubic = cubic(fxy.y);
    vec4 c = texCoords.xxyy + vec2(-0.5, 1.5).xyxy;
    vec4 s = vec4(xcubic.xz + xcubic.yw, ycubic.xz + ycubic.yw);
    vec4 offset = c + vec4(xcubic.yw, ycubic.yw) / s;
    offset *= invTexSize.xxyy;
    vec4 sample0 = texture(tex, offset.xz);
    vec4 sample1 = texture(tex, offset.yz);
    vec4 sample2 = texture(tex, offset.xw);
    vec4 sample3 = texture(tex, offset.yw);
    float sx = s.x / (s.x + s.y);
    float sy = s.z / (s.z + s.w);
    return mix(mix(sample3, sample2, sx), mix(sample1, sample0, sx), sy);
}

vec4 interpolateColors(float t) {
    t = applyCycle(t);
    for (int i = 0; i < uColorCount - 1; i++) {
        if (t >= uFractions[i] && t < uFractions[i + 1]) {
            float local = (t - uFractions[i]) / (uFractions[i + 1] - uFractions[i]);
            return mix(uColors[i], uColors[i + 1], local);
        }
    }
    return uColors[uColorCount - 1];
}

void main() {
    vec4 result;

    if (uHasTexture == 1) {
        result = textureBicubic(uTexture, TexCoord);
    } else if (uMode == 0) {
        result = uColor;
    } else if (uMode == 1 || uMode == 2) {
        vec2 dir = uEnd - uStart;
        float lenSq = dot(dir, dir);
        float t;
        if (lenSq < 0.00001) {
            t = 0.0;
        } else {
            t = dot(WorldPos - uStart, dir) / lenSq;
        }
        if (uMode == 1 && uColorCount == 2) {
            result = mix(uColors[0], uColors[1], applyCycle(t));
        } else {
            result = interpolateColors(t);
        }
    } else if (uMode == 3) {
        vec2 offset = WorldPos - uCenter;
        float dist = length(offset);
        float t = dist / uRadius;
        vec2 focusOffset = uFocus - uCenter;
        float focusDist = length(focusOffset);
        if (focusDist > 0.001) {
            float dotVal = dot(offset, focusOffset);
            t = t / (dotVal / (focusDist * focusDist) + 0.001);
        }
        result = interpolateColors(t);
    } else {
        result = uColor;
    }

    result.a *= uOpacity;
    FragColor = result;
}
