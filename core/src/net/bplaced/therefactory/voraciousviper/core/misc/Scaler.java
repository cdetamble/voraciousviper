package net.bplaced.therefactory.voraciousviper.core.misc;

public class Scaler {

	
	private float scaler;
	private float step;
	private boolean scaleDown;
	private float min;
	private float max;

	public Scaler(float min, float max, float step, boolean scaleDown) {
		this.scaleDown = scaleDown;
		this.min = min;
		this.max = max;
		this.step = step;
		if (scaleDown) {
			this.scaler = max;
		} else {
			this.scaler = min;
		}
	}
	
	public void update() {
		if (scaleDown) {
			scaler -= step;
			if (scaler <= min) {
				scaler = min;
				scaleDown = false;
			}
		} else {
			scaler += step;
			if (scaler >= max) {
				scaler = max;
				scaleDown = true;
			}
		}
	}
	
	public float get() {
		return scaler;
	}

}
