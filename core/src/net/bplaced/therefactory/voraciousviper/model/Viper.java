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

package net.bplaced.therefactory.voraciousviper.model;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.Array;

import net.bplaced.therefactory.voraciousviper.VoraciousViper;
import net.bplaced.therefactory.voraciousviper.constants.Config;
import net.bplaced.therefactory.voraciousviper.model.Tile.TileType;

import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Down;
import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Left;
import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Right;
import static net.bplaced.therefactory.voraciousviper.input.gamescreen.AbstractGameScreenInputProcessor.CompassDirection.Up;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteColor.Green;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteId.Head_X_1;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteId.Head_X_2;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteId.Head_Y_1;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteId.Head_Y_2;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteId.Tail_X;
import static net.bplaced.therefactory.voraciousviper.model.Viper.SpriteId.Tail_Y;

public class Viper {

	private final Sprite[][] sprites;
	private TileFlipable head;
	private TileFlipable tail;
	private final Array<Tile> body;

	private int numLives = Config.NUM_LIVES_INITIAL;
	private int score;
	private int numSteps;
	private final GridPoint2 movementVector;
	private final Level level;
	private MovementDirection movementDirection;
	private MovementDirection previousMovementDirection; // needed for changing color of head sprite in the tick where viper has crashed
	private int color; // pointer to one element in the SpriteColor enum
	private boolean hasCrashed;
	private boolean hasKey;

	public enum MovementDirection {
		Horizontal, Vertical
	}

	enum SpriteColor {
		Green,  // #00aa00
		Blue,   // #0000ff
		Purple // #aa00ff
	}

	enum SpriteId {
		Head_X_1,
		Head_X_2,
		Head_Y_1,
		Head_Y_2,
		Tail_X,
		Tail_Y,
		Body
	}

	// temporary variables
	private GridPoint2 nextHeadPosition;
	private final GridPoint2 currentBodyPosition;
	private final GridPoint2 nextBodyPosition;

	public Viper(Level level, TextureAtlas textureAtlas) {
		this.level = level;
		nextHeadPosition = new GridPoint2();
		currentBodyPosition = new GridPoint2();
		nextBodyPosition = new GridPoint2();
		movementVector = new GridPoint2(1, 0);
		movementDirection = MovementDirection.Horizontal;
		color = Green.ordinal();

		sprites = new Sprite[][] {
			new Sprite[] {
					textureAtlas.createSprite("Head.X.1.Green"),
					textureAtlas.createSprite("Head.X.2.Green"),
					textureAtlas.createSprite("Head.Y.1.Green"),
					textureAtlas.createSprite("Head.Y.2.Green"),
					textureAtlas.createSprite("Tail.X.Green"),
					textureAtlas.createSprite("Tail.Y.Green"),
					textureAtlas.createSprite("Body.Green")
			},
			new Sprite[] {
					textureAtlas.createSprite("Head.X.1.Blue"),
					textureAtlas.createSprite("Head.X.2.Blue"),
					textureAtlas.createSprite("Head.Y.1.Blue"),
					textureAtlas.createSprite("Head.Y.2.Blue"),
					textureAtlas.createSprite("Tail.X.Blue"),
					textureAtlas.createSprite("Tail.Y.Blue"),
					textureAtlas.createSprite("Body.Blue")
			},
			new Sprite[] {
					textureAtlas.createSprite("Head.X.1.Purple"),
					textureAtlas.createSprite("Head.X.2.Purple"),
					textureAtlas.createSprite("Head.Y.1.Purple"),
					textureAtlas.createSprite("Head.Y.2.Purple"),
					textureAtlas.createSprite("Tail.X.Purple"),
					textureAtlas.createSprite("Tail.Y.Purple"),
					textureAtlas.createSprite("Body.Purple")
			}
		};

		head = new TileFlipable(sprites[Green.ordinal()][Head_X_1.ordinal()]);
		tail = new TileFlipable(sprites[Green.ordinal()][Tail_X.ordinal()]);
		body = new Array<Tile>();
	}

	public void restart(boolean resetKey) {
		head = new TileFlipable(sprites[Green.ordinal()][Head_X_1.ordinal()]);
		tail = new TileFlipable(sprites[Green.ordinal()][Tail_X.ordinal()]);

		head.setPosition(3, 9);
		level.setTile(head, head.getPosition());

		body.clear();
		body.add(new Tile(sprites[Green.ordinal()][SpriteId.Body.ordinal()], TileType.Blocking));
		body.first().setPosition(head.getPosition().x - 1, head.getPosition().y);
		level.setTile(body.first(), body.first().getPosition());

		tail.setPosition(head.getPosition().x - 2, head.getPosition().y);
		level.setTile(tail, tail.getPosition());

		movementVector.set(1, 0);
		nextHeadPosition = getNextPositionOfHead();
		movementDirection = MovementDirection.Horizontal;
		color = Green.ordinal();

		if (resetKey)
			setHasKey(false);
	}

	void reset() {
		setHasCrashed(false);
		score = 0;
		numSteps = 0;
		numLives = Config.NUM_LIVES_INITIAL;
	}

	public void update() {
		// do nothing
	}

	void tick(boolean moveViper, boolean animationTick) {
		if (moveViper) {
			nextHeadPosition = getNextPositionOfHead();
			movementDirection = (nextHeadPosition.x == head.getPosition().x ? MovementDirection.Vertical : MovementDirection.Horizontal);
			if (level.getTileAt(nextHeadPosition) != null) {
				switch (level.getTileAt(nextHeadPosition).getType()) {
				
				case Key:
				case Consumable:
					if (level.getTileAt(nextHeadPosition).getType().equals(TileType.Key)) {
						setHasKey(true);
					}
					incrementScoreBy(level.getIndexCurrentLevel() + 1);
					level.incrementNumConsumedTiles();
					if (level.getTileAt(nextHeadPosition).getType().equals(TileType.Key))
						VoraciousViper.getInstance().playSound("audio/sounds/win.ogg");
					else
						VoraciousViper.getInstance().playSound("audio/sounds/collect.ogg");
					grow();
					tickMove(true);
					break;
					
				case Blocking:
					setColor(Viper.SpriteColor.Purple);
					setHasCrashed(true);
					break;
					
				case Door:
					if (hasKey()) {
						setHasKey(false);
						VoraciousViper.getInstance().playSound("audio/sounds/win.ogg");
						incrementScoreBy(level.getIndexCurrentLevel());
						grow();
						tickMove(true);
					} else {
						setColor(Viper.SpriteColor.Purple);
						setHasCrashed(true);
					}
					break;
					
				case Floor:
					break;
				default:
					break;
				}
			} else {
				tickMove(false);
			}
		}
		tickAnimate(animationTick);
		previousMovementDirection = movementDirection;
	}

	public boolean hasKey() {
		return hasKey;
	}

	private void setHasKey(boolean hasKey) {
		this.hasKey = hasKey;
	}

	private void tickMove(boolean grown) {
		// update tail position
		level.removeTile(tail.getPosition());
		tail.setPosition(body.peek().getPosition().x, body.peek().getPosition().y);

		// update body positions
		nextBodyPosition.set(head.getPosition().x, head.getPosition().y);
		for (int i = 0; i < body.size; i++) {
			currentBodyPosition.set(body.get(i).getPosition().x, body.get(i).getPosition().y);
			level.removeTile(currentBodyPosition);
			
			// move the body tile to its next position
			body.get(i).setPosition(nextBodyPosition.x, nextBodyPosition.y);
			level.setTile(body.get(i), body.get(i).getPosition());
			
			nextBodyPosition.set(currentBodyPosition.x, currentBodyPosition.y);
		}

		level.setTile(tail, tail.getPosition());

		// update head position
		head.setPosition(nextHeadPosition.x, nextHeadPosition.y);
		level.setTile(head, head.getPosition());

		numSteps++;
	}

	// is called after the movement of tiles
	private void tickAnimate(boolean animationTick) {
		// animate tail sprite
		tail.setSprite(sprites[color][(body.peek().getPosition().y == tail.getPosition().y ? Tail_X : Tail_Y).ordinal()]);
		tail.setFlipX(body.peek().getPosition().x < tail.getPosition().x); // viper moves left -> flip tail sprite horizontally
		tail.setFlipY(body.peek().getPosition().y < tail.getPosition().y); // viper moves down -> flip tail sprite vertically

		// animate body sprites
		for (int i = 0; i < body.size; i++) {
			body.get(i).setSprite(sprites[color][SpriteId.Body.ordinal()]);	
		}

		// animate head sprite
		if (hasCrashed) { // only change color, but do not flip or change x and y
			if (previousMovementDirection.equals(MovementDirection.Horizontal))
				head.setSprite(sprites[color][(animationTick ? Head_X_1 : Head_X_2).ordinal()]);
			else
				head.setSprite(sprites[color][(animationTick ? Head_Y_2 : Head_Y_2).ordinal()]);
		} else {
			head.setFlipX(nextHeadPosition.x < body.first().getPosition().x); // viper moves left -> flip head sprite horizontally
			head.setFlipY(nextHeadPosition.y < body.first().getPosition().y); // viper moves down -> flip head sprite vertically
			if (body.first().getPosition().x != nextHeadPosition.x) { // viper moves horizontally -> set horizontal head sprites
				head.setSprite(sprites[color][(animationTick ? Head_X_2 : Head_X_1).ordinal()]);
			}
			else if (body.first().getPosition().y != nextHeadPosition.y) { // viper moves vertically -> set vertical head sprites
				head.setSprite(sprites[color][(animationTick ? Head_Y_2 : Head_Y_1).ordinal()]);
			}
		}
	}

	public void moveUp() {
		if (movementDirection.equals(MovementDirection.Vertical))
			return;
		movementVector.x = 0;
		movementVector.y = 1;
	}

	public void moveRight() {
		if (movementDirection.equals(MovementDirection.Horizontal))
			return;
		movementVector.x = 1;
		movementVector.y = 0;
	}

	public void moveDown() {
		if (movementDirection.equals(MovementDirection.Vertical))
			return;
		movementVector.x = 0;
		movementVector.y = -1;
	}

	public void moveLeft() {
		if (movementDirection.equals(MovementDirection.Horizontal))
			return;
		movementVector.x = -1;
		movementVector.y = 0;
	}

	private void grow() {
		body.add(new Tile(sprites[color][SpriteId.Head_X_1.ordinal()], TileType.Blocking));
		body.peek().setPosition(tail.getPosition().x, tail.getPosition().y);
	}

	public void setHasCrashed(boolean hasCrashed) {
		this.hasCrashed = hasCrashed;
	}

	boolean hasCrashed() {
		return hasCrashed;
	}

	public Tile getHead() {
		return head;
	}

	public int getScore() {
		return score;
	}

	public int getNumLives() {
		return numLives;
	}

	public int getNumSteps() {
		return numSteps;
	}

    GridPoint2[] getPositions() {
        GridPoint2[] positions = new GridPoint2[body.size + 2];
        positions[0] = head.getPosition();
        positions[1] = tail.getPosition();
        for (int i = 0; i < body.size; i++) {
            positions[i + 2] = body.get(i).getPosition();
        }
        return positions;
    }

	private void incrementScoreBy(int score) {
		this.score += score;
	}

	public void setColor(SpriteColor spriteColor) {
		this.color = spriteColor.ordinal();
	}

	private GridPoint2 getNextPositionOfHead() {
		nextHeadPosition.set(head.getPosition().x + movementVector.x, head.getPosition().y + movementVector.y);
		return nextHeadPosition;
	}

	public void decrementLives() {
		numLives--;
	}

	void incrementLives() {
        numLives = Math.min(Config.NUM_LIVES_INITIAL, numLives + 1);
	}

	public Sprite getHeadX2() {
		return sprites[color][SpriteId.Head_X_2.ordinal()];
	}

	public void flipMovementDirection() {
		movementDirection = (movementDirection.equals(MovementDirection.Horizontal) ? MovementDirection.Vertical : MovementDirection.Horizontal);
	}

	public MovementDirection getMovementDirection() {
		return movementDirection;
	}

	public void move(int direction) {
		if (direction == Right.ordinal()) {
			moveRight();
		} else if (direction == Left.ordinal()) {
			moveLeft();
		} else if (direction == Down.ordinal()) {
			moveDown();
		} else if (direction == Up.ordinal()) {
			moveUp();
		}
	}

}
