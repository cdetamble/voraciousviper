package net.bplaced.therefactory.voraciousviper.core.model;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Created by Christian on 17.09.2016.
 */
class TileAnimated extends Tile {

    private Sprite spriteAnimated;
    private final Sprite spriteDefault;
    
	TileAnimated(Sprite sprite, Sprite spriteAnimated, TileType tileType) {
        super(sprite, tileType);
        this.setSpriteAnimated(spriteAnimated);
        this.spriteDefault = new Sprite(sprite);
    }

	private void setSpriteAnimated(Sprite spriteAnimated) {
		this.spriteAnimated = spriteAnimated;
	}

	void animate(boolean animationTick) {
		if (animationTick) {
			sprite = spriteAnimated;
		} else {
			sprite = spriteDefault;
		}
	}
    
}
