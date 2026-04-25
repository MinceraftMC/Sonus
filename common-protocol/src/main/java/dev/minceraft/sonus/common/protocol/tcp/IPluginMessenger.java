package dev.minceraft.sonus.common.protocol.tcp;

public interface IPluginMessenger {

    void registerCodec(AbstractPluginMessageCodec codec);
}
