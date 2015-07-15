package core.ui.overlays;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import core.Camera;
import core.ui.Button;
import core.ui.EmptyFrame;
import core.ui.utils.ClickEvent;

public class Inventory extends MenuOverlay {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
				
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
			
			EmptyFrame mainFrame = new EmptyFrame((float) mainFramePos.getX(), (float) mainFramePos.getY(), 
					(float) mainFrameSize.getWidth(), (float) mainFrameSize.getHeight(), null);
			mainFrame.setStill(true);
			mainFrame.setBackground("screen ui/MAIN BOX");
			add(mainFrame);
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
			
			Button armorTab = new Button(null);
			armorTab.setBounds(tabIconPos.getX(), tabIconPos.getY(), tabIconSize.getWidth(), tabIconSize.getHeight());
			armorTab.setIcon("screen ui/Tab Armor");
			armorTab.setBackground("screen ui/Tab Frame");
			armorTab.setStill(true);
			armorTab.addEvent(new ClickEvent(armorTab) {
				public void click() {
					System.out.println("Armor");
				}
			});
			
			Button weaponTab = new Button(null);
			weaponTab.setBounds(tabIconPos.getX(), armorTab.getBounds().getMaxY() + tabIconYOffset,
					tabIconSize.getWidth(), tabIconSize.getHeight());
			weaponTab.setIcon("screen ui/Tab Weapon");
			weaponTab.setBackground("screen ui/Tab Frame");
			weaponTab.setStill(true);
			weaponTab.addEvent(new ClickEvent(weaponTab) {
				public void click() {
					System.out.println("Weapon");
				}
			});
			
			Button offhandTab = new Button(null);
			offhandTab.setBounds(tabIconPos.getX(), weaponTab.getBounds().getMaxY() + tabIconYOffset,
					tabIconSize.getWidth(), tabIconSize.getHeight());
			offhandTab.setIcon("screen ui/Tab Offhand");
			offhandTab.setBackground("screen ui/Tab Frame");
			offhandTab.setStill(true);
			offhandTab.addEvent(new ClickEvent(offhandTab) {
				public void click() {
					System.out.println("Offhand");
				}
			});
			
			Button inventoryTab = new Button(null);
			inventoryTab.setBounds(tabIconPos.getX(), offhandTab.getBounds().getMaxY() + tabIconYOffset, 
					tabIconSize.getWidth(), tabIconSize.getHeight());
			inventoryTab.setIcon("screen ui/Tab Inventory");
			inventoryTab.setBackground("screen ui/Tab Frame");
			inventoryTab.setStill(true);
			inventoryTab.addEvent(new ClickEvent(inventoryTab) {
				public void click() {
					System.out.println("Pocket");
				}
			});
			
			Button optionsTab = new Button(null);
			optionsTab.setBounds(tabIconPos.getX(), inventoryTab.getBounds().getMaxY() + tabIconYOffset,
					tabIconSize.getWidth(), tabIconSize.getHeight());
			optionsTab.setIcon("screen ui/Tab Options");
			optionsTab.setBackground("screen ui/Tab Frame");
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
			inventoryTab.setSurrounding(0, offhandTab);
			add(inventoryTab);
			optionsTab.setSurrounding(0, inventoryTab);
			add(optionsTab);
			
			setKeyboardNavigable(true, armorTab);
			addFrame(null);
			
			setSelectionPointer("screen ui/Pointer");
		}
	}
	
}
