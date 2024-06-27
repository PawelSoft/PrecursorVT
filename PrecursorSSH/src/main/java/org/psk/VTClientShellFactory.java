package org.psk;

import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

public class VTClientShellFactory implements ShellFactory {
    /**
     * Tworzy nową sesję SSH.
     * @param channel Kanał ChannelSession.
     * @return Nowy obiekt <i>VT100SSHClientHandler</i>.
     */
    @Override
    public Command createShell(ChannelSession channel) {
        return new ClientHandler();
    }
}