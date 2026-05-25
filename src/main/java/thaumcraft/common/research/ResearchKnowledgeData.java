package thaumcraft.common.research;

import java.util.Set;
import java.util.TreeSet;

import com.mojang.serialization.Codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ResearchKnowledgeData(Set<String> completed) {
    public static final ResearchKnowledgeData EMPTY = new ResearchKnowledgeData(Set.of());
    public static final Codec<ResearchKnowledgeData> CODEC = Codec.STRING.listOf()
            .xmap(keys -> new ResearchKnowledgeData(Set.copyOf(keys)), data -> data.completed.stream().toList());
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchKnowledgeData> STREAM_CODEC =
            StreamCodec.of(ResearchKnowledgeData::encode, ResearchKnowledgeData::decode);

    public ResearchKnowledgeData {
        TreeSet<String> cleaned = new TreeSet<>();
        completed.forEach(key -> {
            if (key != null && !key.isBlank()) {
                cleaned.add(key);
            }
        });
        completed = Set.copyOf(cleaned);
    }

    public boolean isComplete(String key) {
        return key != null && this.completed.contains(key);
    }

    public ResearchKnowledgeData complete(String key) {
        if (key == null || key.isBlank() || this.isComplete(key)) {
            return this;
        }
        TreeSet<String> copy = new TreeSet<>(this.completed);
        copy.add(key);
        return new ResearchKnowledgeData(copy);
    }

    public ResearchKnowledgeData completeAll(Iterable<String> keys) {
        TreeSet<String> copy = new TreeSet<>(this.completed);
        for (String key : keys) {
            if (key != null && !key.isBlank()) {
                copy.add(key);
            }
        }
        return new ResearchKnowledgeData(copy);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchKnowledgeData data) {
        buffer.writeCollection(data.completed, ByteBufCodecs.STRING_UTF8::encode);
    }

    private static ResearchKnowledgeData decode(RegistryFriendlyByteBuf buffer) {
        return new ResearchKnowledgeData(Set.copyOf(buffer.readList(entryBuffer ->
                ByteBufCodecs.STRING_UTF8.decode((ByteBuf) entryBuffer))));
    }
}
