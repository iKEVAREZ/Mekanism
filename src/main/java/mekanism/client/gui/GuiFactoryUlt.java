package mekanism.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mekanism.api.Coord4D;
import mekanism.client.gui.element.GuiEnergyInfo;
import mekanism.client.gui.element.GuiRecipeTypeUlt;
import mekanism.client.gui.element.GuiRedstoneControlUlt;
import mekanism.client.gui.element.GuiSecurityTabUlt;
import mekanism.client.gui.element.GuiSideConfigurationTab;
import mekanism.client.gui.element.GuiSortingTab;
import mekanism.client.gui.element.GuiTransporterConfigTab;
import mekanism.client.gui.element.GuiUpgradeTabUlt;
import mekanism.client.render.MekanismRenderer;
import mekanism.client.sound.SoundHandler;
import mekanism.common.Mekanism;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.base.IFactory.MachineFuelType;
import mekanism.common.base.IFactory.RecipeType;
import mekanism.common.base.TileNetworkList;
import mekanism.common.inventory.container.ContainerFactoryUlt;
import mekanism.common.item.ItemGaugeDropper;
import mekanism.common.network.PacketTileEntity.TileEntityMessage;
import mekanism.common.tile.TileEntityFactory;
import mekanism.common.util.LangUtils;
import mekanism.common.util.ListUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiFactoryUlt extends GuiMekanism
{
	public TileEntityFactory tileEntity;

	public GuiFactoryUlt(InventoryPlayer inventory, TileEntityFactory tentity)
	{
		super(tentity, new ContainerFactoryUlt(inventory, tentity));
		tileEntity = tentity;

		this.ySize += 37;
		this.xSize += 38;

		guiElements.add(new GuiRedstoneControlUlt(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiSecurityTabUlt(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiUpgradeTabUlt(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiRecipeTypeUlt(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiSideConfigurationTab(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiTransporterConfigTab(this, 34, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiSortingTab(this, tileEntity, tileEntity.tier.guiLocation));
		guiElements.add(new GuiEnergyInfo(() ->
        {
            String multiplier = MekanismUtils.getEnergyDisplay(tileEntity.lastUsage);
            return ListUtils.asList(LangUtils.localize("gui.using") + ": " + multiplier + "/t", LangUtils.localize("gui.needed") + ": " + MekanismUtils.getEnergyDisplay(tileEntity.getMaxEnergy()-tileEntity.getEnergy()));
        }, this, tileEntity.tier.guiLocation));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		int xAxis = (mouseX - (width - xSize) / 2);
		int yAxis = (mouseY - (height - ySize) / 2);

		fontRenderer.drawString(tileEntity.getName(), (xSize/2)-(fontRenderer.getStringWidth(tileEntity.getName())/2), 3, 0x404040);
		fontRenderer.drawString(LangUtils.localize("container.inventory"), 8, (ySize - 120) + 2, 0x404040);

		if(xAxis >= 181 && xAxis <= 189 && yAxis >= 79 && yAxis <= 165) {
			List<String> text = new ArrayList<String>(Arrays.asList("Current Temp: "+tileEntity.temperature+"℃","Max Temp: "+tileEntity.maxTemperature+"℃"));
			drawHoveringText(text, xAxis, yAxis);
		}

		if(xAxis >= 173 && xAxis <= 175 && yAxis >= 79 && yAxis <= 165) {
			List<String> text = new ArrayList<String>(Arrays.asList("Coolant: "+tileEntity.aoCoolant+"/"+tileEntity.maxCoolant+"mB"));
			drawHoveringText(text, xAxis, yAxis);
		}

		if(xAxis >= 202 && xAxis <= 206 && yAxis >= 17 && yAxis <= 69)
		{
			drawHoveringText(MekanismUtils.getEnergyDisplay(tileEntity.getEnergy(), tileEntity.getMaxEnergy()), xAxis, yAxis);
		}

		if(xAxis >= 8 && xAxis <= 168 && yAxis >= 78 && yAxis <= 83)
		{
			if(tileEntity.recipeType.getFuelType() == MachineFuelType.ADVANCED)
			{
				drawHoveringText(tileEntity.gasTank.getGas() != null ? tileEntity.gasTank.getGas().getGas().getLocalizedName() + ": " + tileEntity.gasTank.getStored() : LangUtils.localize("gui.none"), xAxis, yAxis);
			}
			else if(tileEntity.recipeType == RecipeType.INFUSING)
			{
				drawHoveringText(tileEntity.infuseStored.type != null ? tileEntity.infuseStored.type.getLocalizedName() + ": " + tileEntity.infuseStored.amount : LangUtils.localize("gui.empty"), xAxis, yAxis);
			}
		}

		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int mouseX, int mouseY)
	{
		mc.renderEngine.bindTexture(tileEntity.tier.guiLocation);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;
		drawTexturedModalRect(guiWidth, guiHeight, 0, 0, xSize, ySize);

		int xAxis = mouseX - guiWidth;
		int yAxis = mouseY - guiHeight;

		int displayInt;

		displayInt = tileEntity.getScaledEnergyLevel(52);
		drawTexturedModalRect(guiWidth + 203, guiHeight + 17 + 52 - displayInt, 214, 52 - displayInt, 4, displayInt);

		int displayTemp;
		displayTemp = tileEntity.getScaled(87, tileEntity.temperature, tileEntity.maxTemperature);
		drawTexturedModalRect(guiWidth + 181, guiHeight + 166 - displayTemp, 214, 166 - displayTemp, 9, displayTemp);

		int displayCoolant;
		displayCoolant = tileEntity.getScaled(87, tileEntity.aoCoolant, tileEntity.maxCoolant);
		drawTexturedModalRect(guiWidth + 173, guiHeight + 166 - displayCoolant, 223, 166 - displayCoolant, 3, displayCoolant);

		int xOffset = tileEntity.tier == FactoryTier.BASIC ? 59 : (tileEntity.tier == FactoryTier.ADVANCED ? 
			39 : 33);
		int xDistance = tileEntity.tier == FactoryTier.BASIC ? 38 : (tileEntity.tier == FactoryTier.ADVANCED ? 
			26 : 19);
		
		for(int i = 0; i < tileEntity.tier.processes; i++)
		{
			int xPos = xOffset + (i*xDistance);

			displayInt = tileEntity.getScaledProgress(20, i);
			drawTexturedModalRect(guiWidth + xPos, guiHeight + 33, 214, 52, 8, displayInt);
		}

		if(tileEntity.recipeType.getFuelType() == MachineFuelType.ADVANCED)
		{
			if(tileEntity.getScaledGasLevel(160) > 0)
			{
				displayGauge(8, 78, tileEntity.getScaledGasLevel(160), 5, tileEntity.gasTank.getGas().getGas().getSprite());
			}
		}
		else if(tileEntity.recipeType == RecipeType.INFUSING)
		{
			if(tileEntity.getScaledInfuseLevel(160) > 0)
			{
				displayGauge(8, 78, tileEntity.getScaledInfuseLevel(160), 5, tileEntity.infuseStored.type.sprite);
			}
		}
		
		super.drawGuiContainerBackgroundLayer(partialTick, mouseX, mouseY);
	}

	public void displayGauge(int xPos, int yPos, int sizeX, int sizeY, TextureAtlasSprite icon)
	{
		if(icon == null)
		{
			return;
		}

		int guiWidth = (width - xSize) / 2;
		int guiHeight = (height - ySize) / 2;

		mc.renderEngine.bindTexture(MekanismRenderer.getBlocksTexture());
		drawTexturedModalRect(guiWidth + xPos, guiHeight + yPos, icon, sizeX, sizeY);
	}
	
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException
	{
		super.mouseClicked(x, y, button);

		if(button == 0 || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			int xAxis = (x - (width - xSize) / 2);
			int yAxis = (y - (height - ySize) / 2);

			if(xAxis > 8 && xAxis < 168 && yAxis > 78 && yAxis < 83)
			{
				ItemStack stack = mc.player.inventory.getItemStack();
				
				if(!stack.isEmpty() && stack.getItem() instanceof ItemGaugeDropper)
				{
					TileNetworkList data = TileNetworkList.withContents(1);
	
					Mekanism.packetHandler.sendToServer(new TileEntityMessage(Coord4D.get(tileEntity), data));
					SoundHandler.playSound(SoundEvents.UI_BUTTON_CLICK);
				}
			}
		}
	}
}