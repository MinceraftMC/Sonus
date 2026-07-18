package dev.minceraft.sonus.svc.adapter.pipeline;

import dev.minceraft.sonus.svc.adapter.SvcUdpPipelineNode;
import dev.minceraft.sonus.svc.adapter.connection.SvcConnection;
import dev.minceraft.sonus.svc.protocol.SvcUdpMagicCodec;
import dev.minceraft.sonus.svc.protocol.version.VersionedCipher;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@NullMarked
public class SvcPlayerCipherCodec extends SvcUdpPipelineNode<ByteBuf, ByteBuf> {

    public static final int SECRET_SIZE_BYTES = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    private final SvcConnection connection;
    private final SecretKeySpec key;

    private final int ivSize;

    private final Cipher encodeCipher;

    private final Cipher decodeCipher;
    private byte @Nullable [] lastDecodeIv;

    public SvcPlayerCipherCodec(SvcConnection connection, SvcUdpMagicCodec svcCodec, UUID secret) {
        super(svcCodec);
        this.connection = connection;
        this.ivSize = VersionedCipher.getIvSize(connection.getVersion());
        try {
            this.key = createKeySpec(secret);
            this.encodeCipher = VersionedCipher.createCipher(connection.getVersion());
            this.decodeCipher = VersionedCipher.createCipher(connection.getVersion());
        } catch (GeneralSecurityException exception) {
            throw new RuntimeException(exception);
        }
    }

    private static SecretKeySpec createKeySpec(UUID secret) {
        byte[] key = ByteBuffer.wrap(new byte[SECRET_SIZE_BYTES])
                .putLong(secret.getMostSignificantBits())
                .putLong(secret.getLeastSignificantBits())
                .array();
        return new SecretKeySpec(key, "AES");
    }

    private byte[] generateIV() {
        byte[] iv = new byte[this.ivSize];
        RANDOM.nextBytes(iv);
        return iv;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) throws Exception {
        try {
            byte[] unciphered = new byte[msg.readableBytes()];
            msg.readBytes(unciphered);
            byte[] iv = this.generateIV();
            VersionedCipher.initCipher(this.connection.getVersion(), this.encodeCipher, Cipher.ENCRYPT_MODE, this.key, iv);
            byte[] ciphered = this.encodeCipher.doFinal(unciphered);

            out.add(Unpooled.compositeBuffer(2)
                    .addComponent(true, Unpooled.wrappedBuffer(iv))
                    .addComponent(true, Unpooled.wrappedBuffer(ciphered)));
        } finally {
            msg.release();
        }
    }

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out, SvcUdpContext svcCtx) throws Exception {
        try {
            byte[] iv = new byte[this.ivSize];
            msg.readBytes(iv);
            // if the IV hasn't changed, retain old decode cipher
            if (this.lastDecodeIv == null || !Arrays.equals(this.lastDecodeIv, iv)) {
                VersionedCipher.initCipher(this.connection.getVersion(), this.decodeCipher, Cipher.DECRYPT_MODE, this.key, iv);
                this.lastDecodeIv = iv;
            }

            byte[] ciphered = new byte[msg.readableBytes()];
            msg.readBytes(ciphered);
            byte[] unciphered = this.decodeCipher.doFinal(ciphered);
            out.add(Unpooled.wrappedBuffer(unciphered));
        } finally {
            msg.release();
        }
    }
}
