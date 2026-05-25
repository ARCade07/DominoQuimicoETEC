#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;

uniform float u_time;

/* * Função pseudo-aleatória clássica.
 */
float random(in vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

/* * Value Noise 2D com interpolação suave.
 */
float noise(in vec2 st) {
    vec2 i = floor(st);
    vec2 f = fract(st);

    float a = random(i);
    float b = random(i + vec2(1.0, 0.0));
    float c = random(i + vec2(0.0, 1.0));
    float d = random(i + vec2(1.0, 1.0));

    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(a, b, u.x) +
    (c - a) * u.y * (1.0 - u.x) +
    (d - b) * u.x * u.y;
}

/* * FBM (Fractal Brownian Motion)
 */
float fbm(in vec2 st) {
    float value = 0.0;
    float amplitude = 0.5;
    vec2 shift = vec2(100.0);

    mat2 rot = mat2(cos(0.5), sin(0.5),
    -sin(0.5), cos(0.5));

    for (int i = 0; i < 4; ++i) {
        value += amplitude * noise(st);
        st = rot * st * 2.0 + shift;
        amplitude *= 0.5;
    }
    return value;
}

void main() {
    vec2 uv = v_texCoords * 2.0 - 1.0;

    // Escala ajustada para preencher a tela com formas bem distribuídas
    vec2 st = uv * 2.5;

    /*
     * 1. DOMAIN WARPING
     * Distorce as coordenadas para criar os fluxos orgânicos do plasma
     */
    vec2 q = vec2(0.0);
    q.x = fbm(st + u_time * 0.04);
    q.y = fbm(st + vec2(1.0) + u_time * 0.05);

    vec2 r = vec2(0.0);
    r.x = fbm(st + 1.8 * q + vec2(1.7, 9.2) + u_time * 0.06);
    r.y = fbm(st + 1.8 * q + vec2(8.3, 2.8) + u_time * 0.07);

    /*
     * 2. BASE DO PLASMA
     * O noise agora varia amplamente de 0.0 a 1.0 dominando a tela
     */
    float n = fbm(st + r);

    /*
     * 3. VIGNETTE SUTIL
     * Substitui o gradiente forte por um caimento muito leve nas bordas
     */
    float dist = length(uv);
    float vignette = exp(-dist * 0.3);

    // Combina o plasma com o vignette para manter uniformidade global
    float intensidade = n * vignette;

    // Aumenta o contraste para que a variação do plasma fique bem demarcada
    intensidade = smoothstep(0.15, 0.85, intensidade);

    /*
     * =========================
     * INVERSÃO DA PALETA DE CORES
     * =========================
     */

    // Cor da Base (Fundo dominante): #4A0000
    vec3 corBase = vec3(0.290, 0.000, 0.000);

    // Cor do Topo (Veios e manchas escuras concentradas): #0D0202
    vec3 corTopo = vec3(0.051, 0.008, 0.008);

    // Interpola as cores com base na topografia do plasma
    vec3 finalColor = mix(corBase, corTopo, intensidade);

    gl_FragColor = vec4(finalColor, 1.0) * v_color;
}
