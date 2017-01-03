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

package net.bplaced.therefactory.voraciousviper.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import net.bplaced.therefactory.voraciousviper.constants.Config;
import net.bplaced.therefactory.voraciousviper.misc.Utils;
import net.bplaced.therefactory.voraciousviper.screens.TitleScreen;
import net.bplaced.therefactory.voraciousviper.screens.TitleScreen.TitleScreenState;

public class TitleScreenInputProcessor extends InputAdapter {

	private final TitleScreen titleScreen;
	private float deltaY;
	private float lastDeltaY;
	private float touchStartY;
	private boolean touchDragging;
	private final float topY;
	private final float bottomY;
//	private boolean scrollbarHandleDragging;

	public TitleScreenInputProcessor(TitleScreen titleScreen, float topY, float bottomY) {
		this.titleScreen = titleScreen;
		this.topY = topY;
		this.bottomY = bottomY;
	}
	
    @Override
    public boolean keyDown(int keycode) {
        boolean returnValue = super.keyDown(keycode);
        if (keycode == Keys.BACK) {
            if (!titleScreen.getState().equals(TitleScreenState.ShowMainMenu)) {
            	titleScreen.setState(TitleScreenState.ShowMainMenu);
            } else {
                Gdx.app.exit();
            }
            return true;
        }
        return returnValue;
    }
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		Vector2 touchCoordinates = titleScreen.getViewport().unproject(new Vector2(screenX, screenY));
		touchStartY = touchCoordinates.y;
//		scrollbarHandleDragging = Util.within(touchCoordinates, titleScreen.getScrollbarHandle());
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		super.touchUp(screenX, screenY, pointer, button);
//		lastDeltaY = (scrollbarHandleDragging ? deltaY : -deltaY);
		lastDeltaY = -deltaY;
        touchDragging = false;
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		super.touchDragged(screenX, screenY, pointer);
        touchDragging = true;
		Vector2 touchCoordinates = titleScreen.getViewport().unproject(new Vector2(screenX, screenY));
		if (!Utils.within(touchCoordinates, titleScreen.getCloseButton())) {
//			if (scrollbarHandleDragging) {
//            	float scrollbarPosition = Math.max(33, touchCoordinates.y + titleScreen.getScrollbarHandle().getHeight());
//            	titleScreen.setScrollbarPositionY(scrollbarPosition);
//            	if (scrollbarPosition <= 33) {
//            		return true;
//            	}
//            }
            
			deltaY = (touchStartY - touchCoordinates.y) + lastDeltaY;
            deltaY = Math.max(deltaY,
            		titleScreen.getScoreEntries() == null ? 0 :
        			-Config.LINE_HEIGHT_HIGHSCORES * (titleScreen.getNumScoreEntries())); // stop scrolling if only last line is visible
            
//            if (!scrollbarHandleDragging) {
            	deltaY = -deltaY; // invert vertical scrolling direction
            	titleScreen.setScrollbarPositionY((bottomY - topY) / (Config.LINE_HEIGHT_HIGHSCORES * (titleScreen.getNumScoreEntries())) * deltaY + topY);
//            }

            return true;
		}
		return false;
	}
	
    public void update() {
        if (!touchDragging && deltaY < 0) { // scroll score table back to top if dragged down too far
            deltaY = Math.min(0, deltaY + Math.abs(deltaY) / 5);
            lastDeltaY = deltaY;
        }
    }

	public float getDeltaY() {
		return deltaY;
	}

	public void resetDeltaY() {
		deltaY = 0;
		lastDeltaY = 0;
	}
	
}
