package net.bplaced.therefactory.voraciousviper.core.input.gamescreen;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.bplaced.therefactory.voraciousviper.core.input.IRenderableInputProcessor;
import net.bplaced.therefactory.voraciousviper.core.model.Viper;
import net.bplaced.therefactory.voraciousviper.core.screens.GameScreen;

public class SwipeInputProcessor extends AbstractGameScreenInputProcessor implements IRenderableInputProcessor {

	private final Viper viper;
	private final Vector2 vector2TouchStart;
	private final Vector2 vector2TouchEnd;

	public SwipeInputProcessor(GameScreen gameScreen, Viewport viewport, TextureAtlas textureAtlas,
			ShapeRenderer shapeRenderer, BitmapFont font) {
		super(gameScreen, viewport, textureAtlas, shapeRenderer);
		this.viper = gameScreen.getViper();
		vector2TouchStart = new Vector2();
		vector2TouchEnd = new Vector2();
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);
		Vector2 vector2NewCoordinates = viewport.unproject(new Vector2(screenX, screenY));
		vector2TouchEnd.set(vector2NewCoordinates);
		moveViper();
		float tolerance = 10;
		if (Math.abs(vector2NewCoordinates.x - vector2TouchStart.x) > tolerance
				&& Math.abs(vector2NewCoordinates.y - vector2TouchStart.y) > tolerance) {
			vector2TouchStart.set(vector2TouchEnd);
		}
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		super.touchDown(screenX, screenY, pointer, button);
		vector2TouchStart.set(viewport.unproject(new Vector2(screenX, screenY)));
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
		vector2TouchEnd.set(viewport.unproject(new Vector2(screenX, screenY)));
		moveViper();
		return true;
	}
	
	private void moveViper() {
		float deltaX = vector2TouchStart.x - vector2TouchEnd.x;
		float deltaY = vector2TouchStart.y - vector2TouchEnd.y;
		if (Math.abs(deltaX) > Math.abs(deltaY)) {
			if (deltaX < 0) {
				viper.moveRight();
			} else {
				viper.moveLeft();
			}
		} else {
			if (deltaY < 0) {
				viper.moveUp();
			} else {
				viper.moveDown();
			}
		}
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
