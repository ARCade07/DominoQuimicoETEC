package com.domino.texturas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Background extends Actor {

    private final Texture texturaBase;
    private final ShaderProgram shader;
    private float tempo = 0f;

    public Background() {
        this.texturaBase = criarTexturaEmBranco();
        this.shader = carregarShader();

        this.setSize(10000, 10000);
        this.setPosition(
            -this.getWidth() / 2f,
            -this.getHeight() / 2f
        );
    }

    private Texture criarTexturaEmBranco() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture textura = new Texture(pixmap);
        pixmap.dispose();

        return textura;
    }

    private ShaderProgram carregarShader() {
        ShaderProgram.pedantic = false;

        // Usa o vertex shader padrão do SpriteBatch
        String vertexShader =
            "attribute vec4 a_position;\n" +
                "attribute vec4 a_color;\n" +
                "attribute vec2 a_texCoord0;\n" +
                "uniform mat4 u_projTrans;\n" +
                "varying vec4 v_color;\n" +
                "varying vec2 v_texCoords;\n" +
                "void main() {\n" +
                "    v_color = a_color;\n" +
                "    v_color.a = v_color.a * (255.0/254.0);\n" +
                "    v_texCoords = a_texCoord0;\n" +
                "    gl_Position =  u_projTrans * a_position;\n" +
                "}";

        // Lê o arquivo do fragment shader que criamos
        String fragmentShader = Gdx.files.internal("nebulosa.frag").readString();

        ShaderProgram compilado = new ShaderProgram(vertexShader, fragmentShader);

        if (!compilado.isCompiled()) {
            throw new GdxRuntimeException("Falha ao compilar o shader do background: " + compilado.getLog());
        }

        return compilado;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        tempo += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Armazena o shader antigo para não afetar outros atores
        ShaderProgram shaderAnterior = batch.getShader();

        batch.setShader(shader);

        // Atualiza a variável de tempo no shader
        shader.setUniformf("u_time", tempo);

        Color color = getColor();
        batch.setColor(
            color.r,
            color.g,
            color.b,
            color.a * parentAlpha
        );

        // Desenha a textura branca; o shader assumirá o controle de pintar as cores
        batch.draw(
            texturaBase,
            getX(),
            getY(),
            getWidth(),
            getHeight()
        );

        // Restaura o shader padrão da engine
        batch.setShader(shaderAnterior);
    }

    public void dispose() {
        if (texturaBase != null) {
            texturaBase.dispose();
        }
        if (shader != null) {
            shader.dispose();
        }
    }
}
