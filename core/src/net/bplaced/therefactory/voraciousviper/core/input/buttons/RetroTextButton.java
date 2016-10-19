package net.bplaced.therefactory.voraciousviper.core.input.buttons;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Christian on 21.09.2016.
 */
public class RetroTextButton extends AbstractRetroButton {

    private final BitmapFont font;
    final GlyphLayout layout;
	private TextAlign textAlign;
	private boolean autoSize;
	private String text;
	public enum TextAlign {
		CenterMiddle,
		CenterBottom
	}
	
    public RetroTextButton(ShapeRenderer shapeRenderer, int width, int height, BitmapFont font, String text, ClickListener clickListener) {
		super(shapeRenderer, width, height);
		this.font = font;
        layout = new GlyphLayout(font, text);
		this.text = text;
        addListener(clickListener);
        addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            	super.touchUp(event, x, y, pointer, button);
            	setChecked(false);
            }
        });
	}

	@Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        autoSize();
        font.setColor(Color.DARK_GRAY);
        font.draw(batch,
        		getText(),
        		getX()+getWidth()/2-layout.width/2,
        		getY() + (textAlign == TextAlign.CenterBottom ? 30 :  getHeight()/2 + layout.height/2) - (isPressed() ? 2:0));
    }

	String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
		autoSize();
	}

	public void setAlignment(TextAlign textAlignment) {
		this.textAlign = textAlignment;
	}

	public void setThickness(int thickness) {
		this.thickness = thickness;
	}

	public void setAutoSize(boolean autoSize) {
		this.autoSize = autoSize;
		autoSize();
	}

	public void autoSize() {
    	layout.setText(font, getText());
        if (autoSize) {
            setWidth(layout.width + 30);
        }
	}

}
