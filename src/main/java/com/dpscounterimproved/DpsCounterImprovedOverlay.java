package com.dpscounterimproved;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.text.DecimalFormat;
import java.time.Duration;
import java.util.Map;
import javax.inject.Inject;

import com.dpscounterimproved.config.MeterDisplayMode;
import net.runelite.api.Client;
import static net.runelite.api.MenuAction.RUNELITE_OVERLAY;
import net.runelite.api.Player;
import net.runelite.client.party.PartyService;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.QuantityFormatter;

public class DpsCounterImprovedOverlay extends OverlayPanel {
    private static final DecimalFormat DPS_FORMAT = new DecimalFormat("#0.0");
    private static final int PANEL_WIDTH_OFFSET = 10; // assumes 8 for panel component border + 2px between left and right

    private final DpsCounterImprovedPlugin dpsCounterImprovedPlugin;
    private final DpsCounterImprovedConfig dpsCounterImprovedConfig;
    private final PartyService partyService;
    private final Client client;
    private final TooltipManager tooltipManager;

    @Inject
    DpsCounterImprovedOverlay(DpsCounterImprovedPlugin dpsCounterImprovedPlugin, DpsCounterImprovedConfig dpsCounterImprovedConfig, PartyService partyService, Client client, TooltipManager tooltipManager)
    {
        super(dpsCounterImprovedPlugin);
        this.dpsCounterImprovedPlugin = dpsCounterImprovedPlugin;
        this.dpsCounterImprovedConfig = dpsCounterImprovedConfig;
        this.partyService = partyService;
        this.client = client;
        this.tooltipManager = tooltipManager;
        addMenuEntry(RUNELITE_OVERLAY, "Reset", "DPS counter", e -> dpsCounterImprovedPlugin.reset());
        setPaused(false);
    }

    @Override
    public void onMouseOver()
    {
        DpsMember total = dpsCounterImprovedPlugin.getTotal();
        Duration elapsed = total.elapsed();
        long s = elapsed.getSeconds();
        String format;
        if (s >= 3600)
        {
            format = String.format("%d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
        }
        else
        {
            format = String.format("%d:%02d", s / 60, (s % 60));
        }
        tooltipManager.add(new Tooltip("Elapsed time: " + format));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<String, DpsMember> dpsMembers = dpsCounterImprovedPlugin.getMembers();
        if (dpsMembers.isEmpty())
        {
            return null;
        }

        boolean inParty = partyService.isInParty();
        MeterDisplayMode meterDisplayMode = dpsCounterImprovedConfig.meterDisplayMode();
        DpsMember total = dpsCounterImprovedPlugin.getTotal();
        boolean paused = total.isPaused();

        final String title = (inParty ? "Party " : "") + (meterDisplayMode) + (paused ? " (paused)" : "");
        panelComponent.getChildren().add(
                TitleComponent.builder()
                        .text(title)
                        .build());

        int maxWidth = ComponentConstants.STANDARD_WIDTH;
        FontMetrics fontMetrics = graphics.getFontMetrics();

        for (DpsMember dpsMember : dpsMembers.values())
        {
            String left = dpsMember.getName();
            String right;
            switch (meterDisplayMode) {
                case DPS:
                    right = QuantityFormatter.formatNumber(dpsMember.getDps());
                    break;
                case DPT:
                    right = QuantityFormatter.formatNumber(dpsMember.getDps() * .6);
                    break;
                case TOTAL_DAMAGE:
                    right = QuantityFormatter.formatNumber(dpsMember.getDamage());
                    break;
                case DAMAGE_TAKEN:
                    right =  "0.0";
                    break;
                default:
                    right = "Unknown Display Mode";
                    break;
                }

            maxWidth = Math.max(maxWidth, fontMetrics.stringWidth(left) + fontMetrics.stringWidth(right));
            panelComponent.getChildren().add(
                    LineComponent.builder()
                            .left(left)
                            .right(right)
                            .build());
        }

        panelComponent.setPreferredSize(new Dimension(maxWidth + PANEL_WIDTH_OFFSET, 0));

        if (!inParty)
        {
            Player player = client.getLocalPlayer();
            if (player.getName() != null)
            {
                DpsMember self = dpsMembers.get(player.getName());

                if (self != null && total.getDamage() > self.getDamage()) {
                    String right;
                    switch (meterDisplayMode) {
                        case DPS:
                            right = QuantityFormatter.formatNumber(self.getDps());
                            break;
                        case DPT:
                            right = QuantityFormatter.formatNumber(self.getDps() * .6);
                            break;
                        case TOTAL_DAMAGE:
                            right = QuantityFormatter.formatNumber(self.getDamage());
                            break;
                        case DAMAGE_TAKEN:
                            right =  "0.0";
                            break;
                        default:
                            right = "Unknown Display Mode";
                            break;
                    }

                    panelComponent.getChildren().add(
                            LineComponent.builder()
                                    .left(total.getName())
                                    .right(right)
                                    .build());
                }
            }
        }

        return super.render(graphics);
    }

    void setPaused(boolean paused)
    {
        removeMenuEntry(RUNELITE_OVERLAY, "Pause", "DPS counter");
        removeMenuEntry(RUNELITE_OVERLAY, "Unpause", "DPS counter");

        if (paused)
        {
            addMenuEntry(RUNELITE_OVERLAY, "Unpause", "DPS counter", e -> dpsCounterImprovedPlugin.unpause());
        }
        else
        {
            addMenuEntry(RUNELITE_OVERLAY, "Pause", "DPS counter", e -> dpsCounterImprovedPlugin.pause());
        }
    }
}

