#version 120

uniform sampler2D texture;
uniform float time; // Time variable for animation
uniform vec2 resolution; // Screen resolution
uniform float windStrength; // Strength of wind effect (0-1)
uniform float snowDensity; // Density of snow particles (0-1)
uniform vec3 windDirection; // Direction of wind (normalized vector)

varying vec2 texCoord;
varying vec4 vertexColor;

// Hash function for noise generation
float hash(vec2 p) {
    p = 50.0 * fract(p * 0.3183099 + vec2(0.71, 0.113));
    return -1.0 + 2.0 * fract(p.x * p.y * (p.x + p.y));
}

// Simplex noise function for natural-looking noise
float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);

    // Quintic interpolation
    vec2 u = f * f * f * (f * (f * 6.0 - 15.0) + 10.0);

    float a = hash(i + vec2(0.0, 0.0));
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));

    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

// Function to create a single snow particle
float snowParticle(vec2 uv, float size, vec2 offset) {
    uv = uv - offset;
    float particle = smoothstep(size, 0.0, length(uv));
    return particle;
}

void main() {
    // Sample the original texture
    vec4 baseColor = texture2D(texture, texCoord) * vertexColor;

    // Calculate UV coordinates normalized to [0, 1]
    vec2 uv = gl_FragCoord.xy / resolution.xy;

    // Create windy effect by distorting UV coordinates
    float windX = noise(uv * 3.0 + time * 0.1) * windStrength;
    float windY = noise(uv * 3.0 + vec2(time * 0.15, 0.0)) * windStrength * 0.5;

    // Apply wind direction
    vec2 windOffset = vec2(windX * windDirection.x, windY * windDirection.y) * 0.03;

    // Snow particles
    vec4 snowColor = vec4(1.0, 1.0, 1.0, 0.0); // White with no alpha initially

    // Generate multiple snow particles with different sizes and movements
    for (int i = 0; i < 10; i++) {
        float t = time * (0.1 + float(i) * 0.01);
        float size = (0.001 + 0.002 * float(i % 3)) * snowDensity;

        // Create swirl pattern for particle movement
        float swirlX = sin(t + float(i) * 6.28) * 0.3;
        float swirlY = cos(t + float(i) * 6.28) * 0.3;

        // Calculate particle position with wind influence
        vec2 pos = vec2(
        fract(uv.x + swirlX + t * windDirection.x + hash(vec2(float(i), 0.0)) * 0.2),
        fract(uv.y + swirlY + t * windDirection.y + hash(vec2(0.0, float(i))) * 0.2)
        );

        // Apply wind offset
        pos += windOffset;

        // Create snow particle and add to snow color
        float particle = snowParticle(
            vec2(fract(pos.x), fract(pos.y)), // Wrap around screen edges
            size,
            vec2(0.5, 0.5) // Center of the particle
        );

        snowColor.a += particle * (0.3 + 0.7 * noise(pos * 10.0 + time));
    }

    // Blend snow with base texture
    vec4 finalColor = mix(baseColor, vec4(1.0, 1.0, 1.0, 1.0), min(snowColor.a, 0.7));

    // Add a slight blue tint for cold effect
    finalColor.rgb += vec3(0.0, 0.0, 0.2) * snowColor.a * 0.3;

    gl_FragColor = finalColor;
}