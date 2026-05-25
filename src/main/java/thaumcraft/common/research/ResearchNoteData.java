package thaumcraft.common.research;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.lib.utils.HexUtils;

public record ResearchNoteData(String key, int color, List<HexEntry> entries, boolean complete, int copies) {
    public static final ResearchNoteData EMPTY = new ResearchNoteData("", 0x999999, List.of(), false, 0);
    public static final Codec<ResearchNoteData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.optionalFieldOf("key", "").forGetter(ResearchNoteData::key),
            Codec.INT.optionalFieldOf("color", 0x999999).forGetter(ResearchNoteData::color),
            HexEntry.CODEC.listOf().optionalFieldOf("entries", List.of()).forGetter(ResearchNoteData::entries),
            Codec.BOOL.optionalFieldOf("complete", false).forGetter(ResearchNoteData::complete),
            Codec.INT.optionalFieldOf("copies", 0).forGetter(ResearchNoteData::copies))
            .apply(instance, ResearchNoteData::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ResearchNoteData> STREAM_CODEC = StreamCodec.of(
            ResearchNoteData::encode, ResearchNoteData::decode);

    public ResearchNoteData {
        entries = List.copyOf(entries);
    }

    public Map<String, HexEntry> entryMap() {
        return this.entries.stream().collect(Collectors.toMap(entry -> entry.hex().toString(), Function.identity(),
                (first, second) -> second));
    }

    public boolean isEmpty() {
        return this.key.isEmpty() && this.entries.isEmpty();
    }

    public ResearchNoteData withEntry(HexUtils.Hex hex, Optional<Aspect> aspect, int type) {
        List<HexEntry> copy = new ArrayList<>(this.entries);
        copy.removeIf(entry -> entry.hex().equals(hex));
        copy.add(new HexEntry(hex, aspect, type));
        return new ResearchNoteData(this.key, this.color, copy, this.complete, this.copies);
    }

    public ResearchNoteData tryComplete() {
        return this.tryComplete(aspect -> true);
    }

    public ResearchNoteData tryComplete(Predicate<Aspect> discovered) {
        CompletionState state = this.getCompletionState(discovered);
        if (!state.complete()) {
            return this;
        }

        Set<String> connected = state.connected();
        List<HexEntry> remaining = new ArrayList<>();
        for (HexEntry entry : this.entries) {
            if (entry.type() == 1 || connected.contains(entry.hex().toString())) {
                remaining.add(entry);
            }
        }
        return new ResearchNoteData(this.key, this.color, remaining, true, this.copies);
    }

    public CompletionState getCompletionState() {
        return this.getCompletionState(aspect -> true);
    }

    public CompletionState getCompletionState(Predicate<Aspect> discovered) {
        Map<String, HexEntry> byHex = this.entryMap();
        List<HexEntry> anchors = this.entries.stream().filter(entry -> entry.type() == 1).toList();
        if (anchors.isEmpty()) {
            return CompletionState.incomplete(Set.of());
        }

        Set<String> checked = new HashSet<>();
        Set<String> connected = new HashSet<>();
        Set<String> missingAnchors = anchors.stream().map(entry -> entry.hex().toString()).collect(Collectors.toSet());
        HexEntry start = anchors.getFirst();
        if (start.aspect().isEmpty() || !discovered.test(start.aspect().get())) {
            return CompletionState.incomplete(Set.of());
        }
        missingAnchors.remove(start.hex().toString());
        this.collectConnections(start.hex(), byHex, checked, connected, missingAnchors, discovered);
        return missingAnchors.isEmpty() ? CompletionState.complete(connected) : CompletionState.incomplete(connected);
    }

    private void collectConnections(HexUtils.Hex hex, Map<String, HexEntry> entriesByHex, Set<String> checked,
            Set<String> connected, Set<String> missingAnchors, Predicate<Aspect> discovered) {
        checked.add(hex.toString());
        HexEntry source = entriesByHex.get(hex.toString());
        if (source == null || source.aspect().isEmpty() || !discovered.test(source.aspect().get())) {
            return;
        }

        for (int direction = 0; direction < 6; direction++) {
            HexUtils.Hex targetHex = hex.getNeighbour(direction);
            String targetKey = targetHex.toString();
            HexEntry target = entriesByHex.get(targetKey);
            if (target == null || checked.contains(targetKey) || target.type() < 1 || target.aspect().isEmpty()
                    || !discovered.test(target.aspect().get())) {
                continue;
            }

            if (aspectsConnect(source.aspect().get(), target.aspect().get())) {
                connected.add(targetKey);
                missingAnchors.remove(targetKey);
                this.collectConnections(targetHex, entriesByHex, checked, connected, missingAnchors, discovered);
            }
        }
    }

    public static boolean aspectsConnect(Aspect first, Aspect second) {
        return !first.isPrimal() && (first.getComponents()[0] == second || first.getComponents()[1] == second)
                || !second.isPrimal() && (second.getComponents()[0] == first || second.getComponents()[1] == first);
    }

    public static ResearchNoteData starter(String key) {
        return ResearchRegistry.createNoteData(key, ResearchRegistry.deterministicRandom(key));
    }

    public static ResearchNoteData create(ResearchEntry research, Random random) {
        int radius = 1 + Math.min(3, research.complexity());
        Map<String, HexUtils.Hex> generatedHexes = HexUtils.generateHexes(radius);
        List<HexUtils.Hex> anchors = HexUtils.distributeRingRandomly(radius, research.tags().size(), random);
        List<HexEntry> entries = new ArrayList<>();

        for (HexUtils.Hex hex : generatedHexes.values()) {
            entries.add(new HexEntry(hex, Optional.empty(), 0));
        }

        for (int i = 0; i < anchors.size(); i++) {
            HexUtils.Hex anchor = anchors.get(i);
            entries.removeIf(entry -> entry.hex().equals(anchor));
            entries.add(new HexEntry(anchor, Optional.of(research.tags().get(i)), 1));
        }

        if (research.complexity() > 1) {
            int blanks = research.complexity() * 2;
            while (blanks > 0 && removeRandomSafeBlank(entries, random)) {
                blanks--;
            }
        }

        return new ResearchNoteData(research.key(), research.color(), entries, false, 0);
    }

    private static boolean removeRandomSafeBlank(List<HexEntry> entries, Random random) {
        List<HexEntry> blanks = entries.stream().filter(entry -> entry.type() == 0).toList();
        if (blanks.isEmpty()) {
            return false;
        }

        HexEntry candidate = blanks.get(random.nextInt(blanks.size()));
        Map<String, HexEntry> byHex = entries.stream().collect(Collectors.toMap(entry -> entry.hex().toString(),
                Function.identity(), (first, second) -> second));
        for (int direction = 0; direction < 6; direction++) {
            HexUtils.Hex neighbour = candidate.hex().getNeighbour(direction);
            HexEntry neighbourEntry = byHex.get(neighbour.toString());
            if (neighbourEntry == null || neighbourEntry.type() != 1) {
                continue;
            }

            int neighbourCount = 0;
            for (int checkDirection = 0; checkDirection < 6; checkDirection++) {
                HexUtils.Hex adjacent = neighbour.getNeighbour(checkDirection);
                if (byHex.containsKey(adjacent.toString()) && !adjacent.equals(candidate.hex())) {
                    neighbourCount++;
                }
                if (neighbourCount >= 2) {
                    break;
                }
            }
            if (neighbourCount < 2) {
                return false;
            }
        }

        entries.removeIf(entry -> entry.hex().equals(candidate.hex()));
        return true;
    }

    public static ResearchNoteData legacyStarter(String key) {
        List<HexEntry> entries = new ArrayList<>();
        for (HexUtils.Hex hex : HexUtils.generateHexes(3).values()) {
            entries.add(new HexEntry(hex, Optional.empty(), 0));
        }
        entries.add(new HexEntry(new HexUtils.Hex(-2, 0), Optional.of(Aspect.AIR), 1));
        entries.add(new HexEntry(new HexUtils.Hex(2, 0), Optional.of(Aspect.EARTH), 1));
        return new ResearchNoteData(key, 0x999999, entries, false, 0);
    }

    private static void encode(RegistryFriendlyByteBuf buffer, ResearchNoteData data) {
        buffer.writeUtf(data.key);
        buffer.writeVarInt(data.color);
        buffer.writeCollection(data.entries, (entryBuffer, entry) -> HexEntry.STREAM_CODEC.encode(entryBuffer, entry));
        buffer.writeBoolean(data.complete);
        buffer.writeVarInt(data.copies);
    }

    private static ResearchNoteData decode(RegistryFriendlyByteBuf buffer) {
        String key = buffer.readUtf();
        int color = buffer.readVarInt();
        List<HexEntry> entries = buffer.readList(entryBuffer -> HexEntry.STREAM_CODEC.decode(entryBuffer));
        boolean complete = buffer.readBoolean();
        int copies = buffer.readVarInt();
        return new ResearchNoteData(key, color, entries, complete, copies);
    }

    public record HexEntry(HexUtils.Hex hex, Optional<Aspect> aspect, int type) {
        public static final Codec<HexEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                HexUtils.Hex.CODEC.fieldOf("hex").forGetter(HexEntry::hex),
                Aspect.CODEC.optionalFieldOf("aspect").forGetter(HexEntry::aspect),
                Codec.INT.optionalFieldOf("type", 0).forGetter(HexEntry::type))
                .apply(instance, HexEntry::new));
        public static final StreamCodec<ByteBuf, HexEntry> STREAM_CODEC = StreamCodec.of(HexEntry::encode,
                HexEntry::decode);

        private static void encode(ByteBuf buffer, HexEntry entry) {
            HexUtils.Hex.STREAM_CODEC.encode(buffer, entry.hex);
            buffer.writeBoolean(entry.aspect.isPresent());
            entry.aspect.ifPresent(aspect -> Aspect.STREAM_CODEC.encode(buffer, aspect));
            ByteBufCodecs.VAR_INT.encode(buffer, entry.type);
        }

        private static HexEntry decode(ByteBuf buffer) {
            HexUtils.Hex hex = HexUtils.Hex.STREAM_CODEC.decode(buffer);
            Optional<Aspect> aspect = buffer.readBoolean() ? Optional.of(Aspect.STREAM_CODEC.decode(buffer))
                    : Optional.empty();
            int type = ByteBufCodecs.VAR_INT.decode(buffer);
            return new HexEntry(hex, aspect, type);
        }
    }

    public record CompletionState(boolean complete, Set<String> connected) {
        private static CompletionState complete(Set<String> connected) {
            return new CompletionState(true, Set.copyOf(connected));
        }

        private static CompletionState incomplete(Set<String> connected) {
            return new CompletionState(false, Set.copyOf(connected));
        }
    }
}
