package ca.lukegrahamlandry.eternalstructures.client.gui;

import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;

import java.util.function.BiConsumer;

public class JsonConfigGui<T> extends Screen {
    String json;
    TextFieldWidget input;
    Button save;
    BlockPos pos;
    Class<T> clazz;
    BiConsumer<BlockPos, T> sync;
    String[] lines = new String[0];

    public JsonConfigGui(BlockPos pos, String json, Class<T> clazz, BiConsumer<BlockPos, T> sync, TextComponent title) {
        super(title);
        this.json = json;
        this.pos = pos;
        this.clazz = clazz;
        this.sync = sync;
    }

    @Override
    protected void init() {
        super.init();
        this.input = this.addButton(new TextFieldWidget(Minecraft.getInstance().font, 0, 0, this.width, 20, new StringTextComponent("Settings")));
        this.input.setMaxLength(OpenProtectionSettings.MAX_LEN);
        this.input.setValue(this.json);
        this.input.setResponder(this::onTextChanged);
        this.save = this.addButton(new Button(0, 30, 50, 20, new StringTextComponent("Save"), this::onClickSave));
        this.addButton(new Button(60, 30, 50, 20, new StringTextComponent("Copy"), this::onClickCopy));
        this.addButton(new Button(120, 30, 50, 20, new StringTextComponent("Paste"), this::onClickPaste));
        this.updateLines();
    }

    @Override
    public void render(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        super.render(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        int y = 60;
        for (String s : this.lines) {
            drawString(p_230430_1_, Minecraft.getInstance().font, s, 0, y, 16777215);
            y += 11;
        }
    }

    void updateLines(){
        this.lines = this.json.split("[\\r\\n]+");  // lets play does the magic regex i copy pasted from the internet work
    }

    private void onClickCopy(Button button) {
        Minecraft.getInstance().keyboardHandler.setClipboard(this.input.getValue());
    }

    private void onClickPaste(Button button) {
        this.input.setValue(Minecraft.getInstance().keyboardHandler.getClipboard());
    }

    private void onClickSave(Button button) {
        try {
            T data = JsonHelper.get().fromJson(this.input.getValue(), this.clazz);
            this.sync.accept(pos, data);
            this.onClose();
        } catch (JsonSyntaxException e){
            // Unreachable
        }
    }

    private void onTextChanged(String s) {
        try {
            T data = JsonHelper.get().fromJson(s, this.clazz);
            this.json = JsonHelper.get().toJson(data);
            this.updateLines();
            if (this.lines.length > 0) this.lines[0] = "{ // DONT FORGET TO CLICK SAVE!";
            this.save.active = true;
        } catch (JsonSyntaxException e){
            this.save.active = false;
            this.json = e.getMessage();
            this.updateLines();
        }
    }
}
