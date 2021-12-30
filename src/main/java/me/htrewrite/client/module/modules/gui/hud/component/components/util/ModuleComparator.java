package me.htrewrite.client.module.modules.gui.hud.component.components.util;

import me.htrewrite.client.module.Module;

import java.util.Comparator;

public class ModuleComparator implements Comparator<Module> {
    public int compare(Module m1, Module m2) { return m2.getTotalModuleNameLength() - m1.getTotalModuleNameLength(); }
}