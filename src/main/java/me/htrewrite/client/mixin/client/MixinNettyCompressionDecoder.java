package me.htrewrite.client.mixin.client;

import me.htrewrite.client.module.modules.exploit.AntiChunkBanModule;
import net.minecraft.network.NettyCompressionDecoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NettyCompressionDecoder.class)
public class MixinNettyCompressionDecoder {
    @ModifyConstant(method = "decode", constant = {@Constant(intValue=0x200000)})
    private int decodeConstant(int old) {
        return AntiChunkBanModule.max_packet_size;
    }
}