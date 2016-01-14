package engineTester;

import org.lwjgl.opengl.Display;

import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import stateEngine.StateEngine;
import stateEngine.states.GameState;
import stateEngine.states.PauseState;
import fontRendering.TextMaster;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.createDisplay();
		
		Loader loader = new Loader();
		MasterRenderer renderer = new MasterRenderer(loader);
		TextMaster.init(loader);
		
		// **************** State engine to organize each state, holding a map of all states
		StateEngine stateRunner = new StateEngine();
		GameState gameState = new GameState(loader, renderer, stateRunner);
		new PauseState(stateRunner, loader);
		
		//****************Game Loop Below*********************
		while (!Display.isCloseRequested()) {
			stateRunner.update();
			stateRunner.render();
			TextMaster.render();

			DisplayManager.updateDisplay();
		}
		
		//Clean up methods
		gameState.cleanUp();
		DisplayManager.closeDisplay();

	}


}
