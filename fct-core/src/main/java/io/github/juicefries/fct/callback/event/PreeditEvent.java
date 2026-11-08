package io.github.juicefries.fct.callback.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWPreeditCallback;
import org.lwjgl.system.Callback;

/**
 * 输入法组合文本事件。
 *
 * <p>preedit 文本以 UTF-32 码点给出，这里已经解码成 {@link String}；块划分与光标位置保留码点下标。</p>
 *
 * @since 1.0.7
 */
public final class PreeditEvent extends CallbackEventData {

    public final static String PREEDIT_EVENT = registerEventType("Preedit");

    private String text = "";
    private int[] blockSizes = new int[0];
    private int focusedBlock = -1;
    private int caret = 0;

    private PreeditEvent() {

    }

    /**
     * @return 组合中的文本（空串表示组合结束）
     */
    public @NotNull String text() {
        return text;
    }

    /**
     * @return 每个组合块的码点数
     */
    public int[] blockSizes() {
        return blockSizes.clone();
    }

    /**
     * @return 当前聚焦块下标，-1 表示无
     */
    public int focusedBlock() {
        return focusedBlock;
    }

    /**
     * @return 光标位置（码点下标）
     */
    public int caret() {
        return caret;
    }

    public static @NotNull PreeditEvent preedit(long window, String text,
                                                int[] blockSizes, int focusedBlock, int caret) {
        var e = new PreeditEvent();
        e.window = window;
        e.type = PREEDIT_EVENT;
        e.text = text == null ? "" : text;
        e.blockSizes = blockSizes == null ? new int[0] : blockSizes.clone();
        e.focusedBlock = focusedBlock;
        e.caret = caret;
        return e;
    }

    public static @NotNull PreeditEvent preedit(String text,
                                                int[] blockSizes, int focusedBlock, int caret) {
        return preedit(NULL_WINDOW, text, blockSizes, focusedBlock, caret);
    }

    @Override
    public @NotNull PreeditEvent copy() {
        return preedit(window(), text, blockSizes, focusedBlock, caret);
    }

    @Override
    @Contract(pure = true)
    public Class<? extends Callback> getRequiredType() {
        return GLFWPreeditCallback.class;
    }
}
