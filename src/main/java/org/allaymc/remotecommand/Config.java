package org.allaymc.remotecommand;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
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
    @Comment("The token used to verify the request. Do not share it to other!")
    private String token = RandomStringUtils.randomAlphanumeric(32);
    @Comment("The port of the http server")
    @CustomKey("http-server-port")
    private int httpServerPort = 19133;
}
