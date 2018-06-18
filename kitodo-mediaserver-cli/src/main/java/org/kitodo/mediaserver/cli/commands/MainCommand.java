/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * LICENSE file that was distributed with this source code.
 */

package org.kitodo.mediaserver.cli.commands;

import java.util.concurrent.Callable;
import org.springframework.stereotype.Component;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Main command.
 */
@Command(
    name = "kitodo-mediaserver-cli",
    description = "Kitodo Mediaserver CLI"
    )
@Component
public class MainCommand implements Callable {

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Display usage help.")
    private Boolean help = false;

    @Override
    public Object call() throws Exception {
        return null;
    }
}
