package net.bplaced.therefactory.voraciousviper.core.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.bplaced.therefactory.voraciousviper.core.constants.Config;

class TileFlipable extends Tile {

	private boolean flipX;
	private boolean flipY;

	TileFlipable(Sprite sprite) {
		super(sprite);
		setType(TileType.Blocking);
	}
	
	@Override
	public void render(SpriteBatch batch, int x, int y) {
    	if (flipX) {
    		batch.draw(getSprite(),
    				getPosition().x * Config.TILE_WIDTH + getSprite().getWidth(),
    				getPosition().y * Config.TILE_HEIGHT, -getSprite().getWidth(),
    				getSprite().getHeight());
    	}
    	else if (flipY) {
        	batch.draw(getSprite(),
        			getPosition().x * Config.TILE_WIDTH,
        			getPosition().y * Config.TILE_HEIGHT + getSprite().getHeight(),
        			getSprite().getWidth(), -getSprite().getHeight());
    	}
    	else if (getSprite() != null) {
    		batch.draw(getSprite(), getPosition().x * Config.TILE_WIDTH, getPosition().y * Config.TILE_HEIGHT);
    	}
	}

	void setFlipX(boolean flipX) {
		this.flipX = flipX;
	}

	void setFlipY(boolean flipY) {
		this.flipY = flipY;
	}

}
