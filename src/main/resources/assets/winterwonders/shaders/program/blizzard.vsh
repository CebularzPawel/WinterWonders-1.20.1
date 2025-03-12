#version 120

attribute vec3 Position;
attribute vec2 UV0;
attribute vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform float time;
uniform float windStrength;
uniform vec3 windDirection;

varying vec2 texCoord;
varying vec4 vertexColor;

void main() {
    // Basic vertex position calculation
    vec4 viewPos = ModelViewMat * vec4(Position, 1.0);

    // Pass texture coordinates and color to fragment shader
    texCoord = UV0;
    vertexColor = Color;

    // Apply subtle vertex displacement for wind effect
    float windEffect = sin(time * 2.0 + Position.x + Position.y) * windStrength * 0.05;
    viewPos.x += windEffect * windDirection.x;
    viewPos.y += windEffect * windDirection.y;
    viewPos.z += windEffect * windDirection.z;

    // Calculate final position
    gl_Position = ProjMat * viewPos;
}