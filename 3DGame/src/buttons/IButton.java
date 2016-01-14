package buttons;

import guis.GuiTexture;

import java.util.List;

public interface IButton {
	
	void onClick(IButton button);
	
	void onStartHover(IButton button);
	
	void onStopHover(IButton button);
	
	void whileHovering(IButton button);
	
	void show(List<GuiTexture> guiTextureList);
	
	void hide(List<GuiTexture> guiTextureList);
	
	void playerHoverAnimation(float scaleFactor);
	
	void resetScale();
	
	void update();
}
