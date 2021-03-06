package dev.tigr.ares.fabric.gui;

import dev.tigr.ares.core.feature.Command;
import dev.tigr.ares.core.util.global.ReflectionHelper;
import dev.tigr.ares.core.util.render.Color;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.CommandSuggestor;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;

/**
 * @author Tigermouthbear 10/16/20
 */
public class AresChatGUI extends ChatScreen {
    private final Field commandSuggestorField;

    public AresChatGUI(String prefix) {
        super(prefix);

        commandSuggestorField = ReflectionHelper.getField(ChatScreen.class, "commandSuggestor", "field_21616");
        commandSuggestorField.setAccessible(true);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(chatField.getText().startsWith(Command.PREFIX.getValue())) {
            boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
            boolean texture2d = GL11.glIsEnabled(GL11.GL_TEXTURE_2D);

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(Color.ARES_RED.getRed(), Color.ARES_RED.getGreen(), Color.ARES_RED.getBlue(), Color.ARES_RED.getAlpha());
            GL11.glLineWidth(2);
            GL11.glBegin(GL11.GL_LINES);
            {
                // top
                GL11.glVertex2d(2, height - 14);
                GL11.glVertex2d(width, height - 14);

                // bottom
                GL11.glVertex2d(2, height - 2);
                GL11.glVertex2d(width, height - 2);

                // left
                GL11.glVertex2d(2, height - 14);
                GL11.glVertex2d(2, height - 2);

                // right
                GL11.glVertex2d(width, height - 14);
                GL11.glVertex2d(width, height - 2);

            }
            GL11.glEnd();

            if(blend) GL11.glEnable(GL11.GL_BLEND);
            if(texture2d) GL11.glEnable(GL11.GL_TEXTURE_2D);
        }

        this.setFocused(this.chatField);
        this.chatField.setSelected(true);
        fill(matrixStack, 2, this.height - 14, this.width - 2, this.height - 2, client.options.getTextBackgroundColor(Integer.MIN_VALUE));
        this.chatField.render(matrixStack, mouseX, mouseY, partialTicks);

        if(chatField.getText().startsWith(Command.PREFIX.getValue()))
            textRenderer.drawWithShadow(matrixStack, Command.complete(chatField.getText()), 4 + textRenderer.getWidth(chatField.getText()), chatField.y, 7368816);

        try {
            ((CommandSuggestor)commandSuggestorField.get(this)).render(matrixStack, mouseX, mouseY);
        } catch(IllegalAccessException e) {
            e.printStackTrace();
        }

        Style style = this.client.inGameHud.getChatHud().getText(mouseX, mouseY);
        if (style != null && style.getHoverEvent() != null) {
            this.renderTextHoverEffect(matrixStack, style, mouseX, mouseY);
        }
    }

    @Override
    public boolean keyPressed(int keycode, int j, int k) {
        if(keycode == GLFW.GLFW_KEY_TAB && !chatField.getText().contains(" ")) {
            String noPrefix = chatField.getText().replaceFirst(Command.PREFIX.getValue(), "");
            String completed = Command.completeName(noPrefix);
            chatField.setText(chatField.getText() + completed.replaceFirst(noPrefix, ""));
        }

        return super.keyPressed(keycode, j, k);
    }
}
