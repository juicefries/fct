package io.github.juicefries.fct;

import io.github.juicefries.fct.event.SysEvent;
import io.github.juicefries.fct.event.SystemListener;
import io.github.juicefries.fct.event.SystemListener.SysListenerType;
import io.github.juicefries.fct.logging.LoggerFactory;
import io.github.juicefries.fct.sign.ApiSign;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.util.TreeSet;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiSign.InternalApi(since = "0.0.5")
@ApiStatus.Experimental
public class _DEBUG_API {

    private final static Logger logger = LoggerFactory.getLogger("fct-debug-api");

    public static @NotNull JPanel _get_debug_system_listener_list_panel() {
        if (Sys.isDebugApiWarning()) {
            logger.warn("Dangerous operation! The program is trying to call the debugger system listener panel!");
        }

        JPanel panel = new JPanel(new BorderLayout());

        DefaultListModel<SystemListener> model = new DefaultListModel<>();
        JList<SystemListener> list = new JList<>(model);
        SystemListener[] current = new SystemListener[1];

        list.setCellRenderer((_, value, _, sel, _) -> {
            JLabel label = new JLabel();
            SysListenerType t = value.getType();
            boolean pro = t == SysListenerType.Proactive;
            label.setText(String.format("%s [%s] %s  ←  %s",
                    pro ? "●" : "○", t,
                    value.getListenerName(),
                    value.getClass().getName()));
            label.setOpaque(sel);
            if (sel) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setForeground(pro ? new Color(0x2e7d32) : new Color(0x757575));
            }
            return label;
        });

        JTextArea detail = new JTextArea();
        detail.setEditable(false);
        detail.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JPopupMenu popup = new JPopupMenu();
        popup.add(item("发送 Init 事件",      () -> fire(current[0], SysEvent.init())));
        popup.add(item("发送 Terminate 事件", () -> fire(current[0], SysEvent.terminate())));
        popup.add(item("发送 Cancel 事件",    () -> fire(current[0], SysEvent.cancel())));
        popup.addSeparator();
        popup.add(item("移除（Sys.cancel）", () -> cancel(current[0])));
        popup.add(item("强制移除",            () -> forceRemove(current[0])));
        popup.addSeparator();
        popup.add(item("查看详情", () -> showDetail(current[0], detail)));

        list.addMouseListener(new MouseAdapter() {
            private void pop(@NotNull MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int i = list.locationToIndex(e.getPoint());
                    if (i >= 0) {
                        list.setSelectedIndex(i);
                        current[0] = list.getModel().getElementAt(i);
                        popup.show(list, e.getX(), e.getY());
                    }
                }
            }
            @Override public void mousePressed(MouseEvent e)  { pop(e); }
            @Override public void mouseReleased(MouseEvent e) { pop(e); }
        });

        list.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int i = list.locationToIndex(e.getPoint());
                if (i >= 0) {
                    showDetail(list.getModel().getElementAt(i), detail);
                }
            }
        });

        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.add(btn("刷新", () -> refresh(model, list)));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(list), new JScrollPane(detail));
        split.setResizeWeight(0.7);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);

        refresh(model, list);
        return panel;
    }

    public static @NotNull JPanel _get_debug_fct_icon_map_panel() {
        JPanel panel = new JPanel(new BorderLayout());

        var keys = new TreeSet<>(Toolkit.getIconKeys());

        JLabel info = new JLabel("默认图标共 " + keys.size() + " 个（只读，仅用于查看）");
        info.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        panel.add(info, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 4, 10, 10));
        grid.setBackground(Color.WHITE);
        grid.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        for (String key : keys) {
            grid.add(iconCell(key));
        }

        panel.add(new JScrollPane(grid), BorderLayout.CENTER);
        return panel;
    }






    // ========================= 内部工具 =========================

    private static void refresh(@NotNull DefaultListModel<SystemListener> model,
                                @NotNull JList<SystemListener> list) {
        int sel = list.getSelectedIndex();          // 记住选中
        var arr = Sys.listeners.getArray();
        model.clear();
        for (var l : arr) {
            if (l != null) model.addElement(l);
        }
        if (sel >= 0 && sel < model.size()) {        // 恢复选中
            list.setSelectedIndex(sel);
        }
    }

    private static void fire(SystemListener l, SysEvent e) {
        if (l != null) l.event(e);
    }

    private static void cancel(SystemListener l) {
        if (l == null) return;
        try {
            Sys.cancel(l);
        } catch (Exception e) {
            logger.error("cancel listener failed.", e);
        }
    }

    private static void forceRemove(SystemListener l) {
        if (l != null) Sys.listeners.remove(l);
    }

    private static void showDetail(SystemListener l, JTextArea detail) {
        if (l == null) return;
        //noinspection SpellCheckingInspection
        detail.setText(String.format(
                "监听器名称 : %s%n类型       : %s%n来源类     : %s%ngetSource  : %s%nhashCode   : %s",
                l.getListenerName(),
                l.getType(),
                l.getClass().getName(),
                l.getSource(),
                System.identityHashCode(l)
        ));
    }

    private static @NotNull JMenuItem item(String text, Runnable r) {
        JMenuItem mi = new JMenuItem(text);
        mi.addActionListener(_ -> r.run());
        return mi;
    }

    private static @NotNull JButton btn(@SuppressWarnings("SameParameterValue") String text, Runnable r) {
        JButton b = new JButton(text);
        b.addActionListener(_ -> r.run());
        return b;
    }

    private static @NotNull JPanel iconCell(String key) {
        JPanel cell = new JPanel(new BorderLayout(0, 4));
        cell.setBackground(Color.WHITE);
        cell.setBorder(BorderFactory.createLineBorder(new Color(0xdddddd)));

        JLabel icon = new JLabel();
        icon.setHorizontalAlignment(JLabel.CENTER);
        try {
            BufferedImage bi = ImageToolkit.toAwtImage(Toolkit.getIcon(key));
            icon.setIcon(new ImageIcon(bi));
        } catch (Exception e) {
            icon.setText("加载失败");
            icon.setToolTipText(e.toString());
        }
        cell.add(icon, BorderLayout.CENTER);

        JLabel name = new JLabel(key);
        name.setHorizontalAlignment(JLabel.CENTER);
        name.setFont(name.getFont().deriveFont(Font.PLAIN, 10f));
        name.setToolTipText(key);
        cell.add(name, BorderLayout.SOUTH);

        return cell;
    }

}
