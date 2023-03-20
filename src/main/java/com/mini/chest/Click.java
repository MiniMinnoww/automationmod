package com.mini.chest;

public class Click {
    public int slot;
    public int type;

    public Click(int _slot, int clickType) {
        slot = _slot;
        type = clickType;
    }

    public Click(int _slot) {
        slot = _slot;
        type = ClickType.LEFT_CLICK;
    }
}
