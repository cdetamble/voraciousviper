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

package net.bplaced.therefactory.voraciousviper.input.buttons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.I18NBundle;

import net.bplaced.therefactory.voraciousviper.VoraciousViper;

public abstract class AbstractRetroButton extends Button {

    private final ShapeRenderer shapeRenderer;
    int thickness = 3;
    float alpha = 1;
	private final Color colorGray = new Color(127f/255f, 127f/255f, 127f/255f, alpha);
	private final Color colorWhite = new Color(.9f, .9f, .9f, alpha);
	private final Color colorLightGray = new Color(191f/255f, 184f/255f, 191f/255f, alpha);

    AbstractRetroButton(ShapeRenderer shapeRenderer, final int width, float height) {
        super(new ButtonStyle());
        this.shapeRenderer = shapeRenderer;
        I18NBundle bundle = VoraciousViper.getInstance().getBundle();
        setSize(width, height);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                VoraciousViper.getInstance().playSound("audio/sounds/mouse_click.ogg");
            }
        });
    }
    
    @Override
    public void draw(Batch batch, float parentAlpha) {
    	super.draw(batch, parentAlpha);
    	batch.end();
        
    	Gdx.gl.glEnable(GL20.GL_BLEND);
    	Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    	
    	shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    	
    	// update the alpha channel in the colors
    	colorGray.a = alpha;
    	colorLightGray.a = alpha;
    	colorWhite.a = alpha;
    	
    	// win95 style
        for (int i = 0; i < thickness; i++) {
	    	shapeRenderer.setColor(!isPressed() && !isChecked() ? colorWhite : colorGray);
	        shapeRenderer.rect(getX()+i, getY()+i, getWidth()-i*2, getHeight()-i);
        
	        shapeRenderer.setColor(colorGray);
	        shapeRenderer.rect(getX()+1+i, getY()+i, getWidth()-1-i*2, getHeight()-1-i*2);
	        
	    	shapeRenderer.setColor(!isPressed() && !isChecked() ? colorLightGray : colorGray);
	        shapeRenderer.rect(getX()+thickness, getY()+thickness, getWidth()-thickness*2, getHeight()-thickness*2);
        }
    	
        // therefactory style
//    	thickness = 1;
//        shapeRenderer.setColor(.5f, .5f, .5f, .5f);
//        
//        shapeRenderer.rect(getX(), getY()+thickness, getWidth(), getHeight()-thickness*2);
//        shapeRenderer.rect(getX()+thickness, getY(), getWidth()-thickness*2, getHeight());
//        
//        shapeRenderer.setColor(Color.GRAY);
//        shapeRenderer.rect(getX()+thickness*2, getY(), getWidth()-thickness*4, getHeight());
//        shapeRenderer.rect(getX(), getY()+thickness*2, getWidth(), getHeight()-thickness*4);
//        shapeRenderer.rect(getX()+thickness, getY()+thickness, getWidth()-thickness*2, getHeight()-thickness*2);
//        
//        shapeRenderer.setColor(Color.LIGHT_GRAY);
//        shapeRenderer.rect(getX()+thickness*2, getY()+thickness, getWidth()-thickness*4, thickness*2);
//        shapeRenderer.rect(getX()+thickness, getY()+thickness*2, getWidth()-thickness*2, thickness*2);
//        
//		shapeRenderer.setColor(isChecked() || isPressed() ? Color.LIGHT_GRAY : Color.Green);
//        shapeRenderer.rect(getX()+thickness, getY()+thickness*4, getWidth()-thickness*2, getHeight()-thickness*6);
//        shapeRenderer.rect(getX()+thickness*2, getY()+thickness*3, getWidth()-thickness*4, getHeight()-thickness*4);
        
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        
        batch.begin();
    }

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
}
