package particles;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import renderEngine.DisplayManager;
import entities.Player;
import entities.Camera;

public class Particle {

	private Vector3f postion;
	private Vector3f velocity;
	private float gravityEffect;
	private float lifeLength;
	private float rotation;
	private float scale;
	
	private ParticleTexture texture;
	
	private Vector2f texOffset1 = new Vector2f();
	private Vector2f texOffset2 = new Vector2f();
	private float blend;
	
	private float elapsedTime = 0;
	private float distanceFromCamera;
	
	
	public Particle(ParticleTexture texture, Vector3f postion, Vector3f velocity, float gravityEffect,
			float life, float rotation, float scale) {
		this.texture = texture;
		this.postion = postion;
		this.velocity = velocity;
		this.gravityEffect = gravityEffect;
		this.lifeLength = life;
		this.rotation = rotation;
		this.scale = scale;
		ParticleMaster.addParticle(this);
	}
	
	public float getDistance() {
		return this.distanceFromCamera;
	}
	
	public float getBlend() {
		return this.blend;
	}
	
	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public ParticleTexture getTexture() {
		return texture;
	}

	protected Vector3f getPosition() {
		return this.postion;
	}
	
	protected float getRotation() {
		return this.rotation;
	}
	
	protected float getScale() {
		return this.scale;
	}
	
	protected boolean update(Camera camera) {
		
		velocity.y += Player.GRAVITY * gravityEffect * DisplayManager.getFrameTimeSeconds();
		Vector3f change = new Vector3f(velocity);
		change.scale(DisplayManager.getFrameTimeSeconds());
		Vector3f.add(change, postion, postion);
		distanceFromCamera = Vector3f.sub(camera.getPosition(), postion, null).lengthSquared();
		updateTextureCoordInfo();
		elapsedTime += DisplayManager.getFrameTimeSeconds();
		return elapsedTime < lifeLength;
	}
	
	private void updateTextureCoordInfo() {
		float lifeFactor = elapsedTime / lifeLength;
		int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
		float atlasProgression = lifeFactor * stageCount;
		int index1 = (int) Math.floor(atlasProgression); 
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		this.blend = atlasProgression % 1;
		setTextureOffset(texOffset1, index1);
		setTextureOffset(texOffset2, index2);
	}
	
	private void setTextureOffset(Vector2f offset, int index) {
		int col = index % texture.getNumberOfRows();
		int row = index / texture.getNumberOfRows();
		offset.x = (float) col / texture.getNumberOfRows();
		offset.y = (float) row / texture.getNumberOfRows();
	}
	
}
