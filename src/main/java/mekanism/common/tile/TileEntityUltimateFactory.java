package mekanism.common.tile;

import mekanism.api.EnumColor;
import mekanism.api.transmitters.TransmissionType;
import mekanism.common.SideData;
import mekanism.common.Tier.FactoryTier;
import mekanism.common.block.states.BlockStateMachine;
import mekanism.common.tile.component.TileComponentConfig;
import mekanism.common.tile.component.TileComponentEjector;
import mekanism.common.util.InventoryUtils;

public class TileEntityUltimateFactory extends TileEntityFactory {

    private int i;
    private int tempToStop = 300;
    private static int maxTemperatureRise = 9;

    public TileEntityUltimateFactory() {
        super(FactoryTier.ULTIMATE, BlockStateMachine.MachineType.ULTIMATE_FACTORY);

        configComponent = new TileComponentConfig(this, TransmissionType.ITEM, TransmissionType.ENERGY, TransmissionType.GAS);

        configComponent.addOutput(TransmissionType.ITEM, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Input", EnumColor.DARK_RED, new int[]{5, 6, 7, 8, 9, 10, 11, 12, 13}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Output", EnumColor.DARK_BLUE, new int[]{14, 15, 16, 17, 18, 19, 20, 21, 22}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Energy", EnumColor.DARK_GREEN, new int[]{1}));
        configComponent.addOutput(TransmissionType.ITEM, new SideData("Extra", EnumColor.PURPLE, new int[]{4}));
        configComponent.setConfig(TransmissionType.ITEM, new byte[]{4, 0, 0, 3, 1, 2});

        configComponent.addOutput(TransmissionType.GAS, new SideData("None", EnumColor.GREY, InventoryUtils.EMPTY));
        configComponent.addOutput(TransmissionType.GAS, new SideData("Gas", EnumColor.DARK_RED, new int[]{0}));
        configComponent.fillConfig(TransmissionType.GAS, 1);
        configComponent.setCanEject(TransmissionType.GAS, false);

        configComponent.setInputConfig(TransmissionType.ENERGY);

        ejectorComponent = new TileComponentEjector(this);
        ejectorComponent.setOutputData(TransmissionType.ITEM, configComponent.getOutputs(TransmissionType.ITEM).get(2));
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (getActive()) {
            if (super.aoCoolant > 0) {
                if (i % 8 == 0) {
                    tempToStop += (super.getActiveFieldsNumber() * maxTemperatureRise);
                    if (super.temperature < tempToStop) {
                        super.temperature++;
                        super.aoCoolant -= 10;
                    } else if(i % 16 == 0) {
                        super.temperature -= 1;
                    }
                }
            } else {
                if (i % 4 == 0) {
                    tempToStop += (super.getActiveFieldsNumber() * 3 * maxTemperatureRise);
                    if (super.temperature < tempToStop)
                        super.temperature++;
                }
            }
        } else {
            if (i % 16 == 0) {
                if (super.temperature > 23) {
                    super.temperature -= 3;
                }
            }
        }

        if (super.temperature >= maxTemperature) {
            if(!world.isRemote)
            {
            	world.setBlockToAir(pos);
                detonate();
            }
            else {
                if(wasDetonated)
                {
                	world.setBlockToAir(pos);
                }
            }
        }

        i++;
    }
}
