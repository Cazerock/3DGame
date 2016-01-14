package stateEngine;

public interface IState {
	public void init();
	public void postProcess();
	public void update();
	public void render();
	public int getID();
	
}
