package ca.lukegrahamlandry.eternalstructures.client.gui;

import ca.lukegrahamlandry.eternalstructures.ModMain;
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

public class ProtectionSettingsGui extends Screen {
    String json;
    TextFieldWidget input;
    Button save;
    BlockPos pos;
    public ProtectionSettingsGui(BlockPos pos, String json) {
        super(new StringTextComponent("Structure Protection Settings"));
        this.json = json;
        this.pos = pos;
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
            ProtectionInstance.Settings data = JsonHelper.get().fromJson(this.input.getValue(), ProtectionInstance.Settings.class);
            NetworkHandler.INSTANCE.sendToServer(new SaveProtectionSettings(pos, data));
            this.onClose();
        } catch (JsonSyntaxException e){
            // ignore
        }
    }

    private void onTextChanged(String s) {
        try {
            JsonHelper.get().fromJson(s, ProtectionInstance.Settings.class);
            this.save.active = true;
        } catch (JsonSyntaxException e){
            this.save.active = false;
        }
    }
}
