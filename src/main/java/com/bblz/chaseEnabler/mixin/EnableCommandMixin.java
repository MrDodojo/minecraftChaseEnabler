package com.bblz.chaseEnabler.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.bblz.chaseEnabler.ChaseCommand;
// import net.minecraft.server.command.ChaseCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommandManager.class)
public class EnableCommandMixin {
	@Shadow
	@Final
	public CommandDispatcher<ServerCommandSource> dispatcher;

	@Inject(at = @At("RETURN"), method = "<init>")
	private void init(CallbackInfo info) {
		ChaseCommand.register(dispatcher);
	}
}