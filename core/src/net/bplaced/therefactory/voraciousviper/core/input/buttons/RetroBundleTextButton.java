package net.bplaced.therefactory.voraciousviper.core.input.buttons;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.bplaced.therefactory.voraciousviper.core.VoraciousViper;

public class RetroBundleTextButton extends RetroTextButton {

	private final String bundleKey;

	public RetroBundleTextButton(ShapeRenderer shapeRenderer, int width, int height, BitmapFont font, String bundleKey,
			ClickListener clickListener) {
		super(shapeRenderer, width, height, font, bundleKey, clickListener);
		this.bundleKey = bundleKey;
    	layout.setText(font, getText());
	}

	@Override
	protected String getText() {
		return VoraciousViper.getInstance().getBundle().get(bundleKey);
	}

}
