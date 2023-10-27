package ca.lukegrahamlandry.eternalstructures.client.gui;

import ca.lukegrahamlandry.eternalstructures.json.JsonHelper;
import ca.lukegrahamlandry.eternalstructures.network.NetworkHandler;
import ca.lukegrahamlandry.eternalstructures.network.clientbound.OpenProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.network.serverbound.SaveProtectionSettings;
import ca.lukegrahamlandry.eternalstructures.protect.ProtectionInstance;
import com.google.gson.JsonSyntaxException;
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
        this.input = this.addButton(new TextFieldWidget(Minecraft.getInstance().font, 0, 0, this.width, 200, new StringTextComponent("Settings")));
        this.input.setMaxLength(OpenProtectionSettings.MAX_LEN);
        this.input.setValue(this.json);
        this.input.setResponder(this::onTextChanged);
        this.save = this.addButton(new Button(50, 210, 150, 20, new StringTextComponent("Save"), this::onClickSave));
    }

    private void onClickSave(Button button) {
        try {
            T data = JsonHelper.get().fromJson(this.input.getValue(), this.clazz);
            this.sync.accept(pos, data);
            this.onClose();
        } catch (JsonSyntaxException e){
            // ignore
        }
    }

    private void onTextChanged(String s) {
        try {
            JsonHelper.get().fromJson(s, this.clazz);
            this.save.active = true;
        } catch (JsonSyntaxException e){
            this.save.active = false;
        }
    }
}
