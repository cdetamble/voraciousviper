/*
 * Copyright (C) 2016  Christian DeTamble
 *
 * This file is part of Voracious Viper.
 *
 * Voracious Viper is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Voracious Viper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Voracious Viper.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.bplaced.therefactory.voraciousviper.core.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import net.bplaced.therefactory.voraciousviper.core.misc.Utils;

import static net.bplaced.therefactory.voraciousviper.core.constants.Config.WINDOW_HEIGHT;
import static net.bplaced.therefactory.voraciousviper.core.constants.Config.WINDOW_WIDTH;

public class Particles {

    private final ParticleEffectPool fireworksEffectPool;
    private final ParticleEffectPool.PooledEffect[] fireworkEffects;
    private final float[][] startColors; // start colors to choose from for the particle effects

    public Particles(TextureAtlas textureAtlas) {
        fireworkEffects = new ParticleEffectPool.PooledEffect[5];
        startColors = new float[][]{ //
                new float[]{0, .58f, 1}, // blue
                new float[]{1, .984f, .267f}, // yellow
                new float[]{.969f, .11f, 1}, // pink
                new float[]{1, .02f, .082f}, // red
                new float[]{.816f, 0, 1}, // violet
                new float[]{0, 1, .098f}, // green
        };

        ParticleEffect fireworksEffect = new ParticleEffect();
        fireworksEffect.load(Gdx.files.internal("particles/fireworks.p"), textureAtlas);

        // if particle effect includes additive or pre-multiplied particle emitters
        // you can turn off blend function clean-up to save a lot of draw calls
        // but remember to switch the Batch back to GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
        // before drawing "regular" sprites or your Stage.
        fireworksEffect.setEmittersCleanUpBlendFunction(false);

        fireworksEffectPool = new ParticleEffectPool(fireworksEffect, 1, 5);
        for (int i = 0; i < fireworkEffects.length; i++) {
            ParticleEffectPool.PooledEffect effect = fireworksEffectPool.obtain();
            resetFireworksEffect(effect);
            fireworkEffects[i] = effect;
        }
    }

    /**
     * if particle effect includes additive or pre-multiplied particle emitters
     * you can turn off blend function clean-up to save a lot of draw calls
     * but remember to switch the Batch back to GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
     * before drawing "regular" sprites or your Stage.
     *
     * @param batch
     */
    private void resetBlendFunction(SpriteBatch batch) {
        batch.setBlendFunction(-1, -1);
        Gdx.gl20.glBlendFuncSeparate(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, GL20.GL_DST_ALPHA);
    }

    /**
     * Renders the fireworks effects on the given batch and resets each effect if it has reached its end.
     *
     * @param batch
     * @param delta
     */
    public void renderFireworks(SpriteBatch batch, float delta) {
        for (ParticleEffectPool.PooledEffect effect : fireworkEffects) {
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                resetFireworksEffect(effect);
            }
        }
        resetBlendFunction(batch);
    }

    /**
     * Resets the position, start color and duration of all firework effects to random values.
     */
    public void resetFireworksEffects() {
        for (ParticleEffectPool.PooledEffect effect : fireworkEffects) {
            resetFireworksEffect(effect);
        }
    }

    /**
     * Resets the position, start color and duration of the given firework effects to random values.
     *
     * @param effect
     */
    private void resetFireworksEffect(ParticleEffect effect) {
        effect.reset();
        effect.setDuration(Utils.randomWithin(180, 250));
        effect.setPosition(Utils.randomWithin(0, WINDOW_WIDTH), Utils.randomWithin(0, WINDOW_HEIGHT));
        float[] colors = effect.getEmitters().get(0).getTint().getColors();
        int randomStartColor = Utils.randomWithin(0, startColors.length - 1);
        for (int i = 0; i < 6; i++) {
            colors[i] = startColors[randomStartColor][i % 3];
        }
        for (ParticleEmitter emitter : effect.getEmitters()) {
            emitter.getTint().setColors(colors);
        }
    }

    /**
     * Frees all allocated resources.
     */
    public void dispose() {
        for (ParticleEffectPool.PooledEffect effect : fireworkEffects) {
            effect.free();
            effect.dispose();
        }
        fireworksEffectPool.clear();
    }

}

