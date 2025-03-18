package net.cebularz.winterwonders.spells;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpellScheduler {
    private final List<SpellScheduler.ScheduledSpellCast> spellEffect = new CopyOnWriteArrayList<>();

    public void schedule(long delayMillis, Runnable spawnTask) {
        long scheduledTime = System.currentTimeMillis() + delayMillis;
        spellEffect.add(new SpellScheduler.ScheduledSpellCast(scheduledTime, spawnTask));
    }

    public void tick() {
        long now = System.currentTimeMillis();
        List<SpellScheduler.ScheduledSpellCast> scheduledSpellEffects = new ArrayList<>(spellEffect);
        if (!scheduledSpellEffects.isEmpty()) {
            for (SpellScheduler.ScheduledSpellCast spellCast : scheduledSpellEffects) {
                if (spellCast.scheduledTime <= now) {
                    spellCast.castTask.run();
                    spellEffect.remove(spellCast);
                }
            }
        }
    }

    private record ScheduledSpellCast(long scheduledTime, Runnable castTask){}
}
