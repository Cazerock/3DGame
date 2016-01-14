package stateEngine.states;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import renderEngine.Loader;
import stateEngine.IState;
import stateEngine.StateEngine;
import fontMeshCreator.FontType;

public class PauseState implements IState {
	
	StateEngine engine;
	FontType pauseFont;
	
	public boolean justStarted = true;
	
	public PauseState(StateEngine engine, Loader loader) {
		this.engine = engine;
		init();
	}
	
	@Override
	public void init() {
		StateEngine.stateMap.put(getID(), this);
		System.out.println(getID() + " state has been initialized");
	}

	@Override
	public void postProcess() {
	}

	@Override
	public void update() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			System.exit(-1);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_G)) {
			justStarted = true;
			engine.changeState(StateEngine.GameState);
		}
	}

	@Override
	public void render() {
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.02f );
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

	}

	@Override
	public int getID() {
		return StateEngine.PauseState;
	}
	
	
	
}
