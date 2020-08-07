
package com.bacter.demonic.wfwt;

class StationInfo
{
    final String name;
    final String addr;
    final int transmission;
    final long ping;
    final ChannelSession channelSession;

    StationInfo(String name, String addr, int transmission, long ping, ChannelSession channelSession)
    {
        this.name = name;
        this.addr = addr;
        this.transmission = transmission;
        this.ping = ping;
        this.channelSession = channelSession;
    }
}
