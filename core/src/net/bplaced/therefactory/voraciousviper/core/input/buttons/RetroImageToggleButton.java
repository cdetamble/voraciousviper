package net.bplaced.therefactory.voraciousviper.core.input.buttons;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class RetroImageToggleButton extends RetroImageButton {

	private final Drawable drawable;
	private final Drawable drawableToggled;
	private boolean toggled;

	public RetroImageToggleButton(ShapeRenderer shapeRenderer, int width, int height, final Drawable drawable,
			final Drawable drawableToggled, String prefKey, boolean prefValue) {
		super(shapeRenderer, width, height, drawable, prefKey);
		this.drawable = drawable;
		this.drawableToggled = drawableToggled;
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				setChecked(!toggled);
			}
		});
	}
	
	@Override
	public void setChecked(boolean isChecked) {
		setDrawable(isChecked ? drawable : drawableToggled);
		toggled = !toggled;
		super.setChecked(false);
	}
	
	public void setToggled(boolean toggled) {
		this.toggled = toggled;
	}

}
