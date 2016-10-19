package net.bplaced.therefactory.voraciousviper.core.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.GridPoint2;

import net.bplaced.therefactory.voraciousviper.core.constants.Config;

/**
 * Created by Christian on 17.09.2016.
 */
public class Tile {

	private TileType type;
	Sprite sprite;
	private GridPoint2 position;

	enum TileType {
		Floor, Consumable, Blocking, Door, Key
	}

	Tile() {
		
	}
	
	Tile(Sprite sprite, TileType type) {
		this.sprite = sprite;
		this.type = type;
		position = new GridPoint2();
	}

	Tile(Sprite sprite) {
		this(sprite, TileType.Floor);
	}

	void setType(TileType type) {
		this.type = type;
	}

	TileType getType() {
		return type;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public void setSprite(Sprite sprite) {
		this.sprite = sprite;
	}

	void setPosition(int x, int y) {
		position.set(x, y);
	}

	public GridPoint2 getPosition() {
		return position;
	}

	public void render(SpriteBatch batch, int x, int y) {
		batch.draw(sprite, x * Config.TILE_WIDTH, y * Config.TILE_HEIGHT);
	}

}
