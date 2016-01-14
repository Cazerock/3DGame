package stateEngine;

import java.util.HashMap;
import java.util.Map;

public class StateEngine {

	public static int currentState = 0;
	public int prevState = 0;

	public static Map<Integer, IState> stateMap = new HashMap<Integer, IState>();

	// States
	public static int GameState = 0;
	public static int PauseState = 1;
	public static int SplashScreen = 2;
	public static int GlobalText = 1000000000;
	// Methods
	public void update() {
		IState tempState = stateMap.get(currentState);
		tempState.update();
	}

	public void render() {
		IState tempState = stateMap.get(currentState);
		tempState.render();
	}

	public void changeState(int state) {
		prevState = currentState;
		currentState = state;
	}

}
