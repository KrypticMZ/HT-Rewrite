package me.htrewrite.client.module.modules.gui.hud.component.components.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil<T> {
    public List<T> copyArray(List<T> list) {
        List<T> newArray = new ArrayList<>();
        for(T element : list)
            newArray.add(element);

        return newArray;
    }
}