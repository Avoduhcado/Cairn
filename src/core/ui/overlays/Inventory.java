package core.ui.overlays;

import core.Camera;
import core.render.SpriteIndex;
import core.ui.Button;
import core.ui.EmptyFrame;
import core.ui.utils.ClickEvent;

public class Inventory extends MenuOverlay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	private String selectionPointer;
	
	public Inventory() {
		EmptyFrame tabFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.85f), Camera.get().getDisplayHeight(0.2f), 50, 50, null);
		tabFrame.setStill(true);
		tabFrame.setBackground("INVENTORY MENU/ITEM BOX");
		add(tabFrame);
		
		Button armorTab = new Button(null);
		armorTab.setBounds(Camera.get().getDisplayWidth(0.85f), Camera.get().getDisplayHeight(0.2f), 60, 50);
		//armorTab.setBackground("INVENTORY MENU/ITEM BOX");
		armorTab.setIcon("INVENTORY MENU/ARMOR ICON");
		armorTab.setStill(true);
		armorTab.addEvent(new ClickEvent(armorTab) {
			public void click() {
				System.out.println("Armor");
			}
		});
		
		tabFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.85f), (float) armorTab.getBounds().getMaxY(), 50, 50, null);
		tabFrame.setStill(true);
		tabFrame.setBackground("INVENTORY MENU/ITEM BOX");
		add(tabFrame);
		
		Button weaponTab = new Button(null);
		weaponTab.setBounds(Camera.get().getDisplayWidth(0.85f), armorTab.getBounds().getMaxY(), 60, 50);
		weaponTab.setIcon("INVENTORY MENU/RIGHT HAND ICON");
		weaponTab.setStill(true);
		weaponTab.addEvent(new ClickEvent(weaponTab) {
			public void click() {
				System.out.println("Weapon");
			}
		});
		
		tabFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.85f), (float) weaponTab.getBounds().getMaxY(), 50, 50, null);
		tabFrame.setBackground("INVENTORY MENU/ITEM BOX");
		tabFrame.setStill(true);
		add(tabFrame);
		
		Button offhandTab = new Button(null);
		offhandTab.setBounds(Camera.get().getDisplayWidth(0.85f), weaponTab.getBounds().getMaxY(), 60, 50);
		offhandTab.setIcon("INVENTORY MENU/LEFT HAND ICON");
		offhandTab.setStill(true);
		offhandTab.addEvent(new ClickEvent(offhandTab) {
			public void click() {
				System.out.println("Offhand");
			}
		});
		
		tabFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.85f), (float) offhandTab.getBounds().getMaxY(), 50, 50, null);
		tabFrame.setBackground("INVENTORY MENU/ITEM BOX");
		tabFrame.setStill(true);
		add(tabFrame);
		
		Button pocketTab = new Button(null);
		pocketTab.setBounds(Camera.get().getDisplayWidth(0.85f), offhandTab.getBounds().getMaxY(), 60, 50);
		pocketTab.setIcon("INVENTORY MENU/INVENTORY ICON");
		pocketTab.setStill(true);
		pocketTab.addEvent(new ClickEvent(pocketTab) {
			public void click() {
				System.out.println("Pocket");
			}
		});
		
		tabFrame = new EmptyFrame(Camera.get().getDisplayWidth(0.85f), (float) pocketTab.getBounds().getMaxY(), 50, 50, null);
		tabFrame.setBackground("INVENTORY MENU/ITEM BOX");
		tabFrame.setStill(true);
		add(tabFrame);
		
		Button optionsTab = new Button(null);
		optionsTab.setBounds(Camera.get().getDisplayWidth(0.85f), pocketTab.getBounds().getMaxY(), 60, 50);
		optionsTab.setIcon("INVENTORY MENU/OPTIONS ICON");
		optionsTab.setStill(true);
		optionsTab.addEvent(new ClickEvent(optionsTab) {
			public void click() {
				System.out.println("Options");
			}
		});
		
		armorTab.setSurrounding(0, optionsTab);
		add(armorTab);
		weaponTab.setSurrounding(0, armorTab);
		add(weaponTab);
		offhandTab.setSurrounding(0, weaponTab);
		add(offhandTab);
		pocketTab.setSurrounding(0, offhandTab);
		add(pocketTab);
		optionsTab.setSurrounding(0, pocketTab);
		add(optionsTab);
		
		setKeyboardNavigable(true, armorTab);
		
		selectionPointer = "INVENTORY MENU/POINTER";
	}
	
	@Override
	public void draw() {
		super.draw();
		
		SpriteIndex.getSprite(selectionPointer).setFixedSize(75, 50);
		SpriteIndex.getSprite(selectionPointer).setStill(true);
		SpriteIndex.getSprite(selectionPointer).draw((float) get(selection).getBounds().getX() - SpriteIndex.getSprite(selectionPointer).getDrawWidth(),
				(float) get(selection).getBounds().getCenterY() - (SpriteIndex.getSprite(selectionPointer).getDrawHeight() / 2f));
	}
	
}
