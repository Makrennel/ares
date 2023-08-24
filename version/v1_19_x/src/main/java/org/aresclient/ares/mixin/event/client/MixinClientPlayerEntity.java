package org.aresclient.ares.mixin.event.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.ClientPlayerTickable;
import org.aresclient.ares.api.Ares;
import org.aresclient.ares.api.event.AresEvent;
import org.aresclient.ares.api.event.client.SendMovementPacketsEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = ClientPlayerEntity.class, priority = Integer.MAX_VALUE)
public class MixinClientPlayerEntity {
    @Shadow @Final private List<ClientPlayerTickable> tickables;

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", shift = At.Shift.BEFORE))
    public void beforeSendMovementPackets(CallbackInfo ci) {
        // Post and if not cancelled return and let method go on as usual
        if(!Ares.getEventManager().post(new SendMovementPacketsEvent(AresEvent.Era.BEFORE)).isCancelled()) return;
        Ares.getEventManager().post(new SendMovementPacketsEvent(AresEvent.Era.AFTER));

        // Return method before sendMovementPackets can execute
        ci.cancel();

        // Rest of tick method
        for(ClientPlayerTickable clientPlayerTickable: tickables) {
            clientPlayerTickable.tick();
        }
    }

    // If event is not cancelled
    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;sendMovementPackets()V", shift = At.Shift.AFTER))
    public void afterSendMovementPackets(CallbackInfo ci) {
        Ares.getEventManager().post(new SendMovementPacketsEvent(AresEvent.Era.AFTER));
    }
}
