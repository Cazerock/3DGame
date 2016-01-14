package buttons;

import org.lwjgl.util.vector.Vector2f;

import renderEngine.Loader;




public class Button extends AbstractButton {

	public Button(Loader loader, String texture, Vector2f position,
			Vector2f scale) {
		super(loader, texture, position, scale);
		
	}

	@Override
	public void onClick(IButton button) {
		
	}

	@Override
	public void onStartHover(IButton button) {
		playerHoverAnimation(0.09f);
	}

	@Override
	public void onStopHover(IButton button) {
		resetScale();
	}

	@Override
	public void whileHovering(IButton button) {
		
	}

}
