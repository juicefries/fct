import io.github.juicefries.fct.ALabel;
import io.github.juicefries.fct.App;
import io.github.juicefries.fct.Blank;
import io.github.juicefries.fct.Button;
import io.github.juicefries.fct.Color;
import io.github.juicefries.fct.Panel;
import io.github.juicefries.fct.UIManager;
import io.github.juicefries.fct.WindowConfig;
import io.github.juicefries.fct.layout.BoxLayout;
import io.github.juicefries.fct.lwjgl.Hint;

void main() {
    WindowConfig config = new WindowConfig();
    config.addHint(Hint.transparentFramebuffer(true));
    App.run(config,frame -> {
        frame.setTitle("cursor demo");
        frame.setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        frame.setBackground(Color.NONE);

        // Control → 指向手
        var button = new Button("点我");
        button.setIdealSize(0, 56);
        button.setCursorName(UIManager.HAND_CURSOR);

        // Control → 文本 I 形
        var label = new ALabel("可以选中的文本");
        label.setIdealSize(0, 40);
        label.setCursorName(UIManager.TEXT_CURSOR);

        // Control（透明）→ 十字准星
        var area = new Blank();
        area.setIdealSize(0, 160);
        area.setCursorName(UIManager.CROSSHAIR_CURSOR);

        // Control → 禁止操作
        var disabled = new Button("不可点");
        disabled.setIdealSize(0, 56);
        disabled.setCursorName(UIManager.NOT_ALLOWED_CURSOR);
        disabled.setEnabled(false);
        var panel = new Panel();
        panel.setIdealSize(0,56);

        frame.add(button);
        frame.add(label);
        frame.add(area);
        frame.add(disabled);
        frame.add(panel);
    });
}
