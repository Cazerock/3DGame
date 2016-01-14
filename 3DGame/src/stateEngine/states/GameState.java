package stateEngine.states;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import models.RawModel;
import models.TexturedModel;
import objConverter.OBJFileLoader;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import particles.ParticleMaster;
import particles.ParticleSystem;
import particles.ParticleTexture;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import stateEngine.IState;
import stateEngine.StateEngine;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;
import water.WaterFrameBuffers;
import water.WaterRenderer;
import water.WaterShader;
import water.WaterTile;
import buttons.AbstractButton;
import buttons.IButton;
import entities.Camera;
import entities.Entity;
import entities.Light;
import entities.Player;
import fontMeshCreator.FontType;
import fontMeshCreator.GuiText;
import fontRendering.TextMaster;
import guis.GuiRenderer;
import guis.GuiTexture;

public class GameState implements IState{
	
	StringBuilder textBuilder;
	
	Loader loader;
	MasterRenderer renderer;
	
	//Water Rendering
	WaterFrameBuffers buffers = new WaterFrameBuffers();
	WaterShader waterShader = new WaterShader();
	WaterRenderer waterRenderer;

	//Lists
	List<Terrain> terrains = new ArrayList<Terrain>();
	List<Entity> entities = new ArrayList<Entity>();
	List<Entity> normalMapEntities = new ArrayList<Entity>();
	List<WaterTile> waters = new ArrayList<WaterTile>();
	List<Light> lights = new ArrayList<Light>();
	List<GuiTexture> guiTextures = new ArrayList<GuiTexture>();
	
	WaterTile water;
	Light sun;

	Player player;
	Camera camera;
	StateEngine engine;
	Terrain terrain;
	
	//Particle Information
	ParticleTexture fireTexture;
	ParticleSystem fireSystem;
	Random random;
	
	GuiText xCoordDisplay;
	GuiText zCoordDisplay;
	GuiText yCoordDisplay;
	
	FontType invFont; 
	AbstractButton pathButton;
	GuiRenderer guiRenderer;

	
	public GameState(Loader loader, MasterRenderer renderer, StateEngine engine) {
		this.loader = loader;
		this.renderer = renderer;
		this.engine = engine;
		textBuilder = new StringBuilder("Hello world!");
		waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), buffers);
		random = new Random();
		init();
	}
	
	public void init() {
		StateEngine.stateMap.put(getID(), this);

		guiRenderer = new GuiRenderer(loader);
		
		pathButton = new AbstractButton(loader, "path", new Vector2f(0, 0), new Vector2f(.2f, .2f)) {
			

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

		};
		pathButton.show(guiTextures);
		
		invFont = new FontType(loader.loadTexture("harry"), new File("res/harry.fnt"));
		FontType rpgFont = new FontType(loader.loadTexture("crazyFont"), new File("res/crazyFont.fnt"));
			new GuiText("ROMANAE DIES", 3f, invFont,new Vector2f(0f,0), 1, true, StateEngine.GameState).setColour(0, 1, 0);
			
			new GuiText("Pause Menu", 4f, invFont, new Vector2f(0, 0), 1f, true, StateEngine.PauseState).setColour(1, 0, 0);
			new GuiText("Hit  \"ESC\"  to exit", 1.75f, invFont, new Vector2f(0, .4f), 1f, true, StateEngine.PauseState).setColour(1, 1, 1);
			new GuiText("Hit  \"G\"  to go to game", 1.75f, invFont, new Vector2f(0, .45f), 1f, true, StateEngine.PauseState).setColour(1, 1, 1);
			
			xCoordDisplay = new GuiText("0", 1.20f, rpgFont, new Vector2f(0f,0f), 1f, false, StateEngine.GameState);
			xCoordDisplay.setColour(0.6f, .2f, 0);
			zCoordDisplay = new GuiText("0", 1.20f, rpgFont, new Vector2f(0f,.05f), 1f, false, StateEngine.GameState);
			zCoordDisplay.setColour(0.6f, .2f, 0);
			yCoordDisplay = new GuiText("0", 1.20f, rpgFont, new Vector2f(0f,.10f), 1f, false, StateEngine.GameState);
			yCoordDisplay.setColour(0.6f, .2f, 0);
			
		
		ParticleMaster.init(loader, renderer.getProjectionMatrix());
		fireTexture = new ParticleTexture(loader.loadTexture("cosmic"), 4);
			fireTexture.setAdditive(true);
			fireSystem = new ParticleSystem(fireTexture, 1000, 5, .04f, 5, 2);
				fireSystem.randomizeRotation();
				fireSystem.setDirection(new Vector3f(1, 1, 10),  1);
				fireSystem.setLifeError(1);
				fireSystem.setScaleError(2);
				fireSystem.setSpeedError(2);
		
		TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("grassy2"));
			TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("mud"));
			TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("grassFlowers"));
			TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("path"));
			TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture,
				gTexture, bTexture);
			TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));
			terrain = new Terrain(0, -1, loader, texturePack, blendMap, "heightmap");
		
		RawModel bunnyModel = OBJLoader.loadObjModel("person", loader);
			TexturedModel stanfordBunny = new TexturedModel(bunnyModel, new ModelTexture(
				loader.loadTexture("playerTexture")));
		
		player = new Player(stanfordBunny, new Vector3f(100, 5, -100), 0, 100, 0, 0.6f);
		camera = new Camera(player);
		sun = new Light(new Vector3f(10000, 10000, -10000), new Vector3f(1.3f, 1.3f, 1.3f));
		
		TexturedModel tree = new TexturedModel(OBJFileLoader.loadOBJ("box", loader), new ModelTexture(loader.loadTexture("box")));
		for (int i = 0; i < 2; i++) {
			int x = (100*i);
			int z = (-100*i);
			int y = (int) terrain.getHeightOfTerrain(x, z);
			new Entity(tree, new Vector3f(x, y, z), 0, 0, 0, 1);
		}
		
		water = new WaterTile(100, -100, terrain.getHeightOfTerrain(100, -100));
		terrains.add(terrain);
		lights.add(sun);
		entities.add(player);
		waters.add(water);
		System.out.println(getID() + " state has been initialized");
	}

	@Override
	public void postProcess() {
		
	}

	@Override
	public void update() {
		if (Keyboard.isKeyDown(Keyboard.KEY_V)) {
			GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_LINE );
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_B)) {
			GL11.glPolygonMode( GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
		}
		
		pathButton.update();
		
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			xCoordDisplay.remove();
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_P))
			engine.changeState(StateEngine.PauseState);
			
			xCoordDisplay.replace(" X: " + player.getPosition().x);
			zCoordDisplay.replace(" Z: " + player.getPosition().z);
			yCoordDisplay.replace(" Y: " + player.getPosition().y);
			
		
		fireSystem.generateParticles(new Vector3f(player.getPosition().x+50, player.getPosition().y + 20, player.getPosition().z));
		ParticleMaster.update(camera);
	}

	
	
	@Override
	public void render() {
		player.move(terrain);
		camera.move();
		GL11.glEnable(GL30.GL_CLIP_DISTANCE0);
		
		//render reflection texture
		buffers.bindReflectionFrameBuffer();
		float distance = 2 * (camera.getPosition().y - water.getHeight());
		camera.getPosition().y -= distance;
			camera.invertPitch();
				renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, 1, 0, -water.getHeight()+1));
			camera.getPosition().y += distance;
		camera.invertPitch();
		
		//render refraction texture
		buffers.bindRefractionFrameBuffer();
		renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, water.getHeight()));
		
		//render to screen
		GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
		buffers.unbindCurrentFrameBuffer();	
		renderer.renderScene(entities, normalMapEntities, terrains, lights, camera, new Vector4f(0, -1, 0, 100000));	
		waterRenderer.render(waters, camera, sun);
		ParticleMaster.renderParticles(camera);
		guiRenderer.render(guiTextures);

	}
	
	public void cleanUp() {
		TextMaster.cleanUp();
		buffers.cleanUp();
		waterShader.cleanUp();
		guiRenderer.cleanUp();
		renderer.cleanUp();
		loader.cleanUp();
	}
	
	@Override
	public int getID() {
		return StateEngine.GameState;
	}

}
