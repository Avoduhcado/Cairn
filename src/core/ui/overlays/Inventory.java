package core.ui.overlays;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import core.Camera;
import core.ui.Button;
import core.ui.ElementGroup;
import core.ui.EmptyFrame;
import core.ui.UIElement;
import core.ui.utils.ClickEvent;

public class Inventory extends MenuOverlay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EmptyFrame mainFrame;
	private ElementGroup<UIElement> mainPage;
	private Button armorTab;
	private Button weaponTab;
	private Button offhandTab;
	private Button inventoryTab;
	private Button optionsTab;
	
	private String pointer = "screen ui/Pointer";
	
	public Inventory() {		
		{
			Point2D descFramePos = new Point2D.Float(Camera.get().getDisplayWidth(0.825f), Camera.get().getDisplayHeight(0.2f));
			Dimension2D descFrameSize = new Dimension();
			descFrameSize.setSize(318 * Camera.ASPECT_RATIO, 660 * Camera.ASPECT_RATIO);
			
			EmptyFrame descFrame = new EmptyFrame((float) descFramePos.getX(), (float) descFramePos.getY(),
					(float) descFrameSize.getWidth(), (float) descFrameSize.getHeight(), null);
			descFrame.setStill(true);
			descFrame.setBackground("screen ui/RIGHT BOX");
			add(descFrame);
		}
		
		{
			Point2D mainFramePos = new Point2D.Float(Camera.get().getDisplayWidth(0.6075f), Camera.get().getDisplayHeight(0.0f));
			Dimension2D mainFrameSize = new Dimension();
			mainFrameSize.setSize(429 * Camera.ASPECT_RATIO, 877 * Camera.ASPECT_RATIO);
			
			mainFrame = new EmptyFrame((float) mainFramePos.getX(), (float) mainFramePos.getY(), 
					(float) mainFrameSize.getWidth(), (float) mainFrameSize.getHeight(), null);
			mainFrame.setStill(true);
			mainFrame.setBackground("screen ui/MAIN BOX");
			add(mainFrame);
			
			openArmorTab();
		}
		
		{
			Point2D tabPanePos = new Point2D.Float(Camera.get().getDisplayWidth(0.555f), Camera.get().getDisplayHeight(0.185f));
			Dimension2D tabPaneSize = new Dimension();
			tabPaneSize.setSize(111 * Camera.ASPECT_RATIO, 510 * Camera.ASPECT_RATIO);
			
			EmptyFrame tabFrame = new EmptyFrame((float) tabPanePos.getX(), (float) tabPanePos.getY(),
					(float) tabPaneSize.getWidth(), (float) tabPaneSize.getHeight(), null);
			tabFrame.setStill(true);
			tabFrame.setBackground("screen ui/LEFT BOX");
			add(tabFrame);
		}
		
		{
			Point2D tabIconPos = new Point2D.Float(Camera.get().getDisplayWidth(0.555f), Camera.get().getDisplayHeight(0.185f));
			float tabIconYOffset = -Camera.get().getDisplayHeight(0.015f);
			Dimension2D tabIconSize = new Dimension();
			tabIconSize.setSize(110 * Camera.ASPECT_RATIO, 100 * Camera.ASPECT_RATIO);
			
			EmptyFrame tabIconFrame = new EmptyFrame((float) tabIconPos.getX(), (float) tabIconPos.getY(),
					(float) tabIconSize.getWidth(), (float) tabIconSize.getHeight(), null);
			tabIconFrame.setStill(true);
			tabIconFrame.setBackground("screen ui/Tab Frame");
			add(tabIconFrame);
			
			armorTab = new Button(null);
			armorTab.setBounds(tabIconPos.getX(), tabIconPos.getY(), tabIconSize.getWidth(), tabIconSize.getHeight());
			armorTab.setIcon("screen ui/Tab Armor");
			armorTab.setBackground("screen ui/Tab Frame");
			armorTab.setStill(true);
			armorTab.addEvent(new ClickEvent(armorTab) {
				public void click() {
					System.out.println("Armor");
					openArmorTab();
					mainPage.setKeyboardNavigable(true, mainPage.get(2));
					mainPage.setSelectionPointer(pointer);
					Inventory.this.setKeyboardNavigable(false, null);
				}
			});
			add(armorTab);
			
			weaponTab = new Button(null);
			weaponTab.setBounds(tabIconPos.getX(), armorTab.getBounds().getMaxY() + tabIconYOffset,
					tabIconSize.getWidth(), tabIconSize.getHeight());
			weaponTab.setIcon("screen ui/Tab Weapon");
			weaponTab.setBackground("screen ui/Tab Frame");
			weaponTab.setStill(true);
			weaponTab.addEvent(new ClickEvent(weaponTab) {
				public void click() {
					System.out.println("Weapon");
					openWeaponTab();
					mainPage.setKeyboardNavigable(true, mainPage.get(1));
					mainPage.setSelectionPointer(pointer);
					Inventory.this.setKeyboardNavigable(false, null);
				}
			});
			add(weaponTab);
			
			offhandTab = new Button(null);
			offhandTab.setBounds(tabIconPos.getX(), weaponTab.getBounds().getMaxY() + tabIconYOffset,
					tabIconSize.getWidth(), tabIconSize.getHeight());
			offhandTab.setIcon("screen ui/Tab Offhand");
			offhandTab.setBackground("screen ui/Tab Frame");
			offhandTab.setStill(true);
			offhandTab.addEvent(new ClickEvent(offhandTab) {
				public void click() {
					System.out.println("Offhand");
					openOffhandTab();
					mainPage.setKeyboardNavigable(true, mainPage.get(1));
					mainPage.setSelectionPointer(pointer);
					Inventory.this.setKeyboardNavigable(false, null);
				}
			});
			add(offhandTab);
			
			inventoryTab = new Button(null);
			inventoryTab.setBounds(tabIconPos.getX(), offhandTab.getBounds().getMaxY() + tabIconYOffset, 
					tabIconSize.getWidth(), tabIconSize.getHeight());
			inventoryTab.setIcon("screen ui/Tab Inventory");
			inventoryTab.setBackground("screen ui/Tab Frame");
			inventoryTab.setStill(true);
			inventoryTab.addEvent(new ClickEvent(inventoryTab) {
				public void click() {
					System.out.println("Pocket");
					openPocketTab();
				}
			});
			add(inventoryTab);
			
			optionsTab = new Button(null);
			optionsTab.setBounds(tabIconPos.getX(), inventoryTab.getBounds().getMaxY() + tabIconYOffset,
					tabIconSize.getWidth(), tabIconSize.getHeight());
			optionsTab.setIcon("screen ui/Tab Options");
			optionsTab.setBackground("screen ui/Tab Frame");
			optionsTab.setStill(true);
			optionsTab.addEvent(new ClickEvent(optionsTab) {
				public void click() {
					System.out.println("Options");
					openOptionsTab();
				}
			});
			add(optionsTab);
			
			armorTab.setSurrounding(0, optionsTab);
			weaponTab.setSurrounding(0, armorTab);
			offhandTab.setSurrounding(0, weaponTab);
			inventoryTab.setSurrounding(0, offhandTab);
			optionsTab.setSurrounding(0, inventoryTab);
			
			setKeyboardNavigable(true, armorTab);
			addFrame(null);
			
			setSelectionPointer(pointer);
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		mainPage.update();
	}
	
	@Override
	public void draw() {
		super.draw();
		
		mainPage.draw();
	}
	
	private void openArmorTab() {
		String directory = "screen ui/";
		mainPage = new ElementGroup<UIElement>();
		
		EmptyFrame armorFlourish = new EmptyFrame((float) mainFrame.getBounds().getX(), (float) mainFrame.getBounds().getY(),
				429 * Camera.ASPECT_RATIO, 877 * Camera.ASPECT_RATIO, null);
		armorFlourish.setBackground(directory + "Armor Flourish");
		armorFlourish.setStill(true);
		mainPage.add(armorFlourish);
		
		EmptyFrame paperDoll = new EmptyFrame(0, 0, 376 * Camera.ASPECT_RATIO, 424 * Camera.ASPECT_RATIO, null);
		paperDoll.setPosition(mainFrame.getBounds().getCenterX() - (paperDoll.getBounds().getWidth() / 2f), Camera.get().getDisplayHeight(0.275f));
		paperDoll.setBackground(directory + "ARMOR PAPER DOLL");
		paperDoll.setStill(true);
		mainPage.add(paperDoll);
		
		directory += "icons/";
		
		Button headSlot = new Button(null);
		headSlot.setBounds(0, 0, 100 * Camera.ASPECT_RATIO, 100 * Camera.ASPECT_RATIO);
		headSlot.setPosition(mainFrame.getBounds().getCenterX() - (headSlot.getBounds().getWidth() / 2f), Camera.get().getDisplayHeight(0.21f));
		headSlot.setIcon(directory + "Flock Head");
		headSlot.setBackground(directory + "Inventory Tab");
		headSlot.setStill(true);
		mainPage.add(headSlot);
		
		Button bodySlot = new Button(null);
		bodySlot.setBounds(0, 0, 100 * Camera.ASPECT_RATIO, 100 * Camera.ASPECT_RATIO);
		bodySlot.setPosition(mainFrame.getBounds().getCenterX() - (bodySlot.getBounds().getWidth() / 2f), Camera.get().getDisplayHeight(0.355f));
		bodySlot.setIcon(directory + "Flock Chest");
		bodySlot.setBackground(directory + "Inventory Tab");
		bodySlot.setStill(true);
		mainPage.add(bodySlot);
		
		Button legSlot = new Button(null);
		legSlot.setBounds(0, 0, 100 * Camera.ASPECT_RATIO, 100 * Camera.ASPECT_RATIO);
		legSlot.setPosition(mainFrame.getBounds().getCenterX() - (legSlot.getBounds().getWidth() / 2f), Camera.get().getDisplayHeight(0.51f));
		legSlot.setIcon(directory + "Flock Legs");
		legSlot.setBackground(directory + "Inventory Tab");
		legSlot.setStill(true);
		mainPage.add(legSlot);
		
		headSlot.setSurrounding(0, legSlot);
		bodySlot.setSurrounding(0, headSlot);
		legSlot.setSurrounding(0, bodySlot);
		mainPage.setCancelListener(new CancelListener() {
			public void cancel() {
				Inventory.this.setKeyboardNavigable(true, armorTab);
				Inventory.this.setSelectionPointer(pointer);
				mainPage.setKeyboardNavigable(false, null);
			}
		});
	}
	
	private void openWeaponTab() {
		String directory = "screen ui/";
		mainPage = new ElementGroup<UIElement>();
		
		EmptyFrame rightHandFlourish = new EmptyFrame((float) mainFrame.getBounds().getX(), (float) mainFrame.getBounds().getY(),
				429 * Camera.ASPECT_RATIO, 877 * Camera.ASPECT_RATIO, null);
		rightHandFlourish.setBackground(directory + "Right Hand Flourish");
		rightHandFlourish.setStill(true);
		mainPage.add(rightHandFlourish);
		
		directory += "icons/";
		
		Button lightMace = new Button(null);
		lightMace.setBounds(0, 0, 200 * Camera.ASPECT_RATIO, 200 * Camera.ASPECT_RATIO);
		lightMace.setPosition(rightHandFlourish.getBounds().getCenterX() - (lightMace.getBounds().getWidth() / 2f),
				Camera.get().getDisplayHeight(0.21f));
		lightMace.setBackground(directory + "Item Box");
		lightMace.setIcon(directory + "Light Mace");
		lightMace.setStill(true);
		mainPage.add(lightMace);
		
		Button heavyMace = new Button(null);
		heavyMace.setBounds(lightMace.getBounds().getX(), lightMace.getBounds().getMaxY(), 200 * Camera.ASPECT_RATIO, 200 * Camera.ASPECT_RATIO);
		heavyMace.setBackground(directory + "Item Box");
		heavyMace.setIcon(directory + "Heavy Mace");
		heavyMace.setStill(true);
		mainPage.add(heavyMace);
		
		Button polearm = new Button(null);
		polearm.setBounds(heavyMace.getBounds().getX(), heavyMace.getBounds().getMaxY(), 200 * Camera.ASPECT_RATIO, 200 * Camera.ASPECT_RATIO);
		polearm.setBackground(directory + "Item Box");
		polearm.setIcon(directory + "Polearm");
		polearm.setStill(true);
		mainPage.add(polearm);
		
		lightMace.setSurrounding(0, polearm);
		heavyMace.setSurrounding(0, lightMace);
		polearm.setSurrounding(0, heavyMace);
		mainPage.setCancelListener(new CancelListener() {
			public void cancel() {
				Inventory.this.setKeyboardNavigable(true, weaponTab);
				Inventory.this.setSelectionPointer(pointer);
				mainPage.setKeyboardNavigable(false, null);
			}
		});
	}
	
	private void openOffhandTab() {
		String directory = "screen ui/";
		mainPage = new ElementGroup<UIElement>();
		
		EmptyFrame leftHandFlourish = new EmptyFrame((float) mainFrame.getBounds().getX(), (float) mainFrame.getBounds().getY(),
				429 * Camera.ASPECT_RATIO, 877 * Camera.ASPECT_RATIO, null);
		leftHandFlourish.setBackground(directory + "Left Hand Flourish");
		leftHandFlourish.setStill(true);
		mainPage.add(leftHandFlourish);
		
		directory += "icons/";
		
		Button pocket1 = new Button(null);
		pocket1.setBounds(0, 0, 150 * Camera.ASPECT_RATIO, 150 * Camera.ASPECT_RATIO);
		pocket1.setPosition((leftHandFlourish.getBounds().getX() + leftHandFlourish.getBounds().getWidth() * 0.25f)
				- (pocket1.getBounds().getWidth() * 0.5f),
				Camera.get().getDisplayHeight(0.21f));
		pocket1.setBackground(directory + "Item Box");
		pocket1.setIcon(directory + "Milk");
		pocket1.setStill(true);
		mainPage.add(pocket1);
		
		Button pocket2 = new Button(null);
		pocket2.setBounds(pocket1.getBounds().getX(), pocket1.getBounds().getMaxY(), 150 * Camera.ASPECT_RATIO, 150 * Camera.ASPECT_RATIO);
		pocket2.setBackground(directory + "Item Box");
		pocket2.setIcon(directory + "Bell");
		pocket2.setStill(true);
		mainPage.add(pocket2);
		
		Button pocket3 = new Button(null);
		pocket3.setBounds(pocket2.getBounds().getX(), pocket2.getBounds().getMaxY(), 150 * Camera.ASPECT_RATIO, 150 * Camera.ASPECT_RATIO);
		pocket3.setBackground(directory + "Item Box");
		pocket3.setStill(true);
		mainPage.add(pocket3);
		
		Button pocket4 = new Button(null);
		pocket4.setBounds((leftHandFlourish.getBounds().getMaxX() - leftHandFlourish.getBounds().getWidth() * 0.25f)
				- (pocket1.getBounds().getWidth() * 0.5f),
				pocket1.getBounds().getY(), 150 * Camera.ASPECT_RATIO, 150 * Camera.ASPECT_RATIO);
		pocket4.setBackground(directory + "Item Box");
		pocket4.setStill(true);
		mainPage.add(pocket4);

		Button pocket5 = new Button(null);
		pocket5.setBounds(pocket4.getBounds().getX(), pocket4.getBounds().getMaxY(), 150 * Camera.ASPECT_RATIO, 150 * Camera.ASPECT_RATIO);
		pocket5.setBackground(directory + "Item Box");
		pocket5.setStill(true);
		mainPage.add(pocket5);
		
		Button pocket6 = new Button(null);
		pocket6.setBounds(pocket5.getBounds().getX(), pocket5.getBounds().getMaxY(), 150 * Camera.ASPECT_RATIO, 150 * Camera.ASPECT_RATIO);
		pocket6.setBackground(directory + "Item Box");
		pocket6.setStill(true);
		mainPage.add(pocket6);
		
		pocket1.setSurrounding(0, pocket3);
		pocket1.setSurrounding(1, pocket4);
		pocket2.setSurrounding(0, pocket1);
		pocket2.setSurrounding(1, pocket5);
		pocket3.setSurrounding(0, pocket2);
		pocket3.setSurrounding(1, pocket6);
		pocket4.setSurrounding(0, pocket6);
		pocket4.setSurrounding(1, pocket2);
		pocket5.setSurrounding(0, pocket4);
		pocket5.setSurrounding(1, pocket3);
		pocket6.setSurrounding(0, pocket5);
		pocket6.setSurrounding(1, pocket1);
		mainPage.setCancelListener(new CancelListener() {
			public void cancel() {
				Inventory.this.setKeyboardNavigable(true, offhandTab);
				Inventory.this.setSelectionPointer(pointer);
				mainPage.setKeyboardNavigable(false, null);
			}
		});
	}
	
	private void openPocketTab() {
		mainPage = new ElementGroup<UIElement>();
	}
	
	private void openOptionsTab() {
		mainPage = new ElementGroup<UIElement>();
	}
	
}
