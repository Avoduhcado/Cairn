/******************************************************************************
 * Spine Runtimes Software License
 * Version 2.1
 * 
 * Copyright (c) 2013, Esoteric Software
 * All rights reserved.
 * 
 * You are granted a perpetual, non-exclusive, non-sublicensable and
 * non-transferable license to install, execute and perform the Spine Runtimes
 * Software (the "Software") solely for internal use. Without the written
 * permission of Esoteric Software (typically granted by licensing Spine), you
 * may not (a) modify, translate, adapt or otherwise create derivative works,
 * improvements of the Software or develop new applications using the Software
 * or (b) remove, delete, alter or obscure any trademarks or any copyright,
 * trademark, patent or other intellectual property or proprietary rights
 * notices on or in the Software, including any copy thereof. Redistributions
 * in binary or source form must include this license and terms.
 * 
 * THIS SOFTWARE IS PROVIDED BY ESOTERIC SOFTWARE "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL ESOTERIC SOFTARE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *****************************************************************************/

package com.esotericsoftware.spine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.lwjgl.util.vector.Vector4f;

import com.esotericsoftware.spine.Animation.AttachmentTimeline;
import com.esotericsoftware.spine.Animation.ColorTimeline;
import com.esotericsoftware.spine.Animation.CurveTimeline;
import com.esotericsoftware.spine.Animation.DrawOrderTimeline;
import com.esotericsoftware.spine.Animation.EventTimeline;
import com.esotericsoftware.spine.Animation.FfdTimeline;
import com.esotericsoftware.spine.Animation.ScaleTimeline;
import com.esotericsoftware.spine.Animation.FlipXTimeline;
import com.esotericsoftware.spine.Animation.FlipYTimeline;
import com.esotericsoftware.spine.Animation.IkConstraintTimeline;
import com.esotericsoftware.spine.Animation.RotateTimeline;
import com.esotericsoftware.spine.Animation.Timeline;
import com.esotericsoftware.spine.Animation.TranslateTimeline;
import com.esotericsoftware.spine.attachments.Attachment;
import com.esotericsoftware.spine.attachments.AttachmentLoader;
import com.esotericsoftware.spine.attachments.AttachmentType;
import com.esotericsoftware.spine.attachments.Region;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import core.utilities.MathFunctions;

public class SkeletonJson {
	@SuppressWarnings("unused")
	private final AttachmentLoader attachmentLoader;
	private float scale = 1;

	/*public SkeletonJson (TextureAtlas atlas) {
		attachmentLoader = new AtlasAttachmentLoader(atlas);
	}*/

	public SkeletonJson (AttachmentLoader attachmentLoader) {
		this.attachmentLoader = attachmentLoader;
	}

	public float getScale () {
		return scale;
	}

	/** Scales the bones, images, and animations as they are loaded. */
	public void setScale (float scale) {
		this.scale = scale;
	}

	public SkeletonData readSkeletonData (String file) {
		//if (file == null) throw new IllegalArgumentException("file cannot be null.");

		float scale = this.scale;

		SkeletonData skeletonData = new SkeletonData();
		skeletonData.name = file;

		try (BufferedReader br = new BufferedReader(new FileReader(System.getProperty("resources")
				+ "/sprites/" + skeletonData.name + "/" + skeletonData.name + ".json"))) {
			JsonReader reader = new JsonReader(br);
			JsonParser parser = new JsonParser();
			
			JsonObject root = (JsonObject) parser.parse(reader);
	
			// Skeleton.
			JsonObject skeletonMap = root.get("skeleton").getAsJsonObject();
			if (skeletonMap != null) {
				skeletonData.hash = skeletonMap.has("hash") ? skeletonMap.get("hash").getAsString() : null;
				skeletonData.version = skeletonMap.has("spine") ? skeletonMap.get("spine").getAsString() : null;
				skeletonData.width = skeletonMap.has("width") ? skeletonMap.get("width").getAsFloat() : 0;
				skeletonData.height = skeletonMap.has("height") ? skeletonMap.get("height").getAsFloat() : 0;
				skeletonData.imagesPath = skeletonMap.has("images") ? skeletonMap.get("images").getAsString() : null;
			}
	
			// Bones.
			JsonArray boneMap = root.get("bones").getAsJsonArray();
			for (int i = 0; i<boneMap.size(); i++) {
				JsonObject currentBone = boneMap.get(i).getAsJsonObject();
				if(i == 0) {
					skeletonData.centerX = currentBone.has("x") ? currentBone.get("x").getAsFloat() : 0f;
					skeletonData.centerY = currentBone.has("y") ? currentBone.get("y").getAsFloat() : 0f;
				}
				BoneData parent = null;
				String parentName = currentBone.has("parent") ? currentBone.get("parent").getAsString() : null;
				if (parentName != null) {
					parent = skeletonData.findBone(parentName);
					//if (parent == null) throw new SerializationException("Parent bone not found: " + parentName);
				}
				BoneData boneData = new BoneData(currentBone.get("name").getAsString(), parent);
				boneData.length = currentBone.has("length") ? currentBone.get("length").getAsFloat() * scale : 0;
				boneData.x = currentBone.has("x") ? currentBone.get("x").getAsFloat() * scale : 0;
				boneData.y = currentBone.has("y") ? currentBone.get("y").getAsFloat() * scale : 0;
				boneData.rotation = currentBone.has("rotation") ? currentBone.get("rotation").getAsFloat() : 0;
				boneData.scaleX = currentBone.has("scaleX") ? currentBone.get("scaleX").getAsFloat() : 1f;
				boneData.scaleY = currentBone.has("scaleY") ? currentBone.get("scaleY").getAsFloat() : 1f;
				boneData.flipX = currentBone.has("flipX") ? currentBone.get("flipX").getAsBoolean() : false;
				boneData.flipY = currentBone.has("flipY") ? currentBone.get("flipY").getAsBoolean() : false;
				boneData.inheritScale = currentBone.has("inheritScale") ? currentBone.get("inheritScale").getAsBoolean() : true;
				boneData.inheritRotation = currentBone.has("inheritRotation") ? currentBone.get("inheritRotation").getAsBoolean() : true;
	
				String color = currentBone.has("color") ? currentBone.get("color").getAsString() : null;
				if(color != null)
					boneData.getColor().set(MathFunctions.valueOfHex(color));
	
				skeletonData.bones.add(boneData);
			}
	
			// IK constraints.
			/*for (JsonValue ikMap = root.getChild("ik"); ikMap != null; ikMap = ikMap.next) {
				IkConstraintData ikConstraintData = new IkConstraintData(ikMap.getString("name"));
	
				for (JsonValue boneMap = ikMap.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
					String boneName = boneMap.asString();
					BoneData bone = skeletonData.findBone(boneName);
					if (bone == null) throw new SerializationException("IK bone not found: " + boneName);
					ikConstraintData.bones.add(bone);
				}
	
				String targetName = ikMap.getString("target");
				ikConstraintData.target = skeletonData.findBone(targetName);
				if (ikConstraintData.target == null) throw new SerializationException("Target bone not found: " + targetName);
	
				ikConstraintData.bendDirection = ikMap.getBoolean("bendPositive", true) ? 1 : -1;
				ikConstraintData.mix = ikMap.getFloat("mix", 1);
	
				skeletonData.ikConstraints.add(ikConstraintData);
			}*/
	
			// Slots.
			JsonArray slotMap = root.get("slots").getAsJsonArray();
			for (int i = 0; i<slotMap.size(); i++) {
				JsonObject currentSlot = slotMap.get(i).getAsJsonObject();
				
				String slotName = currentSlot.get("name").getAsString();
				String boneName = currentSlot.get("bone").getAsString();
				BoneData boneData = skeletonData.findBone(boneName);
				//if (boneData == null) throw new IllegalArgumentException("Slot bone not found: " + boneName);
				SlotData slotData = new SlotData(slotName, boneData);
	
				String color = currentSlot.has("color") ? currentSlot.get("color").getAsString() : null;
				if(color != null)
					slotData.getColor().set(MathFunctions.valueOfHex(color));
	
				slotData.attachmentName = currentSlot.has("attachment") ? currentSlot.get("attachment").getAsString() : null;
	
				slotData.additiveBlending = currentSlot.has("additive") ? currentSlot.get("additive").getAsBoolean() : false;
	
				skeletonData.slots.add(slotData);
			}
	
			// Skins.
			for(Entry<String, JsonElement> child : root.get("skins").getAsJsonObject().entrySet()) {
				Skin skin = new Skin(child.getKey());
				for(Entry<String, JsonElement> slotEntry : child.getValue().getAsJsonObject().entrySet()) {
					int slotIndex = skeletonData.findSlotIndex(slotEntry.getKey());
					//if (slotIndex == -1) throw new SerializationException("Slot not found: " + slotEntry.name);
					for(Entry<String, JsonElement> entry : slotEntry.getValue().getAsJsonObject().entrySet()) {
						Attachment attachment = readAttachment(skin, entry.getKey(), entry.getValue().getAsJsonObject());
						if(attachment != null) 
							skin.addAttachment(slotIndex, entry.getKey(), attachment);
					}
				}
				skeletonData.skins.add(skin);
				if (skin.name.equals("default")) skeletonData.defaultSkin = skin;
			}
	
			// Events.
			if(root.has("events")) {
				for(Entry<String, JsonElement> eventMap : root.get("events").getAsJsonObject().entrySet()) {
				//for (JsonValue eventMap = root.getChild("events"); eventMap != null; eventMap = eventMap.next) {
					EventData eventData = new EventData(eventMap.getKey());
					JsonObject event = eventMap.getValue().getAsJsonObject();
					eventData.intValue = event.has("int") ? event.get("int").getAsInt() : 0;
					eventData.floatValue = event.has("float") ? event.get("float").getAsFloat() : 0f;
					eventData.stringValue = event.has("string") ? event.get("string").getAsString() : null;
					skeletonData.events.add(eventData);
				}
			}
	
			// Animations.
			for(Entry<String, JsonElement> animationMap : root.get("animations").getAsJsonObject().entrySet()) {
			//for (JsonValue animationMap = root.getChild("animations"); animationMap != null; animationMap = animationMap.next)
				if(!animationMap.getValue().getAsJsonObject().entrySet().isEmpty()) {
					readAnimation(animationMap.getKey(), animationMap.getValue().getAsJsonObject(), skeletonData);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO Remove these calls and just don't declare starting sizes because libGDX is dumb
		//skeletonData.bones.shrink();
		//skeletonData.slots.shrink();
		//skeletonData.skins.shrink();
		//skeletonData.animations.shrink();
		return skeletonData;
	}

	private Attachment readAttachment (Skin skin, String name, JsonObject map) {
		float scale = this.scale;
		name = map.has("name") ? map.get("name").getAsString() : name;
		String path = map.has("path") ? map.get("path").getAsString() : name;

		switch (AttachmentType.valueOf(map.has("type") ? map.get("type").getAsString() : AttachmentType.region.name())) {
		case region: {
			Region region = new Region(name);
			region.setPath(path);
			region.setX((map.has("x") ? map.get("x").getAsFloat() : 0f) * scale);
			region.setY((map.has("y") ? map.get("y").getAsFloat() : 0f) * scale);
			region.setScaleX(map.has("scaleX") ? map.get("scaleX").getAsFloat() : 1f);
			region.setScaleY(map.has("scaleY") ? map.get("scaleY").getAsFloat() : 1f);
			region.setRotation(map.has("rotation") ? map.get("rotation").getAsFloat() : 0f);
			region.setWidth((map.has("width") ? map.get("width").getAsFloat() : 0f) * scale);
			region.setHeight((map.has("height") ? map.get("height").getAsFloat() : 0f) * scale);

			String color = map.has("color") ? map.get("color").getAsString() : null;
			if(color != null)
				region.getColor().set(MathFunctions.valueOfHex(color));

			region.updateOffset();
			return region;
		}
		case boundingbox: {
			/*BoundingBoxAttachment box = attachmentLoader.newBoundingBoxAttachment(skin, name);
			if (box == null) return null;
			float[] vertices = map.require("vertices").asFloatArray();
			if (scale != 1) {
				for (int i = 0, n = vertices.length; i < n; i++)
					vertices[i] *= scale;
			}
			box.setVertices(vertices);
			return box;*/
		}
		case mesh: {
			/*MeshAttachment mesh = attachmentLoader.newMeshAttachment(skin, name, path);
			if (mesh == null) return null;
			mesh.setPath(path);
			float[] vertices = map.require("vertices").asFloatArray();
			if (scale != 1) {
				for (int i = 0, n = vertices.length; i < n; i++)
					vertices[i] *= scale;
			}
			mesh.setVertices(vertices);
			mesh.setTriangles(map.require("triangles").asShortArray());
			mesh.setRegionUVs(map.require("uvs").asFloatArray());
			mesh.updateUVs();

			String color = map.getString("color", null);
			if (color != null) mesh.getColor().set(Color.valueOf(color));

			if (map.has("hull")) mesh.setHullLength(map.require("hull").asInt() * 2);
			if (map.has("edges")) mesh.setEdges(map.require("edges").asIntArray());
			mesh.setWidth(map.getFloat("width", 0) * scale);
			mesh.setHeight(map.getFloat("height", 0) * scale);
			return mesh;*/
		}
		case skinnedmesh: {
			/*SkinnedMeshAttachment mesh = attachmentLoader.newSkinnedMeshAttachment(skin, name, path);
			if (mesh == null) return null;
			mesh.setPath(path);
			float[] uvs = map.require("uvs").asFloatArray();
			float[] vertices = map.require("vertices").asFloatArray();
			FloatArray weights = new FloatArray(uvs.length * 3 * 3);
			IntArray bones = new IntArray(uvs.length * 3);
			for (int i = 0, n = vertices.length; i < n;) {
				int boneCount = (int)vertices[i++];
				bones.add(boneCount);
				for (int nn = i + boneCount * 4; i < nn;) {
					bones.add((int)vertices[i]);
					weights.add(vertices[i + 1] * scale);
					weights.add(vertices[i + 2] * scale);
					weights.add(vertices[i + 3]);
					i += 4;
				}
			}
			mesh.setBones(bones.toArray());
			mesh.setWeights(weights.toArray());
			mesh.setTriangles(map.require("triangles").asShortArray());
			mesh.setRegionUVs(uvs);
			mesh.updateUVs();

			String color = map.getString("color", null);
			if (color != null) mesh.getColor().set(Color.valueOf(color));

			if (map.has("hull")) mesh.setHullLength(map.require("hull").asInt() * 2);
			if (map.has("edges")) mesh.setEdges(map.require("edges").asIntArray());
			mesh.setWidth(map.getFloat("width", 0) * scale);
			mesh.setHeight(map.getFloat("height", 0) * scale);
			return mesh;*/
		}
		}

		// RegionSequenceAttachment regionSequenceAttachment = (RegionSequenceAttachment)attachment;
		//
		// float fps = map.getFloat("fps");
		// regionSequenceAttachment.setFrameTime(fps);
		//
		// String modeString = map.getString("mode");
		// regionSequenceAttachment.setMode(modeString == null ? Mode.forward : Mode.valueOf(modeString));

		return null;
	}

	private void readAnimation (String name, JsonObject map, SkeletonData skeletonData) {
		float scale = this.scale;
		ArrayList<Timeline> timelines = new ArrayList<Timeline>();
		float duration = 0;

		// Slot timelines.
		if(map.has("slots")) {
			for(Entry<String, JsonElement> slotMap : map.get("slots").getAsJsonObject().entrySet()) {
			//for (JsonValue slotMap = map.getChild("slots"); slotMap != null; slotMap = slotMap.next) {
				int slotIndex = skeletonData.findSlotIndex(slotMap.getKey());
				//if (slotIndex == -1) throw new SerializationException("Slot not found: " + slotMap.name);
	
				for(Entry<String, JsonElement> timelineMap : slotMap.getValue().getAsJsonObject().entrySet()) {
				//for (JsonValue timelineMap = slotMap.child; timelineMap != null; timelineMap = timelineMap.next) {
					String timelineName = timelineMap.getKey();
					if (timelineName.equals("color")) {
						ColorTimeline timeline = new ColorTimeline(timelineMap.getValue().getAsJsonArray().size());
						timeline.slotIndex = slotIndex;
	
						int frameIndex = 0;
						for(int i = 0; i<timelineMap.getValue().getAsJsonArray().size(); i++) {
							JsonObject valueMap = timelineMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
							Vector4f color = MathFunctions.valueOfHex(valueMap.get("color").getAsString());
							timeline.setFrame(frameIndex, valueMap.get("time").getAsFloat(), color.x, color.y, color.z, color.w);
							readCurve(timeline, frameIndex, valueMap);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 5 - 5]);
	
					} else if (timelineName.equals("attachment")) {
						AttachmentTimeline timeline = new AttachmentTimeline(timelineMap.getValue().getAsJsonArray().size());
						timeline.slotIndex = slotIndex;
	
						int frameIndex = 0;
						for(int i = 0; i<timelineMap.getValue().getAsJsonArray().size(); i++) {
							JsonObject valueMap = timelineMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
						//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next)
							timeline.setFrame(frameIndex++, valueMap.get("time").getAsFloat(), valueMap.get("name").getAsString());
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
					} else
						throw new RuntimeException("Invalid timeline type for a slot: " + timelineName + " (" + slotMap.getKey() + ")");
				}
			}
		}

		// Bone timelines.
		for(Entry<String, JsonElement> boneMap : map.get("bones").getAsJsonObject().entrySet()) {
		//for (JsonValue boneMap = map.getChild("bones"); boneMap != null; boneMap = boneMap.next) {
			int boneIndex = skeletonData.findBoneIndex(boneMap.getKey());
			//if (boneIndex == -1) throw new SerializationException("Bone not found: " + boneMap.name);

			for(Entry<String, JsonElement> timelineMap : boneMap.getValue().getAsJsonObject().entrySet()) {
			//for (JsonValue timelineMap = boneMap.child; timelineMap != null; timelineMap = timelineMap.next) {
				String timelineName = timelineMap.getKey();
				if (timelineName.equals("rotate")) {
					RotateTimeline timeline = new RotateTimeline(timelineMap.getValue().getAsJsonArray().size());
					timeline.boneIndex = boneIndex;

					int frameIndex = 0;
					for(int i = 0; i<timelineMap.getValue().getAsJsonArray().size(); i++) {
						JsonObject valueMap = timelineMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
					//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						timeline.setFrame(frameIndex, valueMap.get("time").getAsFloat(), valueMap.get("angle").getAsFloat());
						readCurve(timeline, frameIndex, valueMap);
						frameIndex++;
					}
					timelines.add(timeline);
					duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 2 - 2]);

				} else if (timelineName.equals("translate") || timelineName.equals("scale")) {
					TranslateTimeline timeline;
					float timelineScale = 1;
					if (timelineName.equals("scale"))
						timeline = new ScaleTimeline(timelineMap.getValue().getAsJsonArray().size());
					else {
						timeline = new TranslateTimeline(timelineMap.getValue().getAsJsonArray().size());
						timelineScale = scale;
					}
					timeline.boneIndex = boneIndex;

					int frameIndex = 0;
					for(int i = 0; i<timelineMap.getValue().getAsJsonArray().size(); i++) {
						JsonObject valueMap = timelineMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
					//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						float x = (valueMap.has("x") ? valueMap.get("x").getAsFloat() : 0);
						float y = (valueMap.has("y") ? valueMap.get("y").getAsFloat() : 0);
						timeline.setFrame(frameIndex, valueMap.get("time").getAsFloat(), x * timelineScale, y * timelineScale);
						readCurve(timeline, frameIndex, valueMap);
						frameIndex++;
					}
					timelines.add(timeline);
					duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 3 - 3]);

				} else if (timelineName.equals("flipX") || timelineName.equals("flipY")) {
					boolean x = timelineName.equals("flipX");
					FlipXTimeline timeline = x ? new FlipXTimeline(timelineMap.getValue().getAsJsonArray().size()) :
						new FlipYTimeline(timelineMap.getValue().getAsJsonArray().size());
					timeline.boneIndex = boneIndex;

					String field = x ? "x" : "y";
					int frameIndex = 0;
					for(int i = 0; i<timelineMap.getValue().getAsJsonArray().size(); i++) {
						JsonObject valueMap = timelineMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
					//for (JsonValue valueMap = timelineMap.child; valueMap != null; valueMap = valueMap.next) {
						timeline.setFrame(frameIndex, valueMap.get("time").getAsFloat(),
								valueMap.has(field) ? valueMap.get(field).getAsBoolean() : false);
						frameIndex++;
					}
					timelines.add(timeline);
					duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 2 - 2]);

				} else
					throw new RuntimeException("Invalid timeline type for a bone: " + timelineName + " (" + boneMap.getKey() + ")");
			}
		}

		// IK timelines.
		if(map.has("ik")) {
			for(Entry<String, JsonElement> ikMap : map.get("ik").getAsJsonObject().entrySet()) {
			//for (JsonValue ikMap = map.getChild("ik"); ikMap != null; ikMap = ikMap.next) {
				IkConstraintData ikConstraint = skeletonData.findIkConstraint(ikMap.getKey());
				IkConstraintTimeline timeline = new IkConstraintTimeline(ikMap.getValue().getAsJsonArray().size());
				timeline.ikConstraintIndex = skeletonData.getIkConstraints().indexOf(ikConstraint);
				int frameIndex = 0;
				// TODO Might not be an array?
				for(int i = 0; i<ikMap.getValue().getAsJsonArray().size(); i++) {
					JsonObject valueMap = ikMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
				//for (JsonValue valueMap = ikMap.child; valueMap != null; valueMap = valueMap.next) {
					timeline.setFrame(frameIndex, valueMap.get("time").getAsFloat(), valueMap.get("mix").getAsFloat(),
						valueMap.get("bendPositive").getAsBoolean() ? 1 : -1);
					readCurve(timeline, frameIndex, valueMap);
					frameIndex++;
				}
				timelines.add(timeline);
				duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() * 3 - 3]);
			}
		}

		// FFD timelines.
		if(map.has("ffd")) {
			for(Entry<String, JsonElement> ffdMap : map.get("ffd").getAsJsonObject().entrySet()) {
			//for (JsonValue ffdMap = map.getChild("ffd"); ffdMap != null; ffdMap = ffdMap.next) {
				Skin skin = skeletonData.findSkin(ffdMap.getKey());
				//if (skin == null) throw new SerializationException("Skin not found: " + ffdMap.name);
				for(Entry<String, JsonElement> slotMap : ffdMap.getValue().getAsJsonObject().entrySet()) {
				//for (JsonValue slotMap = ffdMap.child; slotMap != null; slotMap = slotMap.next) {
					int slotIndex = skeletonData.findSlotIndex(slotMap.getKey());
					//if (slotIndex == -1) throw new SerializationException("Slot not found: " + slotMap.name);
					for(Entry<String, JsonElement> meshMap : slotMap.getValue().getAsJsonObject().entrySet()) {
					//for (JsonValue meshMap = slotMap.child; meshMap != null; meshMap = meshMap.next) {
						FfdTimeline timeline = new FfdTimeline(meshMap.getValue().getAsJsonArray().size());
						Attachment attachment = skin.getAttachment(slotIndex, meshMap.getKey());
						//if (attachment == null) throw new SerializationException("FFD attachment not found: " + meshMap.name);
						timeline.slotIndex = slotIndex;
						timeline.attachment = attachment;
	
						int vertexCount = 0;
						/*if (attachment instanceof MeshAttachment)
							vertexCount = ((MeshAttachment)attachment).getVertices().length;
						else
							vertexCount = ((SkinnedMeshAttachment)attachment).getWeights().length / 3 * 2;*/
	
						int frameIndex = 0;
						for(int i = 0; i<meshMap.getValue().getAsJsonArray().size(); i++) {
							JsonObject valueMap = meshMap.getValue().getAsJsonArray().get(i).getAsJsonObject();
						//for (JsonValue valueMap = meshMap.child; valueMap != null; valueMap = valueMap.next) {
							float[] vertices;
							JsonArray verticesValue = valueMap.get("vertices").getAsJsonArray();
							if (verticesValue == null) {
								/*if (attachment instanceof MeshAttachment)
									vertices = ((MeshAttachment)attachment).getVertices();
								else*/
									vertices = new float[vertexCount];
							} else {
								vertices = new float[vertexCount];
								int start = valueMap.has("offset") ? valueMap.get("offset").getAsInt() : 0;
								System.arraycopy(verticesValue, 0, vertices, start, verticesValue.size());
								if (scale != 1) {
									for (int j = start, n = j + verticesValue.size(); j < n; j++)
										vertices[j] *= scale;
								}
								/*if (attachment instanceof MeshAttachment) {
									float[] meshVertices = ((MeshAttachment)attachment).getVertices();
									for (int j = 0; j < vertexCount; j++)
										vertices[j] += meshVertices[j];
								}*/
							}
	
							timeline.setFrame(frameIndex, valueMap.get("time").getAsFloat(), vertices);
							readCurve(timeline, frameIndex, valueMap);
							frameIndex++;
						}
						timelines.add(timeline);
						duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
					}
				}
			}
		}

		// Draw order timeline.
		JsonArray drawOrdersMap = map.has("drawOrder") ? map.get("drawOrder").getAsJsonArray() : null;
		if (drawOrdersMap == null) {
			drawOrdersMap = map.has("draworder") ? map.get("draworder").getAsJsonArray() : null;
		}
		if (drawOrdersMap != null) {
			DrawOrderTimeline timeline = new DrawOrderTimeline(drawOrdersMap.size());
			int slotCount = skeletonData.slots.size();
			int frameIndex = 0;
			for(JsonElement drawOrderMap : drawOrdersMap) {
			//for (JsonValue drawOrderMap = drawOrdersMap.child; drawOrderMap != null; drawOrderMap = drawOrderMap.next) {
				int[] drawOrder = null;
				JsonArray offsets = drawOrderMap.getAsJsonObject().has("offsets") ?
						drawOrderMap.getAsJsonObject().get("offsets").getAsJsonArray() : null;
				if (offsets != null) {
					drawOrder = new int[slotCount];
					for (int i = slotCount - 1; i >= 0; i--)
						drawOrder[i] = -1;
					int[] unchanged = new int[slotCount - offsets.size()];
					int originalIndex = 0, unchangedIndex = 0;
					for (JsonElement offsetMap : offsets) {
						int slotIndex = skeletonData.findSlotIndex(offsetMap.getAsJsonObject().get("slot").getAsString());
						//if (slotIndex == -1) throw new SerializationException("Slot not found: " + offsetMap.getString("slot"));
						// Collect unchanged items.
						while (originalIndex != slotIndex)
							unchanged[unchangedIndex++] = originalIndex++;
						// Set changed items.
						drawOrder[originalIndex + offsetMap.getAsJsonObject().get("offset").getAsInt()] = originalIndex++;
					}
					// Collect remaining unchanged items.
					while (originalIndex < slotCount)
						unchanged[unchangedIndex++] = originalIndex++;
					// Fill in unchanged items.
					for (int i = slotCount - 1; i >= 0; i--)
						if (drawOrder[i] == -1) drawOrder[i] = unchanged[--unchangedIndex];
				}
				timeline.setFrame(frameIndex++, drawOrderMap.getAsJsonObject().get("time").getAsFloat(), drawOrder);
			}
			timelines.add(timeline);
			duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
		}

		// Event timeline.
		JsonArray eventsMap = map.has("events") ? map.get("events").getAsJsonArray() : null;
		if (eventsMap != null) {
			EventTimeline timeline = new EventTimeline(eventsMap.size());
			int frameIndex = 0;
			for(JsonElement eventMap : eventsMap) {
				EventData eventData = skeletonData.findEvent(eventMap.getAsJsonObject().get("name").getAsString());
				//if (eventData == null) throw new SerializationException("Event not found: " + eventMap.getString("name"));
				Event event = new Event(eventData);
				event.intValue = eventMap.getAsJsonObject().has("int") ? eventMap.getAsJsonObject().get("int").getAsInt() : 0;
				event.floatValue = eventMap.getAsJsonObject().has("float") ? eventMap.getAsJsonObject().get("float").getAsFloat() : 0f;
				event.stringValue = eventMap.getAsJsonObject().has("string") ? eventMap.getAsJsonObject().get("string").getAsString() : null;
				timeline.setFrame(frameIndex++, eventMap.getAsJsonObject().get("time").getAsFloat(), event);
			}
			timelines.add(timeline);
			duration = Math.max(duration, timeline.getFrames()[timeline.getFrameCount() - 1]);
		}

		//timelines.shrink();
		skeletonData.animations.add(new Animation(name, timelines, duration));
	}

	void readCurve (CurveTimeline timeline, int frameIndex, JsonObject valueMap) {
		//JsonValue curve = valueMap.get("curve");
		if(!valueMap.has("curve")) return;
		JsonElement curve = valueMap.get("curve");
		//if (curve == null) return;
		if (curve.isJsonPrimitive() && curve.getAsString().equals("stepped"))
			timeline.setStepped(frameIndex);
		else if (curve.isJsonArray()) {
			timeline.setCurve(frameIndex, curve.getAsJsonArray().get(0).getAsFloat(), curve.getAsJsonArray().get(1).getAsFloat(),
					curve.getAsJsonArray().get(2).getAsFloat(), curve.getAsJsonArray().get(3).getAsFloat());
		}
	}
}
