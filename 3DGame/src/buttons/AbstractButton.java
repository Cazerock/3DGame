package buttons;

import java.util.List;

import guis.GuiTexture;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector2f;

import renderEngine.DisplayManager;
import renderEngine.Loader;

public abstract class AbstractButton implements IButton {
	
	private GuiTexture texture;
	
	private Vector2f originalScale;
	
	private boolean isHidden = false, isHovering = false;
	
	public AbstractButton(Loader loader, String texture, Vector2f position, Vector2f scale) {
		this.texture = new GuiTexture(loader.loadTexture(texture), position, scale);
		originalScale = scale;
	}
	
	public void update() {
		if (!isHidden) {
			Vector2f location = texture.getPosition();
			Vector2f scale = texture.getScale();
			
			Vector2f mouseCoords = DisplayManager.getNormalizedMouseCoord();
			
			if (location.y + scale.y > -mouseCoords.y && location.y - scale.y < -mouseCoords.y 
					&& location.x + scale.x > mouseCoords.x && location.x - scale.x < mouseCoords.x) {
				whileHovering(this);
				if (!isHovering) {
					isHovering = true;
					onStartHover(this);
				}
				
				while (Mouse.next()) {
					if (Mouse.isButtonDown(0)) {
						onClick(this);
					}
				}
			} else {
				if (isHovering)	{
					isHovering = false;
					onStopHover(this);
				}
			}
		}
	}
	
	@Override
	public void show(List<GuiTexture> guiTextureList) {
		if (isHidden) {
			guiTextureList.add(texture);
			isHidden = false;
		}
	}
	
	@Override
	public void hide(List<GuiTexture> guiTextureList) {
		if (!isHidden) {
			guiTextureList.remove(texture);
			isHidden = true;
		}
	}
	
	@Override
	public void resetScale() {
		texture.setScale(originalScale);
	}
	
	@Override
	public void playerHoverAnimation(float scaleFactor) {
		texture.setScale(new Vector2f(originalScale.x + scaleFactor, originalScale.y + scaleFactor));
	}
}
