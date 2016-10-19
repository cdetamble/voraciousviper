package net.bplaced.therefactory.voraciousviper.core.input;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Created by Christian on 17.09.2016.
 */
public interface IRenderableInputProcessor extends InputProcessor {

    void update();
	void render(SpriteBatch batch, BitmapFont font);
    void render(ShapeRenderer shapeRenderer);
	void dispose();
	
}
