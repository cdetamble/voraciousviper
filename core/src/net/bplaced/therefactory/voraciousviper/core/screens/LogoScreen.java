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

package net.bplaced.therefactory.voraciousviper.core.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.core.constants.Config;

public class LogoScreen extends ScreenAdapter {

    private final FitViewport viewport;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ShapeRenderer sr;
    private final Music libGdxSound;
    private final Sprite there;
    private final Sprite factory;
    private final Sprite libGdxLogo;
    private int numFrames, deltaXThere, deltaXCursor, deltaXFactory = 64;
    private boolean showCursor = true;
    private float alpha;
	private final AssetManager assetManager;
	
    // flags for states in the animation
    private boolean writeThefactory = true;
    private boolean writeRe = false;
    private final Sound thefactorySound;
    private final Sound goBackSound;
    private final Sound reSound;
    private long timestamp;
    private boolean libGdxLogoFinished;

    public LogoScreen(SpriteBatch batch, ShapeRenderer sr, AssetManager assetManager) {
        this.batch = batch;
        this.sr = sr;
        this.assetManager = assetManager;

        camera = new OrthographicCamera();
        viewport = new FitViewport(512, 288, camera); // use 16:9 viewport to avoid scaling issues
        camera.position.set(Config.WINDOW_WIDTH / 2, Config.WINDOW_HEIGHT / 2, 0);
        camera.update();

        there = new Sprite(new Texture("there.png"));
        factory = new Sprite(new Texture("factory.png"));
        libGdxLogo = new Sprite(new Texture("libgdx.png"));

        assetManager.load("audio/sounds/keyboard.ogg", Sound.class);
        assetManager.load("audio/sounds/keyboard_go_back.ogg", Sound.class);
        assetManager.load("audio/sounds/re.ogg", Sound.class);
        assetManager.load("audio/sounds/libgdx.ogg", Music.class);
        assetManager.finishLoading();

        thefactorySound = assetManager.get("audio/sounds/keyboard.ogg", Sound.class);
        goBackSound = assetManager.get("audio/sounds/keyboard_go_back.ogg", Sound.class);
        reSound = assetManager.get("audio/sounds/re.ogg", Sound.class);

        libGdxSound =  assetManager.get("audio/sounds/libgdx.ogg", Music.class);

        resetAnimation();
    }

    @Override
    public void render(float delta) {
        if (!libGdxLogoFinished) {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updateAndRenderLibGdxLogo();
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            updateAndRenderTheRefactoryLogo();
        }
    }

    private void updateAndRenderLibGdxLogo() {
        updateLibGdxLogo();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(libGdxLogo, Config.WINDOW_WIDTH/2 - libGdxLogo.getWidth()/4,
                Config.WINDOW_HEIGHT/2 - libGdxLogo.getHeight()/4,
                libGdxLogo.getWidth()/2,
                libGdxLogo.getHeight()/2);

        VoraciousViper.getInstance().getFadeSprite().setAlpha(alpha);
        VoraciousViper.getInstance().getFadeSprite().draw(batch);

        batch.end();
    }

    private void updateLibGdxLogo() {

        // go to next screen as soon as user taps the screen
        if (Gdx.input.justTouched()) {
            libGdxSound.stop();
            alpha = 1;
            libGdxLogoFinished = true;
            return;
        }

        // fade in
        if (timestamp == 0 && alpha > 0) {
            alpha = Math.max(0, alpha - Config.FADING_SPEED/4);
        } else {

            // wait a bit
            if (timestamp == 0)
                timestamp = System.currentTimeMillis();
            if (System.currentTimeMillis() - 1200 > timestamp) {
                
                // fade out
                alpha = Math.min(1, alpha + Config.FADING_SPEED/4);

                if (alpha == 1) {
                    libGdxLogoFinished = true;
                }
            }
        }
    }

    private void updateAndRenderTheRefactoryLogo() {
        updateTheRefactory();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        there.setX((Config.WINDOW_WIDTH - there.getWidth() - factory.getWidth()) / 2);
        there.setY(Config.WINDOW_HEIGHT / 2 - there.getHeight() / 2);
        there.draw(batch);

        batch.end();

        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeType.Filled);
        sr.setColor(Color.BLACK);

        // black background of "factory" for overlaying the "there" sprite
        sr.rect(factory.getX(), 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        sr.end();

        batch.begin();
        factory.setX(there.getX() + there.getWidth() + 1 - deltaXFactory);
        factory.setY(there.getY());
        factory.draw(batch);
        
        VoraciousViper.getInstance().getFadeSprite().setAlpha(alpha);
        VoraciousViper.getInstance().getFadeSprite().draw(batch);
        batch.end();

        sr.begin(ShapeType.Filled);

        // hide letters
        sr.rect(there.getX() + deltaXThere, 0, Config.WINDOW_WIDTH, Config.WINDOW_HEIGHT);

        // cursor
        if (showCursor) {
            sr.setColor(Color.WHITE);
            sr.rect(there.getX() + deltaXCursor, Config.WINDOW_HEIGHT / 2 - 50 / 2, 4, 50);
        }
        sr.end();
    }

    private void updateTheRefactory() {

        // go to main menu as soon as user taps the screen
        if (Gdx.input.justTouched()) {
            VoraciousViper.getInstance().showTitleScreen();
        }

        // show logo animation
        numFrames++;
        int typeSpeedForOneLetter = 8;
        if (numFrames < 0) {
            blinkCursor();
        } else {
            if (writeThefactory) {
                if (numFrames == 1) {
                    alpha = 0;
                    showCursor = true;
                    thefactorySound.play();
                }
                if (numFrames == 9 * typeSpeedForOneLetter) {
                    deltaXThere += 41;
                } else if (numFrames % typeSpeedForOneLetter == 0 && numFrames < 9 * typeSpeedForOneLetter) {
                    deltaXThere += 33;
                }
                deltaXCursor = deltaXThere;
                if (numFrames > 10 * typeSpeedForOneLetter) {
                    blinkCursor();
                    if (numFrames > 17 * typeSpeedForOneLetter) {
                        writeThefactory = false;
                        writeRe = true;
                        deltaXCursor -= 41;
                        goBackSound.play();
                        showCursor = true;
                    }
                }
            } else if (writeRe) {
                if (numFrames % typeSpeedForOneLetter == 0 && numFrames < 24 * typeSpeedForOneLetter) {
                    deltaXCursor -= 33;
                } else if (numFrames >= 24 * typeSpeedForOneLetter && numFrames < 26 * typeSpeedForOneLetter) {
                    blinkCursor();
                }

                // insert letter "R"
                else if (numFrames == 26 * typeSpeedForOneLetter) {
                    reSound.play();
                    showCursor = true;
                    deltaXFactory -= 33;
                    deltaXCursor += 33;
                    deltaXThere += 33;
                }

                // insert letter "E"
                else if (numFrames == 30 * typeSpeedForOneLetter) {
                    deltaXFactory -= 33;
                    deltaXCursor += 33;
                    deltaXThere += 33;
                } else if (numFrames > 32 * typeSpeedForOneLetter && numFrames <= 37 * typeSpeedForOneLetter) {
                    //blinkCursor();
                    showCursor = false;
                    
                }

                // fade out
                else if (numFrames > 37 * typeSpeedForOneLetter) {
                    showCursor = false;
                    alpha = Math.min(1, alpha + Config.FADING_SPEED/4);
                }

                if (alpha == 1) {
                    if (Config.DEBUG_MODE) {
                        resetAnimation();
                    } else {
                        VoraciousViper.getInstance().showTitleScreen();
                    }
                }
            }
        }
    }

    private void resetAnimation() {
        timestamp = 0;
        libGdxLogoFinished = false;
        alpha = 1;
        numFrames = -40;
        deltaXThere = 0;
        deltaXCursor = 0;
        deltaXFactory = 64;
        showCursor = true;
        writeThefactory = true;
        writeRe = false;
        libGdxSound.play();
    }

    private void blinkCursor() {
        if ((numFrames % 20 == 0)) {
            showCursor = !showCursor;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        goBackSound.dispose();
        thefactorySound.dispose();
        reSound.dispose();
        libGdxSound.dispose();
        assetManager.unload("audio/sounds/keyboard.ogg");
        assetManager.unload("audio/sounds/keyboard_go_back.ogg");
        assetManager.unload("audio/sounds/re.ogg");
        assetManager.unload("audio/sounds/libgdx.ogg");
    }

    @Override
    public void hide() {
        super.hide();
        goBackSound.stop();
        thefactorySound.stop();
        reSound.stop();
        libGdxSound.stop();
    }

}
