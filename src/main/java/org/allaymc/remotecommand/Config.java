package org.allaymc.remotecommand;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.CustomKey;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author daoge_cmd
 */
@Getter
@Accessors(fluent = true)
public class Config extends OkaeriConfig {
    private String token = RandomStringUtils.randomAlphanumeric(32);
    @CustomKey("http-server-port")
    private int httpServerPort = 19133;
}
