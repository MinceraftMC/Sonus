package dev.minceraft.sonus.plasmo.adapter;

import dev.minceraft.sonus.service.data.ISonusPlayer;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PlasmoTranslationHolder {

    private final Set<String> translationKeys = ConcurrentHashMap.newKeySet();

    public PlasmoTranslationHolder() {

    }

    public String registerTranslationKey(String key) {
        this.translationKeys.add(key);
        return key;
    }

    public Map<String, String> getTranslations(ISonusPlayer player, Locale locale) {
        Map<String, String> result = new HashMap<>(this.translationKeys.size());
        for (String translationKey : this.translationKeys) {
            String rendered = player.renderPlainComponent(Component.translatable(translationKey), locale);
            result.put(translationKey, rendered);
        }
        return result;
    }
}
