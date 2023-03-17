package com.mini.chest;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.lwjgl.glfw.GLFW;

public class Macro {
    private boolean isRunningMacro = false;
    private boolean temp = false;
    private static KeyBinding keyBinding;

    public Macro() {
        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Start Trading",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_P,
                "Macro Mod"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                isRunningMacro = !isRunningMacro;
                temp = false;
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (isRunningMacro) completeTrade();
        });
    }

    private void completeTrade() {
        // Completes one set of pearl trades
        assert MinecraftClient.getInstance().player != null;
        ScreenHandler screenHandler = MinecraftClient.getInstance().player.currentScreenHandler;
        if (!(screenHandler instanceof GenericContainerScreenHandler)) return;

        if (!temp) {
            // Open pearl section
            slotClick(screenHandler, 13, ClickType.LEFT_CLICK);
            temp = true;
        }

        // Buy pearls
        slotClick(screenHandler, 4, ClickType.LEFT_CLICK);
        slotClick(screenHandler, 22, ClickType.LEFT_CLICK);
        slotClick(screenHandler, 25, ClickType.LEFT_CLICK);
        slotClick(screenHandler, 13, ClickType.LEFT_CLICK);

        // Navigate back
        slotClick(screenHandler, 44, ClickType.LEFT_CLICK);
        slotClick(screenHandler, 35, ClickType.LEFT_CLICK);

        // Sell pearls
        slotClick(screenHandler, 4, ClickType.RIGHT_CLICK);
        slotClick(screenHandler, 22, ClickType.LEFT_CLICK);

        // Navigate back
        slotClick(screenHandler, 35, ClickType.LEFT_CLICK);
    }

    private void slotClick(ScreenHandler screenHandler, int slotIndex, int clickType) {
        int syncId = screenHandler.syncId;
        int slotId = screenHandler.slots.get(slotIndex).id;

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        ItemStack itemStack = screenHandler.getSlot(slotIndex).getStack();

        Int2ObjectMap<ItemStack> modifiedStacks = new Int2ObjectOpenHashMap<>();
        modifiedStacks.put(slotIndex, itemStack);

        assert networkHandler != null;
        networkHandler.sendPacket(new ClickSlotC2SPacket(syncId, screenHandler.getRevision(), slotId, clickType, SlotActionType.PICKUP, itemStack, modifiedStacks));
    }

}
