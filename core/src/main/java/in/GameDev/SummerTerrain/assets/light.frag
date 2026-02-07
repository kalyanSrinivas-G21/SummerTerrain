#ifdef GL_ES
precision mediump float;
#endif

uniform sampler2D u_texture;
uniform vec2 u_lightPos;   // light position (0..1)
uniform float u_radius;    // light radius
uniform float u_intensity; // light strength

varying vec2 v_texCoords;

void main() {
    vec4 color = texture2D(u_texture, v_texCoords);

    float dist = distance(v_texCoords, u_lightPos);
    float light = 1.0 - smoothstep(0.0, u_radius, dist);

    light *= u_intensity;

    // Darkness outside light
    color.rgb *= clamp(light, 0.2, 1.0);

    gl_FragColor = color;
}
