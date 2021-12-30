package me.htrewrite.client.capes.obj;

import me.htrewrite.client.util.PostRequest;
import net.minecraft.util.ResourceLocation;

import java.io.File;

public class CapeObj {
    public final int id;
    public final String name, url, file_name;
    public final ResourceLocation resourceLocation;

    public CapeObj(int id, String name, String url) {
        this.id = id;
        this.name = name;
        this.url = url;

        String[] urlSplit = url.split("/");
        file_name = urlSplit[urlSplit.length-1];

        resourceLocation = new ResourceLocation("htrewrite", "capes/"+file_name);
    }
}