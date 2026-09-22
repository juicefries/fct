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
        result = texture(uTexture, TexCoord);
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
