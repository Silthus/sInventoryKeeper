package net.silthus.inventorykeeper.util;

import de.exlll.configlib.configs.yaml.YamlConfiguration;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

public class ConfigUtil {

    @SafeVarargs
    public static <TConfig extends YamlConfiguration>
    void loadRecursiveConfigs(
            JavaPlugin plugin,
            String path,
            Class<TConfig> configClass,
            ConfigLoader<TConfig>... loaders) {

        File dir = new File(plugin.getDataFolder(), path);
        dir.mkdirs();
        loadConfigs(dir, "", configClass, Arrays.asList(loaders));
    }

    @SafeVarargs
    public static <TConfig extends YamlConfiguration>
    void loadRecursiveConfigs(
            Path path, Class<TConfig> configClass, ConfigLoader<TConfig>... configLoaders) {

        File dir = path.toFile();
        dir.mkdirs();
        loadConfigs(dir, "", configClass, Arrays.asList(configLoaders));
    }

    private static <TConfig extends YamlConfiguration>
    void loadConfigs(
            File baseFolder,
            String path,
            Class<TConfig> configClass,
            Collection<ConfigLoader<TConfig>> loaders) {

        for (File file : Objects.requireNonNull(baseFolder.listFiles())) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                loadConfigs(file, path + "." + fileName.toLowerCase(), configClass, loaders);
            } else {
                path = StringUtils.strip(path, ".");
                for (ConfigLoader<TConfig> loader : loaders) {
                    try {
                        if (loader instanceof ConfiguredConfigLoader) {
                            ConfiguredConfigLoader configuredConfigLoader = (ConfiguredConfigLoader) loader;
                            if (!configuredConfigLoader.matches(file)) continue;
                            configuredConfigLoader.setPath(path);
                            String id =
                                    path
                                            + "."
                                            + file.getName()
                                            .toLowerCase()
                                            .replace(configuredConfigLoader.getSuffix(), "");
                            id = StringUtils.strip(id, ".");

                            TConfig config = configClass.getDeclaredConstructor(Path.class).newInstance(file.toPath());
                            config.load();
                            loader.loadConfig(
                                    id,
                                    file,
                                    config);
                        } else {
                            String id = path + "." + file.getName().toLowerCase().replace(".yaml", "").replace(".yml", "");
                            id = StringUtils.strip(id, ".");
                            TConfig config = configClass.getDeclaredConstructor(Path.class).newInstance(file.toPath());
                            config.load();
                            loader.loadConfig(
                                    id,
                                    file,
                                    config);
                        }
                    } catch (InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
