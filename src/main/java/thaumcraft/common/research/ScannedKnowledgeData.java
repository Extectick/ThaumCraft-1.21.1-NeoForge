package thaumcraft.common.research;

import java.util.Set;
import java.util.TreeSet;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ScannedKnowledgeData(Set<String> objects, Set<String> entities, Set<String> phenomena) {
    public static final ScannedKnowledgeData EMPTY = new ScannedKnowledgeData(Set.of(), Set.of(), Set.of());
    public static final Codec<ScannedKnowledgeData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.listOf().fieldOf("objects").forGetter(data -> data.objects.stream().toList()),
            Codec.STRING.listOf().fieldOf("entities").forGetter(data -> data.entities.stream().toList()),
            Codec.STRING.listOf().fieldOf("phenomena").forGetter(data -> data.phenomena.stream().toList()))
            .apply(instance, (objects, entities, phenomena) -> new ScannedKnowledgeData(Set.copyOf(objects),
                    Set.copyOf(entities), Set.copyOf(phenomena))));
    public static final StreamCodec<RegistryFriendlyByteBuf, ScannedKnowledgeData> STREAM_CODEC =
            StreamCodec.of(ScannedKnowledgeData::encode, ScannedKnowledgeData::decode);

    public ScannedKnowledgeData {
        objects = clean(objects);
        entities = clean(entities);
        phenomena = clean(phenomena);
    }

    public boolean isEmpty() {
        return this.objects.isEmpty() && this.entities.isEmpty() && this.phenomena.isEmpty();
    }

    public boolean has(ScanResult scan) {
        if (scan == null) {
            return false;
        }
        return switch (scan.kind()) {
            case OBJECT -> this.objects.contains(scan.key());
            case ENTITY -> this.entities.contains(scan.key());
            case PHENOMENA -> this.phenomena.contains(scan.key());
        };
    }

    public ScannedKnowledgeData add(ScanResult scan) {
        if (scan == null || scan.key().isBlank() || this.has(scan)) {
            return this;
        }
        TreeSet<String> objects = new TreeSet<>(this.objects);
        TreeSet<String> entities = new TreeSet<>(this.entities);
        TreeSet<String> phenomena = new TreeSet<>(this.phenomena);
        switch (scan.kind()) {
            case OBJECT -> objects.add(scan.key());
            case ENTITY -> entities.add(scan.key());
            case PHENOMENA -> phenomena.add(scan.key());
        }
        return new ScannedKnowledgeData(objects, entities, phenomena);
    }

    private static Set<String> clean(Set<String> values) {
        TreeSet<String> cleaned = new TreeSet<>();
        values.forEach(value -> {
            if (value != null && !value.isBlank()) {
                cleaned.add(value);
            }
        });
        return Set.copyOf(cleaned);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ScannedKnowledgeData data) {
        buffer.writeCollection(data.objects, ByteBufCodecs.STRING_UTF8::encode);
        buffer.writeCollection(data.entities, ByteBufCodecs.STRING_UTF8::encode);
        buffer.writeCollection(data.phenomena, ByteBufCodecs.STRING_UTF8::encode);
    }

    private static ScannedKnowledgeData decode(RegistryFriendlyByteBuf buffer) {
        Set<String> objects = Set.copyOf(buffer.readList(entryBuffer ->
                ByteBufCodecs.STRING_UTF8.decode((ByteBuf) entryBuffer)));
        Set<String> entities = Set.copyOf(buffer.readList(entryBuffer ->
                ByteBufCodecs.STRING_UTF8.decode((ByteBuf) entryBuffer)));
        Set<String> phenomena = Set.copyOf(buffer.readList(entryBuffer ->
                ByteBufCodecs.STRING_UTF8.decode((ByteBuf) entryBuffer)));
        return new ScannedKnowledgeData(objects, entities, phenomena);
    }
}
