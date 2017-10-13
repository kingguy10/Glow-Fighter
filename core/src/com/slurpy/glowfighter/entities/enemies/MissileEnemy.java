package com.slurpy.glowfighter.entities.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.slurpy.glowfighter.Core;
import com.slurpy.glowfighter.entities.Category;
import com.slurpy.glowfighter.entities.Entity;
import com.slurpy.glowfighter.entities.EntityDef;
import com.slurpy.glowfighter.entities.Team;
import com.slurpy.glowfighter.entities.traits.Damage;
import com.slurpy.glowfighter.managers.AssetManager.EffectAsset;
import com.slurpy.glowfighter.parts.Part;
import com.slurpy.glowfighter.parts.PolygonPart;

public class MissileEnemy extends Entity implements Damage{
	
	private static final float SPEED = 10f;

	public MissileEnemy(Vector2 pos, float rot) {
		super(getEntityDef(pos, rot));
		body.setLinearVelocity(SPEED, 0);
	}

	@Override
	public void update() {
		Vector2 playerPos = Core.entities.getGroup("player").iterator().next().getPosition();
		float angle = playerPos.sub(getPosition()).angleRad();
		angle -= body.getAngle();
		if(angle > MathUtils.PI)angle -= MathUtils.PI2;
		float clamp = 2f * Gdx.graphics.getDeltaTime();
		angle = MathUtils.clamp(angle, -clamp, clamp);
		angle += body.getAngle();
		body.setLinearVelocity(MathUtils.cos(angle) * SPEED, MathUtils.sin(angle) * SPEED);
		body.setTransform(body.getPosition(), angle);
		
		//PlayerPos still equals diff
		float dist = playerPos.len2();
		Color color = colors[0];
		color.set(Color.SCARLET).lerp(Color.CYAN, dist / 64);
	}

	@Override
	public void hit(Entity other) {
		Core.graphics.drawParticle(EffectAsset.Explosion, body.getPosition());
		delete();
	}
	
	@Override
	public float getDamage() {
		return 5f;
	}
	
	private static EntityDef entityDef = new EntityDef();
	
	private static EntityDef getEntityDef(Vector2 pos, float rot){
		entityDef.pos.set(pos);
		entityDef.rot = rot;
		entityDef.setColor(Color.CYAN.cpy());
		return entityDef;
	}
	
	private static float size = 0.1f;
	private static Vector2[] polygon = new Vector2[]{new Vector2(size, 0), new Vector2(-size, -size), new Vector2(-size, size)};
	
	static{
		entityDef.polygon = polygon;
		entityDef.parts = new Part[2];
		entityDef.category = Category.ENTITY;
		entityDef.team = Team.ENEMY;
		entityDef.bullet = false;
		entityDef.bodyType = BodyType.DynamicBody;
		entityDef.parts = new Part[]{
				new PolygonPart(polygon, 0.04f)
		};
	}
}
