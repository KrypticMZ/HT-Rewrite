package me.htrewrite.client.event.custom;

import me.htrewrite.client.Wrapper;
import me.zero.alpine.fork.event.type.Cancellable;

public class CustomEvent extends Cancellable {
    private float partialTicks = Wrapper.getMC().getRenderPartialTicks();
    private Era era = Era.PRE;

    public CustomEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
    public CustomEvent() {}

    public Era getEra() { return era; }
    public void setEra(Era era) { this.era = era; }
    public float getPartialTicks() { return partialTicks; }

    public enum Era {
        PRE, POST
    }
}