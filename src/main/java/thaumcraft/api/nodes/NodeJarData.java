package thaumcraft.api.nodes;

import java.util.Map;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import thaumcraft.api.aspects.AspectList;

public record NodeJarData(UUID nodeId, NodeType nodeType, NodeModifier nodeModifier, AspectList aspects,
        AspectList baseAspects) {
    public static final NodeJarData EMPTY = new NodeJarData(new UUID(0L, 0L), NodeType.NORMAL, null,
            AspectList.EMPTY, AspectList.EMPTY);

    public static final Codec<NodeJarData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("node_id").forGetter(NodeJarData::nodeId),
            Codec.INT.fieldOf("type").forGetter(data -> data.nodeType.ordinal()),
            Codec.INT.optionalFieldOf("modifier", -1)
                    .forGetter(data -> data.nodeModifier == null ? -1 : data.nodeModifier.ordinal()),
            AspectList.CODEC.fieldOf("aspects").forGetter(NodeJarData::aspects),
            AspectList.CODEC.optionalFieldOf("base_aspects", AspectList.EMPTY).forGetter(NodeJarData::baseAspects))
            .apply(instance, NodeJarData::fromCodec));

    public static final StreamCodec<RegistryFriendlyByteBuf, NodeJarData> STREAM_CODEC = StreamCodec.of(
            NodeJarData::encode, NodeJarData::decode);

    public NodeJarData {
        nodeId = nodeId == null ? new UUID(0L, 0L) : nodeId;
        nodeType = nodeType == null ? NodeType.NORMAL : nodeType;
        aspects = aspects == null ? new AspectList() : aspects.copy();
        baseAspects = baseAspects == null || baseAspects.isEmpty() ? aspects.copy() : baseAspects.copy();
    }

    public boolean isEmpty() {
        return this.nodeId.getMostSignificantBits() == 0L
                && this.nodeId.getLeastSignificantBits() == 0L
                && this.aspects.isEmpty()
                && this.baseAspects.isEmpty();
    }

    @Override
    public AspectList aspects() {
        return this.aspects.copy();
    }

    @Override
    public AspectList baseAspects() {
        return this.baseAspects.copy();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof NodeJarData other)) {
            return false;
        }
        return this.nodeId.equals(other.nodeId)
                && this.nodeType == other.nodeType
                && this.nodeModifier == other.nodeModifier
                && mapsEqual(this.aspects.asMap(), other.aspects.asMap())
                && mapsEqual(this.baseAspects.asMap(), other.baseAspects.asMap());
    }

    @Override
    public int hashCode() {
        int result = this.nodeId.hashCode();
        result = 31 * result + this.nodeType.hashCode();
        result = 31 * result + (this.nodeModifier == null ? 0 : this.nodeModifier.hashCode());
        result = 31 * result + this.aspects.asMap().hashCode();
        result = 31 * result + this.baseAspects.asMap().hashCode();
        return result;
    }

    private static NodeJarData fromCodec(UUID id, int type, int modifier, AspectList aspects,
            AspectList baseAspects) {
        NodeType safeType = type >= 0 && type < NodeType.values().length ? NodeType.values()[type] : NodeType.NORMAL;
        return new NodeJarData(id, safeType, NodeModifier.byOrdinal(modifier), aspects, baseAspects);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, NodeJarData data) {
        buffer.writeUUID(data.nodeId);
        buffer.writeVarInt(data.nodeType.ordinal());
        buffer.writeVarInt(data.nodeModifier == null ? 0 : data.nodeModifier.ordinal() + 1);
        AspectList.STREAM_CODEC.encode(buffer, data.aspects);
        AspectList.STREAM_CODEC.encode(buffer, data.baseAspects);
    }

    private static NodeJarData decode(RegistryFriendlyByteBuf buffer) {
        UUID id = buffer.readUUID();
        int typeOrdinal = buffer.readVarInt();
        int modifierOrdinal = buffer.readVarInt() - 1;
        AspectList aspects = AspectList.STREAM_CODEC.decode(buffer);
        AspectList baseAspects = AspectList.STREAM_CODEC.decode(buffer);
        return fromCodec(id, typeOrdinal, modifierOrdinal, aspects, baseAspects);
    }

    private static boolean mapsEqual(Map<?, ?> left, Map<?, ?> right) {
        return left.equals(right);
    }
}
