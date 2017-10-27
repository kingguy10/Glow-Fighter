package com.slurpy.glowfighter.entities;

import static com.badlogic.gdx.math.MathUtils.atan2;
import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.slurpy.glowfighter.Core;
import com.slurpy.glowfighter.entities.traits.Health;
import com.slurpy.glowfighter.entities.traits.Knockback;
import com.slurpy.glowfighter.guns.BurstGun;
import com.slurpy.glowfighter.guns.Gun;
import com.slurpy.glowfighter.guns.PeaShooter;
import com.slurpy.glowfighter.guns.RocketRepeater;
import com.slurpy.glowfighter.guns.Shotgun;
import com.slurpy.glowfighter.managers.AssetManager.SoundAsset;
import com.slurpy.glowfighter.parts.LinePart;
import com.slurpy.glowfighter.parts.Part;
import com.slurpy.glowfighter.parts.PolygonPart;
import com.slurpy.glowfighter.parts.TrailPart;
import com.slurpy.glowfighter.utils.Action;

public class Player extends Entity implements Health, Knockback{
	
	private static final float speed = 200;
	private static final float boost = 40;
	//private static final float maxSpeed = 40f;
	//private static final float slowMaxSpeed = maxSpeed * 0.2f;
	private static final float maxHealth = 100;
	
	private int gunType = 0;
	private Gun defaultGun = new PeaShooter(this);
	
	private float health = maxHealth;
	private boolean dead = false;
	
	public Player(Vector2 pos, float rot) {
		super(getEntityDef(pos, rot));
		body.setLinearDamping(5f);
		
		Core.bindings.subscribe(Action.nextWeapon, () -> {
			gunType++;
			if(gunType < 0)gunType = 3;
			if(gunType > 3)gunType = 0;
			if(gunType == 0){
				defaultGun = new PeaShooter(this);
			}else if(gunType == 1){
				defaultGun = new Shotgun(this);
			}else if(gunType == 2){
				defaultGun = new BurstGun(this);
			}else{
				defaultGun = new RocketRepeater(this);
			}
		});
		
		Core.bindings.subscribe(Action.lastWeapon, () -> {
			gunType--;
			if(gunType < 0)gunType = 3;
			if(gunType > 3)gunType = 0;
			if(gunType == 0){
				defaultGun = new PeaShooter(this);
			}else if(gunType == 1){
				defaultGun = new Shotgun(this);
			}else if(gunType == 2){
				defaultGun = new BurstGun(this);
			}else{
				defaultGun = new RocketRepeater(this);
			}
		});
		
		Core.bindings.subscribe(Action.boost, () -> {
			Vector2 move = new Vector2();
			if(Core.bindings.isActionPressed(Action.moveUp)){
				move.add(0, 1);
			}
			if(Core.bindings.isActionPressed(Action.moveDown)){
				move.add(0, -1);
			}
			if(Core.bindings.isActionPressed(Action.moveLeft)){
				move.add(-1, 0);
			}
			if(Core.bindings.isActionPressed(Action.moveRight)){
				move.add(1, 0);
			}
			
			body.applyLinearImpulse(move.nor().scl(boost), body.getPosition(), true);
		});
	}

	@Override
	public void update() {
		if(dead){
			colors[0].set(Color.DARK_GRAY);
			return;
		}
		body.setTransform(body.getPosition(), atan2(-(Gdx.input.getY() - Gdx.graphics.getHeight()/2), Gdx.input.getX() - Gdx.graphics.getWidth()/2));
		
		Vector2 move = new Vector2();
		if(Core.bindings.isActionPressed(Action.moveUp)){
			move.add(0, speed);
		}
		if(Core.bindings.isActionPressed(Action.moveDown)){
			move.add(0, -speed);
		}
		if(Core.bindings.isActionPressed(Action.moveLeft)){
			move.add(-speed, 0);
		}
		if(Core.bindings.isActionPressed(Action.moveRight)){
			move.add(speed, 0);
		}
		move.scl(Gdx.graphics.getDeltaTime() * 30);
		
		body.applyForceToCenter(move, true);
		
		/*Vector2 vel = body.getLinearVelocity();
		if(Core.bindings.isActionPressed(Action.moveSlow)){
			if(vel.len2() > slowMaxSpeed * slowMaxSpeed)vel.setLength2(slowMaxSpeed * slowMaxSpeed);
		}else{
			if(vel.len2() > maxSpeed * maxSpeed)vel.setLength2(maxSpeed * maxSpeed);
		}
		body.setLinearVelocity(vel);*/
		
		float angle = body.getAngle();
		Vector2 pos = body.getPosition().cpy().add(cos(angle) * size * 2, sin(angle) * size * 2);
		defaultGun.update(Core.bindings.isActionPressed(Action.primary), pos, angle);
		
		health += 5f * Gdx.graphics.getDeltaTime();
		if(health > maxHealth)health = maxHealth;
		float color = health / maxHealth;
		colors[0].set(1, color, color, 1);
	}
	
	@Override
	public void hit(Entity other){
		Core.graphics.shake(0.3f);
	}
	
	@Override
	public void takeDamage(float dmg) {
		health -= dmg;
		if(health < 0){
			if(!dead)Core.audio.playSound(SoundAsset.PlayerDie);
			dead = true;
		}else{
			Core.audio.playSound(SoundAsset.Hit);
		}
	}
	
	@Override
	public float getKnockback() {
		return 40;
	}
	
	private static EntityDef entityDef = new EntityDef();
	
	private static EntityDef getEntityDef(Vector2 pos, float rot){
		entityDef.pos.set(pos);
		entityDef.rot = rot;
		entityDef.parts = new Part[]{
				new TrailPart(new PolygonPart(polygon, 0.1f), 1f, 0.65f),
				new LinePart(new Vector2(0, 0), new Vector2(-0.3f, 0), 0.1f)
		};
		return entityDef;
	}
	
	private static float size = 0.4f;
	private static Vector2[] polygon = new Vector2[]{new Vector2(size, 0), new Vector2(-size, -size), new Vector2(-size, size)};
	
	static{
		entityDef.polygon = polygon;
		entityDef.parts = new Part[2];
		entityDef.category = Category.ENTITY;
		entityDef.team = Team.FRIENDLY;
		entityDef.bullet = true;
		entityDef.setColor(Color.WHITE.cpy());
		entityDef.bodyType = BodyType.DynamicBody;
	}
}
