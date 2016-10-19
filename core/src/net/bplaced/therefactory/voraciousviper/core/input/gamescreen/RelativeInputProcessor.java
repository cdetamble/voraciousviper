package net.bplaced.therefactory.voraciousviper.core.input.gamescreen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;

import net.bplaced.therefactory.voraciousviper.core.constants.Config;
import net.bplaced.therefactory.voraciousviper.core.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.model.Viper;
import net.bplaced.therefactory.voraciousviper.core.model.Viper.MovementDirection;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen;

public class RelativeInputProcessor extends AbstractGameScreenInputProcessor implements IRenderableInputProcessor {

	private final Viper viper;
	private Vector2 vector2;

	public RelativeInputProcessor(GameScreen gameScreen, FitViewport viewport, TextureAtlas textureAtlas,
			ShapeRenderer shapeRenderer, BitmapFont font) {
		super(gameScreen, viewport, textureAtlas, shapeRenderer);
		this.viper = gameScreen.getLevel().getViper();
		vector2 = new Vector2();
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		if (numTouches > 1) return true;
		vector2.set(screenX, screenY);
		vector2 = viewport.unproject(vector2);
		if (viper.getMovementDirection().equals(MovementDirection.Horizontal)) {
			if (vector2.y < viper.getHead().getPosition().y * Config.TILE_HEIGHT)
				viper.moveDown();
			else
				viper.moveUp();
		} else {
			if (vector2.x < viper.getHead().getPosition().x * Config.TILE_WIDTH)
				viper.moveLeft();
			else 
				viper.moveRight();
		}
		viper.flipMovementDirection();
		return true;
	}

	@Override
	public void update() {
		// do nothing
	}

	@Override
	public void dispose() {
		// do nothing
	}

}
