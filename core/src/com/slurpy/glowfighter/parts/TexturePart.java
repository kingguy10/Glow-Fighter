package com.slurpy.glowfighter.parts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.slurpy.glowfighter.Core;

public class TexturePart extends Part {
	
	public final TextureRegion region;
	public final Vector2 pos;
	public final Vector2 size;
	public final Vector2 origin;
	public float rot;
	
	private final Vector2 tempPos = new Vector2();
	
	public TexturePart(TextureRegion region, Vector2 pos, Vector2 size, Vector2 origin, float rot){
		this.region = region;
		this.pos = pos;
		this.size = size;
		this.origin = origin;
		this.rot = rot;
	}

	public TexturePart(TextureRegion region, Vector2 pos, Vector2 size, float rot) {
		this.region = region;
		this.pos = pos;
		this.size = size;
		origin = new Vector2(size).scl(0.5f);
		this.rot = rot;
	}

	@Override
	public void draw(Vector2 pos, float rot, Color color) {
		tempPos.set(this.pos).rotateRad(rot).sub(origin).add(pos);
		Core.graphics.drawTexture(region, tempPos, size, origin, rot + this.rot);
	}

	@Override
	public TexturePart clone() {
		return new TexturePart(new TextureRegion(region), pos.cpy(), size.cpy(), origin.cpy(), rot);
	}
}
